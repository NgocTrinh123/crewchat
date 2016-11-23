package com.dazone.crewchat.dto;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Admin on 5/20/2016.
 */
public class UserDetailDto implements Serializable {
    private int UserNo;
    private int ModUserNo;
    private String ModDate;
    private String UserID;
    private String Password;
    private String PasswordChangeDate;
    private String Name_Default;
    private String Name_EN;
    private String Name;
    private String MailAddress;
    private int Sex;
    private String CellPhone;
    private String CompanyPhone;
    private String ExtensionNumber;
    private String EntranceDate;
    private String BirthDate;
    private boolean UserPhoto;
    private String Photo;
    private String TimeZone;
    private boolean Enabled;
    private boolean IsVirtual;
    private ArrayList<BelongDepartmentDTO> Belongs;
    private int DepartNo;
    private String DepartName;
    private int DepartSortNo;
    private int PositionNo;
    private String PositionName;
    private int PositionSortNo;
    private int DutyNo;
    private String DutyName;
    private int DutySortNo;
    private String NameAndUserID;
    private String AvatarUrl;

    public int getUserNo() {
        return UserNo;
    }

    public void setUserNo(int userNo) {
        UserNo = userNo;
    }

    public int getModUserNo() {
        return ModUserNo;
    }

    public void setModUserNo(int modUserNo) {
        ModUserNo = modUserNo;
    }

    public String getModDate() {
        return ModDate;
    }

    public void setModDate(String modDate) {
        ModDate = modDate;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getPasswordChangeDate() {
        return PasswordChangeDate;
    }

    public void setPasswordChangeDate(String passwordChangeDate) {
        PasswordChangeDate = passwordChangeDate;
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

    public void setVirtual(boolean virtual) {
        IsVirtual = virtual;
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
