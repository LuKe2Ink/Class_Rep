package com.example.classrep;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.classrep.database.entity.Settings;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private static final int DELAY_TO_HOME = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new Handler().postDelayed(() -> {
            Intent homeActivity = new Intent(MainActivity.this, SelectionActivity.class);
            startActivity(homeActivity);
            finish();
        }, DELAY_TO_HOME);
    }
}