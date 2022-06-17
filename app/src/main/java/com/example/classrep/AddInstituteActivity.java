package com.example.classrep;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.widget.EditText;

import com.example.classrep.database.ClassRepDB;
import com.example.classrep.database.entity.Institute;
import com.example.classrep.utilities.SingleToneClass;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Calendar;
import java.util.Date;

public class AddInstituteActivity extends AppCompatActivity {

    private int institute_index;
    private ClassRepDB db;

    private FloatingActionButton add;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_institute);

        add = findViewById(R.id.insertInstitute);

        db = ClassRepDB.getDatabase(AddInstituteActivity.this);
        //Toast.makeText(getBaseContext(), String.valueOf(globalVariables.getData("institute")), Toast.LENGTH_SHORT).show();

        AsyncTask.execute(()->{
            institute_index = db.ClassRepDAO().getMaxIdInstitute();
            institute_index++;
            //db.ClassRepDAO().insertInstitute(new Institute(institute_index, "bruh1", "bruh2", ""));
        });

        add.setOnClickListener( view ->{
            EditText editInstituteName = findViewById(R.id.editInstituteName);
            EditText editInstituteGrade = findViewById(R.id.editInstituteGrade);
            String instituteName = editInstituteName.getText().toString();
            String instituteGrade = editInstituteGrade.getText().toString();

            AsyncTask.execute(()->{
                db.ClassRepDAO().insertInstitute(new Institute(institute_index, instituteName, instituteGrade, "", Date.from(Calendar.getInstance().toInstant())));
                Intent intent = new Intent(getBaseContext(), SelectionActivity.class);
                startActivity(intent);
            });
        });

    }


    public void onBackPressed() {

        new AlertDialog.Builder(AddInstituteActivity.this)
            .setTitle("Stai tornando indietro")
            .setMessage("Sei sicuro di non voler più inserire una classe?")

                //se clicca si
            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(getBaseContext(), SelectionActivity.class);
                    startActivity(intent);
                }
            })
                //se clicca no
            .setNegativeButton(android.R.string.no,null)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .create()
            .show();
        //alert box
    }
}