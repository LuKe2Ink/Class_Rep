package com.example.classrep.fragment;

import android.content.AsyncQueryHandler;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Toast;

import com.example.classrep.R;
import com.example.classrep.SingleActivity.PtaActivity;
import com.example.classrep.adapter.PtaAdapter;
import com.example.classrep.adder.AddPtaActivity;
import com.example.classrep.database.ClassRepDB;
import com.example.classrep.database.entity.Event;
import com.example.classrep.database.entity.PTAmeeting;
import com.example.classrep.database.entity.Parent;
import com.example.classrep.utilities.SingleToneClass;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;

public class PTAFragment extends Fragment implements PtaAdapter.onPtaListener{

    private PtaAdapter adapter;
    private List<PTAmeeting> ptaMeetings = new ArrayList<>();
    private List<Integer> removePta = new ArrayList<>();
    private List<Integer> numberParentsForPta = new ArrayList<>();
    private boolean trash = false;

    private ClassRepDB db;
    private RecyclerView recycle;
    private SingleToneClass singleToneClass;

    private View view;
    private MaterialToolbar topAppbar;
    private FloatingActionButton add;
    private BlurView blurView;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        db = ClassRepDB.getDatabase(getContext());

        singleToneClass = com.example.classrep.utilities.SingleToneClass.getInstance();

        view = inflater.inflate(R.layout.fragments_home, container, false);
        recycle = view.findViewById(R.id.recyclerViewHome);
        topAppbar = view.findViewById(R.id.topAppBarHome);
        add = view.findViewById(R.id.addHome);

        blurView = view.findViewById(R.id.blurViewFragment);
        backgroundBlur();

        AsyncTask.execute(()->{
            ptaMeetings = db.ClassRepDAO().getAllPTAmeeting(singleToneClass.getData("institute"));
            for (int i = 0; i< ptaMeetings.size() ; i++){
                numberParentsForPta.add(db.ClassRepDAO().getEventParents(ptaMeetings.get(i).getId_pta()).size());
            }

            createRecycler();
        });

        add.setOnClickListener(view -> {
            if (trash){
                if(!removePta.isEmpty()){
                    Predicate<PTAmeeting> contain = x -> (removePta.contains(x.getId_pta()));
                    ptaMeetings.removeIf(contain);
                    openOrCloseTrashcan(false, topAppbar.getMenu().findItem(R.id.trash), View.INVISIBLE);

                    AsyncTask.execute(()->{
                        db.ClassRepDAO().deletePTAmeeting(removePta);
                        db.ClassRepDAO().deleteParentFromPta(removePta);
                        removePta.clear();

                    });
                    adapter.notifyDataSetChanged();
                }
            } else {
                Intent intent = new Intent(this.getContext(), AddPtaActivity.class);
                startActivity(intent);
            }
        });

        recycle.addOnScrollListener(new RecyclerView.OnScrollListener(){
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                for (int i = 0; i< ptaMeetings.size(); i++){
                    RecyclerView.ViewHolder view = recycle.findViewHolderForLayoutPosition(i);
                    if(view != null){
                        CheckBox check = view.itemView.findViewById(R.id.checkBoxAdapter);
                        int visible = trash ? View.VISIBLE : View.INVISIBLE;
                        check.setVisibility(visible);
                        if(removePta.contains(ptaMeetings.get(i).getId_pta())){
                            check.setChecked(true);
                        } else {
                            check.setChecked(false);
                        }
                    }
                }
            }
        });

        topAppbar.setOnMenuItemClickListener(menuItem -> {
            switch (menuItem.getItemId()){
                case R.id.trash:
                    if(trash){
                        openOrCloseTrashcan(false, menuItem, View.INVISIBLE);
                        removePta.clear();
                    } else {
                        openOrCloseTrashcan(true, menuItem, View.VISIBLE);
                    }
                    break;
                case R.id.deselectAll:
                    for (int i = 0; i< ptaMeetings.size(); i++){
                        RecyclerView.ViewHolder view = recycle.findViewHolderForLayoutPosition(i);
                        if(view != null){
                            CheckBox check = view.itemView.findViewById(R.id.checkBoxAdapter);
                            check.setChecked(false);
                        }
                    }
                    removePta.clear();
                    break;
                case R.id.selectAll:
                    for (int i = 0; i< ptaMeetings.size(); i++){
                        RecyclerView.ViewHolder view = recycle.findViewHolderForLayoutPosition(i);
                        if(view != null){
                            CheckBox check = view.itemView.findViewById(R.id.checkBoxAdapter);
                            check.setChecked(true);
                        }
                        removePta.add(ptaMeetings.get(i).getId_pta());
                    }
                    break;
            }
            return false;
        });

        return view;
    }

    private void createRecycler() {
        adapter = new PtaAdapter(view.getContext(), ptaMeetings, this);
        getActivity().runOnUiThread(()->recycle.setAdapter(adapter));
    }

    @Override
    public void onPtaClick(int position) {
        int id = ptaMeetings.get(position).getId_pta();

        if(trash){
            RecyclerView.ViewHolder view = recycle.findViewHolderForAdapterPosition(position);
            CheckBox check = view.itemView.findViewById(R.id.checkBoxAdapter);
            if (!removePta.contains(id)) {
                removePta.add(id);
                check.setChecked(true);
            } else {
                removePta.remove(removePta.indexOf(id));
                check.setChecked(false);
            }
        } else {
            int item = ptaMeetings.get(position).getId_pta();

            singleToneClass.setData("pta", item);

            AsyncTask.execute(()->{
                Intent intent = new Intent(getContext(), PtaActivity.class);
                List<Parent> parents = db.ClassRepDAO().getPTAmeetingParents(id);
                String jsParent = new Gson().toJson(parents);
                System.out.println(jsParent);
                intent.putExtra("parents", jsParent);
                String jsPta = new Gson().toJson(db.ClassRepDAO().getSinglePta(item));
                intent.putExtra("pta", jsPta);
                System.out.println(jsPta);
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

        for (int i = 0; i< ptaMeetings.size(); i++){
            RecyclerView.ViewHolder view = recycle.findViewHolderForLayoutPosition(i);
            if(view != null){
                CheckBox check = view.itemView.findViewById(R.id.checkBoxAdapter);
                check.setVisibility(visible);
            }
        }


        add.setImageResource(boo ? R.drawable.ic_open_trashcan : R.drawable.ic_baseline_add_24);
    }
    public void backgroundBlur(){
        float radius = 5f;

        View decorView = getActivity().getWindow().getDecorView();
        //ViewGroup you want to start blur from. Choose root as close to BlurView in hierarchy as possible.
        ViewGroup rootView = decorView.findViewById(android.R.id.content);
        //Set drawable to draw in the beginning of each blurred frame (Optional).
        //Can be used in case your layout has a lot of transparent space and your content
        //gets kinda lost after after blur is applied.
        Drawable windowBackground = decorView.getBackground();

        blurView.setupWith(rootView)
                .setFrameClearDrawable(windowBackground)
                .setBlurAlgorithm(new RenderScriptBlur(getContext()))
                .setBlurRadius(radius)
                .setHasFixedTransformationMatrix(true);
    }
}