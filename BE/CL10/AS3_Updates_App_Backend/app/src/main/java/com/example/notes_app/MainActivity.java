package com.example.notes_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private TextView goto_register;
    private Button loginbtn;
    private EditText email;
    private EditText passwd;
    private String mail;
    private String pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //get resources
        goto_register = findViewById(R.id.register);
        loginbtn = findViewById(R.id.change);
        email = findViewById(R.id.email);
        passwd = findViewById(R.id.password);

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

        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    mail = email.getText().toString();
                    pass = passwd.getText().toString();

//                    if(mail.equals("a@g.co") && pass.equals("123")){
                    if(true){
                        Intent intent = new Intent(MainActivity.this, Home.class);
                        startActivity(intent);
                    }else{
                        Toast.makeText(MainActivity.this,"Invalid Credentials!", Toast.LENGTH_LONG).show();
                    }
                }catch(Exception e){
                    Toast.makeText(MainActivity.this,e.toString(), Toast.LENGTH_LONG).show();
                }
            }
        });



    }
}