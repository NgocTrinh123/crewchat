package com.dazone.crewchat.dto;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Dat on 4/26/2016.
 */
public class MessageUnreadCountDTO implements Serializable {

    @SerializedName("MessageNo")
    private long MessageNo;
    @SerializedName("UnreadCount")
    private int UnreadCount;

    @Override
    public String toString() {
        return "MessageUnreadCountDTO{" +
                "MessageNo=" + MessageNo +
                ", UnreadCount=" + UnreadCount +
                '}';
    }

    public long getMessageNo() {
        return MessageNo;
    }

    public void setMessageNo(long messageNo) {
        MessageNo = messageNo;
    }

    public int getUnreadCount() {
        return UnreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        UnreadCount = unreadCount;
    }
}
