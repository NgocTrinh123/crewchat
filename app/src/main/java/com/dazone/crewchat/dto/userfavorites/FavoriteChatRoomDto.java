package com.dazone.crewchat.dto.userfavorites;

/**
 * Created by Admin on 7/1/2016.
 */
public class FavoriteChatRoomDto {
    private long FavoriteChatRoomNo;
    private long RegUserNo;
    private long RoomNo;
    private String ModDate;

    @Override
    public String toString() {
        return "FavoriteChatRoomDto{" +
                "FavoriteChatRoomNo=" + FavoriteChatRoomNo +
                ", RegUserNo=" + RegUserNo +
                ", RoomNo=" + RoomNo +
                ", ModDate='" + ModDate + '\'' +
                '}';
    }

    public long getFavoriteChatRoomNo() {
        return FavoriteChatRoomNo;
    }

    public void setFavoriteChatRoomNo(long favoriteChatRoomNo) {
        FavoriteChatRoomNo = favoriteChatRoomNo;
    }

    public long getRegUserNo() {
        return RegUserNo;
    }

    public void setRegUserNo(long regUserNo) {
        RegUserNo = regUserNo;
    }

    public long getRoomNo() {
        return RoomNo;
    }

    public void setRoomNo(long roomNo) {
        RoomNo = roomNo;
    }

    public String getModDate() {
        return ModDate;
    }

    public void setModDate(String modDate) {
        ModDate = modDate;
    }

}
