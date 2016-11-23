package com.dazone.crewchat.database;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import com.dazone.crewchat.dto.TreeUserDTOTemp;
import com.dazone.crewchat.dto.userfavorites.FavoriteUserDto;
import com.dazone.crewchat.utils.CrewChatApplication;
import com.dazone.crewchat.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FavoriteUserDBHelper {
    public static final String TABLE_NAME = "FavoriteUserTbl";

    public static final String ID = "Id";

    public static final String GROUP_USER_NO = "group_user_no";
    public static final String REG_USER_NO = "reg_user_no";
    public static final String GROUP_NO = "group_no";
    public static final String USER_NO = "user_no";
    public static final String SORT_NO = "sort_no";
    public static final String MOD_DATE = "mod_date";

    public static final String IS_TOP = "is_top_favorite";

    public static final String SQL_EXCUTE = "CREATE TABLE " + TABLE_NAME + "("
            + ID + " integer primary key autoincrement not null,"
            + GROUP_USER_NO +" integer,"
            + REG_USER_NO + " integer, "
            + GROUP_NO + " integer, "
            + USER_NO + " integer, "
            + SORT_NO + " integer, "
            + MOD_DATE + " text, "
            + IS_TOP + " integer);";


    // this version just get one user
    public static ArrayList<FavoriteUserDto> getFavoriteByGroup(long groupNumber)
    {
        String[] columns = new String[] { "*"};
        ContentResolver resolver = CrewChatApplication.getInstance()
                .getApplicationContext().getContentResolver();
        Cursor cursor = resolver.query(
                AppContentProvider.GET_FAVORITE_USER_CONTENT_URI, columns, GROUP_NO + "=" + groupNumber + " AND "+IS_TOP+" = 0", null, null);

        ArrayList<FavoriteUserDto> favoriteUsers = new ArrayList<>();

        if(cursor!=null){
            if(cursor.getCount()>0){
                try {
                    while (cursor.moveToNext()){
                        int id  = Integer.parseInt(cursor.getString(cursor.getColumnIndex(ID)));
                        int groupUserNo = Integer.parseInt(cursor.getString(cursor.getColumnIndex(GROUP_USER_NO)));
                        int regUserNo = Integer.parseInt(cursor.getString(cursor.getColumnIndex(REG_USER_NO)));
                        int userNo = Integer.parseInt(cursor.getString(cursor.getColumnIndex(USER_NO)));
                        int groupNo = Integer.parseInt(cursor.getString(cursor.getColumnIndex(GROUP_NO)));
                        int sortNo = Integer.parseInt(cursor.getString(cursor.getColumnIndex(SORT_NO)));
                        String modDate = cursor.getString(cursor.getColumnIndex(MOD_DATE));

                        int isTop = Integer.parseInt(cursor.getString(cursor.getColumnIndex(IS_TOP)));

                        FavoriteUserDto user = new FavoriteUserDto();
                        user.setDbId(id);
                        user.setGroupUserNo(groupUserNo);
                        user.setRegUserNo(regUserNo);
                        user.setUserNo(userNo);
                        user.setGroupNo(groupNo);
                        user.setSortNo(sortNo);
                        user.setModDate(modDate);
                        user.setIsTop(isTop);

                        favoriteUsers.add(user);
                    }

                }finally {
                    cursor.close();
                }

            }
            cursor.close();
        }

        // Sort favorite user
        Collections.sort(favoriteUsers, new Comparator<FavoriteUserDto>() {
            @Override
            public int compare(FavoriteUserDto r1, FavoriteUserDto r2) {
                TreeUserDTOTemp name1 = AllUserDBHelper.getAUser(r1.getUserNo());
                TreeUserDTOTemp name2 = AllUserDBHelper.getAUser(r2.getUserNo());
                if (name1 != null && name2 != null){
                    return name1.getName().compareToIgnoreCase(name2.getName());
                }
                return -1;
            }
        });

        return favoriteUsers;
    }


    // this version just get one user
    public static ArrayList<FavoriteUserDto> getFavoriteTop()
    {
        String[] columns = new String[] { "*"};
        ContentResolver resolver = CrewChatApplication.getInstance()
                .getApplicationContext().getContentResolver();
        Cursor cursor = resolver.query(
                AppContentProvider.GET_FAVORITE_USER_CONTENT_URI, columns, IS_TOP+" = "+1, null, null);

        ArrayList<FavoriteUserDto> favoriteUsers = new ArrayList<>();

        if(cursor!=null){
            if(cursor.getCount()>0){
                try {
                    while (cursor.moveToNext()){
                        int id  = Integer.parseInt(cursor.getString(cursor.getColumnIndex(ID)));
                        int groupUserNo = Integer.parseInt(cursor.getString(cursor.getColumnIndex(GROUP_USER_NO)));
                        int regUserNo = Integer.parseInt(cursor.getString(cursor.getColumnIndex(REG_USER_NO)));
                        int userNo = Integer.parseInt(cursor.getString(cursor.getColumnIndex(USER_NO)));
                        int groupNo = Integer.parseInt(cursor.getString(cursor.getColumnIndex(GROUP_NO)));
                        int sortNo = Integer.parseInt(cursor.getString(cursor.getColumnIndex(SORT_NO)));
                        String modDate = cursor.getString(cursor.getColumnIndex(MOD_DATE));

                        int isTop = Integer.parseInt(cursor.getString(cursor.getColumnIndex(IS_TOP)));

                        FavoriteUserDto user = new FavoriteUserDto();
                        user.setDbId(id);
                        user.setGroupUserNo(groupUserNo);
                        user.setRegUserNo(regUserNo);
                        user.setUserNo(userNo);
                        user.setGroupNo(groupNo);
                        user.setSortNo(sortNo);
                        user.setModDate(modDate);
                        user.setIsTop(isTop);

                        favoriteUsers.add(user);
                    }

                }finally {
                    cursor.close();
                }

            }
            cursor.close();
        }

        // Sort favorite user
        Collections.sort(favoriteUsers, new Comparator<FavoriteUserDto>() {
            @Override
            public int compare(FavoriteUserDto r1, FavoriteUserDto r2) {
                TreeUserDTOTemp name1 = AllUserDBHelper.getAUser(r1.getUserNo());
                TreeUserDTOTemp name2 = AllUserDBHelper.getAUser(r2.getUserNo());
                if (name1 != null && name2 != null){
                    return name1.getName().compareToIgnoreCase(name2.getName());
                }
                return -1;
            }
        });

        return favoriteUsers;
    }

    public static boolean isExist(FavoriteUserDto userDto){

        String[] columns = new String[] { "*"};
        ContentResolver resolver = CrewChatApplication.getInstance()
                .getApplicationContext().getContentResolver();
        Cursor cursor = resolver.query(
                AppContentProvider.GET_FAVORITE_USER_CONTENT_URI, columns, USER_NO + "=" + userDto.getUserNo(), null, null);

        if(cursor!=null) {
            if (cursor.getCount() > 0) {
                cursor.close();
                return true;
            }
            cursor.close();
        }

        return false;
    }

    public static FavoriteUserDto isFavoriteUser(long userId){

        String[] columns = new String[] { "*"};
        ContentResolver resolver = CrewChatApplication.getInstance()
                .getApplicationContext().getContentResolver();
        Cursor cursor = resolver.query(
                AppContentProvider.GET_FAVORITE_USER_CONTENT_URI, columns, USER_NO + "=" + userId, null, null);

        if(cursor!=null) {
            if (cursor.getCount() > 0) {
                if (cursor.moveToFirst()){
                    int id  = Integer.parseInt(cursor.getString(cursor.getColumnIndex(ID)));
                    int groupUserNo = Integer.parseInt(cursor.getString(cursor.getColumnIndex(GROUP_USER_NO)));
                    int regUserNo = Integer.parseInt(cursor.getString(cursor.getColumnIndex(REG_USER_NO)));
                    int userNo = Integer.parseInt(cursor.getString(cursor.getColumnIndex(USER_NO)));
                    int groupNo = Integer.parseInt(cursor.getString(cursor.getColumnIndex(GROUP_NO)));
                    int sortNo = Integer.parseInt(cursor.getString(cursor.getColumnIndex(SORT_NO)));
                    String modDate = cursor.getString(cursor.getColumnIndex(MOD_DATE));
                    int isTop = Integer.parseInt(cursor.getString(cursor.getColumnIndex(IS_TOP)));

                    FavoriteUserDto user = new FavoriteUserDto();
                    user.setDbId(id);
                    user.setGroupUserNo(groupUserNo);
                    user.setRegUserNo(regUserNo);
                    user.setUserNo(userNo);
                    user.setGroupNo(groupNo);
                    user.setSortNo(sortNo);
                    user.setModDate(modDate);
                    user.setIsTop(isTop);
                    return user;
                }

                cursor.close();

            }
            cursor.close();
        }

        return null;
    }

    public static boolean deleteFavoriteUser(long groupNo, long userNo){
        try {
            ContentResolver resolver = CrewChatApplication.getInstance()
                    .getApplicationContext().getContentResolver();
            resolver.delete(AppContentProvider.GET_FAVORITE_USER_CONTENT_URI, USER_NO + "=" + userNo + " AND "+ GROUP_NO+"="+ groupNo, null);
            return true;

        } catch (Exception e) {
            // TODO: handle exception
            Utils.printLogs("Update chatroom error ####");
        }

        return false;
    }


    public static boolean deleteFavoriteUsers(long groupNo){
        try {
            ContentResolver resolver = CrewChatApplication.getInstance()
                    .getApplicationContext().getContentResolver();
            resolver.delete(AppContentProvider.GET_FAVORITE_USER_CONTENT_URI, GROUP_NO + "=" + groupNo, null);
            return true;

        } catch (Exception e) {
            // TODO: handle exception
            Utils.printLogs("Update chatroom error ####");
        }

        return false;
    }

    public static boolean updateUser(FavoriteUserDto user) {

        try {
            ContentValues values = new ContentValues();

            values.put(GROUP_USER_NO, user.getGroupUserNo());
            values.put(REG_USER_NO, user.getRegUserNo());
            values.put(SORT_NO, user.getSortNo());
            values.put(MOD_DATE, user.getModDate());
            values.put(IS_TOP, user.getIsTop());

            ContentResolver resolver = CrewChatApplication.getInstance()
                    .getApplicationContext().getContentResolver();
            resolver.update(AppContentProvider.GET_FAVORITE_USER_CONTENT_URI, values, USER_NO + " = " +user.getUserNo() + " AND "+GROUP_NO+ " = "+user.getGroupNo(), null);
            return true;

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return false;
    }
    public static boolean addUsers(List<FavoriteUserDto> users) {
        try {

            for (FavoriteUserDto user : users){

                if (!isExistByKey(user)){
                    ContentValues values = new ContentValues();
                    values.put(GROUP_USER_NO, user.getGroupUserNo());
                    values.put(REG_USER_NO, user.getRegUserNo());
                    values.put(GROUP_NO, user.getGroupNo());
                    values.put(USER_NO, user.getUserNo());
                    values.put(SORT_NO, user.getSortNo());
                    values.put(MOD_DATE, user.getModDate());
                    values.put(IS_TOP, user.getIsTop());

                    ContentResolver resolver = CrewChatApplication.getInstance()
                            .getApplicationContext().getContentResolver();
                    resolver.insert(AppContentProvider.GET_FAVORITE_USER_CONTENT_URI, values);
                }else{
                    updateUser(user);
                }
            }
            return true;

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return false;
    }


    public static boolean isExistByKey(FavoriteUserDto userDto){

        String[] columns = new String[] { "*"};
        ContentResolver resolver = CrewChatApplication.getInstance()
                .getApplicationContext().getContentResolver();
        Cursor cursor = resolver.query(
                AppContentProvider.GET_FAVORITE_USER_CONTENT_URI, columns, USER_NO + "=" + userDto.getUserNo()+" AND "+ GROUP_NO + "="+ userDto.getGroupNo(), null, null);
        if(cursor!=null) {
            if (cursor.getCount() > 0) {
                cursor.close();
                return true;
            }
            cursor.close();
        }

        return false;
    }

    public static boolean addFavoriteUser(FavoriteUserDto user) {
        try {
            if (!isExistByKey(user)) {
                ContentValues values = new ContentValues();
                values.put(GROUP_USER_NO, user.getGroupUserNo());
                values.put(REG_USER_NO, user.getRegUserNo());
                values.put(GROUP_NO, user.getGroupNo());
                values.put(USER_NO, user.getUserNo());
                values.put(SORT_NO, user.getSortNo());
                values.put(MOD_DATE, user.getModDate());
                values.put(IS_TOP, user.getIsTop());

                ContentResolver resolver = CrewChatApplication.getInstance()
                        .getApplicationContext().getContentResolver();
                resolver.insert(AppContentProvider.GET_FAVORITE_USER_CONTENT_URI, values);
                return true;
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return false;
    }

    public static boolean clearFavorites() {
        try {

            ContentResolver resolver = CrewChatApplication.getInstance()
                    .getApplicationContext().getContentResolver();
            resolver.delete(AppContentProvider.GET_FAVORITE_USER_CONTENT_URI, null,null);
            return true;
        } catch (Exception e) {
            // TODO: handle exception
        }
        return false;
    }
}
