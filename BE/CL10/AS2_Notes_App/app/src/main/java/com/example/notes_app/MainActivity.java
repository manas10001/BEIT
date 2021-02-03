package com.example.notes_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private TextView goto_register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        goto_register = findViewById(R.id.register);

        goto_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = new Intent(MainActivity.this, Register.class);
                    startActivity(intent);
                }catch(Exception e){
                    Toast.makeText(MainActivity.this,e.toString(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}