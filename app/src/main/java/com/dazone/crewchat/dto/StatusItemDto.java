package com.dazone.crewchat.dto;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by Admin on 5/26/2016.
 */
public class StatusItemDto implements Parcelable{
    String userID;
    int status;

    protected StatusItemDto(Parcel in) {
       readFromParcel(in);
    }

    public StatusItemDto() { ; };


    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userID);
        dest.writeInt(status);
    }

    private void readFromParcel(Parcel in) {
        userID = in.readString();
        status = in.readInt();
    }

    public static final Creator<StatusItemDto> CREATOR = new Creator<StatusItemDto>() {
        @Override
        public StatusItemDto createFromParcel(Parcel in) {
            return new StatusItemDto(in);
        }

        @Override
        public StatusItemDto[] newArray(int size) {
            return new StatusItemDto[size];
        }
    };
}
