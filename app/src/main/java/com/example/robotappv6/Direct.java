package com.example.robotappv6;

import android.widget.TextView;

import androidx.annotation.NonNull;

import java.io.OutputStream;

public class Direct {
    private boolean forward = false;
    private boolean backward = false;
    private boolean right = false;
    private boolean left = false;
    private float speed = 0.0f;
    private float sliderSpeed = 0.0f;

    void setDirection(boolean forward, boolean backward, boolean right, boolean left) {
        this.forward = forward;
        this.backward = backward;
        this.left = left;
        this.right = right;
    }

    String getDirection() {
        String str;
        this.setSpeedDirection();
        int speed = (int)(255 * (this.speed / 100));
        if(this.forward && !this.backward && !this.right && !this.left) {
            str = "FF";
        }
        else if(!this.forward && this.backward && !this.right && !this.left) {
            str = "BB";
        }
        else if(!this.forward && !this.backward && this.right && !this.left) {
            str = "RR";
        }
        else if(!this.forward && !this.backward && !this.right && this.left) {
            str = "LL";
        }
        else if(this.forward && !this.backward && this.right && !this.left) {
            str = "FR";
        }
        else if(this.forward && !this.backward && !this.right && this.left) {
            str = "FL";
        }
        else if(!this.forward && this.backward && this.right && !this.left) {
            str = "BR";
        }
        else if(!this.forward && this.backward && !this.right && this.left) {
            str = "BL";
        }
        else {
            str = "SS";
        }
        return String.format(str + "%03d",speed);
    }

    void stopDirection() {
        this.forward = false;
        this.backward = false;
        this.left = false;
        this.right = false;
        this.speed = 0.0f;
    }

    void sendDirection(@NonNull OutputStream outputStream) {
        try{
            outputStream.write(getDirection().getBytes());
        }catch (Exception ignored) {

        }
    }

    void updateTextDirection(@NonNull TextView directionTextView) {
        directionTextView.setText(getDirection());
    }

    void setSpeedDirection() {
        this.speed = this.sliderSpeed;
    }
    void setSliderSpeed(float speed) {this.sliderSpeed = speed;}
}
