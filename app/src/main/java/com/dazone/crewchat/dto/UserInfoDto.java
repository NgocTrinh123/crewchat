package com.dazone.crewchat.dto;

/**
 * Created by Admin on 6/8/2016.
 */
public class UserInfoDto {
    private int UserNo;
    private String StateMessage;

    public int getUserNo() {
        return UserNo;
    }

    public void setUserNo(int userNo) {
        UserNo = userNo;
    }

    public String getStateMessage() {
        return StateMessage;
    }

    public void setStateMessage(String stateMessage) {
        StateMessage = stateMessage;
    }
}
