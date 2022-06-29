package com.example.classrep.SingleActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;

import com.example.classrep.HomeActivity;
import com.example.classrep.R;
import com.example.classrep.adapter.PersonAdapter;
import com.example.classrep.database.ClassRepDB;
import com.example.classrep.database.entity.Adhesion;
import com.example.classrep.database.entity.Child;
import com.example.classrep.database.entity.Event;
import com.example.classrep.database.entity.PTAmeeting;
import com.example.classrep.database.entity.Parent;
import com.example.classrep.utilities.SingleToneClass;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;

public class EventActivity extends AppCompatActivity {
    
    private ClassRepDB db;
    private BlurView blurView;
    private Event event;
    private List<Parent> parents = new ArrayList<>();
    private List<Child> children = new ArrayList<>();
    private List<Adhesion> adhesion = new ArrayList<>();

    private TextInputEditText titolo;
    private TextInputEditText data;
    private TextInputEditText posto;
    private TextInputEditText note;

    private Switch childSwitch;
    private Switch adhesionSwitch;

    private Button addParent;
    private Button addChild;
    private Button addAdhesion;

    private RecyclerView parentRecycle;
    private PersonAdapter parentAdapter;
    private RecyclerView childRecycle;
    private PersonAdapter childAdapter;
    private RecyclerView adhesionRecycler;
    private PersonAdapter adhesionAdapter;

    private ConstraintLayout expandableChild;
    private ConstraintLayout expandableAdhesion;

    private MaterialToolbar topAppbar;

    private int idInstitute;
    private int idEvent;
    private boolean modify = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        db = ClassRepDB.getDatabase(getBaseContext());

        blurView = findViewById(R.id.blurViewModifyEvent);
        backgroundBlur();
        parentRecycle = findViewById(R.id.putParentModify);
        childRecycle = findViewById(R.id.putChildModify);
        adhesionRecycler = findViewById(R.id.putAdhesionModify);

        expandableChild = findViewById(R.id.expandableLayoutChildModify);
        expandableAdhesion = findViewById(R.id.expandableLayoutAdhesionModify);

        addParent = findViewById(R.id.addParentModify);
        addChild = findViewById(R.id.addChildModify);
        addAdhesion = findViewById(R.id.addAdhesionModify);

        childSwitch = findViewById(R.id.childSwitchModify);
        adhesionSwitch = findViewById(R.id.adhesionSwitchModify);

        topAppbar = findViewById(R.id.eventModifyTopAppBar);

        titolo = findViewById(R.id.titoloModify);
        data = findViewById(R.id.giornoModify);
        posto = findViewById(R.id.postoModify);
        note = findViewById(R.id.notaModify);

        Intent intent = this.getIntent();
        String jsParents;
        jsParents = intent.getStringExtra("parents");
        //System.out.println(jsParents);
        Type listTypeEvent = new TypeToken<List<Parent>>() {}.getType();
        parents = new Gson().fromJson(String.valueOf(jsParents), listTypeEvent);

        String jsChildren;
        jsChildren = intent.getStringExtra("children");
        Type listTypeEvent2 = new TypeToken<List<Child>>() {}.getType();
        children = new Gson().fromJson(String.valueOf(jsChildren), listTypeEvent2);
        if(!children.isEmpty()){
            childSwitch.setChecked(true);
            expandableChild.setVisibility(View.VISIBLE);
        }

        String jsAdhesion;
        jsAdhesion = intent.getStringExtra("adhesion");
        Type listTypeEvent3 = new TypeToken<List<Adhesion>>() {}.getType();
        adhesion = new Gson().fromJson(String.valueOf(jsAdhesion), listTypeEvent3);
        if(!adhesion.isEmpty()){
            adhesionSwitch.setChecked(true);
            expandableAdhesion.setVisibility(View.VISIBLE);
        }

        createRecycler();

        String jsEvent = intent.getStringExtra("event");
        Type listTypeEvent1 = new TypeToken<Event>() {}.getType();
        event = new Gson().fromJson(String.valueOf(jsEvent), listTypeEvent1);

        SingleToneClass singleToneClass = com.example.classrep.utilities.SingleToneClass.getInstance();
        idInstitute = singleToneClass.getData("institute");
        idEvent = singleToneClass.getData("event");
        modifyEnable(modify);
        setAll();
    }
    private void createRecycler() {
        System.out.println(children);
        parentAdapter = new PersonAdapter(getBaseContext(), "parent", true);
        parentAdapter.addParents(parents);
        parentAdapter.notifyDataSetChanged();
        this.runOnUiThread(()->parentRecycle.setAdapter(parentAdapter));

        childAdapter = new PersonAdapter(getBaseContext(), "child", true);
        childAdapter.addChildren(children);
        childAdapter.notifyDataSetChanged();
        this.runOnUiThread(()->childRecycle.setAdapter(childAdapter));

        adhesionAdapter = new PersonAdapter(getBaseContext(), "adhesion", true);
        adhesionAdapter.addAdhesion(adhesion);
        adhesionAdapter.notifyDataSetChanged();
        this.runOnUiThread(()->adhesionRecycler.setAdapter(adhesionAdapter));
    }

    private void setAll(){
        System.out.println(event.getTitle());
        titolo.setText(event.getTitle());
        String pattern = "dd/MM/yyyy HH:mm";
        SimpleDateFormat simpleDate = new SimpleDateFormat(pattern);
        String dateAll = simpleDate.format(event.getDate());
        data.setText(dateAll);
        posto.setText(event.getPlace());
        note.setText(event.getNote());
    }

    public void modifyEnable(boolean bool){
        titolo.setEnabled(bool);
        data.setEnabled(bool);
        posto.setEnabled(bool);
        note.setEnabled(bool);
        addParent.setVisibility(bool ? View.VISIBLE : View.GONE);
        parentAdapter.setSingleOrAdd(!bool);

        if(childSwitch.isChecked()){
            addChild.setVisibility(bool ? View.VISIBLE : View.GONE);
        } else {
            expandableChild.setVisibility(bool ? View.VISIBLE : View.GONE);
        }
        childAdapter.setSingleOrAdd(!bool);

        if(adhesionSwitch.isChecked()){
            addAdhesion.setVisibility(bool ? View.VISIBLE : View.GONE);
        } else {
            expandableAdhesion.setVisibility(bool ? View.VISIBLE : View.GONE);
        }
        adhesionAdapter.setSingleOrAdd(!bool);
    }

    public void backgroundBlur(){
        float radius = 5f;

        View decorView = getWindow().getDecorView();
        //ViewGroup you want to start blur from. Choose root as close to BlurView in hierarchy as possible.
        ViewGroup rootView = decorView.findViewById(android.R.id.content);
        //Set drawable to draw in the beginning of each blurred frame (Optional).
        //Can be used in case your layout has a lot of transparent space and your content
        //gets kinda lost after after blur is applied.
        Drawable windowBackground = decorView.getBackground();

        blurView.setupWith(rootView)
                .setFrameClearDrawable(windowBackground)
                .setBlurAlgorithm(new RenderScriptBlur(getBaseContext()))
                .setBlurRadius(radius)
                .setHasFixedTransformationMatrix(true);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra("fragment", "event");
        startActivity(intent);
    }
}