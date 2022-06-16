package com.example.classrep;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.example.classrep.fragment.CalendarFragment;
import com.example.classrep.fragment.EventFragment;
import com.example.classrep.fragment.FundFragment;
import com.example.classrep.fragment.MeetingFragment;
import com.example.classrep.fragment.PTAFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationMenuView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.frame_menu, new CalendarFragment());
        fragmentTransaction.commit();

        bottomNavigationMenuView = findViewById(R.id.bottom_navigation);
        bottomNavigationMenuView.setSelectedItemId(R.id.calendar);
        bottomNavigationMenuView.setOnItemSelectedListener(item -> {
            item.setChecked(true);
            switch(item.getItemId()){
                case R.id.fund:
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_menu, new FundFragment()).commit();
                    break;
                case R.id.pta:
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_menu, new PTAFragment()).commit();
                    break;
                case R.id.calendar:
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_menu, new CalendarFragment()).commit();
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
}