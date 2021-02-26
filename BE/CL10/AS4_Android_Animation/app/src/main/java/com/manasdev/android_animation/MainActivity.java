package com.manasdev.android_animation;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    ImageView imageView;
    Animation rotate;
    Button startAnim, btn2, btn3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        startAnim = findViewById(R.id.rotate);
        btn2 = findViewById(R.id.rotate2);
        btn3 = findViewById(R.id.rotate3);

        startAnim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rotate(3000);
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rotate(2000);
            }
        });

        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rotate(1000);
            }
        });

    }

    public void rotate(int duration){
        rotate = AnimationUtils.loadAnimation(this, R.anim.rotate_anim);
        rotate.setDuration(duration);
        imageView.startAnimation(rotate);
    }
}