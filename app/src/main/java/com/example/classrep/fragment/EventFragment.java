package com.example.classrep.fragment;

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
import com.example.classrep.SingleActivity.EventActivity;
import com.example.classrep.SingleActivity.PtaActivity;
import com.example.classrep.adapter.EventAdapter;
import com.example.classrep.adapter.MeetingAdapter;
import com.example.classrep.adder.AddEventActivity;
import com.example.classrep.database.ClassRepDB;
import com.example.classrep.database.entity.Adhesion;
import com.example.classrep.database.entity.Child;
import com.example.classrep.database.entity.Event;
import com.example.classrep.database.entity.PTAmeeting;
import com.example.classrep.database.entity.Parent;
import com.example.classrep.utilities.SingleToneClass;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.function.Predicate;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;

public class EventFragment extends Fragment implements EventAdapter.onEventListener {

    private EventAdapter adapter;
    private List<Event> events = new ArrayList<>();
    private List<Integer> removeEvents = new ArrayList<>();
    private List<Integer> numberParentsForEvents = new ArrayList<>();
    private List<Integer> numberChildrenForEvents = new ArrayList<>();
    private boolean trash = false;

    private int count = 1;

    private ClassRepDB db;
    private RecyclerView recycle;
    private SingleToneClass singleToneClass;

    private View view;
    private MaterialToolbar topAppbar;
    private FloatingActionButton add;
    private BlurView blurView;


    @RequiresApi(api = Build.VERSION_CODES.O)
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
            events = db.ClassRepDAO().getAllEvent(singleToneClass.getData("institute"));
            System.out.println(events);
            for (int i=0; i<events.size() ; i++){
                numberParentsForEvents.add(db.ClassRepDAO().getEventParents(events.get(i).getId_event()).size());
                numberChildrenForEvents.add(db.ClassRepDAO().getEventChildren(events.get(i).getId_event()).size());
            }

            createRecycler();
        });

        add.setOnClickListener(view -> {
            if (trash){
                if(!removeEvents.isEmpty()){
                    Predicate<Event> contain = x -> (removeEvents.contains(x.getId_event()));
                    events.removeIf(contain);
                    openOrCloseTrashcan(false, topAppbar.getMenu().findItem(R.id.trash), View.INVISIBLE);


                    AsyncTask.execute(()->{
                        db.ClassRepDAO().deleteEvent(removeEvents);
                        db.ClassRepDAO().deleteParentFromEvent(removeEvents);
                        removeEvents.clear();
                    });
                    adapter.notifyDataSetChanged();
                }
            } else {
                Intent intent = new Intent(this.getContext(), AddEventActivity.class);
                startActivity(intent);
            }
        });

        recycle.addOnScrollListener(new RecyclerView.OnScrollListener(){
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                for (int i = 0; i< events.size(); i++){
                    RecyclerView.ViewHolder view = recycle.findViewHolderForLayoutPosition(i);
                    if(view != null){
                        CheckBox check = view.itemView.findViewById(R.id.checkBoxAdapter);
                        int visible = trash ? View.VISIBLE : View.INVISIBLE;
                        check.setVisibility(visible);
                        if(removeEvents.contains(events.get(i).getId_event())){
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
                        removeEvents.clear();
                    } else {
                        openOrCloseTrashcan(true, menuItem, View.VISIBLE);
                    }
                    break;
                case R.id.deselectAll:
                    for (int i = 0; i< events.size(); i++){
                        RecyclerView.ViewHolder view = recycle.findViewHolderForLayoutPosition(i);
                        if(view != null){
                            CheckBox check = view.itemView.findViewById(R.id.checkBoxAdapter);
                            check.setChecked(false);
                        }
                    }
                    removeEvents.clear();
                    break;
                case R.id.selectAll:
                    for (int i = 0; i< events.size(); i++){
                        RecyclerView.ViewHolder view = recycle.findViewHolderForLayoutPosition(i);
                        if(view != null){
                            CheckBox check = view.itemView.findViewById(R.id.checkBoxAdapter);
                            check.setChecked(true);
                        }
                        removeEvents.add(events.get(i).getId_event());
                    }
                    break;
            }
            return false;
        });

        return view;
    }

    private void createRecycler() {
        adapter = new EventAdapter(view.getContext(), events, numberParentsForEvents, numberChildrenForEvents, this);
        getActivity().runOnUiThread(()->recycle.setAdapter(adapter));
    }

    @Override
    public void onEventClick(int position) {
        if(trash){
            int id = events.get(position).getId_event();
            RecyclerView.ViewHolder view = recycle.findViewHolderForAdapterPosition(position);
            CheckBox check = view.itemView.findViewById(R.id.checkBoxAdapter);
            if (!removeEvents.contains(id)) {
                removeEvents.add(id);
                check.setChecked(true);
            } else {
                removeEvents.remove(removeEvents.indexOf(id));
                check.setChecked(false);
            }
        } else {
            int item = events.get(position).getId_event();

            singleToneClass.setData("event", item);

            AsyncTask.execute(()->{
                Intent intent = new Intent(getContext(), EventActivity.class);
                List<Parent> parents = db.ClassRepDAO().getEventParents(item);
                String jsParent = new Gson().toJson(parents);
                intent.putExtra("parents", jsParent);
                List<Child> children = db.ClassRepDAO().getEventChildren(item);
                String jsChild = new Gson().toJson(children);
                intent.putExtra("children", jsChild);
                List<Adhesion> adhesions = db.ClassRepDAO().getEventAdhesion(item);
                String hsAdhesion = new Gson().toJson(adhesions);
                intent.putExtra("adhesion", hsAdhesion);
                String jsEvent = new Gson().toJson(db.ClassRepDAO().getSingleEvent(item));
                intent.putExtra("event", jsEvent);
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

        for (int i = 0; i< events.size(); i++){
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