package com.example.classrep;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Canvas;
import android.os.Bundle;
import android.widget.ListView;

import com.example.classrep.adapter.IstitutiAdapter;
import com.example.classrep.database.Istitute;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private ListView listView;
    private List<String> lista;

    private List<Istitute> listIstitutes = new ArrayList<>();
    private IstitutiAdapter adapter;
    private RecyclerView recycle;
    //private AppDatabase db;

    private String image;
    private String istituto;
    private String classe;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        getSupportActionBar().hide();
        recycle = findViewById(R.id.istituti);

        //db = AppDatabase.getInstance(HomeActivity.this.getBaseContext());

        provaRecycler();
        recycle.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL) {
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                // Do not draw the divider
            }
        });
    }

    private void provaRecycler(){
        listIstitutes.add(new Istitute(1, "bruh1", "bruhone", ""));
        listIstitutes.add(new Istitute(2, "bruh2", "bruhone", ""));
        listIstitutes.add(new Istitute(3, "bruh3", "bruhone", ""));
        adapter = new IstitutiAdapter(getApplicationContext(), listIstitutes);
        recycle.setAdapter(adapter);
    }

}