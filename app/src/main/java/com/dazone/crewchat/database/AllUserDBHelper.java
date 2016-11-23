package com.dazone.crewchat.database;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import com.dazone.crewchat.dto.TreeUserDTOTemp;
import com.dazone.crewchat.utils.CrewChatApplication;
import com.dazone.crewchat.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by THANHTUNG on 18/02/2016.
 */
public class AllUserDBHelper {
    public static final String TABLE_NAME = "AllAccountTbl";
    public static final String ID = "Id";
    public static final String ALL_DEPART_NO = "depart_no";
    public static final String ALL_USER_ID = "user_id";
    public static final String ALL_USER_NO = "user_no";
    public static final String ALL_POSITION = "position";
    public static final String ALL_USER_NAME = "name";
    public static final String ALL_USER_NAME_EN = "name_en";
    public static final String ALL_AVATAR_URL = "avatar_url";
    public static final String ALL_CELL_PHONE = "cell_phone";
    public static final String ALL_COMPANY_NUMBER = "company_number";
    public static final String ALL_USER_STATUS = "user_status";
    public static final String ALL_USER_STATUS_STRING = "user_status_string";

    public static final String SQL_EXCUTE = "CREATE TABLE " + TABLE_NAME + "("
            + ID + " integer primary key autoincrement not null," + ALL_DEPART_NO + " integer," + ALL_USER_ID + " integer," + ALL_POSITION
            + " text, " + ALL_USER_NAME + " text, " + ALL_USER_NAME_EN + " text, "
            + ALL_USER_STATUS + " integer, "
            + ALL_USER_STATUS_STRING + " text, "
            + ALL_USER_NO + " integer, "
            + ALL_AVATAR_URL + " text, " + ALL_CELL_PHONE + " text," + ALL_COMPANY_NUMBER + " text );";

    public static ArrayList<TreeUserDTOTemp> getUser() {
        String[] columns = new String[]{"*"};
        ContentResolver resolver = CrewChatApplication.getInstance()
                .getApplicationContext().getContentResolver();
        Cursor cursor = resolver.query(
                AppContentProvider.GET_ALL_CONTENT_URI, columns, null,
                null, null);
        ArrayList<TreeUserDTOTemp> arrayList = new ArrayList<>();
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                try {
                    while (!cursor.isLast()) {
                        cursor.moveToNext();
                        TreeUserDTOTemp userDto = new TreeUserDTOTemp();
                        userDto.setDBId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(ID))));
                        int userNo = Integer.parseInt(cursor.getString(cursor.getColumnIndex(ALL_USER_NO)));
                        userDto.setUserNo(userNo);
                        userDto.setUserID(cursor.getString(cursor.getColumnIndex(ALL_USER_ID)));
                        userDto.setDepartNo(Integer.parseInt(cursor.getString(cursor.getColumnIndex(ALL_DEPART_NO))));
                        userDto.setPosition(cursor.getString(cursor.getColumnIndex(ALL_POSITION)));
                        userDto.setAvatarUrl(cursor.getString(cursor.getColumnIndex(ALL_AVATAR_URL)));
                        userDto.setCellPhone(cursor.getString(cursor.getColumnIndex(ALL_CELL_PHONE)));
                        userDto.setCompanyPhone(cursor.getString(cursor.getColumnIndex(ALL_COMPANY_NUMBER)));
                        userDto.setName(cursor.getString(cursor.getColumnIndex(ALL_USER_NAME)));
                        userDto.setNameEN(cursor.getString(cursor.getColumnIndex(ALL_USER_NAME_EN)));

                        String statusStr = cursor.getString(cursor.getColumnIndex(ALL_USER_STATUS)) == null ? "0" : cursor.getString(cursor.getColumnIndex(ALL_USER_STATUS));
                        int status = Integer.parseInt(statusStr);
                        userDto.setStatus(status);

                        // Get belong to here
                        userDto.setBelongs(BelongsToDBHelper.getBelongs(userNo));

                        userDto.setUserStatusString(cursor.getString(cursor.getColumnIndex(ALL_USER_STATUS_STRING)));
                        arrayList.add(userDto);
                    }

                } finally {
                    cursor.close();
                }
            }
            cursor.close();
        }

        return arrayList;
    }

    public static TreeUserDTOTemp getAUser(long userNo){

        String[] columns = new String[] { "*"};
        ContentResolver resolver = CrewChatApplication.getInstance().getApplicationContext().getContentResolver();
        Cursor cursor = resolver.query(
                AppContentProvider.GET_ALL_CONTENT_URI, columns, ALL_USER_NO + "=" + userNo, null, null);
        if (cursor != null){

            if (cursor.getCount() > 0){
                try {
                    cursor.moveToFirst();
                    TreeUserDTOTemp userDto = new TreeUserDTOTemp();
                    userDto.setDBId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(ID))));
                    userDto.setUserNo(Integer.parseInt(cursor.getString(cursor.getColumnIndex(ALL_USER_NO))));
                    userDto.setUserID(cursor.getString(cursor.getColumnIndex(ALL_USER_ID)));
                    userDto.setDepartNo(Integer.parseInt(cursor.getString(cursor.getColumnIndex(ALL_DEPART_NO))));
                    userDto.setPosition(cursor.getString(cursor.getColumnIndex(ALL_POSITION)));
                    userDto.setAvatarUrl(cursor.getString(cursor.getColumnIndex(ALL_AVATAR_URL)));
                    userDto.setCellPhone(cursor.getString(cursor.getColumnIndex(ALL_CELL_PHONE)));
                    userDto.setCompanyPhone(cursor.getString(cursor.getColumnIndex(ALL_COMPANY_NUMBER)));
                    userDto.setName(cursor.getString(cursor.getColumnIndex(ALL_USER_NAME)));
                    userDto.setNameEN(cursor.getString(cursor.getColumnIndex(ALL_USER_NAME_EN)));
                    userDto.setStatus(Integer.parseInt(cursor.getString(cursor.getColumnIndex(ALL_USER_STATUS))));

                    String statusString = cursor.getString(cursor.getColumnIndex(ALL_USER_STATUS_STRING));

                    Utils.printLogs("Update status : Get a user status for userName ="+userDto.getUserID()+" status string ="+statusString);
                    // Get belong to here
                    userDto.setBelongs(BelongsToDBHelper.getBelongs(userNo));
                    userDto.setUserStatusString(statusString);
                    return userDto;
                }catch (Exception e){
                    e.printStackTrace();
                } finally {
                    cursor.close();
                }
            }
            cursor.close();
        }

        return null;
    }


    public static String getAUserStatus(int userNo){

        String[] columns = new String[] { ALL_USER_STATUS_STRING };
        ContentResolver resolver = CrewChatApplication.getInstance()
                .getApplicationContext().getContentResolver();
        Cursor cursor = resolver.query(
                AppContentProvider.GET_ALL_CONTENT_URI, columns, ALL_USER_NO + "=" + userNo, null, null);

        if (cursor != null){

            if (cursor.getCount() > 0){
                try {
                    cursor.moveToFirst();
                    return cursor.getString(cursor.getColumnIndex(ALL_USER_STATUS_STRING));
                }catch (Exception e){
                    e.printStackTrace();
                } finally {
                    cursor.close();
                }
            }
            cursor.close();
        }

        return null;
    }

    public static boolean updateStatus(int userId, int status){
        try {

            ContentResolver resolver = CrewChatApplication.getInstance()
                    .getApplicationContext().getContentResolver();

            ContentValues conValues = new ContentValues();
            conValues.put(ALL_USER_STATUS, status);
            resolver.update(AppContentProvider.GET_ALL_CONTENT_URI, conValues, ID + "=" + userId, null);
            return true;

        } catch (Exception e) {
            // TODO: handle exception
            Utils.printLogs("Catch to update user status ###");
        }

        return false;
    }

    public static boolean updateStatusString(int userId, String status){
        try {

            ContentResolver resolver = CrewChatApplication.getInstance()
                    .getApplicationContext().getContentResolver();

            ContentValues conValues = new ContentValues();
            conValues.put(ALL_USER_STATUS_STRING, status);
            resolver.update(AppContentProvider.GET_ALL_CONTENT_URI, conValues, ID + "=" + userId, null);

            Utils.printLogs("Update status string for userid = "+userId+" statusMsg = "+status);

            return true;

        } catch (Exception e) {
            // TODO: handle exception
        }

        return false;
    }

    public static boolean updateUser(TreeUserDTOTemp treeUserDTOTemp){
        try {
                ContentResolver resolver = CrewChatApplication.getInstance()
                        .getApplicationContext().getContentResolver();
                ContentValues values = new ContentValues();

                values.put(ALL_DEPART_NO, treeUserDTOTemp.getDepartNo());
                values.put(ALL_USER_ID, treeUserDTOTemp.getUserID());
                values.put(ALL_USER_NO, treeUserDTOTemp.getUserNo());
                values.put(ALL_AVATAR_URL, treeUserDTOTemp.getAvatarUrl());
                values.put(ALL_POSITION, treeUserDTOTemp.getPosition());
                values.put(ALL_CELL_PHONE, treeUserDTOTemp.getCellPhone());
                values.put(ALL_COMPANY_NUMBER, treeUserDTOTemp.getCompanyPhone());
                values.put(ALL_USER_NAME, treeUserDTOTemp.getName());
                values.put(ALL_USER_NAME_EN, treeUserDTOTemp.getNameEN());
                values.put(ALL_USER_STATUS_STRING, treeUserDTOTemp.getUserStatusString());
                values.put(ALL_USER_STATUS, treeUserDTOTemp.getStatus());
                resolver.update(AppContentProvider.GET_ALL_CONTENT_URI, values, ALL_USER_NO + "=" + treeUserDTOTemp.getUserNo(), null);

            return true;

        } catch (Exception e) {
            // TODO: handle exception
            Utils.printLogs("Catch to update user info ###");
        }

        return false;
    }

    public synchronized static boolean isExist(TreeUserDTOTemp temp){

        String[] columns = new String[] { "*"};
        ContentResolver resolver = CrewChatApplication.getInstance()
                .getApplicationContext().getContentResolver();

        Cursor cursor = resolver.query(AppContentProvider.GET_ALL_CONTENT_URI, columns, ALL_USER_NO + "=" + temp.getUserNo(), null, null);

        if(cursor!=null) {
            if (cursor.getCount() > 0) {
                cursor.close();
                return true;
            }else {
                cursor.close();
            }
        }

        return false;
    }

    public synchronized static boolean addUser(List<TreeUserDTOTemp> list) {
        try {
            for (TreeUserDTOTemp treeUserDTOTemp : list) {
                // Check user before insert
                if (!isExist(treeUserDTOTemp)){
                    // perform insert to belongs to

                    boolean isSuccess = BelongsToDBHelper.addDepartment(treeUserDTOTemp.getBelongs());
                    if (isSuccess) {
                        // perform insert to database
                        ContentValues values = new ContentValues();
                        values.put(ALL_DEPART_NO, treeUserDTOTemp.getDepartNo());
                        values.put(ALL_USER_ID, treeUserDTOTemp.getUserID());
                        values.put(ALL_USER_NO, treeUserDTOTemp.getUserNo());
                        values.put(ALL_AVATAR_URL, treeUserDTOTemp.getAvatarUrl());
                        values.put(ALL_POSITION, treeUserDTOTemp.getPosition());
                        values.put(ALL_CELL_PHONE, treeUserDTOTemp.getCellPhone());
                        values.put(ALL_COMPANY_NUMBER, treeUserDTOTemp.getCompanyPhone());
                        values.put(ALL_USER_NAME, treeUserDTOTemp.getName());
                        values.put(ALL_USER_NAME_EN, treeUserDTOTemp.getNameEN());
                        values.put(ALL_USER_STATUS_STRING, treeUserDTOTemp.getUserStatusString());
                        values.put(ALL_USER_STATUS, treeUserDTOTemp.getStatus());
                        ContentResolver resolver = CrewChatApplication.getInstance()
                                .getApplicationContext().getContentResolver();
                        resolver.insert(AppContentProvider.GET_ALL_CONTENT_URI, values);
                    } else {
                        return false;
                    }

                } else{ // Perform update user to database
                    updateUser(treeUserDTOTemp);
                }
            }
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
            resolver.delete(AppContentProvider.GET_ALL_CONTENT_URI, null, null);
            return true;
        } catch (Exception e) {
            // TODO: handle exception
        }
        return false;
    }
}
