package com.manasdev.android_animation;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;

public class MainActivity extends AppCompatActivity {

    ImageView imageView;
    Animation rotate;
    Button speed_1x, speed_2x, speed_3x;
    SeekBar dsb;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
//        speed_1x = findViewById(R.id.rotate);
//        speed_2x = findViewById(R.id.rotate2);
//        speed_3x = findViewById(R.id.rotate3);
        dsb = findViewById(R.id.dsb);

//        speed_1x.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                rotate(3000);
//            }
//        });
//
//        speed_2x.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                rotate(2000);
//            }
//        });
//
//        speed_3x.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                rotate(1000);
//            }
//        });

        //seekbar handling
        dsb.setMin(100);
        dsb.setMax(5000);

        dsb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    rotate(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

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