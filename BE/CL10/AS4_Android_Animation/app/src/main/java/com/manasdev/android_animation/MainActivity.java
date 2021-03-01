package com.manasdev.android_animation;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    ImageView imageView;
    Animation rotate;
    Button speed_1x, speed_2x, speed_3x;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        speed_1x = findViewById(R.id.rotate);
        speed_2x = findViewById(R.id.rotate2);
        speed_3x = findViewById(R.id.rotate3);

        speed_1x.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rotate(3000);
            }
        });

        speed_2x.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rotate(2000);
            }
        });

        speed_3x.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rotate(1000);
            }
        });

    }

    public void rotate(int duration){
        rotate = new RotateAnimation(0,360,RotateAnimation.RELATIVE_TO_SELF,.5f,RotateAnimation.RELATIVE_TO_SELF,.5f);
        rotate.setDuration(duration);
        rotate.setRepeatCount(Animation.INFINITE);
        rotate.setInterpolator(new LinearInterpolator());
        imageView.startAnimation(rotate);
    }
}