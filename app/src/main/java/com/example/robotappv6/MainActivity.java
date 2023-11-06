package com.example.robotappv6;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.slider.Slider;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    BluetoothManager bluetoothManager;
    BluetoothAdapter bluetoothAdapter;
    Set<BluetoothDevice> pairedDevices;
    BluetoothDevice arduinoDevice = null;
    BluetoothSocket bluetoothSocket;

    InputStream inputStream;
    OutputStream outputStream;

    TextView textView;
    TextView directionTextView;

    Direct direction = new Direct();
    Button buttonFF;
    Button buttonFL;
    Button buttonFR;
    Button buttonLL;
    Button buttonRR;
    Button buttonBB;
    Button buttonBR;
    Button buttonBL;

    Slider directionSpeedSlider;

    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    @SuppressWarnings("ClickableViewAccessibility")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.textField);
        directionTextView = findViewById(R.id.directionTextView);

        buttonFF = findViewById(R.id.buttonFF);
        buttonFL = findViewById(R.id.buttonFL);
        buttonFR = findViewById(R.id.buttonFR);
        buttonRR = findViewById(R.id.buttonRR);
        buttonLL = findViewById(R.id.buttonLL);
        buttonBB = findViewById(R.id.buttonBB);
        buttonBL = findViewById(R.id.buttonBL);
        buttonBR = findViewById(R.id.buttonBR);

        directionSpeedSlider = findViewById(R.id.directionSpeedSlider);

        bluetoothManager = getSystemService(BluetoothManager.class);
        bluetoothAdapter = bluetoothManager.getAdapter();

        if(checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_DENIED) {
            requestPermissions(new String[] {Manifest.permission.BLUETOOTH_CONNECT} , 100);
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

        try {
            bluetoothSocket = arduinoDevice.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
            bluetoothSocket.connect();
            inputStream = bluetoothSocket.getInputStream();
            outputStream = bluetoothSocket.getOutputStream();
            textView.setText("Polaczono do arduino");
        }
        catch (Exception e) {
            textView.setText("Nie poloczono do arduino");
        }

        directionTextView.setText("SS000");

        directionSpeedSlider.addOnChangeListener((slider, value, fromUser) -> direction.setSliderSpeed(value));

        buttonFF.setOnTouchListener((v, event) -> {
            if(event.getAction() == MotionEvent.ACTION_DOWN) {
                direction.setDirection(true, false, false, false);
                direction.sendDirection(outputStream);
                direction.updateTextDirection(directionTextView);
                return true;
            }
            else if(event.getAction() == MotionEvent.ACTION_UP) {
                direction.stopDirection();
                direction.sendDirection(outputStream);
                direction.updateTextDirection(directionTextView);
                return true;
            }
            return false;
        });

        buttonBB.setOnTouchListener((v, event) -> {
            if(event.getAction() == MotionEvent.ACTION_DOWN) {
                direction.setDirection(false, true, false, false);
                direction.sendDirection(outputStream);
                direction.updateTextDirection(directionTextView);
                return true;
            }
            else if(event.getAction() == MotionEvent.ACTION_UP) {
                direction.stopDirection();
                direction.sendDirection(outputStream);
                direction.updateTextDirection(directionTextView);
                return true;
            }
            return false;
        });

        buttonFL.setOnTouchListener((v, event) -> {
            if(event.getAction() == MotionEvent.ACTION_DOWN) {
                direction.setDirection(true, false, false, true);
                direction.sendDirection(outputStream);
                direction.updateTextDirection(directionTextView);
                return true;
            }
            else if(event.getAction() == MotionEvent.ACTION_UP) {
                direction.stopDirection();
                direction.sendDirection(outputStream);
                direction.updateTextDirection(directionTextView);
                return true;
            }
            return false;
        });

        buttonFR.setOnTouchListener((v, event) -> {
            if(event.getAction() == MotionEvent.ACTION_DOWN) {
                direction.setDirection(true, false, true, false);
                direction.sendDirection(outputStream);
                direction.updateTextDirection(directionTextView);
                return true;
            }
            else if(event.getAction() == MotionEvent.ACTION_UP) {
                direction.stopDirection();
                direction.sendDirection(outputStream);
                direction.updateTextDirection(directionTextView);
                return true;
            }
            return false;
        });

        buttonBL.setOnTouchListener((v, event) -> {
            if(event.getAction() == MotionEvent.ACTION_DOWN) {
                direction.setDirection(false, true, false, true);
                direction.sendDirection(outputStream);
                direction.updateTextDirection(directionTextView);
                return true;
            }
            else if(event.getAction() == MotionEvent.ACTION_UP) {
                direction.stopDirection();
                direction.sendDirection(outputStream);
                direction.updateTextDirection(directionTextView);
                return true;
            }
            return false;
        });

        buttonBR.setOnTouchListener((v, event) -> {
            if(event.getAction() == MotionEvent.ACTION_DOWN) {
                direction.setDirection(false, true, true, false);
                direction.sendDirection(outputStream);
                direction.updateTextDirection(directionTextView);
                return true;
            }
            else if(event.getAction() == MotionEvent.ACTION_UP) {
                direction.stopDirection();
                direction.sendDirection(outputStream);
                direction.updateTextDirection(directionTextView);
                return true;
            }
            return false;
        });

        buttonRR.setOnTouchListener((v, event) -> {
            if(event.getAction() == MotionEvent.ACTION_DOWN) {
                direction.setDirection(false, false, true, false);
                direction.sendDirection(outputStream);
                direction.updateTextDirection(directionTextView);
                return true;
            }
            else if(event.getAction() == MotionEvent.ACTION_UP) {
                direction.stopDirection();
                direction.sendDirection(outputStream);
                direction.updateTextDirection(directionTextView);
                return true;
            }
            return false;
        });

        buttonLL.setOnTouchListener((v, event) -> {
            if(event.getAction() == MotionEvent.ACTION_DOWN) {
                direction.setDirection(false, false, false, true);
                direction.sendDirection(outputStream);
                direction.updateTextDirection(directionTextView);
                return true;
            }
            else if(event.getAction() == MotionEvent.ACTION_UP) {
                direction.stopDirection();
                direction.sendDirection(outputStream);
                direction.updateTextDirection(directionTextView);
                return true;
            }
            return false;
        });

    }

}