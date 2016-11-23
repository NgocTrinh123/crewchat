package com.dazone.crewchat.dto;

import android.content.ClipData;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Admin on 5/26/2016.
 */
public class StatusDto implements Parcelable {

    private ArrayList<StatusItemDto> items;

    public StatusDto(){

    };

    protected StatusDto(Parcel in) {
        readFromParcel(in);
    }

    public ArrayList<StatusItemDto> getItems() {
        return items;
    }

    public void setItems(ArrayList<StatusItemDto> items) {
        this.items = items;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        Bundle b = new Bundle();
        b.putParcelableArrayList("items", items);
        dest.writeBundle(b);
    }

    private void readFromParcel(Parcel in) {
        Bundle b = in.readBundle(StatusItemDto.class.getClassLoader());
        items = b.getParcelableArrayList("items");
    }


    public static final Creator<StatusDto> CREATOR = new Creator<StatusDto>() {
        @Override
        public StatusDto createFromParcel(Parcel in) {
            return new StatusDto(in);
        }

        @Override
        public StatusDto[] newArray(int size) {
            return new StatusDto[size];
        }
    };
}
