package com.example.classrep.adder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Person;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;

import com.example.classrep.R;
import com.example.classrep.adapter.EventAdapter;
import com.example.classrep.adapter.PersonAdapter;

import java.util.ArrayList;
import java.util.List;

public class AddEventActivity extends AppCompatActivity {

    private PersonAdapter parentAdapter;
    private PersonAdapter childAdapter;
    private PersonAdapter adhesionAdapter;
    private List<Integer> parents = new ArrayList<>();
    private List<Integer> children = new ArrayList<>();
    private List<Integer> adhesion = new ArrayList<>();

    private int parentsCount = 1;
    private int childrenCount = 1;


    private RecyclerView recycleParent;

    private RecyclerView recycleChild;
    private ConstraintLayout constraintLayoutChild;

    private RecyclerView recycleAdhesion;
    private ConstraintLayout constraintLayoutAdhesion;

    private Button addParent;
    private Button addChild;
    private Button addAdhesion;

    private Switch childSwitch;
    private Switch adhesionSwitch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        ConstraintLayout layout = findViewById(R.id.expandableLayoutChild);
        layout.setVisibility(View.GONE);
        recycleParent = findViewById(R.id.putParent);
        recycleChild = findViewById(R.id.putChild);
        recycleAdhesion = findViewById(R.id.putAdhesion);

        constraintLayoutChild = findViewById(R.id.expandableLayoutChild);
        constraintLayoutAdhesion = findViewById(R.id.expandableLayoutAdhesion);

        addParent = findViewById(R.id.addParent);
        addChild = findViewById(R.id.addChild);
        addAdhesion = findViewById(R.id.addAdhesion);

        childSwitch = findViewById(R.id.childSwitch);
        adhesionSwitch = findViewById(R.id.adhesionSwitch);

        addParent.setOnClickListener( view ->{
            System.out.println(parents);
            parents.add(parentsCount);
            parentsCount++;
            parentAdapter.notifyDataSetChanged();
        });

        addChild.setOnClickListener( view ->{
            children.add(childrenCount);
            childrenCount++;
            childAdapter.notifyDataSetChanged();
        });

        addAdhesion.setOnClickListener( view ->{
            adhesion.add(0);
            adhesionAdapter.notifyDataSetChanged();
            addAdhesion.setVisibility(View.GONE);
        });

        childSwitch.setOnCheckedChangeListener((view, check)->{
            if(check){
                constraintLayoutChild.setVisibility(View.VISIBLE);
            } else {
                constraintLayoutChild.setVisibility(View.GONE);
            }
        });

        adhesionSwitch.setOnCheckedChangeListener((view, check)->{
            if(check){
                constraintLayoutAdhesion.setVisibility(View.VISIBLE);
            } else {
                constraintLayoutAdhesion.setVisibility(View.GONE);
            }
        });

        createRecycler();

    }

    private void createRecycler() {
        parentAdapter = new PersonAdapter(this, parents, "parent");
        recycleParent.setAdapter(parentAdapter);

        childAdapter = new PersonAdapter(this, children, "child");
        recycleChild.setAdapter(childAdapter);

        adhesionAdapter = new PersonAdapter(this, adhesion, "adhesion");
        recycleAdhesion.setAdapter((adhesionAdapter));
    }
}