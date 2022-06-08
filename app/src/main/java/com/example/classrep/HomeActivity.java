package com.example.classrep;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.classrep.adapter.InstituteAdapter;
import com.example.classrep.database.Institute;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements InstituteAdapter.onInstituteListener {

    private List<Institute> listInstitutes = new ArrayList<>();
    private InstituteAdapter adapter;
    private RecyclerView recycle;
    //private AppDatabase db;

    private FloatingActionButton add;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        getSupportActionBar().hide();
        recycle = findViewById(R.id.istituti);

        add = findViewById(R.id.addIstitute);

        //db = AppDatabase.getInstance(HomeActivity.this.getBaseContext());

        createRecycler();

        add.setOnClickListener(view -> {
            Intent intent = new Intent(this, AddInstituteActivity.class);
            startActivity(intent);
        });

    }

    private void createRecycler(){
        listInstitutes.add(new Institute(1, "bruh1", "bruhone", ""));
        //listInstitutes.add(new Institute(2, "bruh2", "bruhone", ""));
        //listInstitutes.add(new Institute(3, "bruh3", "bruhone", ""));

        if(listInstitutes.isEmpty()){
            TextView empty = findViewById(R.id.empty);
            if (empty.getVisibility() != View.VISIBLE) {
                empty.setVisibility(View.VISIBLE);
            } else {
                empty.setVisibility(View.INVISIBLE);
            }
        } else {
            adapter = new InstituteAdapter(getApplicationContext(), listInstitutes, this);
            recycle.setAdapter(adapter);
        }
    }

    @Override
    public void onInstituteClick(int position) {
        Institute item = listInstitutes.get(position);
        Toast.makeText(getBaseContext(), item.toString(), Toast.LENGTH_SHORT).show();
        /*Intent intent = new Intent(this, AddInstituteActivity.class);
        startActivity(intent);*/
    }
}