package com.dazone.crewchat.database;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import com.dazone.crewchat.dto.userfavorites.FavoriteGroupDto;
import com.dazone.crewchat.utils.CrewChatApplication;
import com.dazone.crewchat.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class FavoriteGroupDBHelper {
    public static final String TABLE_NAME = "FavoriteGroupTbl";

    public static final String ID = "Id";

    public static final String NAME = "name";
    public static final String SORT_NO = "sort_no";
    public static final String GROUP_NO = "group_no";
    public static final String USER_NO = "user_no";
    public static final String MOD_DATE = "mod_date";

    public static final String SQL_EXCUTE = "CREATE TABLE " + TABLE_NAME + "("
            + ID + " integer primary key autoincrement not null,"
            + NAME +" text,"
            + SORT_NO + " integer, "
            + GROUP_NO + " integer, "
            + USER_NO + " integer, "
            + MOD_DATE + " text );";


    // this version just get one user
    public static ArrayList<FavoriteGroupDto> getFavoriteGroup()
    {
        String[] columns = new String[] { "*"};
        ContentResolver resolver = CrewChatApplication.getInstance()
                .getApplicationContext().getContentResolver();
        Cursor cursor = resolver.query(
                AppContentProvider.GET_FAVORITE_GROUP_CONTENT_URI, columns, null , null, null);

        ArrayList<FavoriteGroupDto> favoriteGroups = new ArrayList<>();

        if(cursor!=null){
            if(cursor.getCount()>0){
                try {
                    while (cursor.moveToNext()){

                        int id  = Integer.parseInt(cursor.getString(cursor.getColumnIndex(ID)));
                        String name = cursor.getString(cursor.getColumnIndex(NAME));
                        int sortNo = Integer.parseInt(cursor.getString(cursor.getColumnIndex(SORT_NO)));
                        int groupNo = Integer.parseInt(cursor.getString(cursor.getColumnIndex(GROUP_NO)));
                        int userNo = Integer.parseInt(cursor.getString(cursor.getColumnIndex(USER_NO)));
                        String modDate = cursor.getString(cursor.getColumnIndex(MOD_DATE));


                        FavoriteGroupDto group = new FavoriteGroupDto();

                        group.setDbId(id);
                        group.setName(name);
                        group.setUserNo(userNo);
                        group.setGroupNo(groupNo);
                        group.setSortNo(sortNo);
                        group.setModDate(modDate);

                        group.setUserList(FavoriteUserDBHelper.getFavoriteByGroup(groupNo));

                        favoriteGroups.add(group);
                    }

                }finally {
                    cursor.close();
                }

            }
            cursor.close();
        }
        return favoriteGroups;
    }

    public static boolean isExist(FavoriteGroupDto group){

        String[] columns = new String[] { "*"};
        ContentResolver resolver = CrewChatApplication.getInstance()
                .getApplicationContext().getContentResolver();
        Cursor cursor = resolver.query(
                AppContentProvider.GET_FAVORITE_GROUP_CONTENT_URI, columns, GROUP_NO + "=" + group.getGroupNo(), null, null);

        if(cursor!=null) {
            if (cursor.getCount() > 0) {
                cursor.close();
                return true;
            }
            cursor.close();
        }

        return false;
    }


    public static boolean deleteFavoriteGroup(long groupNo){
        try {

            if(FavoriteUserDBHelper.deleteFavoriteUsers(groupNo)){
                ContentResolver resolver = CrewChatApplication.getInstance()
                        .getApplicationContext().getContentResolver();
                resolver.delete(AppContentProvider.GET_FAVORITE_GROUP_CONTENT_URI, GROUP_NO + "=" + groupNo, null);
            }
            return true;

        } catch (Exception e) {
            // TODO: handle exception
            Utils.printLogs("Update favorite group error ####");
        }

        return false;
    }

    public static boolean updateGroup(FavoriteGroupDto group) {

        try {

            boolean success = FavoriteUserDBHelper.addUsers(group.getUserList());
            if (!success){ // Insert user list is not successfully
                return false;
            }

            ContentValues values = new ContentValues();
            values.put(NAME, group.getName());
            values.put(SORT_NO, group.getSortNo());
            values.put(USER_NO, group.getUserNo());
            values.put(MOD_DATE, group.getModDate());

            ContentResolver resolver = CrewChatApplication.getInstance()
                    .getApplicationContext().getContentResolver();
            resolver.update(AppContentProvider.GET_FAVORITE_GROUP_CONTENT_URI, values, GROUP_NO+ " = " +group.getGroupNo(), null);
            return true;

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return false;
    }

    public static boolean updateGroup(long groupNo, String groupName) {

        try {
            ContentValues values = new ContentValues();
            values.put(NAME, groupName);

            ContentResolver resolver = CrewChatApplication.getInstance()
                    .getApplicationContext().getContentResolver();
            resolver.update(AppContentProvider.GET_FAVORITE_GROUP_CONTENT_URI, values, GROUP_NO+ " = " +groupNo, null);
            return true;

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return false;
    }

    public static boolean addGroups(List<FavoriteGroupDto> groups) {
        try {

            for (FavoriteGroupDto group : groups){

                if (!isExist(group)){

                    boolean success = FavoriteUserDBHelper.addUsers(group.getUserList());

                    if (!success){ // Insert user list is not successfully
                        break;
                    }

                    ContentValues values = new ContentValues();
                    values.put(NAME, group.getName());
                    values.put(SORT_NO, group.getSortNo());
                    values.put(GROUP_NO, group.getGroupNo());
                    values.put(USER_NO, group.getUserNo());
                    values.put(MOD_DATE, group.getModDate());

                    ContentResolver resolver = CrewChatApplication.getInstance()
                            .getApplicationContext().getContentResolver();
                    resolver.insert(AppContentProvider.GET_FAVORITE_GROUP_CONTENT_URI, values);
                }else{
                    updateGroup(group);
                }

            }
            return true;

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return false;
    }

    public static boolean addGroup(FavoriteGroupDto group) {
        try {
            if (!isExist(group)){
                ContentValues values = new ContentValues();

                values.put(NAME, group.getName());
                values.put(SORT_NO, group.getSortNo());
                values.put(GROUP_NO, group.getGroupNo());
                values.put(USER_NO, group.getUserNo());
                values.put(MOD_DATE, group.getModDate());

                ContentResolver resolver = CrewChatApplication.getInstance()
                        .getApplicationContext().getContentResolver();
                resolver.insert(AppContentProvider.GET_FAVORITE_GROUP_CONTENT_URI, values);
            }else{
                updateGroup(group);
            }

            return true;

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return false;
    }

    public static boolean clearGroups() {
        try {

            ContentResolver resolver = CrewChatApplication.getInstance()
                    .getApplicationContext().getContentResolver();
            resolver.delete(AppContentProvider.GET_FAVORITE_GROUP_CONTENT_URI, null,null);
            return true;
        } catch (Exception e) {
            // TODO: handle exception
        }
        return false;
    }
}
