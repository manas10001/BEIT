package com.example.notes_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Reset_password extends AppCompatActivity {
    EditText oldPassword, newPassword, reNewPassword;
    Button change, cancle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        oldPassword = findViewById(R.id.oldPassword);
        newPassword = findViewById(R.id.newPassword);
        reNewPassword = findViewById(R.id.reNewPassword);

        change = findViewById(R.id.change);
        cancle = findViewById(R.id.cancle);

        //change pass
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Toast.makeText(Reset_password.this,"Password Updated!", Toast.LENGTH_LONG).show();
                }catch(Exception e){
                    Toast.makeText(Reset_password.this,e.toString(), Toast.LENGTH_LONG).show();
                }
            }
        });

        //cancle operation go to home
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Toast.makeText(Reset_password.this,"Operation Abort", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Reset_password.this, Home.class);
                    startActivity(intent);
                }catch(Exception e){
                    Toast.makeText(Reset_password.this,e.toString(), Toast.LENGTH_LONG).show();
                }
            }
        });


    }
}