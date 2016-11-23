package com.dazone.crewchat.dto;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Admin on 5/25/2016.
 */
public class MessageDto {
    @SerializedName("Message")
    private String Message;

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }
}
