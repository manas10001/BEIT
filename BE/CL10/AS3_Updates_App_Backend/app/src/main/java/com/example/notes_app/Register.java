package com.example.notes_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Register extends AppCompatActivity {

    private TextView goto_login;
    private Button register;
    private EditText mail, pass, repass;
    private String password, repassword, email;
    DBManager db;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        sessionManager = new SessionManager(this);

        //if a session exists redirect user to home
        if (!sessionManager.getSesion().equals("noUser")) {
            Intent intent = new Intent(Register.this, Home.class);
            startActivity(intent);
        }

        goto_login = findViewById(R.id.delAcc);
        mail = findViewById(R.id.email);
        pass = findViewById(R.id.password);
        repass = findViewById(R.id.repassword);
        register = findViewById(R.id.register);

        //init db
        db = new DBManager(this);

        //switch to login
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

        //register a user
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    password = pass.getText().toString();
                    repassword = repass.getText().toString();
                    email = mail.getText().toString();

                    if(password.equals(repassword)){

                        if(db.insertUser(email,password)){
                            Toast.makeText(Register.this, "Account created!", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(Register.this, "Maybe the account already exists!", Toast.LENGTH_LONG).show();
                        }
                    }else{
                        Toast.makeText(Register.this, "Password and retyped password dont match", Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception ex){
                    Toast.makeText(Register.this, ex.toString(), Toast.LENGTH_LONG).show();
                }
            }
        });

    }
}