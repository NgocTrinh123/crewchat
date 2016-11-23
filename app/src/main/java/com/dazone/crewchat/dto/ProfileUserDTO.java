package com.dazone.crewchat.dto;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Dat on 4/27/2016.
 */
public class ProfileUserDTO implements Serializable {
    @SerializedName("UserNo")
    private long UserNo;
    @SerializedName("UserID")
    private String UserID;
    @SerializedName("Name")
    private String Name;
    @SerializedName("MailAddress")
    private String MailAddress;
    @SerializedName("Sex")
    private int Sex;
    @SerializedName("CellPhone")
    private String CellPhone;
    @SerializedName("CompanyPhone")
    private String CompanyPhone;
    @SerializedName("ExtensionNumber")
    private String ExtensionNumber;
    @SerializedName("EntranceDate")
    private String EntranceDate;
    @SerializedName("BirthDate")
    private String BirthDate;
    @SerializedName("UserPhoto")
    private boolean UserPhoto;
    @SerializedName("Photo")
    private String Photo;
    @SerializedName("TimeZone")
    private String TimeZone;
    @SerializedName("Enabled")
    private boolean Enabled;
    @SerializedName("IsVirtual")
    private boolean IsVirtual;
    @SerializedName("Belongs")
    private ArrayList<BelongDepartmentDTO> Belongs = new ArrayList<>();
    @SerializedName("DepartNo")
    private int DepartNo;
    @SerializedName("DepartName")
    private String DepartName;
    @SerializedName("DepartSortNo")
    private int DepartSortNo;
    @SerializedName("PositionNo")
    private int PositionNo;
    @SerializedName("PositionName")
    private String PositionName;
    @SerializedName("PositionSortNo")
    private int PositionSortNo;
    @SerializedName("DutyNo")
    private int DutyNo;
    @SerializedName("DutyName")
    private String DutyName;
    @SerializedName("DutySortNo")
    private int DutySortNo;
    @SerializedName("NameAndUserID")
    private String NameAndUserID;
    @SerializedName("AvatarUrl")
    private String AvatarUrl;

    @Override
    public String toString() {
        return "ProfileUserDTO{" +
                "UserNo=" + UserNo +
                ", UserID=" + UserID +
                ", Name=" + Name +
                ", MailAddress=" + MailAddress +
                ", Sex=" + Sex +
                ", CellPhone=" + CellPhone +
                ", CompanyPhone=" + CompanyPhone +
                ", ExtensionNumber=" + ExtensionNumber +
                ", EntranceDate=" + EntranceDate +
                ", BirthDate=" + BirthDate +
                ", UserPhoto=" + UserPhoto +
                ", Photo=" + Photo +
                ", TimeZone=" + TimeZone +
                ", Enabled=" + Enabled +
                ", IsVirtual=" + IsVirtual +
                ", Belongs=" + Belongs +
                ", DepartNo=" + DepartNo +
                ", DepartName='" + DepartName + '\'' +
                ", DepartSortNo=" + DepartSortNo +
                ", PositionNo=" + PositionNo +
                ", PositionName='" + PositionName + '\'' +
                ", PositionSortNo=" + PositionSortNo +
                ", DutyNo=" + DutyNo +
                ", DutyName='" + DutyName + '\'' +
                ", DutySortNo=" + DutySortNo +
                ", NameAndUserID='" + NameAndUserID + '\'' +
                ", AvatarUrl='" + AvatarUrl + '\'' +
                '}';
    }


    public String getPositionName() {
        return PositionName;
    }

    public void setPositionName(String positionName) {
        PositionName = positionName;
    }

    public long getUserNo() {
        return UserNo;
    }

    public void setUserNo(long userNo) {
        UserNo = userNo;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getMailAddress() {
        return MailAddress;
    }

    public void setMailAddress(String mailAddress) {
        MailAddress = mailAddress;
    }

    public int getSex() {
        return Sex;
    }

    public void setSex(int sex) {
        Sex = sex;
    }

    public String getCellPhone() {
        return CellPhone;
    }

    public void setCellPhone(String cellPhone) {
        CellPhone = cellPhone;
    }

    public String getCompanyPhone() {
        return CompanyPhone;
    }

    public void setCompanyPhone(String companyPhone) {
        CompanyPhone = companyPhone;
    }

    public String getExtensionNumber() {
        return ExtensionNumber;
    }

    public void setExtensionNumber(String extensionNumber) {
        ExtensionNumber = extensionNumber;
    }

    public String getEntranceDate() {
        return EntranceDate;
    }

    public void setEntranceDate(String entranceDate) {
        EntranceDate = entranceDate;
    }

    public String getBirthDate() {
        return BirthDate;
    }

    public void setBirthDate(String birthDate) {
        BirthDate = birthDate;
    }

    public boolean isUserPhoto() {
        return UserPhoto;
    }

    public void setUserPhoto(boolean userPhoto) {
        UserPhoto = userPhoto;
    }

    public String getPhoto() {
        return Photo;
    }

    public void setPhoto(String photo) {
        Photo = photo;
    }

    public String getTimeZone() {
        return TimeZone;
    }

    public void setTimeZone(String timeZone) {
        TimeZone = timeZone;
    }

    public boolean isEnabled() {
        return Enabled;
    }

    public void setEnabled(boolean enabled) {
        Enabled = enabled;
    }

    public boolean isVirtual() {
        return IsVirtual;
    }

    public void setIsVirtual(boolean isVirtual) {
        IsVirtual = isVirtual;
    }

    public ArrayList<BelongDepartmentDTO> getBelongs() {
        return Belongs;
    }

    public void setBelongs(ArrayList<BelongDepartmentDTO> belongs) {
        Belongs = belongs;
    }

    public int getDepartNo() {
        return DepartNo;
    }

    public void setDepartNo(int departNo) {
        DepartNo = departNo;
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

    public int getPositionNo() {
        return PositionNo;
    }

    public void setPositionNo(int positionNo) {
        PositionNo = positionNo;
    }

    public int getPositionSortNo() {
        return PositionSortNo;
    }

    public void setPositionSortNo(int positionSortNo) {
        PositionSortNo = positionSortNo;
    }

    public int getDutyNo() {
        return DutyNo;
    }

    public void setDutyNo(int dutyNo) {
        DutyNo = dutyNo;
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

    public String getNameAndUserID() {
        return NameAndUserID;
    }

    public void setNameAndUserID(String nameAndUserID) {
        NameAndUserID = nameAndUserID;
    }

    public String getAvatarUrl() {
        return AvatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        AvatarUrl = avatarUrl;
    }
}
