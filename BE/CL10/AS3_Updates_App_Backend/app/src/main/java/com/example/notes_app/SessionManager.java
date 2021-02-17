package com.example.notes_app;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager  {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String SHARED_PREF_NAME = "session";
    String SESSION_ID = "email";

    public SessionManager(Context context){
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    //create session
    public void createSession(String email){
        editor.putString(SESSION_ID, email).commit();
    }

    //returns email or returns noUser
    public String getSesion(){
        return sharedPreferences.getString(SESSION_ID, "noUser");
    }

    //removes a session
    public void removeSession(){
        editor.putString(SESSION_ID, "noUser").commit();
    }

}
