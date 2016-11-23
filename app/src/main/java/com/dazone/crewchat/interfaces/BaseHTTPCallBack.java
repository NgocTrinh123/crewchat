package com.dazone.crewchat.interfaces;


import com.dazone.crewchat.dto.ErrorDto;

public interface BaseHTTPCallBack {
    void onHTTPSuccess();
    void onHTTPFail(ErrorDto errorDto);
}
