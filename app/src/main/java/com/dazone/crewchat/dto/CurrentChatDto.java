package com.dazone.crewchat.dto;

import java.util.Date;

/**
 * Created by david on 12/23/15.
 */
public class CurrentChatDto extends DataDto implements DrawImageItem{
    private String userName ="";
    private long lastDate = 0;
    private int status = 0;
    private String avatar = "";
    private int color = -1;

    @Override
    public String toString() {
        return "CurrentChatDto{" +
                "userName='" + userName + '\'' +
                ", lastDate=" + lastDate +
                ", status=" + status +
                ", avatar='" + avatar + '\'' +
                ", color=" + color +
                '}';
    }

    public CurrentChatDto(String userName, String context) {
        this.userName = userName;
        this.lastDate = new Date().getTime();
        this.content = context;
    }

    public CurrentChatDto() {
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setLastDate(long lastDate) {
        this.lastDate = lastDate;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getUserName() {
        return userName;
    }

    public long getLastDate() {
        return lastDate;
    }

    public int getStatus() {
        return status;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    @Override
    public String getImageLink() {
        return avatar;
    }

    @Override
    public String getImageTitle() {
        return userName;
    }
    
}
