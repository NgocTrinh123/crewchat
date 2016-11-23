package com.dazone.crewchat.dto.userfavorites;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Admin on 6/28/2016.
 */
public class FavoriteUserDto extends BaseFavoriteDto implements Parcelable{
    private int dbId;

    private int SortNo;
    private String ModDate;
    private int GroupUserNo;
    private int GroupNo;
    private int RegUserNo;
    private int UserNo;

    private int isTop = 0;

    public FavoriteUserDto(){

    }

    protected FavoriteUserDto(Parcel in) {
        dbId = in.readInt();
        SortNo = in.readInt();
        ModDate = in.readString();
        GroupUserNo = in.readInt();
        GroupNo = in.readInt();
        RegUserNo = in.readInt();
        UserNo = in.readInt();
        isTop = in.readInt();
    }

    public static final Creator<FavoriteUserDto> CREATOR = new Creator<FavoriteUserDto>() {
        @Override
        public FavoriteUserDto createFromParcel(Parcel in) {
            return new FavoriteUserDto(in);
        }

        @Override
        public FavoriteUserDto[] newArray(int size) {
            return new FavoriteUserDto[size];
        }
    };

    @Override
    public String toString() {
        return "FavoriteUserDto{" +
                "dbId=" + dbId +
                ", SortNo=" + SortNo +
                ", ModDate='" + ModDate + '\'' +
                ", GroupUserNo=" + GroupUserNo +
                ", GroupNo=" + GroupNo +
                ", RegUserNo=" + RegUserNo +
                ", UserNo=" + UserNo +
                '}';
    }

    public int getDbId() {
        return dbId;
    }

    public void setDbId(int dbId) {
        this.dbId = dbId;
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

    public int getGroupUserNo() {
        return GroupUserNo;
    }

    public void setGroupUserNo(int groupUserNo) {
        GroupUserNo = groupUserNo;
    }

    public int getGroupNo() {
        return GroupNo;
    }

    public void setGroupNo(int groupNo) {
        GroupNo = groupNo;
    }

    public int getRegUserNo() {
        return RegUserNo;
    }

    public void setRegUserNo(int regUserNo) {
        RegUserNo = regUserNo;
    }

    public int getUserNo() {
        return UserNo;
    }

    public void setUserNo(int userNo) {
        UserNo = userNo;
    }

    public int getIsTop() {
        return isTop;
    }

    public void setIsTop(int isTop) {
        this.isTop = isTop;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(dbId);
        dest.writeInt(SortNo);
        dest.writeString(ModDate);
        dest.writeInt(GroupUserNo);
        dest.writeInt(GroupNo);
        dest.writeInt(RegUserNo);
        dest.writeInt(UserNo);
        dest.writeInt(isTop);
    }
}
