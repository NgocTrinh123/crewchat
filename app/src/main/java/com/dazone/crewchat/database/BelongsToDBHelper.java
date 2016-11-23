package com.dazone.crewchat.database;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import com.dazone.crewchat.dto.BelongDepartmentDTO;
import com.dazone.crewchat.utils.CrewChatApplication;
import com.dazone.crewchat.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class BelongsToDBHelper {
    public static final String TABLE_NAME = "BelongToTbl";
    public static final String DB_ID = "db_id";

    public static final String BELONG_NO = "belong_no";
    public static final String USER_NO = "user_no";

    public static final String IS_DEFAULT = "is_default";

    public static final String DUTY_NO = "duty_no";
    public static final String DUTY_NAME = "duty_name";
    public static final String DUTY_SORT_NO = "duty_sort_no";

    public static final String DEPART_NO = "depart_no";
    public static final String DEPART_NAME = "depart_name";
    public static final String DEPART_SORT_NO = "depart_sort_no";

    public static final String POSITION_NO = "position_no";
    public static final String POSITION_NAME = "position_name";
    public static final String POSITION_SORT_NO = "position_sort_no";



    public static final String SQL_EXCUTE = "CREATE TABLE " + TABLE_NAME + "("
            + DB_ID + " integer primary key autoincrement not null,"
            + BELONG_NO +" integer, "
            + USER_NO + " integer, "
            + IS_DEFAULT + " integer, "
            + DUTY_NO + " integer, "
            + DUTY_NAME + " text, "
            + DUTY_SORT_NO + " integer, "
            + DEPART_NO + " integer, "
            + DEPART_NAME + " text, "
            + DEPART_SORT_NO + " integer, "
            + POSITION_NO + " integer, "
            + POSITION_SORT_NO + " integer, "
            + POSITION_NAME + " text );";


    // this version just get one user
    public synchronized static ArrayList<BelongDepartmentDTO> getBelongs(long fromUserNo)
    {
        String[] columns = new String[] { "*"};
        ContentResolver resolver = CrewChatApplication.getInstance()
                .getApplicationContext().getContentResolver();
        Cursor cursor = resolver.query(
                AppContentProvider.GET_BELONG_TO_CONTENT_URI, columns, USER_NO + " = "+fromUserNo,
                null, null);

        ArrayList<BelongDepartmentDTO> belongs = new ArrayList<>();

        if(cursor!=null){
            if(cursor.getCount()>0){
                try {
                    while (cursor.moveToNext()){
                        int id  = Integer.parseInt(cursor.getString(cursor.getColumnIndex(DB_ID)));

                        int belongNo = Integer.parseInt(cursor.getString(cursor.getColumnIndex(BELONG_NO)));
                        int userNo = Integer.parseInt(cursor.getString(cursor.getColumnIndex(USER_NO)));

                        boolean isDefault = Integer.parseInt(cursor.getString(cursor.getColumnIndex(IS_DEFAULT))) == 1;

                        int dutyNo = Integer.parseInt(cursor.getString(cursor.getColumnIndex(DUTY_NO)));
                        String dutyName = cursor.getString(cursor.getColumnIndex(DUTY_NAME));
                        int dutySortNo = Integer.parseInt(cursor.getString(cursor.getColumnIndex(DUTY_NO)));

                        int departNo = Integer.parseInt(cursor.getString(cursor.getColumnIndex(DEPART_NO)));
                        String departName = cursor.getString(cursor.getColumnIndex(DEPART_NAME));
                        int departSortNo = Integer.parseInt(cursor.getString(cursor.getColumnIndex(DEPART_SORT_NO)));

                        int positionNo = Integer.parseInt(cursor.getString(cursor.getColumnIndex(POSITION_NO)));
                        String positionName = cursor.getString(cursor.getColumnIndex(POSITION_NAME));
                        int positionSortNo = Integer.parseInt(cursor.getString(cursor.getColumnIndex(POSITION_SORT_NO)));

                        BelongDepartmentDTO belong = new BelongDepartmentDTO(
                                id,
                                belongNo,
                                userNo,
                                departNo,
                                positionNo,
                                dutyNo,
                                isDefault,
                                departName,
                                departSortNo,
                                positionName,
                                positionSortNo,
                                dutyName,
                                dutySortNo
                        );

                        belongs.add(belong);
                    }

                }finally {
                    cursor.close();
                }

            }
            cursor.close();
        }
        return belongs;
    }

    public synchronized static boolean isExist(BelongDepartmentDTO belong){

        String[] columns = new String[] { "*"};
        ContentResolver resolver = CrewChatApplication.getInstance()
                .getApplicationContext().getContentResolver();
        Cursor cursor = resolver.query(AppContentProvider.GET_BELONG_TO_CONTENT_URI, columns, BELONG_NO + " = " + belong.getBelongNo(), null, null);

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


    public static boolean updateDepart(BelongDepartmentDTO depart){
        try {
            ContentResolver resolver = CrewChatApplication.getInstance()
                    .getApplicationContext().getContentResolver();
            ContentValues values = new ContentValues();

            values.put(BELONG_NO, depart.getBelongNo());
            values.put(USER_NO, depart.getUserNo());
            values.put(DEPART_NO, depart.getDepartNo());
            values.put(POSITION_NO, depart.getPositionNo());
            values.put(DUTY_NO, depart.getDutyNo());

            int isDefault = 0;
            if (depart.isDefault()) { isDefault = 1;}

            values.put(IS_DEFAULT, isDefault);

            values.put(DEPART_NAME, depart.getDepartName());
            values.put(DEPART_SORT_NO, depart.getDepartSortNo());

            values.put(POSITION_NAME, depart.getPositionName());
            values.put(POSITION_SORT_NO, depart.getPositionNo());

            values.put(DUTY_NAME, depart.getDutyName());
            values.put(DUTY_SORT_NO, depart.getDutySortNo());


            resolver.update(AppContentProvider.GET_BELONG_TO_CONTENT_URI, values, BELONG_NO + "=" + depart.getBelongNo(), null);

            return true;

        } catch (Exception e) {
            // TODO: handle exception
            Utils.printLogs("Catch to update user info ###");
        }

        return false;
    }

    public static boolean addDepartment(List<BelongDepartmentDTO> belongs) {
        try {

            for (BelongDepartmentDTO belong : belongs){

                if (!isExist(belong)) {
                    ContentValues values = new ContentValues();

                    values.put(BELONG_NO, belong.getBelongNo());
                    values.put(USER_NO, belong.getUserNo());
                    values.put(DEPART_NO, belong.getDepartNo());
                    values.put(POSITION_NO, belong.getPositionNo());
                    values.put(DUTY_NO, belong.getDutyNo());

                    int isDefault = 0;
                    if (belong.isDefault()) { isDefault = 1;}

                    values.put(IS_DEFAULT, isDefault);

                    values.put(DEPART_NAME, belong.getDepartName());
                    values.put(DEPART_SORT_NO, belong.getDepartSortNo());

                    values.put(POSITION_NAME, belong.getPositionName());
                    values.put(POSITION_SORT_NO, belong.getPositionNo());

                    values.put(DUTY_NAME, belong.getDutyName());
                    values.put(DUTY_SORT_NO, belong.getDutySortNo());

                    ContentResolver resolver = CrewChatApplication.getInstance()
                            .getApplicationContext().getContentResolver();
                    resolver.insert(AppContentProvider.GET_BELONG_TO_CONTENT_URI, values);
                } else {
                    updateDepart(belong);
                }
            }
            return true;

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return false;
    }

    public static boolean addDepartment(BelongDepartmentDTO belong) {
        try {
            ContentValues values = new ContentValues();

            values.put(BELONG_NO, belong.getBelongNo());
            values.put(USER_NO, belong.getUserNo());
            values.put(DEPART_NO, belong.getDepartNo());
            values.put(POSITION_NO, belong.getPositionNo());
            values.put(DUTY_NO, belong.getDutyNo());

            int isDefault = 0;
            if (belong.isDefault()) { isDefault = 1;}

            values.put(IS_DEFAULT, isDefault);

            values.put(DEPART_NAME, belong.getDepartName());
            values.put(DEPART_SORT_NO, belong.getDepartSortNo());
            values.put(POSITION_NAME, belong.getPositionName());
            values.put(POSITION_SORT_NO, belong.getPositionNo());
            values.put(DUTY_NAME, belong.getDutyName());
            values.put(DUTY_SORT_NO, belong.getDutySortNo());

                ContentResolver resolver = CrewChatApplication.getInstance()
                        .getApplicationContext().getContentResolver();
                resolver.insert(AppContentProvider.GET_BELONG_TO_CONTENT_URI, values);
            return true;

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return false;
    }

    public static boolean clearBelong() {
        try {

            ContentResolver resolver = CrewChatApplication.getInstance()
                    .getApplicationContext().getContentResolver();
            resolver.delete(AppContentProvider.GET_BELONG_TO_CONTENT_URI, null,null);
            return true;
        } catch (Exception e) {
            // TODO: handle exception
        }
        return false;
    }
}
