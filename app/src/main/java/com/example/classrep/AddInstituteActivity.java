package com.example.classrep;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.example.classrep.utilities.SingleToneClass;

public class AddInstituteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_institute);

        SingleToneClass singleToneClass = com.example.classrep.utilities.SingleToneClass.getInstance();
;
    }
}