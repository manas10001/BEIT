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
    Button change, cancel;
    String password, email, repass;
    SessionManager sessionManager;
    DBManager db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        oldPassword = findViewById(R.id.oldPassword);
        newPassword = findViewById(R.id.password);
        reNewPassword = findViewById(R.id.rePassword);

        change = findViewById(R.id.delAcc);
        cancel = findViewById(R.id.cancel);

        sessionManager  = new SessionManager(this);
        db = new DBManager(this);

        //change pass
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    email = sessionManager.getSesion();

                    //verify old password
                    if(db.verifyLogin(email, oldPassword.getText().toString())){
                        repass = reNewPassword.getText().toString();
                        password = newPassword.getText().toString();

                        //match retyped pass
                        if(password.equals(repass)){
                            //change password
                            db.updatePassword(email, password);
                            Toast.makeText(Reset_password.this,"Password Updated!", Toast.LENGTH_SHORT).show();

                        }else{
                            Toast.makeText(Reset_password.this,"New and retyped new password don't match!", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(Reset_password.this,"Invalid old password!", Toast.LENGTH_SHORT).show();
                    }
                }catch(Exception e){
                    Toast.makeText(Reset_password.this,e.toString(), Toast.LENGTH_LONG).show();
                }
            }
        });

        //cancle operation go to home
        cancel.setOnClickListener(new View.OnClickListener() {
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