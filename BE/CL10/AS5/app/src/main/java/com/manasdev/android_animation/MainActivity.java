 package com.manasdev.android_animation;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

 public class MainActivity extends AppCompatActivity {

    ImageView imageView;
    Animation rotate;
    SeekBar dsb;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        dsb = findViewById(R.id.dsb);

        //seekbar handling
        dsb.setMin(3000);
        dsb.setMax(5000);

        dsb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    int rt = 5500-progress;
                    rotate(rt);
                    //Toast.makeText(MainActivity.this, ""+rt,Toast.LENGTH_SHORT).show();
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