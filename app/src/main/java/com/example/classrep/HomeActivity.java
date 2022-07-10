package com.example.classrep;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.AsyncQueryHandler;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import com.example.classrep.database.ClassRepDB;
import com.example.classrep.database.entity.Event;
import com.example.classrep.database.entity.Fund;
import com.example.classrep.database.entity.FundChronology;
import com.example.classrep.database.entity.Meeting;
import com.example.classrep.database.entity.PTAmeeting;
import com.example.classrep.fragment.CalendarFragment;
import com.example.classrep.fragment.EventFragment;
import com.example.classrep.fragment.FundFragment;
import com.example.classrep.fragment.MeetingFragment;
import com.example.classrep.fragment.PTAFragment;
import com.example.classrep.utilities.SingleToneClass;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;

import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationMenuView;

    private ClassRepDB db;
    private int institute_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        db = ClassRepDB.getDatabase(getBaseContext());
        SingleToneClass singleToneClass = com.example.classrep.utilities.SingleToneClass.getInstance();
        institute_id = singleToneClass.getData("institute");
        bottomNavigationMenuView = findViewById(R.id.bottom_navigation);

        Intent intent = getIntent();

        if(intent.getStringExtra("fragment").contains("calendar")){
            callCalendarFragment();
            bottomNavigationMenuView.setSelectedItemId(R.id.calendar);
        } else if(intent.getStringExtra("fragment").contains("fund")){Bundle bundle= new Bundle();
            callFundFragment();
            bottomNavigationMenuView.setSelectedItemId(R.id.fund);
        } else if(intent.getStringExtra("fragment").contains("pta")){
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_menu, new PTAFragment()).commit();
            bottomNavigationMenuView.setSelectedItemId(R.id.pta);
        } else if(intent.getStringExtra("fragment").contains("meeting")){
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_menu, new MeetingFragment()).commit();
            bottomNavigationMenuView.setSelectedItemId(R.id.meeting);
        } else if(intent.getStringExtra("fragment").contains("event")){
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_menu, new EventFragment()).commit();
            bottomNavigationMenuView.setSelectedItemId(R.id.event);
        }



        bottomNavigationMenuView.setOnItemSelectedListener(item -> {
            item.setChecked(true);
                switch(item.getItemId()){
                    case R.id.fund:
                        callFundFragment();
                        break;
                    case R.id.pta:
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame_menu, new PTAFragment()).commit();
                        break;
                    case R.id.calendar:
                        callCalendarFragment();
                        break;
                    case R.id.meeting:
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame_menu, new MeetingFragment()).commit();
                        break;
                    case R.id.event:
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame_menu, new EventFragment()).commit();
                        break;
                }
            return false;
        });

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, SelectionActivity.class);
        startActivity(intent);
    }

    public void callCalendarFragment(){

        AsyncTask.execute(()->{

            Bundle bundle= new Bundle();
            List<Event> events = db.ClassRepDAO().getAllEvent(institute_id);
            List<Meeting> meetings = db.ClassRepDAO().getAllMeeting(institute_id);
            List<PTAmeeting> ptas = db.ClassRepDAO().getAllPTAmeeting(institute_id);

            String jsEvent = new Gson().toJson(events);
            bundle.putString("events", jsEvent);
            String jsMeet = new Gson().toJson(meetings);
            bundle.putString("meetings", jsMeet);
            String jsPta = new Gson().toJson(ptas);
            bundle.putString("ptas", jsPta);

            CalendarFragment calendarFragment = new CalendarFragment();
            calendarFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_menu, calendarFragment).commit();
        });
    }

    public void callFundFragment(){
        AsyncTask.execute(()->{

            Bundle bundle= new Bundle();
            Fund fund = db.ClassRepDAO().getFund(institute_id);
            List<FundChronology> fundChronologies = db.ClassRepDAO().getLastTwoChronology(fund.getId_fund());
            String jsFundChron = new Gson().toJson(fundChronologies);
            bundle.putString("funds", jsFundChron);
            String jsFund = new Gson().toJson(fund);
            bundle.putString("fund", jsFund);

            FundFragment fundFragment = new FundFragment();
            fundFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_menu, fundFragment).commit();
        });
    }
}