package com.example.classrep;

import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.AsyncTaskLoader;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.classrep.adapter.InstituteAdapter;
import com.example.classrep.database.ClassRepDB;
import com.example.classrep.database.Institute;
import com.example.classrep.utilities.SingleToneClass;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements InstituteAdapter.onInstituteListener {

    private Institute prova;
    private List<Institute> listInstitutes = new ArrayList<>();
    private InstituteAdapter adapter;
    private RecyclerView recycle;
    private ClassRepDB db;

    private FloatingActionButton add;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        listInstitutes.add(new Institute(1, "bruh1", "bruhone", ""));

        db = ClassRepDB.getDatabase(HomeActivity.this);

        getSupportActionBar().hide();
        recycle = findViewById(R.id.istituti);

        add = findViewById(R.id.addIstitute);

        AsyncTask.execute(()->{
            //db.ClassRepDAO().insertIstituti(new Institute(1, "bruh", "bruh1", ""));
            prova = db.ClassRepDAO().getInstitute(1);
        });

        createRecycler();

        add.setOnClickListener(view -> {
            /*Intent intent = new Intent(this, AddInstituteActivity.class);
            startActivity(intent);*/
            Toast.makeText(getBaseContext(), prova.getIstituto(), Toast.LENGTH_SHORT).show();
        });

    }

    private void createRecycler(){

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

        SingleToneClass singleToneClass = com.example.classrep.utilities.SingleToneClass.getInstance();
        singleToneClass.setData(item.getId());

        Intent intent = new Intent(this, AddInstituteActivity.class);
        startActivity(intent);
    }
}