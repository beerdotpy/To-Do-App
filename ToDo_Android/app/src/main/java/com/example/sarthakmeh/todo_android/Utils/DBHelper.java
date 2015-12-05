package com.example.sarthakmeh.todo_android.Utils;

/**
 * Used SQLite Helper class for DB operations
 */
import java.util.HashMap;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "/ToDo.sqlite";
    private HashMap hp;
    public DBHelper(Context context)

    {
        super(context, Environment.getExternalStorageDirectory().getPath() + DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(
                "create table to_do " +
                        "(_id integer primary key autoincrement," +
                        "task text,time text,location text,status text)"
        );
        db.execSQL(
                "create table user " +
                        "(_id integer primary key autoincrement,name text," +
                        "email email,password password)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS to_do");
        onCreate(db);
    }

    public boolean insertUser(String name, String email, String password)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("email", email);
        contentValues.put("password", password);
        db.insert("user", null, contentValues);
        return true;
    }

    public boolean insertData(String task, String time, String location,String status)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("task", task);
        contentValues.put("time", time);
        contentValues.put("location", location);
        contentValues.put("status", status);
        db.insert("to_do", null, contentValues);
        return true;
    }

    public Cursor getData(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("select * from to_do ", null);
        return res;
    }

    public Boolean checkUser(String email,String pass){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor user =  db.rawQuery("select email from user where email= \"" + email +
                "\" AND password=\"" + pass+"\";", null);
        if (user.getCount() == 1){
            return true;
        }else {
            return false;
        }
    }
//
//    public int numberOfRows() {
//        SQLiteDatabase db = this.getReadableDatabase();
//        int numRows = (int) DatabaseUtils.queryNumEntries(db, "user_location");
//        return numRows;
//    }
}