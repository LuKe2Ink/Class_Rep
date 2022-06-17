package com.example.classrep.fragment;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.classrep.R;
import com.example.classrep.SelectionActivity;
import com.example.classrep.database.ClassRepDB;
import com.example.classrep.database.entity.Event;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Calendar;
import java.util.Date;

public class EventFragment extends Fragment {

    private FloatingActionButton add;

    private ClassRepDB db;

    private int cont = 1;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event, container, false);
        add = view.findViewById(R.id.insertEvent);
        db = ClassRepDB.getDatabase(getContext());

        add.setOnClickListener( v ->{
            AsyncTask.execute(()->{
                db.ClassRepDAO().insertEvent(new Event(cont, 1, "bella pe' te",
                        false, false, Date.from(Calendar.getInstance().toInstant()), "", ""));
                cont++;
            });
        });

        return view;
    }
}