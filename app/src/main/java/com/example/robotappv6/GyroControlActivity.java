package com.example.robotappv6;

import static android.util.Half.EPSILON;
import static java.lang.Math.sqrt;
import static java.security.AccessController.getContext;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class GyroControlActivity extends AppCompatActivity {

    Direction direction = new Direction();

    DisplayMetrics displayMetrics;

    ImageView imageViewCircle;
    TextView textViewVoltage;
    private final String GYRO_CONTROL_TAG = "GyroControlTag";

    BluetoothDevice arduinoDevice = null;
    BluetoothSocket bluetoothSocket;

    InputStream inputStream;
    OutputStream outputStream;

    RxThread rxThread;
    String inputData = "";

    SensorManager sensorManager;
    Sensor rotationSensor;
    SensorEventListener rvListener;

    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        Log.d(GYRO_CONTROL_TAG, "Inicjalizowanie sensora");

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        Log.d(GYRO_CONTROL_TAG, "Zarejestrowano sensor");

        BluetoothManager bluetoothManager = getSystemService(BluetoothManager.class);
        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();

        if (checkSelfPermission(android.Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_DENIED) {
            requestPermissions(new String[]{android.Manifest.permission.BLUETOOTH_CONNECT}, 100);
        }

        if (!bluetoothAdapter.isEnabled()) {
            if (checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivity(enableBtIntent);
            }
        }

        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

        if (!pairedDevices.isEmpty()) {
            for (BluetoothDevice device : pairedDevices) {
                if (device.getName().equals("HC-05")) {
                    Log.d(GYRO_CONTROL_TAG, "Znaleziono urzadzenie HC-05");
                    arduinoDevice = device;
                }
            }
        }

        try {
            bluetoothSocket = arduinoDevice.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
            bluetoothSocket.connect();
            Log.d(GYRO_CONTROL_TAG, "Polaczono z bluetooth");
            inputStream = bluetoothSocket.getInputStream();
            outputStream = bluetoothSocket.getOutputStream();
            rxThread.start();
        } catch (Exception e) {
            Log.e(GYRO_CONTROL_TAG, "Nie polaczono z bluetooth");
        }

        Log.d(GYRO_CONTROL_TAG, "Utworzenie aktywności GyroControlActivity");

        setContentView(R.layout.activity_gyro_control);

        imageViewCircle = findViewById(R.id.imageViewCircle);
        textViewVoltage = findViewById(R.id.textViewVoltageGyro);
        displayMetrics = getResources().getDisplayMetrics();

        Log.d(GYRO_CONTROL_TAG, "tag1");

        final float scale = displayMetrics.density;

        Log.d(GYRO_CONTROL_TAG, "tag2");

        float pixels = (175 * scale + 0.5f);
        rvListener = new SensorEventListener() {
            int i = 5;

            @Override
            public void onSensorChanged(SensorEvent event) {
                float[] rotationMatrix = new float[16];
                SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values);

                float[] remappedRotationMatrix = new float[16];
                SensorManager.remapCoordinateSystem(rotationMatrix,
                        SensorManager.AXIS_X,
                        SensorManager.AXIS_Y,
                        remappedRotationMatrix);

                float[] orientations = new float[3];
                SensorManager.getOrientation(remappedRotationMatrix, orientations);

                for (int i = 1; i < 3; i++) {
                    orientations[i] = (float) (Math.toDegrees(orientations[i]));
                }

                float rotY = orientations[1]; //XROT
                float rotX = orientations[2]; //YROT

                float tranX;
                float tranY;

                if (rotX > 45) {
                    tranX = 45.0f * (pixels / 45.0f);
                } else if (rotX < -45) {
                    tranX = -45.0f * (pixels / 45.0f);
                } else {
                    tranX = rotX * (pixels / 45.0f);
                }

                if (rotY > 45) {
                    tranY = -45.0f * (pixels / 45.0f);
                } else if (rotY < -45) {
                    tranY = 45.0f * (pixels / 45.0f);
                } else {
                    tranY = -rotY * (pixels / 45.0f);
                }

                imageViewCircle.setTranslationX(tranX);
                imageViewCircle.setTranslationY(tranY);

                if (tranX > 100) {
                    direction.setRight(true);
                } else {
                    direction.setRight(false);
                }

                if (tranX < -100) {
                    direction.setLeft(true);
                } else {
                    direction.setLeft(false);
                }

                if (tranY < -100) {
                    direction.setForward(true);
                } else {
                    direction.setForward(false);
                }

                if (tranY > 100) {
                    direction.setBackward(true);
                } else {
                    direction.setBackward(false);
                }

                double speed = Math.sqrt(tranX * tranX + tranY * tranY) / ((45.0f * (pixels / 45.0f)));
                if (speed > 1) speed = 1;
                Log.d(GYRO_CONTROL_TAG, "Speed: " + speed);

                if (i == 5) {
                    direction.setSliderSpeed((float) speed);
                    direction.sendDirection(outputStream);
                    i = 0;
                } else {
                    i++;
                }

            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
    }

//    int maxX = 400;
//    int maxY = 400;
//    int minX = -400;
//    int minY = -400;
//
//    float timestamp;
//    double NS2S = 1.0 / 1_000_000_000;
//    float EPISOLON = 0.25f;
//    int i = 0;
//    @Override
//    public void onSensorChanged(SensorEvent event) {
//
//        if(timestamp != 0) {
//            float dt = (float) ((event.timestamp - timestamp)*NS2S);
//            float rotationY = event.values[0];
//            float rotationX = event.values[1];
//
//            //Log.d(GYRO_CONTROL_TAG, "" + dt);
//
//            if (rotationY > EPISOLON || rotationX > EPISOLON || rotationY < -EPISOLON || rotationX < -EPISOLON) {
//                float x = imageViewCircle.getTranslationX();
//                float y = imageViewCircle.getTranslationY();
//
//                if ((x < maxX && rotationX > 0) || (x > minX && rotationX < 0)) {
//                    x += rotationX/dt*15;
//                }
//
//                if ((y < maxY && rotationY > 0) || (y > minY && rotationY < 0)) {
//                    y += rotationY/dt*15;
//                }
//                imageViewCircle.setTranslationY(y);
//                imageViewCircle.setTranslationX(x);
//            }
//        }
//        timestamp = event.timestamp;
//
//        //Log.d(GYRO_CONTROL_TAG, "x: " + imageViewCircle.getTranslationX());
//        //Log.d(GYRO_CONTROL_TAG, "y: " + imageViewCircle.getTranslationY());
//
//        float speed = 0;
//
//        if( i == 3) {
//            if(imageViewCircle.getTranslationY() < -100) {
//                speed = -(imageViewCircle.getTranslationY() + 100)/100;
//                if(speed > 1) speed = 1;
//                Log.d(GYRO_CONTROL_TAG, "speed: " + speed);
//
//                direction.setSliderSpeed(speed);
//                direction.setDirection(true, false, false, false);
//                direction.sendDirection(outputStream);
//            }
//            else if(imageViewCircle.getTranslationY() > 100) {
//                speed = (imageViewCircle.getTranslationY() - 100)/100;
//                if(speed > 1) speed = 1;
//
//                direction.setSliderSpeed(speed);
//                direction.setDirection(false, true, false, false);
//                direction.sendDirection(outputStream);
//            }
//            else if(imageViewCircle.getTranslationX() < -100) {
//                speed = -(imageViewCircle.getTranslationX() + 100)/100;
//                if(speed > 1) speed = 1;
//
//                direction.setSliderSpeed(speed);
//                direction.setDirection(false, false, true, false);
//                direction.sendDirection(outputStream);
//            }
//            else if(imageViewCircle.getTranslationX() > 100) {
//                speed = (imageViewCircle.getTranslationX() - 100)/100;
//                if(speed > 1) speed = 1;
//
//                direction.setSliderSpeed(speed);
//                direction.setDirection(false, false, false, true);
//                direction.sendDirection(outputStream);
//            }
//            else {
//                speed = 0;
//                direction.setSliderSpeed(0);
//                direction.stopDirection();
//                direction.sendDirection(outputStream);
//            }
//            i = 0;
//        }
//        else {
//            i++;
//        }
//    }

    //    @Override
//    public void onAccuracyChanged(Sensor sensor, int accuracy) {
//
//    }
    class RxThread extends Thread {
        public boolean isRunning;
        byte[] data;

        RxThread() {
            isRunning = true;
            data = new byte[128];
        }

        public void run() {
            while (isRunning) {
                try {
                    if (inputStream.available() > 2) {
                        inputStream.read(data);
                        inputData = new String(data);
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!inputData.equals("")) {
                                textViewVoltage.setText("Napięcie na ogniwach: " + inputData);
                            }
                        }
                    });
                    Thread.sleep(100);
                } catch (Exception e) {

                }
            }
        }
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                    rxThread.isRunning = false;
                    break;
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(rvListener, rotationSensor, 2*1000*1000);
    }

    @Override
    protected  void onPause() {
        super.onPause();
        sensorManager.unregisterListener(rvListener);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            bluetoothSocket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}