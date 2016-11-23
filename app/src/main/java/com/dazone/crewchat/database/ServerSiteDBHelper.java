package com.dazone.crewchat.database;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;

import com.dazone.crewchat.utils.CrewChatApplication;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by maidinh on 8/17/2015.
 */
public class ServerSiteDBHelper {
    public static final String TABLE_NAME = "server_site_tbl";
    public static final String ID = "Id";
    public static final String SERVER_SITE_ID = "server_site_id";
    public static final String SERVER_SITE_LINK = "server_site_content";
    public static final String SQL_EXCUTE = "create table "
            + TABLE_NAME
            + " ("
            + ID + " integer primary key autoincrement, "
            + SERVER_SITE_ID + " integer, "
            + SERVER_SITE_LINK + " text"
            + ");";

    public static boolean addServerSite(String server_link) {
        try {
            if(!getAllSerVerSite().contains(server_link)) {
                ContentValues values = new ContentValues();
                values.put(SERVER_SITE_LINK, server_link);
                ContentResolver resolver = CrewChatApplication.getInstance()
                        .getApplicationContext().getContentResolver();
                resolver.insert(AppContentProvider.GET_SERVER_SITE_CONTENT_URI, values);
                return true;
            }


        } catch (Exception e) {
            // TODO: handle exception
        }
        return false;
    }
    public static void addServerSites(List<String> sites)
    {
        if(sites!=null)
        {
            for(String site: sites)
            {
                addServerSite(site);
            }
        }
    }
    public static ArrayList<String> getAllSerVerSite() {
        String[] columns = new String[] { ID, SERVER_SITE_ID, SERVER_SITE_LINK};
        ContentResolver resolver = CrewChatApplication.getInstance()
                .getApplicationContext().getContentResolver();
        ArrayList<String> arrayList = new ArrayList<String>();
        Cursor cursor = resolver.query(
                AppContentProvider.GET_SERVER_SITE_CONTENT_URI, columns, null,
                null, null);

        if (cursor != null) {
            if (cursor.getCount() > 0) {
                try {

                    while (!cursor.isLast()) {
                        cursor.moveToNext();


                        arrayList.add(cursor.getString(cursor.getColumnIndex(SERVER_SITE_LINK)));
                    }
                } finally {
                    cursor.close();
                }
            }
        }
        return arrayList;
    }
}
