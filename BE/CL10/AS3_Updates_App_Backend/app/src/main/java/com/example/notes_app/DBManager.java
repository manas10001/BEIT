package com.example.notes_app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBManager extends SQLiteOpenHelper {

    public static final String TABLE = "UserDetails";
    public static final String DB_Name = "UserData.db";

    //create db
    public DBManager(@Nullable Context context) {
        super(context, DB_Name, null, 1);

        SQLiteDatabase db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table "+TABLE+"(email TEXT PRIMARY KEY, password TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists UserDetails");
//        onCreate(db);
    }

    //verify login
    public boolean verifyLogin(String email, String password){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from UserDetails where email = ? and password  = ?",new String[]{email, password});
        if(cursor.getCount() > 0)
            return true;
        return false;
    }

    //insert user
    public boolean insertUser(String email,String password){
        //if user already exists
        if(verifyLogin(email, password)){
            return false;
        }
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("email",email);
        contentValues.put("password", password);
        long res = db.insert(TABLE, null, contentValues);

        if(res == -1)
            return false;
        return true;
    }

    //update password
    public boolean updatePassword(String email, String password){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("email",email);
        contentValues.put("password", password);

        Cursor cursor = db.rawQuery("select * from UserDetails where email=?",new String[] {email});
        if(cursor.getCount() > 0) {
            long res = db.update(TABLE, contentValues, "email=?", new String[]{email});

            if (res == -1)
                return false;
            return true;
        }else
            return false;
    }

    //delete user
    public boolean deleteUser(String email){
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery("select * from UserDetails where email = ?",new String[] {email});
        if(cursor.getCount() > 0) {
            long res = db.delete(TABLE, "email=?", new String[]{email});

            if (res == -1)
                return false;
            return true;
        }else
            return false;
    }

}