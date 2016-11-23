package com.dazone.crewchat.dto;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Dat on 5/4/2016.
 */
public class ChatRoomDTO implements Serializable {
    @SerializedName("Id")
    private int Id;
    @SerializedName("RoomNo")
    private long RoomNo;
    @SerializedName("MakeUserNo")
    private int MakeUserNo;
    @SerializedName("ModDate")
    private String ModDate;
    @SerializedName("IsOne")
    private boolean IsOne;
    @SerializedName("RoomTitle")
    private String RoomTitle;
    @SerializedName("LastedMsg")
    private String LastedMsg;
    @SerializedName("LastedMsgDate")
    private String LastedMsgDate;
    @SerializedName("UnReadCount")
    private int UnReadCount;
    @SerializedName("UserNos")
    private ArrayList<Integer> UserNos;

    @Override
    public String toString() {
        return "ChatRoomDTO{" +
                "RoomNo=" + RoomNo +
                ", MakeUserNo=" + MakeUserNo +
                ", ModDate='" + ModDate + '\'' +
                ", IsOne=" + IsOne +
                ", RoomTitle='" + RoomTitle + '\'' +
                ", LastedMsg='" + LastedMsg + '\'' +
                ", LastedMsgDate='" + LastedMsgDate + '\'' +
                ", UnReadCount=" + UnReadCount +
                ", UserNos=" + UserNos +
                '}';
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public long getRoomNo() {
        return RoomNo;
    }

    public void setRoomNo(long roomNo) {
        RoomNo = roomNo;
    }

    public int getMakeUserNo() {
        return MakeUserNo;
    }

    public void setMakeUserNo(int makeUserNo) {
        MakeUserNo = makeUserNo;
    }

    public String getModDate() {
        return ModDate;
    }

    public void setModDate(String modDate) {
        ModDate = modDate;
    }

    public boolean isOne() {
        return IsOne;
    }

    public void setIsOne(boolean isOne) {
        IsOne = isOne;
    }

    public String getRoomTitle() {
        return RoomTitle;
    }

    public void setRoomTitle(String roomTitle) {
        RoomTitle = roomTitle;
    }

    public String getLastedMsg() {
        return LastedMsg;
    }

    public void setLastedMsg(String lastedMsg) {
        LastedMsg = lastedMsg;
    }

    public String getLastedMsgDate() {
        return LastedMsgDate;
    }

    public void setLastedMsgDate(String lastedMsgDate) {
        LastedMsgDate = lastedMsgDate;
    }

    public int getUnReadCount() {
        return UnReadCount;
    }

    public void setUnReadCount(int unReadCount) {
        UnReadCount = unReadCount;
    }

    public ArrayList<Integer> getUserNos() {
        return UserNos;
    }

    public void setUserNos(ArrayList<Integer> userNos) {
        UserNos = userNos;
    }
}
