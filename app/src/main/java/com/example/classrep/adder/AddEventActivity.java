package com.example.classrep.adder;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Person;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.classrep.HomeActivity;
import com.example.classrep.R;
import com.example.classrep.SelectionActivity;
import com.example.classrep.adapter.EventAdapter;
import com.example.classrep.adapter.PersonAdapter;
import com.example.classrep.database.ClassRepDB;
import com.example.classrep.database.entity.Adhesion;
import com.example.classrep.database.entity.Child;
import com.example.classrep.database.entity.Event;
import com.example.classrep.database.entity.Meeting;
import com.example.classrep.database.entity.PTAmeeting;
import com.example.classrep.database.entity.Parent;
import com.example.classrep.utilities.SingleToneClass;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddEventActivity extends AppCompatActivity {

    private PersonAdapter parentAdapter;
    private PersonAdapter childAdapter;
    private PersonAdapter adhesionAdapter;
    private List<Integer> parents = new ArrayList<>();
    private List<Integer> children = new ArrayList<>();
    private List<Integer> adhesion = new ArrayList<>();

    private int parentsCount = 1;
    private int childrenCount = 1;
    private boolean isThereChild = false;
    private boolean isThereAdhesion = false;

    private ClassRepDB db;

    private RecyclerView recycleParent;

    private RecyclerView recycleChild;
    private ConstraintLayout constraintLayoutChild;

    private RecyclerView recycleAdhesion;
    private ConstraintLayout constraintLayoutAdhesion;

    private TextInputEditText title;
    private TextInputEditText date;
    private TextInputEditText place;
    private TextInputEditText note;

    private Button addParent;
    private Button addChild;
    private Button addAdhesion;

    private Switch childSwitch;
    private Switch adhesionSwitch;

    private MaterialToolbar topAppbar;

    int item;

    int hours;
    int minutes;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        db = ClassRepDB.getDatabase(AddEventActivity.this);

        SingleToneClass singleToneClass = com.example.classrep.utilities.SingleToneClass.getInstance();
        item = singleToneClass.getData("institute");

        Toast.makeText(this, String.valueOf(item), Toast.LENGTH_SHORT).show();

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

        topAppbar = findViewById(R.id.eventTopAppBar);

        title = findViewById(R.id.titolo);
        date = findViewById(R.id.giorno);
        place = findViewById(R.id.posto);
        note = findViewById(R.id.nota);

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

            isThereChild = check;
        });

        adhesionSwitch.setOnCheckedChangeListener((view, check)->{
            if(check){
                constraintLayoutAdhesion.setVisibility(View.VISIBLE);
            } else {
                constraintLayoutAdhesion.setVisibility(View.GONE);
            }

            isThereAdhesion = check;
        });

        date.setOnClickListener(view ->{
            DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                    date.setText(dayOfMonth+"/"+(month+1)+"/"+year);
                    TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int Selectedhour, int Selectedminutes) {
                            int hours = Selectedhour;
                            int minutes = Selectedminutes;
                            date.setText(date.getText().toString()+" "+String.format(Locale.ITALY, "%02d:%02d", hours, minutes));
                        }
                    };

                    int style = AlertDialog.THEME_HOLO_DARK;

                    TimePickerDialog timePickerDialog = new TimePickerDialog(date.getContext(), style, onTimeSetListener, hours, minutes, true);

                    timePickerDialog.setTitle("Scegli l'orario");
                    timePickerDialog.show();
                }
            };

            int style = AlertDialog.THEME_HOLO_DARK;

            DatePickerDialog datePickerDialog = new DatePickerDialog(this, onDateSetListener,
                    Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH),
                        Calendar.getInstance().get(Calendar.DAY_OF_MONTH));

            datePickerDialog.show();
        });


        topAppbar.setOnMenuItemClickListener(menuItem -> {
            switch (menuItem.getItemId()){
                case R.id.cancelAll:
                    parents.clear();
                    parentAdapter.notifyDataSetChanged();
                    children.clear();
                    childAdapter.notifyDataSetChanged();
                    adhesion.clear();
                    adhesionAdapter.notifyDataSetChanged();

                    childSwitch.setChecked(false);
                    adhesionSwitch.setChecked(false);

                    title.setText("");
                    date.setText("");
                    place.setText("");
                    note.setText("");
                    break;
                case R.id.confirm:
                    if(title.getText().toString().isEmpty() ||
                            date.getText().toString().isEmpty() ||
                                place.getText().toString().isEmpty() ||
                                    note.getText().toString().isEmpty()){
                        new androidx.appcompat.app.AlertDialog.Builder(AddEventActivity.this)
                                .setTitle("Ci sono alcuni elementi mancanti")
                                //se clicca ok
                                .setPositiveButton(android.R.string.yes, null)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .create()
                                .show();
                    } else if(parents.isEmpty()) {
                        new androidx.appcompat.app.AlertDialog.Builder(AddEventActivity.this)
                                .setTitle("Non hai inserito nessun genitore")
                                .setMessage("Vuoi comunque aggiungere l'evento?")
                                //se clicca ok
                                .setPositiveButton("Si", (dialog, which)->{
                                    verifyDate();
                                })
                                .setNegativeButton("No", null)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .create()
                                .show();
                    } else {
                        verifyDate();
                    }
                    break;
            }
            return false;
        });

        createRecycler();
    }

    private void createRecycler() {
        parentAdapter = new PersonAdapter(this, "parent", false);
        parentAdapter.addPeople(parents);
        recycleParent.setAdapter(parentAdapter);

        childAdapter = new PersonAdapter(this, "child", false);
        childAdapter.addPeople(children);
        recycleChild.setAdapter(childAdapter);

        adhesionAdapter = new PersonAdapter(this, "adhesion", false);
        adhesionAdapter.addPeople(adhesion);
        recycleAdhesion.setAdapter((adhesionAdapter));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void verifyDate(){
        Instant instatNow = Calendar.getInstance().toInstant();
        Instant instatTaken = null;
        try {
            instatTaken = new SimpleDateFormat("dd/MM/yyyy").parse(date.getText().toString()).toInstant();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        System.out.println(instatNow);
        System.out.println(instatTaken);
        if(instatNow.isAfter(instatTaken)){
            new androidx.appcompat.app.AlertDialog.Builder(AddEventActivity.this)
                    .setTitle("La data inserita è prima di quella odierna")
                    .setMessage("Vuoi comunque aggiungere l'evento?")
                    //se clicca ok
                    .setPositiveButton("Si", (dialog, which)->{
                        addEvent();
                    })
                    .setNegativeButton("No", null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .create()
                    .show();
        } else {
            addEvent();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void addEvent(){

        AsyncTask.execute(()->{
            int maxid = db.ClassRepDAO().getMaxIdEvent() + 1;
            Event event;
            try {
                    event = new Event(maxid, item, title.getText().toString()
                        , isThereChild, isThereAdhesion,
                        new SimpleDateFormat("dd/MM/yyyy").parse(date.getText().toString())
                        , note.getText().toString(), place.getText().toString());
                db.ClassRepDAO().insertEvent(event);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if(!parents.isEmpty()){
                for (int i=0; i<parents.size(); i++){
                    View view = recycleParent.getChildAt(i);
                    EditText nome = view.findViewById(R.id.nome);
                    EditText cognome = view.findViewById(R.id.cognome);
                    EditText orario = view.findViewById(R.id.time);
                    int maxidParent = db.ClassRepDAO().getMaxIdParent()+1;
                    String ora = orario.getText().toString();
                    try {
                        if(ora.isEmpty()){
                            db.ClassRepDAO().insertParent(new Parent(maxidParent, maxid, 0,
                                    nome.getText().toString(), cognome.getText().toString(),
                                    new SimpleDateFormat("HH:mm").parse("00:00")));
                        } else {
                            db.ClassRepDAO().insertParent(new Parent(maxidParent, maxid, 0,
                                    nome.getText().toString(), cognome.getText().toString(),
                                    new SimpleDateFormat("HH:mm").parse(orario.getText().toString())));
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
            if(isThereChild){
                for (int i=0; i<children.size(); i++){
                    View view = recycleParent.getChildAt(i);
                    EditText nome = view.findViewById(R.id.nome);
                    EditText cognome = view.findViewById(R.id.cognome);
                    int maxidChild = db.ClassRepDAO().getMaxIdChild()+1;
                    db.ClassRepDAO().insertChild(new Child(maxidChild, maxid,
                            nome.getText().toString(), cognome.getText().toString()));
                }
            }
            if(isThereAdhesion){
                View view = recycleParent.getChildAt(0);
                EditText cognome = view.findViewById(R.id.cognome);
                db.ClassRepDAO().insertAdhesion(new Adhesion(maxid,
                        Double.parseDouble(cognome.getText().toString())));
            }


            callIntent();
        });
    }

    public void onBackPressed() {
        new androidx.appcompat.app.AlertDialog.Builder(AddEventActivity.this)
            .setTitle("Stai tornando indietro")
            .setMessage("Sei sicuro di non voler più inserire un evento?")
            //se clicca si
            .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                callIntent();
            })
            //se clicca no
            .setNegativeButton(android.R.string.no,null)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .create()
            .show();


    }

    public void callIntent(){
        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra("fragment", "event");
        startActivity(intent);
    }
}