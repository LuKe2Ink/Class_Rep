package com.example.classrep;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.example.classrep.adapter.InstituteAdapter;
import com.example.classrep.adder.AddInstituteActivity;
import com.example.classrep.database.ClassRepDB;
import com.example.classrep.database.entity.Event;
import com.example.classrep.database.entity.Institute;
import com.example.classrep.database.entity.Meeting;
import com.example.classrep.database.entity.PTAmeeting;
import com.example.classrep.database.entity.Settings;
import com.example.classrep.utilities.SingleToneClass;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;


public class SelectionActivity extends AppCompatActivity implements InstituteAdapter.onInstituteListener {

    private List<Institute> listInstitutes;
    private InstituteAdapter adapter;
    private RecyclerView recycle;
    private MaterialToolbar topAppbar;
    private FloatingActionButton add;
    private BlurView blurView;

    private ClassRepDB db;

    private int pageHeight = 1120;
    private int pagewidth = 792;

    Bitmap bmp, scaledbmp;

    private boolean trash = false;
    List<Integer> removeInstitute = new ArrayList<>();

    private static final int PERMISSION_REQUEST_CODE = 200;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);

        blurView = findViewById(R.id.blurView);
        topAppbar = findViewById(R.id.topAppBar);

        bmp = BitmapFactory.decodeResource(getResources(), R.drawable.hqdefault);
        scaledbmp = Bitmap.createScaledBitmap(bmp, 140, 140, false);
        //listInstitutes.add(new Institute(1, "bruh1", "bruhone", ""));

        db = ClassRepDB.getDatabase(SelectionActivity.this);

        recycle = findViewById(R.id.institute);

        add = findViewById(R.id.addInstitute);

        AsyncTask.execute(()->{
            //db.ClassRepDAO().insertInstitute(new Institute(1, "bruh", "bruh1", "", Date.from(Calendar.getInstance().toInstant())));
            //db.ClassRepDAO().insertMeeting(new Meeting(1, "Prova", "scolastica", Date.from(Calendar.getInstance().toInstant()),"", ""));
            listInstitutes = db.ClassRepDAO().getAllInstitute();
            createRecycler();
        });

        add.setOnClickListener(view -> {
            if (trash){
                if(!removeInstitute.isEmpty()){
                    Predicate<Institute> contain = x -> (removeInstitute.contains(x.getId_institute()));
                    listInstitutes.removeIf(contain);
                    System.out.println(listInstitutes);
                    openOrCloseTrashcan(false, topAppbar.getMenu().findItem(R.id.trash), View.INVISIBLE);

                    AsyncTask.execute(()->{
                        db.ClassRepDAO().deleteInstitute(removeInstitute);
                        removeInstitute.clear();
                    });
                    adapter.notifyDataSetChanged();
                }
            } else {
                Intent intent = new Intent(this, AddInstituteActivity.class);
                startActivity(intent);
            }
        });

        topAppbar.setOnMenuItemClickListener(menuItem -> {
            switch (menuItem.getItemId()){
                case R.id.trash:
                    if(trash){
                        openOrCloseTrashcan(false, menuItem, View.INVISIBLE);
                        removeInstitute.clear();
                    } else {
                        openOrCloseTrashcan(true, menuItem, View.VISIBLE);
                    }
                    break;
                case R.id.deselectAll:
                    for (int i=0; i<listInstitutes.size(); i++){
                        RecyclerView.ViewHolder view = recycle.findViewHolderForAdapterPosition(i);
                        if(view != null){
                            CheckBox check = view.itemView.findViewById(R.id.checkBox);
                            check.setChecked(false);
                        }
                    }
                    removeInstitute.clear();
                    break;
                case R.id.selectAll:
                    for (int i=0; i<listInstitutes.size(); i++){
                        RecyclerView.ViewHolder view = recycle.findViewHolderForAdapterPosition(i);
                        if(view != null){
                            CheckBox check = view.itemView.findViewById(R.id.checkBox);
                            check.setChecked(true);
                        }
                        removeInstitute.add(listInstitutes.get(i).getId_institute());
                    }
                    break;
            }
            return false;
        });


        recycle.addOnScrollListener(new RecyclerView.OnScrollListener(){
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                for (int i=0; i<listInstitutes.size(); i++){
                    RecyclerView.ViewHolder view = recycle.findViewHolderForLayoutPosition(i);
                    if(view != null){
                        CheckBox check = view.itemView.findViewById(R.id.checkBox);
                        int visible = trash ? View.VISIBLE : View.INVISIBLE;
                        check.setVisibility(visible);
                        if(removeInstitute.contains(listInstitutes.get(i).getId_institute())){
                            check.setChecked(true);
                        } else{
                            check.setChecked(false);
                        }
                    }
                }
            }
        });

        backgroundBlur();
        //Toast.makeText(getBaseContext(), String.valueOf(singleToneClass.getData("institute")), Toast.LENGTH_SHORT).show();

    }

    private void createRecycler(){

        if(listInstitutes.isEmpty()){
            TextView empty = findViewById(R.id.empty);
            this.runOnUiThread(() -> empty.setVisibility(View.VISIBLE));
        } else {
            adapter = new InstituteAdapter(getApplicationContext(), listInstitutes, this);
            this.runOnUiThread(() ->recycle.setAdapter(adapter));
        }
    }

    @Override
    public void onInstituteClick(int position) {
        if(trash){
            int id = listInstitutes.get(position).getId_institute();
            RecyclerView.ViewHolder view = recycle.findViewHolderForAdapterPosition(position);
            CheckBox check = view.itemView.findViewById(R.id.checkBox);
            if (!removeInstitute.contains(id)) {
                removeInstitute.add(id);
                check.setChecked(true);
            } else {
                removeInstitute.remove(removeInstitute.indexOf(id));
                check.setChecked(false);
            }
        } else {
            int item = listInstitutes.get(position).getId_institute();

            SingleToneClass singleToneClass = com.example.classrep.utilities.SingleToneClass.getInstance();
            singleToneClass.setData("institute",item);
            AsyncTask.execute(()->{
                Settings setting = db.ClassRepDAO().getSetting(item);
                singleToneClass.setImageBackground(setting.getImage());

                Intent intent = new Intent(this, HomeActivity.class);
                intent.putExtra("fragment", "calendar");
                startActivity(intent);
            });
        }
    }

    private void openOrCloseTrashcan(boolean boo, MenuItem item, int visible){
        trash = boo;
        item.setIcon(boo ? R.drawable.ic_baseline_close_24 : R.drawable.ic_closed_trashcan);
        MenuItem deselectAll = topAppbar.getMenu().findItem(R.id.deselectAll);
        deselectAll.setVisible(boo);
        MenuItem selectAll = topAppbar.getMenu().findItem(R.id.selectAll);
        selectAll.setVisible(boo);

        for (int i=0; i<listInstitutes.size(); i++){
            RecyclerView.ViewHolder view = recycle.findViewHolderForAdapterPosition(i);
            if(view != null){
                CheckBox check = view.itemView.findViewById(R.id.checkBox);
                check.setVisibility(visible);
            }
        }

        add.setImageResource(boo ? R.drawable.ic_open_trashcan : R.drawable.ic_baseline_add_24 );
    }

    public void backgroundBlur(){
        float radius = 5f;

        View decorView = getWindow().getDecorView();
        //ViewGroup you want to start blur from. Choose root as close to BlurView in hierarchy as possible.
        ViewGroup rootView = decorView.findViewById(android.R.id.content);
        //Set drawable to draw in the beginning of each blurred frame (Optional).
        //Can be used in case your layout has a lot of transparent space and your content
        //gets kinda lost after after blur is applied.
        Drawable windowBackground = decorView.getBackground();

        blurView.setupWith(rootView)
                .setFrameClearDrawable(windowBackground)
                .setBlurAlgorithm(new RenderScriptBlur(this))
                .setBlurRadius(radius)
                .setHasFixedTransformationMatrix(true);
    }

}