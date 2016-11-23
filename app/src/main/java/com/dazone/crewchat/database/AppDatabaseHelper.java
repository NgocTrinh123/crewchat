package com.dazone.crewchat.database;

/**
 * Created by maidinh on 8/13/2015.
 */

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.dazone.crewchat.constant.Statics;
import com.dazone.crewchat.utils.Utils;

public class AppDatabaseHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = Statics.DATABASE_NAME;
    public static final int DB_VERSION = Statics.DATABASE_VERSION;
    public static final String[] TABLE_NAMES = new String[]{
            UserDBHelper.TABLE_NAME,
            ServerSiteDBHelper.TABLE_NAME,
            AllUserDBHelper.TABLE_NAME,
            ChatMessageDBHelper.TABLE_NAME,
            ChatRomDBHelper.TABLE_NAME,
            DepartmentDBHelper.TABLE_NAME,
            FavoriteUserDBHelper.TABLE_NAME,
            FavoriteGroupDBHelper.TABLE_NAME,
            BelongsToDBHelper.TABLE_NAME
    };
    public static final String[] EXECUTE = new String[]{
            UserDBHelper.SQL_EXCUTE,
            ServerSiteDBHelper.SQL_EXCUTE,
            AllUserDBHelper.SQL_EXCUTE,
            ChatMessageDBHelper.SQL_EXCUTE,
            ChatRomDBHelper.SQL_EXCUTE,
            DepartmentDBHelper.SQL_EXCUTE,
            FavoriteUserDBHelper.SQL_EXCUTE,
            FavoriteGroupDBHelper.SQL_EXCUTE,
            BelongsToDBHelper.SQL_EXCUTE
    };

    public AppDatabaseHelper(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
        // TODO Auto-generated constructor stub
    }

    public AppDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.beginTransaction();
        try {
            execMultipleSQL(db, EXECUTE);
            db.setTransactionSuccessful();
        } catch (android.database.SQLException e) {
            throw e;
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        Utils.printLogs("Update db version");
        Utils.printLogs("newVersion : " + newVersion);
        try {
            dropMultipleSQL(db, TABLE_NAMES);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        this.onCreate(db);
    }

    private void execMultipleSQL(SQLiteDatabase db, String[] sql) throws android.database.SQLException {
        for (String s : sql) {
            if (s.trim().length() > 0) {
                try {
                    db.execSQL(s);
                } catch (android.database.SQLException e) {
                    throw new android.database.SQLException();
                }
            }
        }
    }

    private void execSQL(SQLiteDatabase db, String s) throws android.database.SQLException {
        if (s.trim().length() > 0) {
            try {
                db.execSQL(s);
            } catch (android.database.SQLException e) {
                throw new android.database.SQLException();
            }
        }
    }

    private void dropMultipleSQL(SQLiteDatabase db, String[] tablename) throws android.database.SQLException {
        for (String s : tablename) {
            if (s.trim().length() > 0) {
                try {
                    db.execSQL("DROP TABLE IF EXISTS " + s + ";");
                } catch (android.database.SQLException e) {
                    throw new android.database.SQLException();
                }
            }
        }
    }
}
