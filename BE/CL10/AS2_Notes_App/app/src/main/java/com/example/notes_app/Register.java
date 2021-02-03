package com.example.notes_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class Register extends AppCompatActivity {

    private TextView goto_login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        goto_login = findViewById(R.id.login);

        goto_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = new Intent(Register.this, MainActivity.class);
                    startActivity(intent);
                }catch(Exception e){
                    Toast.makeText(Register.this,e.toString(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}