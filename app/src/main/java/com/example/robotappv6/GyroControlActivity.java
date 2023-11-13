package com.example.robotappv6;

import static android.util.Half.EPSILON;
import static java.lang.Math.sqrt;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class GyroControlActivity extends AppCompatActivity implements SensorEventListener {

    Direction direction = new Direction();

    ImageView imageViewCircle;
    private final String GYRO_CONTROL_TAG = "GyroControlTag";

    BluetoothDevice arduinoDevice = null;
    BluetoothSocket bluetoothSocket;

    InputStream inputStream;
    OutputStream outputStream;

    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        BluetoothManager bluetoothManager = getSystemService(BluetoothManager.class);
        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();

        if(checkSelfPermission(android.Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_DENIED) {
            requestPermissions(new String[] {android.Manifest.permission.BLUETOOTH_CONNECT}, 100);
        }

        if(!bluetoothAdapter.isEnabled()) {
            if(checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivity(enableBtIntent);
            }
        }

        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

        if(!pairedDevices.isEmpty()) {
            for(BluetoothDevice device : pairedDevices) {
                if(device.getName().equals("HC-05")) {
                    Log.d(GYRO_CONTROL_TAG, "Znaleziono urzadzenie HC-05");
                    arduinoDevice = device;
                }
            }
        }

        try{
            bluetoothSocket = arduinoDevice.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
            bluetoothSocket.connect();
            Log.d(GYRO_CONTROL_TAG, "Polaczono z bluetooth");
            inputStream = bluetoothSocket.getInputStream();
            outputStream = bluetoothSocket.getOutputStream();
        }
        catch (Exception e) {
            Log.e(GYRO_CONTROL_TAG, "Nie polaczono z bluetooth");
        }

        Log.d(GYRO_CONTROL_TAG,"Utworzenie aktywności GyroControlActivity");

        setContentView(R.layout.activity_gyro_control);

        imageViewCircle = findViewById(R.id.imageViewCircle);

        Log.d(GYRO_CONTROL_TAG, "Inicjalizowanie sensora");

        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);

        Log.d(GYRO_CONTROL_TAG, "Zarejestrowano sensor");

    }
    int maxX = 400;
    int maxY = 400;
    int minX = -400;
    int minY = -400;

    float timestamp;
    double NS2S = 1.0 / 1_000_000_000;
    float EPISOLON = 0.25f;
    int i = 0;
    @Override
    public void onSensorChanged(SensorEvent event) {

        if(timestamp != 0) {
            float dt = (float) ((event.timestamp - timestamp)*NS2S);
            float rotationY = event.values[0];
            float rotationX = event.values[1];

            //Log.d(GYRO_CONTROL_TAG, "" + dt);

            if (rotationY > EPISOLON || rotationX > EPISOLON || rotationY < -EPISOLON || rotationX < -EPISOLON) {
                float x = imageViewCircle.getTranslationX();
                float y = imageViewCircle.getTranslationY();

                if ((x < maxX && rotationX > 0) || (x > minX && rotationX < 0)) {
                    x += rotationX/dt*15;
                }

                if ((y < maxY && rotationY > 0) || (y > minY && rotationY < 0)) {
                    y += rotationY/dt*15;
                }
                imageViewCircle.setTranslationY(y);
                imageViewCircle.setTranslationX(x);
            }
        }
        timestamp = event.timestamp;

        //Log.d(GYRO_CONTROL_TAG, "x: " + imageViewCircle.getTranslationX());
        //Log.d(GYRO_CONTROL_TAG, "y: " + imageViewCircle.getTranslationY());

        float speed = 0;

        if( i == 3) {
            if(imageViewCircle.getTranslationY() < -100) {
                speed = -(imageViewCircle.getTranslationY() + 100)/100;
                if(speed > 1) speed = 1;
                Log.d(GYRO_CONTROL_TAG, "speed: " + speed);

                direction.setSliderSpeed(speed);
                direction.setDirection(true, false, false, false);
                direction.sendDirection(outputStream);
            }
            else if(imageViewCircle.getTranslationY() > 100) {
                speed = (imageViewCircle.getTranslationY() - 100)/100;
                if(speed > 1) speed = 1;

                direction.setSliderSpeed(speed);
                direction.setDirection(false, true, false, false);
                direction.sendDirection(outputStream);
            }
            else if(imageViewCircle.getTranslationX() < -100) {
                speed = -(imageViewCircle.getTranslationX() + 100)/100;
                if(speed > 1) speed = 1;

                direction.setSliderSpeed(speed);
                direction.setDirection(false, false, true, false);
                direction.sendDirection(outputStream);
            }
            else if(imageViewCircle.getTranslationX() > 100) {
                speed = (imageViewCircle.getTranslationX() - 100)/100;
                if(speed > 1) speed = 1;

                direction.setSliderSpeed(speed);
                direction.setDirection(false, false, false, true);
                direction.sendDirection(outputStream);
            }
            else {
                speed = 0;
                direction.setSliderSpeed(0);
                direction.stopDirection();
                direction.sendDirection(outputStream);
            }
            i = 0;
        }
        else {
            i++;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}