package com.dazone.crewchat.dto;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Dat on 4/27/2016.
 */
public class BelongDepartmentDTO implements Serializable {
    @SerializedName("DbId")
    private int DbId;

    @SerializedName("BelongNo")
    private int BelongNo;
    @SerializedName("UserNo")
    private int UserNo;
    @SerializedName("DepartNo")
    private int DepartNo;
    @SerializedName("PositionNo")
    private int PositionNo;
    @SerializedName("DutyNo")
    private int DutyNo;
    @SerializedName("IsDefault")
    private boolean IsDefault;
    @SerializedName("DepartName")
    private String DepartName;
    @SerializedName("DepartSortNo")
    private int DepartSortNo;
    @SerializedName("PositionName")
    private String PositionName;
    @SerializedName("PositionSortNo")
    private int PositionSortNo;
    @SerializedName("DutyName")
    private String DutyName;
    @SerializedName("DutySortNo")
    private int DutySortNo;

    @Override
    public String toString() {
        return "BelongDepartmentDTO{" +
                "DbId=" + DbId +
                ", UserNo=" + UserNo +
                ", DepartNo=" + DepartNo +
                ", PositionNo=" + PositionNo +
                ", DutyNo=" + DutyNo +
                ", IsDefault=" + IsDefault +
                ", DepartName='" + DepartName + '\'' +
                ", DepartSortNo=" + DepartSortNo +
                ", PositionName='" + PositionName + '\'' +
                ", PositionSortNo=" + PositionSortNo +
                ", DutyName='" + DutyName + '\'' +
                ", DutySortNo=" + DutySortNo +
                '}';
    }

    public BelongDepartmentDTO(int dbId, int belongNo, int userNo, int departNo, int positionNo, int dutyNo, boolean isDefault, String departName, int departSortNo, String positionName, int positionSortNo, String dutyName, int dutySortNo) {
        DbId = dbId;
        BelongNo = belongNo;
        UserNo = userNo;
        DepartNo = departNo;
        PositionNo = positionNo;
        DutyNo = dutyNo;
        IsDefault = isDefault;
        DepartName = departName;
        DepartSortNo = departSortNo;
        PositionName = positionName;
        PositionSortNo = positionSortNo;
        DutyName = dutyName;
        DutySortNo = dutySortNo;
    }

    public BelongDepartmentDTO(int belongNo, int userNo, int departNo, int positionNo, int dutyNo, boolean isDefault, String departName, int departSortNo, String positionName, int positionSortNo, String dutyName, int dutySortNo) {
        BelongNo = belongNo;
        UserNo = userNo;
        DepartNo = departNo;
        PositionNo = positionNo;
        DutyNo = dutyNo;
        IsDefault = isDefault;
        DepartName = departName;
        DepartSortNo = departSortNo;
        PositionName = positionName;
        PositionSortNo = positionSortNo;
        DutyName = dutyName;
        DutySortNo = dutySortNo;
    }

    public int getDbId() {
        return DbId;
    }

    public void setDbId(int dbId) {
        DbId = dbId;
    }

    public void setDefault(boolean aDefault) {
        IsDefault = aDefault;
    }

    public int getUserNo() {
        return UserNo;
    }

    public int getBelongNo() {
        return BelongNo;
    }

    public void setBelongNo(int belongNo) {
        BelongNo = belongNo;
    }

    public void setUserNo(int userNo) {
        UserNo = userNo;
    }

    public int getDepartNo() {
        return DepartNo;
    }

    public void setDepartNo(int departNo) {
        DepartNo = departNo;
    }

    public int getPositionNo() {
        return PositionNo;
    }

    public void setPositionNo(int positionNo) {
        PositionNo = positionNo;
    }

    public int getDutyNo() {
        return DutyNo;
    }

    public void setDutyNo(int dutyNo) {
        DutyNo = dutyNo;
    }

    public boolean isDefault() {
        return IsDefault;
    }

    public void setIsDefault(boolean isDefault) {
        IsDefault = isDefault;
    }

    public String getDepartName() {
        return DepartName;
    }

    public void setDepartName(String departName) {
        DepartName = departName;
    }

    public int getDepartSortNo() {
        return DepartSortNo;
    }

    public void setDepartSortNo(int departSortNo) {
        DepartSortNo = departSortNo;
    }

    public String getPositionName() {
        return PositionName;
    }

    public void setPositionName(String positionName) {
        PositionName = positionName;
    }

    public int getPositionSortNo() {
        return PositionSortNo;
    }

    public void setPositionSortNo(int positionSortNo) {
        PositionSortNo = positionSortNo;
    }

    public String getDutyName() {
        return DutyName;
    }

    public void setDutyName(String dutyName) {
        DutyName = dutyName;
    }

    public int getDutySortNo() {
        return DutySortNo;
    }

    public void setDutySortNo(int dutySortNo) {
        DutySortNo = dutySortNo;
    }
}
