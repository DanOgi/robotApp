package com.example.robotappv6;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Button;
import android.Manifest;

import com.google.android.material.slider.Slider;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class KeyControlActivity extends AppCompatActivity {

    Direction direction = new Direction();
    private final String KEY_CONTROL_TAG = "KeyControlTag";
    Button buttonFF;
    Button buttonFR;
    Button buttonFL;
    Button buttonRR;
    Button buttonLL;
    Button buttonBB;
    Button buttonBR;
    Button buttonBL;

    Slider slider;


    BluetoothManager bluetoothManager;

    BluetoothAdapter bluetoothAdapter;
    BluetoothDevice arduinoDevice = null;
    BluetoothSocket bluetoothSocket;

    Set<BluetoothDevice> pairedDevices;

    InputStream inputStream;
    OutputStream outputStream;

    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    @SuppressLint("ClickableViewAccessibility")
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        Log.d(KEY_CONTROL_TAG, "Stworzono aktywnosc KeyControlActivity");

        setContentView(R.layout.activity_key_controll);

        bluetoothManager = getSystemService(BluetoothManager.class);
        bluetoothAdapter = bluetoothManager.getAdapter();

        if(checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_DENIED) {
            requestPermissions(new String[] {Manifest.permission.BLUETOOTH_CONNECT}, 100);
        }

        if(!bluetoothAdapter.isEnabled()) {
            if(checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivity(enableBtIntent);
            }
        }

        pairedDevices = bluetoothAdapter.getBondedDevices();

        if(!pairedDevices.isEmpty()) {
            for(BluetoothDevice device : pairedDevices) {
                if(device.getName().equals("HC-05")) {
                    arduinoDevice = device;
                }
            }
        }

        try{
            bluetoothSocket = arduinoDevice.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
            bluetoothSocket.connect();
            Log.d(KEY_CONTROL_TAG, "Polaczono z bluetooth");
            inputStream = bluetoothSocket.getInputStream();
            outputStream = bluetoothSocket.getOutputStream();
        }
        catch (Exception e) {
            Log.e(KEY_CONTROL_TAG, "Nie polaczono z bluetooth");
        }

        buttonFF = findViewById(R.id.buttonFF);
        buttonFR = findViewById(R.id.buttonFR);
        buttonFL = findViewById(R.id.buttonFL);

        buttonRR = findViewById(R.id.buttonRR);
        buttonLL = findViewById(R.id.buttonLL);

        buttonBB = findViewById(R.id.buttonBB);
        buttonBR = findViewById(R.id.buttonBR);
        buttonBL = findViewById(R.id.buttonBL);

        slider = findViewById(R.id.sliderSpeed);

        slider.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                Log.d(KEY_CONTROL_TAG, "wartosc slidera" + value);

                direction.setSliderSpeed(value);

            }
        });

        buttonFF.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                Log.d(KEY_CONTROL_TAG, "Przycisk w dol FF");

                direction.setDirection(true,false,false,false);
                direction.setSpeedDirection();
                direction.sendDirection(outputStream);

                return true;
            }
            else if(event.getAction() == MotionEvent.ACTION_UP) {
                Log.d(KEY_CONTROL_TAG, "Przycisk w gore FF");

                direction.stopDirection();
                direction.sendDirection(outputStream);


                return true;
            }
            return false;
        });

        buttonFR.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                Log.d(KEY_CONTROL_TAG, "Przycisk w dol FR");

                direction.setDirection(true,false,true,false);
                direction.setSpeedDirection();
                direction.sendDirection(outputStream);

                return true;
            }
            else if(event.getAction() == MotionEvent.ACTION_UP) {
                Log.d(KEY_CONTROL_TAG, "Przycisk w gore FR");

                direction.stopDirection();
                direction.sendDirection(outputStream);

                return true;
            }
            return false;
        });

        buttonFL.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                Log.d(KEY_CONTROL_TAG, "Przycisk w dol FL");

                direction.setDirection(true,false,false,true);
                direction.setSpeedDirection();
                direction.sendDirection(outputStream);

                return true;
            }
            else if(event.getAction() == MotionEvent.ACTION_UP) {
                Log.d(KEY_CONTROL_TAG, "Przycisk w gore FL");

                direction.stopDirection();
                direction.sendDirection(outputStream);

                return true;
            }
            return false;
        });

        buttonRR.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                Log.d(KEY_CONTROL_TAG, "Przycisk w dol RR");

                direction.setDirection(false, false, true, false);
                direction.setSpeedDirection();
                direction.sendDirection(outputStream);

                return true;
            }
            else if(event.getAction() == MotionEvent.ACTION_UP) {
                Log.d(KEY_CONTROL_TAG, "Przycisk w gore RR");

                direction.stopDirection();
                direction.sendDirection(outputStream);

                return true;
            }
            return false;
        });

        buttonLL.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                Log.d(KEY_CONTROL_TAG, "Przycisk w dol LL");

                direction.setDirection(false, false, false, true);
                direction.setSpeedDirection();
                direction.sendDirection(outputStream);

                return true;
            }
            else if(event.getAction() == MotionEvent.ACTION_UP) {
                Log.d(KEY_CONTROL_TAG, "Przycisk w gore LL");

                direction.stopDirection();
                direction.sendDirection(outputStream);

                return true;
            }
            return false;
        });

        buttonBB.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                Log.d(KEY_CONTROL_TAG, "Przycisk w dol BB");

                direction.setDirection(false, true, false, false);
                direction.setSpeedDirection();
                direction.sendDirection(outputStream);

                return true;
            }
            else if(event.getAction() == MotionEvent.ACTION_UP) {
                Log.d(KEY_CONTROL_TAG, "Przycisk w gore BB");

                direction.stopDirection();
                direction.sendDirection(outputStream);

                return true;
            }
            return false;
        });

        buttonBR.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                Log.d(KEY_CONTROL_TAG, "Przycisk w dol BR");

                direction.setDirection(false,true,true,false);
                direction.setSpeedDirection();
                direction.sendDirection(outputStream);

                return true;
            }
            else if(event.getAction() == MotionEvent.ACTION_UP) {
                Log.d(KEY_CONTROL_TAG, "Przycisk w gore BR");

                direction.stopDirection();
                direction.sendDirection(outputStream);

                return true;
            }
            return false;
        });

        buttonBL.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                Log.d(KEY_CONTROL_TAG, "Przycisk w dol BL");

                direction.setDirection(false,true,false,true);
                direction.setSpeedDirection();
                direction.sendDirection(outputStream);

                return true;
            }
            else if(event.getAction() == MotionEvent.ACTION_UP) {
                Log.d(KEY_CONTROL_TAG, "Przycisk w gore BL");

                direction.stopDirection();
                direction.sendDirection(outputStream);

                return true;
            }
            return false;
        });

    }
}