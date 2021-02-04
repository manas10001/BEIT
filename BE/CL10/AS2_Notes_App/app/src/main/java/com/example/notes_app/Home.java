package com.example.notes_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class Home extends AppCompatActivity {

    private Button leave;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toast.makeText(Home.this,"Login Success", Toast.LENGTH_SHORT).show();

        leave = findViewById(R.id.leave);

        leave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = new Intent(Home.this, MainActivity.class);
                    startActivity(intent);
                }catch(Exception e){
                    Toast.makeText(Home.this,e.toString(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}