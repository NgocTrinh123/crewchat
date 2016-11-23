package com.dazone.crewchat.database;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;

import android.text.TextUtils;
import com.dazone.crewchat.constant.Statics;
import com.dazone.crewchat.dto.UserDto;
import com.dazone.crewchat.utils.CrewChatApplication;
import com.dazone.crewchat.utils.Utils;

/**
 * Created by maidinh on 8/13/2015.
 */
public class UserDBHelper {
    public static final String TABLE_NAME = "UserTbl";
    public static final String ID = "Id";
    public static final String USER_ID = "user_id";
    public static final String USER_SESSION = "user_session";
    public static final String USER_NAME = "user_name";
    public static final String USER_COMPANY = "user_company";
    public static final String USER_FULLNAME = "user_fullname";
    public static final String USER_AVATAR = "user_avartar";
    public static final String USER_STATUS = "user_status";

    public static final String SQL_EXCUTE = "CREATE TABLE " + TABLE_NAME + "("
            + ID + " integer primary key autoincrement not null,"
            +USER_ID+" integer,"
            + USER_SESSION + " text, "
            + USER_NAME + " text, "
            + USER_COMPANY + " text, "
            + USER_FULLNAME + " text, "
            + USER_STATUS + " integer, "
            + USER_AVATAR + " text );";

    // this version just get one user
    public static UserDto getUser()
    {
        String[] columns = new String[] { "*"};
        ContentResolver resolver = CrewChatApplication.getInstance()
                .getApplicationContext().getContentResolver();
        Cursor cursor = resolver.query(
                AppContentProvider.GET_USER_CONTENT_URI, columns, null,
                null, null);
        UserDto userDto = userDto = new UserDto();
        if(cursor!=null){
            if(cursor.getCount()>0){
                try {
                    if(cursor.moveToFirst()){

                        userDto.Id = Integer.parseInt(cursor.getString(cursor.getColumnIndex(USER_ID)));
                        userDto.session = cursor.getString(cursor.getColumnIndex(USER_SESSION));
                        userDto.userID = cursor.getString(cursor.getColumnIndex(USER_NAME));
                        userDto.NameCompany = cursor.getString(cursor.getColumnIndex(USER_COMPANY));
                        userDto.FullName = cursor.getString(cursor.getColumnIndex(USER_FULLNAME));
                        userDto.avatar = cursor.getString(cursor.getColumnIndex(USER_AVATAR));
                        String status = cursor.getString(cursor.getColumnIndex(USER_STATUS));
                        String status_is_null = !TextUtils.isEmpty(status) ? cursor.getString(cursor.getColumnIndex(USER_STATUS)) : Statics.USER_LOGOUT+"";
                        userDto.status = Integer.parseInt(status_is_null);
                    }

                }finally {
                    cursor.close();
                }

            }
            cursor.close();
        }
        return userDto;
    }

    public static boolean updateStatus(int userId, int status){
        try {

            ContentResolver resolver = CrewChatApplication.getInstance()
                    .getApplicationContext().getContentResolver();

            ContentValues conValues = new ContentValues();
            conValues.put(USER_STATUS, status);
            resolver.update(AppContentProvider.GET_USER_CONTENT_URI, conValues, USER_ID + "=" + userId, null);
            return true;

        } catch (Exception e) {
            // TODO: handle exception
            Utils.printLogs("Catch to update current user status ###");
        }

        return false;
    }

    public static boolean addUser(UserDto userDto) {
        clearUser();
        try {
            ContentValues values = new ContentValues();
            values.put(USER_ID, userDto.Id);
            values.put(USER_SESSION, userDto.session);
            values.put(USER_NAME, userDto.userID);
            values.put(USER_COMPANY, userDto.NameCompany);
            values.put(USER_FULLNAME, userDto.FullName);
            values.put(USER_AVATAR, userDto.avatar);
            ContentResolver resolver = CrewChatApplication.getInstance()
                    .getApplicationContext().getContentResolver();
            resolver.insert(AppContentProvider.GET_USER_CONTENT_URI, values);
            return true;
        } catch (Exception e) {
            // TODO: handle exception
        }
        return false;
    }
    public static boolean clearUser() {
        try {

            ContentResolver resolver = CrewChatApplication.getInstance()
                    .getApplicationContext().getContentResolver();
            resolver.delete(AppContentProvider.GET_USER_CONTENT_URI, null,null);
            return true;
        } catch (Exception e) {
            // TODO: handle exception
        }
        return false;
    }
}
