package com.example.notes_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Delete_Account extends AppCompatActivity {

    Button deleteAcc, cancel;
    EditText pass, repass;
    String password, email, repassword;
    SessionManager sessionManager;
    DBManager db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete__account);

        pass = findViewById(R.id.password);
        repass = findViewById(R.id.rePassword);
        deleteAcc = findViewById(R.id.delAcc);
        cancel = findViewById(R.id.cancel);

        db = new DBManager(this);
        sessionManager = new SessionManager(this);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    Intent intent = new Intent(Delete_Account.this, Home.class);
                    startActivity(intent);
                }catch (Exception e){
                    Toast.makeText(Delete_Account.this, e.toString(), Toast.LENGTH_LONG).show();
                }
            }
        });

        deleteAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    password = pass.getText().toString();
                    repassword = repass.getText().toString();
                    //validate passwords
                    if(password.equals(repassword)){
                        email = sessionManager.getSesion();
                        if(db.verifyLogin(email, password)){
                            db.deleteUser(email);
                            Toast.makeText(Delete_Account.this,"Account deleted", Toast.LENGTH_LONG).show();
                            sessionManager.removeSession();
                            Intent intent = new Intent(Delete_Account.this, MainActivity.class);
                            startActivity(intent);
                        }else{
                            Toast.makeText(Delete_Account.this,"Invalid Password!", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(Delete_Account.this,"Password and retyped password don't match!", Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    Toast.makeText(Delete_Account.this,e.toString(), Toast.LENGTH_LONG).show();
                }
            }
        });

    }
}