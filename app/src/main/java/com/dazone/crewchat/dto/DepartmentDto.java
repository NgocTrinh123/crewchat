package com.dazone.crewchat.dto;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Admin on 6/9/2016.
 */
public class DepartmentDto implements Serializable {
    private int dbId;

    private long DepartNo;
    private long ModUserNo;
    private String ModDate;
    private long ParentNo;
    private String Name_Default;
    private String Name_EN;
    private String Name;
    private String ShortName;
    private int SortNo;
    private boolean Enabled;
    private ArrayList<DepartmentDto> ChildDepartments;

    public int getDbId() {
        return dbId;
    }

    public void setDbId(int dbId) {
        this.dbId = dbId;
    }

    public long getDepartNo() {
        return DepartNo;
    }

    public void setDepartNo(long departNo) {
        DepartNo = departNo;
    }

    public long getModUserNo() {
        return ModUserNo;
    }

    public void setModUserNo(long modUserNo) {
        ModUserNo = modUserNo;
    }

    public String getModDate() {
        return ModDate;
    }

    public void setModDate(String modDate) {
        ModDate = modDate;
    }

    public long getParentNo() {
        return ParentNo;
    }

    public void setParentNo(long parentNo) {
        ParentNo = parentNo;
    }

    public String getName_Default() {
        return Name_Default;
    }

    public void setName_Default(String name_Default) {
        Name_Default = name_Default;
    }

    public String getName_EN() {
        return Name_EN;
    }

    public void setName_EN(String name_EN) {
        Name_EN = name_EN;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getShortName() {
        return ShortName;
    }

    public void setShortName(String shortName) {
        ShortName = shortName;
    }

    public int getSortNo() {
        return SortNo;
    }

    public void setSortNo(int sortNo) {
        SortNo = sortNo;
    }

    public boolean isEnabled() {
        return Enabled;
    }

    public void setEnabled(boolean enabled) {
        Enabled = enabled;
    }

    public ArrayList<DepartmentDto> getChildDepartments() {
        return ChildDepartments;
    }

    public void setChildDepartments(ArrayList<DepartmentDto> childDepartments) {
        ChildDepartments = childDepartments;
    }
}
