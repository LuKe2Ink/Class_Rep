package com.example.classrep.SingleActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.example.classrep.HomeActivity;
import com.example.classrep.R;
import com.example.classrep.adapter.FundChronologyAdapter;
import com.example.classrep.adapter.MeetingAdapter;
import com.example.classrep.database.ClassRepDB;
import com.example.classrep.database.entity.Event;
import com.example.classrep.database.entity.FundChronology;
import com.example.classrep.database.entity.Meeting;
import com.example.classrep.database.entity.PTAmeeting;
import com.example.classrep.utilities.SingleToneClass;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.gson.Gson;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;

public class FundChronologyActivity extends AppCompatActivity {

    private List<FundChronology> fundChronologies = new ArrayList<>();

    private MaterialToolbar toolbar;
    private RecyclerView recycle;


    private FundChronologyAdapter adapter;
    private ClassRepDB db;
    int item;
    private BlurView blurView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fund_chronology);

        blurView = findViewById(R.id.blurViewFundChron);
        backgroundBlur();

        SingleToneClass singleToneClass = com.example.classrep.utilities.SingleToneClass.getInstance();
        item = singleToneClass.getData("institute");
        if(!singleToneClass.getImageBackground().contains("nada")){
            ConstraintLayout background = findViewById(R.id.backgroundEventFundChron);

            Uri uri = Uri.parse(singleToneClass.getImageBackground());
            Bitmap bmImg = null;
            try {
                bmImg = BitmapFactory.decodeStream( getContentResolver().openInputStream(uri));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            BitmapDrawable background1 = new BitmapDrawable(bmImg);
            background.setBackground(background1);
        }

        db = ClassRepDB.getDatabase(this);

        recycle = findViewById(R.id.recyclerViewFund);
        toolbar = findViewById(R.id.topAppBarFundChron);

        toolbar.setNavigationOnClickListener(menuItem->{
            AsyncTask.execute(()->{
                Intent intent = new Intent(this, HomeActivity.class);
                List<Event> events = db.ClassRepDAO().getAllEvent(item);
                List<Meeting> meetings = db.ClassRepDAO().getAllMeeting(item);
                List<PTAmeeting> ptas = db.ClassRepDAO().getAllPTAmeeting(item);

                String jsEvent = new Gson().toJson(events);
                intent.putExtra("events", jsEvent);
                String jsMeet = new Gson().toJson(meetings);
                intent.putExtra("meetings", jsMeet);
                String jsPta = new Gson().toJson(ptas);
                intent.putExtra("ptas", jsPta);

                startActivity(intent);
            });
        });

        AsyncTask.execute(()->{
            fundChronologies = db.ClassRepDAO().getAllFundChronology(singleToneClass.getData("fund"));

            createRecycler();
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FundChronologyActivity.this, HomeActivity.class);
                intent.putExtra("fragment", "fund");
                FundChronologyActivity.this.startActivity(intent);
            }
        });
    }
    private void createRecycler() {
        adapter = new FundChronologyAdapter(this, fundChronologies);
        this.runOnUiThread(()->recycle.setAdapter(adapter));
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra("fragment", "fund");
        startActivity(intent);
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