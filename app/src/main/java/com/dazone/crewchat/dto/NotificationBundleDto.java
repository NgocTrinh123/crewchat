package com.dazone.crewchat.dto;

/**
 * Created by Admin on 6/21/2016.
 */
public class NotificationBundleDto {
    private int AttachNo;
    private int UnreadTotalCount;
    private long RoomNo;
    private String Message;
    private long MessageNo;
    private String AttachFileName;
    private int WriteUserNo;
    private int AttachFileType;
    private String AttachFilePath;
    private int AttachFileSize;
    private boolean ShowNotification;

    public int getAttachNo() {
        return AttachNo;
    }

    public void setAttachNo(int attachNo) {
        AttachNo = attachNo;
    }

    public int getUnreadTotalCount() {
        return UnreadTotalCount;
    }

    public void setUnreadTotalCount(int unreadTotalCount) {
        UnreadTotalCount = unreadTotalCount;
    }

    public long getRoomNo() {
        return RoomNo;
    }

    public void setRoomNo(long roomNo) {
        RoomNo = roomNo;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public long getMessageNo() {
        return MessageNo;
    }

    public void setMessageNo(long messageNo) {
        MessageNo = messageNo;
    }

    public String getAttachFileName() {
        return AttachFileName;
    }

    public void setAttachFileName(String attachFileName) {
        AttachFileName = attachFileName;
    }

    public int getWriteUserNo() {
        return WriteUserNo;
    }

    public void setWriteUserNo(int writeUserNo) {
        WriteUserNo = writeUserNo;
    }

    public int getAttachFileType() {
        return AttachFileType;
    }

    public void setAttachFileType(int attachFileType) {
        AttachFileType = attachFileType;
    }

    public String getAttachFilePath() {
        return AttachFilePath;
    }

    public void setAttachFilePath(String attachFilePath) {
        AttachFilePath = attachFilePath;
    }

    public int getAttachFileSize() {
        return AttachFileSize;
    }

    public void setAttachFileSize(int attachFileSize) {
        AttachFileSize = attachFileSize;
    }

    public boolean isShowNotification() {
        return ShowNotification;
    }

    public void setShowNotification(boolean showNotification) {
        ShowNotification = showNotification;
    }
}
