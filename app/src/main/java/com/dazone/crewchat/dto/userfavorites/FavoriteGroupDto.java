package com.dazone.crewchat.dto.userfavorites;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Admin on 6/28/2016.
 */
public class FavoriteGroupDto extends BaseFavoriteDto implements Parcelable{
    private int dbId;

    private String Name;
    private int SortNo;
    private String ModDate;
    private ArrayList<FavoriteUserDto> UserList;
    private int GroupNo;
    private int UserNo;

    public FavoriteGroupDto(){

    }

    public FavoriteGroupDto(String name, int sortNo) {
        Name = name;
        SortNo = sortNo;
    }

    protected FavoriteGroupDto(Parcel in) {
        dbId = in.readInt();
        Name = in.readString();
        SortNo = in.readInt();
        ModDate = in.readString();
        GroupNo = in.readInt();
        UserNo = in.readInt();
    }

    public static final Creator<FavoriteGroupDto> CREATOR = new Creator<FavoriteGroupDto>() {
        @Override
        public FavoriteGroupDto createFromParcel(Parcel in) {
            return new FavoriteGroupDto(in);
        }

        @Override
        public FavoriteGroupDto[] newArray(int size) {
            return new FavoriteGroupDto[size];
        }
    };

    @Override
    public String toString() {
        return "FavoriteGroupDto{" +
                "dbId=" + dbId +
                ", Name='" + Name + '\'' +
                ", SortNo=" + SortNo +
                ", ModDate='" + ModDate + '\'' +
                ", UserList=" + UserList +
                ", GroupNo=" + GroupNo +
                ", UserNo=" + UserNo +
                '}';
    }

    public int getDbId() {
        return dbId;
    }

    public void setDbId(int dbId) {
        this.dbId = dbId;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public int getSortNo() {
        return SortNo;
    }

    public void setSortNo(int sortNo) {
        SortNo = sortNo;
    }

    public String getModDate() {
        return ModDate;
    }

    public void setModDate(String modDate) {
        ModDate = modDate;
    }

    public ArrayList<FavoriteUserDto> getUserList() {
        return UserList;
    }

    public void setUserList(ArrayList<FavoriteUserDto> userList) {
        UserList = userList;
    }

    public int getGroupNo() {
        return GroupNo;
    }

    public void setGroupNo(int groupNo) {
        GroupNo = groupNo;
    }

    public int getUserNo() {
        return UserNo;
    }

    public void setUserNo(int userNo) {
        UserNo = userNo;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(dbId);
        dest.writeString(Name);
        dest.writeInt(SortNo);
        dest.writeString(ModDate);
        dest.writeInt(GroupNo);
        dest.writeInt(UserNo);
    }
}
