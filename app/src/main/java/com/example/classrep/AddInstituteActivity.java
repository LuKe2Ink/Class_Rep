package com.example.classrep;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.classrep.database.ClassRepDB;
import com.example.classrep.database.entity.Institute;
import com.example.classrep.utilities.SingleToneClass;

import java.util.Map;

public class AddInstituteActivity extends AppCompatActivity {

    private int institute_index;
    private ClassRepDB db;
    private SingleToneClass globalVariables;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_institute);
        db = ClassRepDB.getDatabase(AddInstituteActivity.this);
        globalVariables = com.example.classrep.utilities.SingleToneClass.getInstance();
        globalVariables.setData("institute", 1);

        Toast.makeText(getBaseContext(), String.valueOf(globalVariables.getData("institute")), Toast.LENGTH_SHORT).show();

        AsyncTask.execute(()->{
            institute_index = db.ClassRepDAO().getMaxIdInstitute();
            institute_index++;
            //db.ClassRepDAO().insertInstitute(new Institute(institute_index, "bruh1", "bruh2", ""));
        });
    }


    public void onBackPressed() {
        globalVariables.deleteData("institute");
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }
}