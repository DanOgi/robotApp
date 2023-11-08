package com.example.robotappv6;

import static android.util.Half.EPSILON;
import static java.lang.Math.sqrt;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

public class GyroControlActivity extends AppCompatActivity implements SensorEventListener {

    ImageView imageViewCircle;
    private final String GYRO_CONTROL_TAG = "GyroControlTag";
    private SensorManager sensorManager;
    private Sensor gyroscope;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(GYRO_CONTROL_TAG,"Utworzenie aktywności GyroControlActivity");

        setContentView(R.layout.activity_gyro_control);

        imageViewCircle = findViewById(R.id.imageViewCircle);

        Log.d(GYRO_CONTROL_TAG, "Inicjalizowanie sensora");
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);
        Log.d(GYRO_CONTROL_TAG, "Zarejestrowano sensor");

    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        float xPosition = imageViewCircle.getTranslationX();
        float yPosition = imageViewCircle.getTranslationY();

        xPosition += event.values[1]*70;
        yPosition += event.values[0]*140;

        Log.d(GYRO_CONTROL_TAG, "TimeStamp: " + event.timestamp);

        imageViewCircle.setTranslationX(xPosition);
        imageViewCircle.setTranslationY(yPosition);

        //Log.d(GYRO_CONTROL_TAG, "Kolo x: " + imageViewCircle.getTranslationX());
        //Log.d(GYRO_CONTROL_TAG, "kolo y: " + imageViewCircle.getTranslationY());
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}