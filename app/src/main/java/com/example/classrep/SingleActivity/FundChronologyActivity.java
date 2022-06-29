package com.example.classrep.SingleActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

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

import java.util.ArrayList;
import java.util.List;

public class FundChronologyActivity extends AppCompatActivity {

    private List<FundChronology> fundChronologies = new ArrayList<>();

    private MaterialToolbar toolbar;
    private RecyclerView recycle;


    private FundChronologyAdapter adapter;
    private ClassRepDB db;
    int item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fund_chronology);

        SingleToneClass singleToneClass = com.example.classrep.utilities.SingleToneClass.getInstance();
        item = singleToneClass.getData("institute");

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
}