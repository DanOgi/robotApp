package com.example.robotappv6;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.Button;

public class KeyControlActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_key_controll);

//        buttonFF.setOnTouchListener((v, event) -> {
//            if(event.getAction() == MotionEvent.ACTION_DOWN) {
//                direction.setDirection(true, false, false, false);
//                direction.sendDirection(outputStream);
//                direction.updateTextDirection(directionTextView);
//                return true;
//            }
//            else if(event.getAction() == MotionEvent.ACTION_UP) {
//                direction.stopDirection();
//                direction.sendDirection(outputStream);
//                direction.updateTextDirection(directionTextView);
//                return true;
//            }
//            return false;
//        });
    }
}