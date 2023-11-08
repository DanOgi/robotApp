package com.example.robotappv6;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    static final String MAIN_TAG = "MainTag";

    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    @SuppressWarnings("ClickableViewAccessibility")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(MAIN_TAG, "Stworzenie mainActivity");

        setContentView(R.layout.activity_main);

        Button buttonKeyControlActivity = findViewById(R.id.buttonKeyControl);
        Button buttonGyroControlActivity = findViewById(R.id.buttonGyroControl);

        buttonKeyControlActivity.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, KeyControlActivity.class)));

        buttonGyroControlActivity.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, GyroControlActivity.class)));

    }

}