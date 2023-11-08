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

import java.util.Set;
import java.util.UUID;

public class KeyControlActivity extends AppCompatActivity {

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
    Set<BluetoothDevice> pairedDevices;
    BluetoothDevice arduinoDevice = null;
    BluetoothSocket bluetoothSocket;

    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    @SuppressLint("ClickableViewAccessibility")
    protected void onCreate(Bundle savedInstanceState) {

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
            Log.d(KEY_CONTROL_TAG, "Polaczono z bluetooth");
        }
        catch (Exception e) {
            Log.e(KEY_CONTROL_TAG, "Nie polaczono z bluetooth");
        }

        super.onCreate(savedInstanceState);

        Log.d(KEY_CONTROL_TAG, "Stworzono aktywnosc KeyControlActivity");

        setContentView(R.layout.activity_key_controll);

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
            }
        });

        buttonFF.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                Log.d(KEY_CONTROL_TAG, "Przycisk w dol FF");
                return true;
            }
            else if(event.getAction() == MotionEvent.ACTION_UP) {
                Log.d(KEY_CONTROL_TAG, "Przycisk w gore FF");
                return true;
            }
            return false;
        });

        buttonFR.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                Log.d(KEY_CONTROL_TAG, "Przycisk w dol FR");
                return true;
            }
            else if(event.getAction() == MotionEvent.ACTION_UP) {
                Log.d(KEY_CONTROL_TAG, "Przycisk w gore FR");
                return true;
            }
            return false;
        });

        buttonFL.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                Log.d(KEY_CONTROL_TAG, "Przycisk w dol FL");
                return true;
            }
            else if(event.getAction() == MotionEvent.ACTION_UP) {
                Log.d(KEY_CONTROL_TAG, "Przycisk w gore FL");
                return true;
            }
            return false;
        });

        buttonRR.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                Log.d(KEY_CONTROL_TAG, "Przycisk w dol RR");
                return true;
            }
            else if(event.getAction() == MotionEvent.ACTION_UP) {
                Log.d(KEY_CONTROL_TAG, "Przycisk w gore RR");
                return true;
            }
            return false;
        });

        buttonLL.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                Log.d(KEY_CONTROL_TAG, "Przycisk w dol LL");
                return true;
            }
            else if(event.getAction() == MotionEvent.ACTION_UP) {
                Log.d(KEY_CONTROL_TAG, "Przycisk w gore LL");
                return true;
            }
            return false;
        });

        buttonBB.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                Log.d(KEY_CONTROL_TAG, "Przycisk w dol BB");
                return true;
            }
            else if(event.getAction() == MotionEvent.ACTION_UP) {
                Log.d(KEY_CONTROL_TAG, "Przycisk w gore BB");
                return true;
            }
            return false;
        });

        buttonBR.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                Log.d(KEY_CONTROL_TAG, "Przycisk w dol BR");
                return true;
            }
            else if(event.getAction() == MotionEvent.ACTION_UP) {
                Log.d(KEY_CONTROL_TAG, "Przycisk w gore BR");
                return true;
            }
            return false;
        });

        buttonBL.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                Log.d(KEY_CONTROL_TAG, "Przycisk w dol BL");
                return true;
            }
            else if(event.getAction() == MotionEvent.ACTION_UP) {
                Log.d(KEY_CONTROL_TAG, "Przycisk w gore BL");
                return true;
            }
            return false;
        });

    }
}