package com.dazone.crewchat.sqlite;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;


/**
 * Created by Dat on 4/12/2016.
 */
public class DataBaseDAO {

    protected SQLiteDatabase database;
    private DataBaseHelper dbHelper;
    private Context mContext;

    public DataBaseDAO(Context context) {
        this.mContext = context;
        dbHelper = DataBaseHelper.getInstance(mContext);
        open();

    }

    public void open() throws SQLException {
        if (dbHelper == null)
            dbHelper = DataBaseHelper.getInstance(mContext);
        database = dbHelper.getWritableDatabase();
    }
}
