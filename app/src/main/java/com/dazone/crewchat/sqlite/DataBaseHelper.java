package com.dazone.crewchat.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseHelper extends SQLiteOpenHelper {

    private static DataBaseHelper sInstance;
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Projects.db";

    // TABLES
    public static final String TABLE_DEPARTMENT = "table_department";

    // TABLE_DEPARTMENT Columns
    public static final String KEY_DEPARTMENT_ID = "department_id";
    public static final String KEY_DEPARTMENT_NAME = "department_name";
    public static final String KEY_DEPARTMENT_USER_NO = "department_user_no";
    public static final String KEY_DEPARTMENT_IS_HIDE = "department_is_hide";


    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_TABLE_USER = "CREATE TABLE " + TABLE_DEPARTMENT +
                "(" +
                KEY_DEPARTMENT_ID + " TEXT ," +
                KEY_DEPARTMENT_NAME + " TEXT ," +
                KEY_DEPARTMENT_USER_NO + " TEXT ," +
                KEY_DEPARTMENT_IS_HIDE + " TEXT ," +
                "PRIMARY KEY (" + KEY_DEPARTMENT_ID + "," + KEY_DEPARTMENT_USER_NO + ")" +
                ")";


        db.execSQL(CREATE_TABLE_USER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_DEPARTMENT);
            onCreate(db);
        }
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    public static synchronized DataBaseHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DataBaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }


}
