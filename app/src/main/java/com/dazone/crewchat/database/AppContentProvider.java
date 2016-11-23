package com.dazone.crewchat.database;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import com.dazone.crewchat.BuildConfig;

/**
 * Created by maidinh on 8/13/2015.
 */
public class AppContentProvider extends ContentProvider {

    /* database helper */
    AppDatabaseHelper mDatabaseHelper;

    /* key for uri matches */
    private static final int GET_USER_KEY = 1;
    private static final int GET_ALL_KEY = 10;
    private static final int GET_SERVER_SITE_KEY = 5;
    private static final int GET_MESSAGE_KEY = 99;
    private static final int GET_CHAT_ROOM_KEY = 100;
    private static final int GET_DEPART_KEY = 101;
    private static final int GET_FAVORITE_USER_KEY = 102;
    private static final int GET_FAVORITE_GROUP_KEY = 103;
    private static final int GET_BELONG_TO_KEY = 104;
    /* authority */
    private static final String AUTHORITY = BuildConfig.APPLICATION_ID+".provider";

    /* Uri Matches */
    private static UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    /* path */
    private static final String GET_USER_PATH = "request_user";
    private static final String GET_SERVER_SITE_PATH = "request_server_site";
    private static final String GET_ALL_PATH = "request_all";
    private static final String GET_MESSAGE_PATH = "request_message";
    private static final String GET_CHAT_ROOM_PATH = "request_chat_room";
    private static final String GET_DEPART_PATH = "request_chat_depart";
    private static final String GET_FAVORITE_USER_PATH = "request_favorite_user";
    private static final String GET_FAVORITE_GROUP_PATH = "request_favorite_group";
    private static final String GET_BELONG_TO_PATH = "request_belong_to";
    /* content uri */
    public static final Uri GET_USER_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + GET_USER_PATH);
    public static final Uri GET_SERVER_SITE_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + GET_SERVER_SITE_PATH);
    public static final Uri GET_ALL_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + GET_ALL_PATH);
    public static final Uri GET_MESSAGE_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + GET_MESSAGE_PATH);
    public static final Uri GET_CHAT_ROOM_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + GET_CHAT_ROOM_PATH);
    public static final Uri GET_DEPARTMENT_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + GET_DEPART_PATH);
    public static final Uri GET_FAVORITE_USER_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + GET_FAVORITE_USER_PATH);
    public static final Uri GET_FAVORITE_GROUP_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + GET_FAVORITE_GROUP_PATH);
    public static final Uri GET_BELONG_TO_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + GET_BELONG_TO_PATH);

    static {
        sUriMatcher.addURI(AUTHORITY, GET_USER_PATH, GET_USER_KEY);
        sUriMatcher.addURI(AUTHORITY, GET_SERVER_SITE_PATH, GET_SERVER_SITE_KEY);
        sUriMatcher.addURI(AUTHORITY, GET_ALL_PATH, GET_ALL_KEY);
        sUriMatcher.addURI(AUTHORITY, GET_MESSAGE_PATH, GET_MESSAGE_KEY);
        sUriMatcher.addURI(AUTHORITY, GET_CHAT_ROOM_PATH, GET_CHAT_ROOM_KEY);
        sUriMatcher.addURI(AUTHORITY, GET_DEPART_PATH, GET_DEPART_KEY);
        sUriMatcher.addURI(AUTHORITY, GET_FAVORITE_USER_PATH, GET_FAVORITE_USER_KEY);
        sUriMatcher.addURI(AUTHORITY, GET_FAVORITE_GROUP_PATH, GET_FAVORITE_GROUP_KEY);
        sUriMatcher.addURI(AUTHORITY, GET_BELONG_TO_PATH, GET_BELONG_TO_KEY);
    }


    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // TODO Auto-generated method stub
        int row_deleted = 0;
        SQLiteDatabase db = null;
        db = mDatabaseHelper.getWritableDatabase();
        int uriKey = sUriMatcher.match(uri);
        switch (uriKey) {
            case GET_USER_KEY:
                row_deleted = db.delete(UserDBHelper.TABLE_NAME, selection, selectionArgs);
                break;
            case GET_SERVER_SITE_KEY:
                row_deleted = db.delete(ServerSiteDBHelper.TABLE_NAME, selection, selectionArgs);
                break;
            case GET_ALL_KEY:
                row_deleted = db.delete(AllUserDBHelper.TABLE_NAME, selection, selectionArgs);
                break;

            case GET_MESSAGE_KEY:
                row_deleted = db.delete(ChatMessageDBHelper.TABLE_NAME, selection, selectionArgs);
                break;

            case GET_CHAT_ROOM_KEY:
                row_deleted = db.delete(ChatRomDBHelper.TABLE_NAME, selection, selectionArgs);
                break;
            case GET_DEPART_KEY:
                row_deleted = db.delete(DepartmentDBHelper.TABLE_NAME, selection, selectionArgs);
                break;
            case GET_FAVORITE_USER_KEY:
                row_deleted = db.delete(FavoriteUserDBHelper.TABLE_NAME, selection, selectionArgs);
                break;

            case GET_FAVORITE_GROUP_KEY:
                row_deleted = db.delete(FavoriteGroupDBHelper.TABLE_NAME, selection, selectionArgs);
                break;

            case GET_BELONG_TO_KEY:
                row_deleted = db.delete(BelongsToDBHelper.TABLE_NAME, selection, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        return row_deleted;
    }

    @Override
    public String getType(Uri uri) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO Auto-generated method stub
        Uri result_uri = null;
        SQLiteDatabase db = null;
        db = mDatabaseHelper.getWritableDatabase();
        int uriKey = sUriMatcher.match(uri);
        long id = 0;
        switch (uriKey) {
            case GET_USER_KEY:
                id = db.insertWithOnConflict(UserDBHelper.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                result_uri = Uri.parse(GET_USER_CONTENT_URI + "/" + Long.toString(id));
                break;
            case GET_SERVER_SITE_KEY:
                id = db.insertWithOnConflict(ServerSiteDBHelper.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                result_uri = Uri.parse(GET_SERVER_SITE_CONTENT_URI + "/" + Long.toString(id));
                break;
            case GET_ALL_KEY:
                id = db.insertWithOnConflict(AllUserDBHelper.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                result_uri = Uri.parse(GET_ALL_CONTENT_URI + "/" + Long.toString(id));
                break;

            case GET_MESSAGE_KEY:
                id = db.insertWithOnConflict(ChatMessageDBHelper.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                result_uri = Uri.parse(GET_MESSAGE_CONTENT_URI + "/" + Long.toString(id));
                break;

            case GET_CHAT_ROOM_KEY:
                id = db.insertWithOnConflict(ChatRomDBHelper.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                result_uri = Uri.parse(GET_CHAT_ROOM_CONTENT_URI + "/" + Long.toString(id));
                break;
            case GET_DEPART_KEY:
                id = db.insertWithOnConflict(DepartmentDBHelper.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                result_uri = Uri.parse(GET_DEPARTMENT_CONTENT_URI + "/" + Long.toString(id));
                break;

            case GET_FAVORITE_USER_KEY:
                id = db.insertWithOnConflict(FavoriteUserDBHelper.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                result_uri = Uri.parse(GET_FAVORITE_USER_CONTENT_URI + "/" + Long.toString(id));
                break;

            case GET_FAVORITE_GROUP_KEY:
                id = db.insertWithOnConflict(FavoriteGroupDBHelper.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                result_uri = Uri.parse(GET_FAVORITE_GROUP_CONTENT_URI + "/" + Long.toString(id));
                break;

            case GET_BELONG_TO_KEY:
                id = db.insertWithOnConflict(BelongsToDBHelper.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                result_uri = Uri.parse(GET_BELONG_TO_CONTENT_URI+ "/" + Long.toString(id));
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        return result_uri;
    }

    @Override
    public boolean onCreate() {
        // TODO Auto-generated method stub
        mDatabaseHelper = new AppDatabaseHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        // TODO Auto-generated method stub
        Cursor cursor = null;
        SQLiteDatabase db = null;
        db = mDatabaseHelper.getWritableDatabase();
        SQLiteQueryBuilder querybuilder = new SQLiteQueryBuilder();
        int uriKey = sUriMatcher.match(uri);
        switch (uriKey) {
            case GET_USER_KEY:
                querybuilder.setTables(UserDBHelper.TABLE_NAME);
                cursor = querybuilder.query(db, null, selection, selectionArgs, null, null, sortOrder);
                cursor.setNotificationUri(getContext().getContentResolver(), uri);
                break;
            case GET_SERVER_SITE_KEY:
                querybuilder.setTables(ServerSiteDBHelper.TABLE_NAME);
                cursor = querybuilder.query(db, null, selection, selectionArgs, null, null, sortOrder);
                cursor.setNotificationUri(getContext().getContentResolver(), uri);
                break;
            case GET_ALL_KEY:
                querybuilder.setTables(AllUserDBHelper.TABLE_NAME);
                cursor = querybuilder.query(db, null, selection, selectionArgs, null, null, sortOrder);
                cursor.setNotificationUri(getContext().getContentResolver(), uri);
                break;

            case GET_MESSAGE_KEY:
                querybuilder.setTables(ChatMessageDBHelper.TABLE_NAME);
                cursor = querybuilder.query(db, null, selection, selectionArgs, null, null, sortOrder);
                cursor.setNotificationUri(getContext().getContentResolver(), uri);
                break;

            case GET_CHAT_ROOM_KEY:
                querybuilder.setTables(ChatRomDBHelper.TABLE_NAME);
                cursor = querybuilder.query(db, null, selection, selectionArgs, null, null, sortOrder);
                cursor.setNotificationUri(getContext().getContentResolver(), uri);
                break;
            case GET_DEPART_KEY:
                querybuilder.setTables(DepartmentDBHelper.TABLE_NAME);
                cursor = querybuilder.query(db, null, selection, selectionArgs, null, null, sortOrder);
                cursor.setNotificationUri(getContext().getContentResolver(), uri);
                break;

            case GET_FAVORITE_USER_KEY:
                querybuilder.setTables(FavoriteUserDBHelper.TABLE_NAME);
                cursor = querybuilder.query(db, null, selection, selectionArgs, null, null, sortOrder);
                cursor.setNotificationUri(getContext().getContentResolver(), uri);
                break;

            case GET_FAVORITE_GROUP_KEY:
                querybuilder.setTables(FavoriteGroupDBHelper.TABLE_NAME);
                cursor = querybuilder.query(db, null, selection, selectionArgs, null, null, sortOrder);
                cursor.setNotificationUri(getContext().getContentResolver(), uri);
                break;

            case GET_BELONG_TO_KEY:
                querybuilder.setTables(BelongsToDBHelper.TABLE_NAME);
                cursor = querybuilder.query(db, null, selection, selectionArgs, null, null, sortOrder);
                cursor.setNotificationUri(getContext().getContentResolver(), uri);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO Auto-generated method stub
        int row_update = 0;
        SQLiteDatabase db = null;
        db = mDatabaseHelper.getWritableDatabase();
        int uriKey = sUriMatcher.match(uri);
        switch (uriKey) {
            case GET_USER_KEY:
                row_update = db.update(UserDBHelper.TABLE_NAME, values, selection, selectionArgs);
                break;
            case GET_SERVER_SITE_KEY:
                row_update = db.update(ServerSiteDBHelper.TABLE_NAME, values, selection, selectionArgs);
                break;
            case GET_ALL_KEY:
                row_update = db.update(AllUserDBHelper.TABLE_NAME, values, selection, selectionArgs);
                break;
            case GET_MESSAGE_KEY:
                row_update = db.update(ChatMessageDBHelper.TABLE_NAME, values, selection, selectionArgs);
                break;
            case GET_CHAT_ROOM_KEY:
                row_update = db.update(ChatRomDBHelper.TABLE_NAME, values, selection, selectionArgs);
                break;
            case GET_DEPART_KEY:
                row_update = db.update(DepartmentDBHelper.TABLE_NAME, values, selection, selectionArgs);
                break;
            case GET_FAVORITE_USER_KEY:
                row_update = db.update(FavoriteUserDBHelper.TABLE_NAME, values, selection, selectionArgs);
                break;
            case GET_FAVORITE_GROUP_KEY:
                row_update = db.update(FavoriteGroupDBHelper.TABLE_NAME, values, selection, selectionArgs);
                break;
            case GET_BELONG_TO_KEY:
                row_update = db.update(BelongsToDBHelper.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        return row_update;
    }

}
