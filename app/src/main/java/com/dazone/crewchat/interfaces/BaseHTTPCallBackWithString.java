package com.dazone.crewchat.interfaces;


import com.dazone.crewchat.dto.ErrorDto;

public interface BaseHTTPCallBackWithString {
    void onHTTPSuccess(String message);
    void onHTTPFail(ErrorDto errorDto);
}
