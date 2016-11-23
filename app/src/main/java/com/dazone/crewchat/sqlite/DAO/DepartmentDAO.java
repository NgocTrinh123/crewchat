package com.dazone.crewchat.sqlite.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.dazone.crewchat.sqlite.DataBaseHelper;
import com.dazone.crewchat.sqlite.DataBaseDAO;
import com.dazone.crewchat.sqlite.TO.Department;

/**
 * Created by Dat on 4/12/2016.
 */
public class DepartmentDAO extends DataBaseDAO {
    public DepartmentDAO(Context context) {
        super(context);
    }

    public long insert(Department department) {
        ContentValues values = new ContentValues();
        values.put(DataBaseHelper.KEY_DEPARTMENT_ID, department.getDepartment_id());
        values.put(DataBaseHelper.KEY_DEPARTMENT_NAME, department.getDepartment_name());
        values.put(DataBaseHelper.KEY_DEPARTMENT_USER_NO, department.getDepartment_user_no());
        values.put(DataBaseHelper.KEY_DEPARTMENT_IS_HIDE, department.getDepartment_is_hide());

        return database.insert(DataBaseHelper.TABLE_DEPARTMENT, null, values);
    }

    /**
     * Get DEPARTMENT by USER NO and DEPARTMENT ID
     */
    public Department getDepartmentByUserNoAndID(long departmentID, long userNo) {
        Department department = new Department();

        String QUERY =
                String.format("SELECT  * FROM %s " +
                                "WHERE %s=%s " +
                                "AND " +
                                "%s=%s",
                        DataBaseHelper.TABLE_DEPARTMENT,
                        DataBaseHelper.KEY_DEPARTMENT_ID,
                        departmentID,
                        DataBaseHelper.KEY_DEPARTMENT_USER_NO,
                        userNo
                );

        Cursor cursor = database.rawQuery(QUERY, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    department.setDepartment_is_hide(cursor.getString(cursor.getColumnIndex(DataBaseHelper.KEY_DEPARTMENT_IS_HIDE)));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return department;
    }

    public long updateDepartmentByUserNoAndID(long departmentID, long userNo, int isHide) {
        ContentValues values = new ContentValues();
        values.put(DataBaseHelper.KEY_DEPARTMENT_IS_HIDE, isHide);

        long result = database.update(DataBaseHelper.TABLE_DEPARTMENT, values,
                DataBaseHelper.KEY_DEPARTMENT_ID + "=? AND " + DataBaseHelper.KEY_DEPARTMENT_USER_NO + "=?",
                new String[]{String.valueOf(departmentID), String.valueOf(userNo)});
        return result;

    }
}
