package com.example.classrep.adder;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import com.example.classrep.HomeActivity;
import com.example.classrep.R;
import com.example.classrep.SelectionActivity;
import com.example.classrep.database.ClassRepDB;
import com.example.classrep.database.entity.Adhesion;
import com.example.classrep.database.entity.Child;
import com.example.classrep.database.entity.Event;
import com.example.classrep.database.entity.Meeting;
import com.example.classrep.database.entity.PTAmeeting;
import com.example.classrep.database.entity.Parent;
import com.example.classrep.utilities.SingleToneClass;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.chip.Chip;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddMeetingActivity extends AppCompatActivity {

    private ClassRepDB db;
    private String tipo = "scolastico";

    private TextInputEditText title;
    private TextInputEditText date;
    private TextInputEditText place;
    private TextInputEditText note;

    private Chip chipScolastico;
    private Chip chipExtaScolastico;
    int item;

    int hours;
    int minutes;


    private MaterialToolbar topAppbar;



    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_meeting);

        SingleToneClass singleToneClass = com.example.classrep.utilities.SingleToneClass.getInstance();
        item = singleToneClass.getData("institute");

        db = ClassRepDB.getDatabase(AddMeetingActivity.this);

        title = findViewById(R.id.titoloMeeting);
        place = findViewById(R.id.postoMeeting);
        note = findViewById(R.id.notaMeeting);
        date = findViewById(R.id.giornoMeeting);

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
            DatePickerDialog datePickerDialog = new DatePickerDialog(this, onDateSetListener,
                    Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH),
                    Calendar.getInstance().get(Calendar.DAY_OF_MONTH));

            datePickerDialog.show();
        });

        chipScolastico = findViewById(R.id.chip1);
        chipExtaScolastico = findViewById(R.id.chip2);

        chipScolastico.setOnClickListener(view->{
            tipo = "scolastico";
        });
        chipExtaScolastico.setOnClickListener(view->{
            tipo = "extra-scolastico";
        });

        topAppbar = findViewById(R.id.meetingTopAppBar);

        topAppbar.setOnMenuItemClickListener(menuItem -> {
            switch (menuItem.getItemId()){
                case R.id.cancelAll:
                    title.setText("");
                    date.setText("");
                    place.setText("");
                    note.setText("");
                    chipExtaScolastico.setChecked(false);
                    chipScolastico.setChecked(true);
                    break;
                case R.id.confirm:
                    if(title.getText().toString().isEmpty() ||
                            date.getText().toString().isEmpty() ||
                            place.getText().toString().isEmpty() ||
                            note.getText().toString().isEmpty()){
                        new androidx.appcompat.app.AlertDialog.Builder(AddMeetingActivity.this)
                                .setTitle("Ci sono alcuni elementi mancanti")
                                //se clicca ok
                                .setPositiveButton(android.R.string.yes, null)
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
        if(instatNow.isAfter(instatTaken)){
            new androidx.appcompat.app.AlertDialog.Builder(AddMeetingActivity.this)
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

    private void addEvent(){
        System.out.println("entra");

        AsyncTask.execute(()->{
            int maxid = db.ClassRepDAO().getMaxIdMeeting() + 1;
            Meeting meeting;
            try {
                Date data = new SimpleDateFormat("dd/MM/yyyy").parse(date.getText().toString());
                System.out.println(data);
                meeting = new Meeting(maxid, item, title.getText().toString(), tipo, data
                        , place.getText().toString(),note.getText().toString());
                db.ClassRepDAO().insertMeeting(meeting);
            } catch (ParseException e) {
                System.out.println(e);
            }
            callIntent();
        });
    }

    @Override
    public void onBackPressed() {
        new androidx.appcompat.app.AlertDialog.Builder(AddMeetingActivity.this)
                .setTitle("Stai tornando indietro")
                .setMessage("Sei sicuro di non voler più inserire una riunione?")
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
        intent.putExtra("fragment", "meeting");
        startActivity(intent);
    }
}