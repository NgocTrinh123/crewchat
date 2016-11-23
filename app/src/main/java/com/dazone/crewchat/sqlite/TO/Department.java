package com.dazone.crewchat.sqlite.TO;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Dat on 4/12/2016.
 */
public class Department implements Parcelable {
    private String department_id;
    private String department_name;
    private String department_user_no;
    private String department_is_hide;

    public Department() {

    }

    public String getDepartment_id() {
        return department_id;
    }

    public void setDepartment_id(String department_id) {
        this.department_id = department_id;
    }

    public String getDepartment_name() {
        return department_name;
    }

    public void setDepartment_name(String department_name) {
        this.department_name = department_name;
    }

    public String getDepartment_user_no() {
        return department_user_no;
    }

    public void setDepartment_user_no(String department_user_no) {
        this.department_user_no = department_user_no;
    }

    public String getDepartment_is_hide() {
        return department_is_hide;
    }

    public void setDepartment_is_hide(String department_is_hide) {
        this.department_is_hide = department_is_hide;
    }

    public static Creator<Department> getCREATOR() {
        return CREATOR;
    }

    public Department(Parcel in) {
        department_id = in.readString();
        department_name = in.readString();
        department_user_no = in.readString();
        department_is_hide = in.readString();

    }

    public static final Creator<Department> CREATOR = new Creator<Department>() {
        @Override
        public Department createFromParcel(Parcel in) {
            return new Department(in);
        }

        @Override
        public Department[] newArray(int size) {
            return new Department[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(department_id);
        dest.writeString(department_name);
        dest.writeString(department_user_no);
        dest.writeString(department_is_hide);
    }
}
