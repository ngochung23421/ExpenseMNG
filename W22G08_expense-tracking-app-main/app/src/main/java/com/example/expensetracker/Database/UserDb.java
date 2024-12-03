package com.example.expensetracker.Database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.expensetracker.model.UserModel;

public class UserDb extends SQLiteOpenHelper {

    public static final String DB_NAME = "users_db";

    public static final int DB_VERSION = 1;

    public static final String TABLE_NAME = "users";
    //define col
    public static final String ID_COL = "id";
    public static final String USERNAME_COL = "username";
    public static final String PASSWORD_COL = "password";
    public static final String EMAIL_COL = "email";
    public static final String PHONE_COL = "phone";


    public UserDb(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String query = "CREATE TABLE " + TABLE_NAME + " ( "
                + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT,  "
                + USERNAME_COL + " VARCHAR(60) NOT NULL, "
                + PASSWORD_COL + " VARCHAR(200) NOT NULL, "
                + EMAIL_COL + " VARCHAR(60) NOT NULL, "
                + PHONE_COL + " VARCHAR(20)) ";

        //thuc thi tao bang du lieu
        sqLiteDatabase.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public long insertDataUser(String username, String password, String email, String phone){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(USERNAME_COL, username);
        values.put(PASSWORD_COL, password);
        values.put(EMAIL_COL, email);
        values.put(PHONE_COL, phone);
        long insert = sqLiteDatabase.insert(TABLE_NAME, null, values);
        sqLiteDatabase.close();
        return  insert;
    }

    @SuppressLint("Range")
    public UserModel getInfoUser(String username, String password){
        UserModel user = new UserModel();
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            String[] cols = {ID_COL,USERNAME_COL,EMAIL_COL,PHONE_COL};
            String condition = USERNAME_COL + " =? AND " + PASSWORD_COL + " =? ";
            //where username = ? and password = ?
            String[] params = {username, password};
            Cursor cursor = db.query(TABLE_NAME, cols, condition, params, null, null, null);
            //select id, username, email, phone from users where username = ? and passwoed = ?
            if (cursor.getCount() > 0){
                cursor.moveToFirst();
                // do du lieu tu db vao model
                user.setId(cursor.getInt(cursor.getColumnIndex(ID_COL)));
                user.setUsername(cursor.getString(cursor.getColumnIndex(USERNAME_COL)));
                user.setEmail(cursor.getString(cursor.getColumnIndex(EMAIL_COL)));
                user.setPhone(cursor.getString(cursor.getColumnIndex(PHONE_COL)));
            }
            cursor.close();
            db.close();
        } catch (Exception e){
            throw new RuntimeException(e);
        }
        return user;
    }
}

