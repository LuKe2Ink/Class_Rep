package com.example.classrep.fragment;

import android.app.ActionBar;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toolbar;

import com.example.classrep.R;
import com.example.classrep.SelectionActivity;
import com.example.classrep.SingleActivity.FundChronologyActivity;
import com.example.classrep.database.ClassRepDB;
import com.example.classrep.database.entity.Fund;
import com.example.classrep.database.entity.FundChronology;
import com.example.classrep.utilities.SingleToneClass;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

import java.sql.Date;
import java.util.Calendar;

public class FundFragment extends Fragment {

    private ClassRepDB db;

    DrawerLayout drawerLayout;
    NavigationView drawer;
    MaterialToolbar toolbar;
    Button add;
    Button minus;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fund, container, false);

        db = ClassRepDB.getDatabase(this.getContext());

        drawerLayout = view.findViewById(R.id.drawerLayoutFund);
        drawer = view.findViewById(R.id.drawerFund);
        toolbar = view.findViewById(R.id.fundAppBar);

        add = view.findViewById(R.id.addMoney);
        minus = view.findViewById(R.id.minusMoney);

        add.setOnClickListener(view1->{
            AsyncTask.execute(()->{
                //db.ClassRepDAO().insertFund(new Fund(1, 1, 0.00));
                //db.ClassRepDAO().insertFundChronology(new FundChronology(1, 1, 40.00, "minus", Date.from(Calendar.getInstance().toInstant()),"Prelievo per festa fine anno"));
                db.ClassRepDAO().insertFundChronology(new FundChronology(2, 1, 40.00, "add", Date.from(Calendar.getInstance().toInstant()),"Raccolta soldi"));
            });
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this.getActivity(), drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        toolbar.setOnMenuItemClickListener(menuItem->{

            SingleToneClass singleToneClass = com.example.classrep.utilities.SingleToneClass.getInstance();
            singleToneClass.setData("fund", 1);

            Intent intent = new Intent(this.getContext(), FundChronologyActivity.class);
            startActivity(intent);
            return false;
        });

        return view;
    }
}