package com.example.classrep.fragment;

import android.content.Intent;
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
import com.example.classrep.adapter.MeetingAdapter;
import com.example.classrep.adder.AddMeetingActivity;
import com.example.classrep.database.ClassRepDB;
import com.example.classrep.database.entity.Meeting;
import com.example.classrep.utilities.SingleToneClass;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class MeetingFragment extends Fragment implements MeetingAdapter.onMeetingListener {

    private MeetingAdapter adapter;
    private List<Meeting> meetings = new ArrayList<>();
    private List<Integer> removeMeetings = new ArrayList<>();
    private boolean trash = false;

    private ClassRepDB db;
    private RecyclerView recycle;
    private SingleToneClass singleToneClass;

    private View view;
    private MaterialToolbar topAppbar;
    private FloatingActionButton add;


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

        AsyncTask.execute(()->{
            meetings = db.ClassRepDAO().getAllMeeting(singleToneClass.getData("institute"));

            createRecycler();
        });

        add.setOnClickListener(view -> {
            if (trash){
                if(!removeMeetings.isEmpty()){
                    Predicate<Meeting> contain = x -> (removeMeetings.contains(x.getId_meeting()));
                    meetings.removeIf(contain);
                    openOrCloseTrashcan(false, topAppbar.getMenu().findItem(R.id.trash), View.INVISIBLE);

                    AsyncTask.execute(()->{
                        db.ClassRepDAO().deleteMeeting(removeMeetings);
                        removeMeetings.clear();
                    });
                    adapter.notifyDataSetChanged();
                }
            } else {
                Intent intent = new Intent(this.getContext(), AddMeetingActivity.class);
                startActivity(intent);
            }
        });

        recycle.addOnScrollListener(new RecyclerView.OnScrollListener(){
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                for (int i=0; i<meetings.size(); i++){
                    RecyclerView.ViewHolder view = recycle.findViewHolderForLayoutPosition(i);
                    if(view != null){
                        CheckBox check = view.itemView.findViewById(R.id.checkBoxAdapter);
                        int visible = trash ? View.VISIBLE : View.INVISIBLE;
                        check.setVisibility(visible);
                        if(removeMeetings.contains(meetings.get(i).getId_meeting())){
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
                        removeMeetings.clear();
                    } else {
                        openOrCloseTrashcan(true, menuItem, View.VISIBLE);
                    }
                    break;
                case R.id.deselectAll:
                    for (int i=0; i<meetings.size(); i++){
                        RecyclerView.ViewHolder view = recycle.findViewHolderForLayoutPosition(i);
                        if(view != null){
                            CheckBox check = view.itemView.findViewById(R.id.checkBoxAdapter);
                            check.setChecked(false);
                        }
                    }
                    removeMeetings.clear();
                    break;
                case R.id.selectAll:
                    for (int i=0; i<meetings.size(); i++){
                        RecyclerView.ViewHolder view = recycle.findViewHolderForLayoutPosition(i);
                        if(view != null){
                            CheckBox check = view.itemView.findViewById(R.id.checkBoxAdapter);
                            check.setChecked(true);
                        }
                        removeMeetings.add(meetings.get(i).getId_meeting());
                    }
                    break;
            }
            return false;
        });

        return view;
    }

    private void createRecycler() {
        adapter = new MeetingAdapter(view.getContext(), meetings, this);
        getActivity().runOnUiThread(()->recycle.setAdapter(adapter));
    }

    @Override
    public void onMeetingClick(int position) {
        if(trash){
            int id = meetings.get(position).getId_meeting();
            RecyclerView.ViewHolder view = recycle.findViewHolderForAdapterPosition(position);
            CheckBox check = view.itemView.findViewById(R.id.checkBoxAdapter);
            if (!removeMeetings.contains(id)) {
                removeMeetings.add(id);
                check.setChecked(true);
            } else {
                removeMeetings.remove(removeMeetings.indexOf(id));
                check.setChecked(false);
            }
        } else {
            int item = meetings.get(position).getId_meeting();

            singleToneClass.setData("meeting", item);

//            Intent intent = new Intent(this, SingleMeetingActivity.class);
//            startActivity(intent);
        }
    }

    private void openOrCloseTrashcan(boolean boo, MenuItem item, int visible){
        trash = boo;
        item.setIcon(boo ? R.drawable.ic_baseline_close_24 : R.drawable.ic_closed_trashcan);
        MenuItem deselectAll = topAppbar.getMenu().findItem(R.id.deselectAll);
        deselectAll.setVisible(boo);
        MenuItem selectAll = topAppbar.getMenu().findItem(R.id.selectAll);
        selectAll.setVisible(boo);

        for (int i=0; i<meetings.size(); i++){
            RecyclerView.ViewHolder view = recycle.findViewHolderForLayoutPosition(i);
            if(view != null){
                CheckBox check = view.itemView.findViewById(R.id.checkBoxAdapter);
                check.setVisibility(visible);
            }
        }


        add.setImageResource(boo ? R.drawable.ic_open_trashcan : R.drawable.ic_baseline_add_24);
    }
}