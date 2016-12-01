package com.dazone.crewchat.dto;

import com.google.gson.annotations.SerializedName;

/**
 * Created by david on 2/25/15.
 */
public class ErrorDto {

    public boolean unAuthentication;

    @SerializedName("code")
    public int code = 1;

    @SerializedName("message")
    public String message = "";

    public ErrorDto() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "ErrorDto{" +
                "unAuthentication=" + unAuthentication +
                ", code=" + code +
                ", message='" + message + '\'' +
                '}';
    }
}
