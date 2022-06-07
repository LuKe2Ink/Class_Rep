package com.example.classrep;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.classrep.adapter.IstitutiAdapter;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private ListView listView;
    private List<String> lista;

    private List<Istituto> listaIstituto = new ArrayList<>();
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
    }

    private void provaRecycler(){
        listaIstituto.add(new Istituto(1, "bruh1", "bruhone", ""));
        listaIstituto.add(new Istituto(2, "bruh2", "bruhone", ""));
        listaIstituto.add(new Istituto(3, "bruh3", "bruhone", ""));
        adapter = new IstitutiAdapter(getApplicationContext(), listaIstituto);
        recycle.setAdapter(adapter);
    }

}