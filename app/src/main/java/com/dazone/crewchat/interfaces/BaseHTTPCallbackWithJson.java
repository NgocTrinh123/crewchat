package com.dazone.crewchat.interfaces;

import com.dazone.crewchat.dto.ErrorDto;

/**
 * Created by Admin on 5/20/2016.
 */
public interface BaseHTTPCallbackWithJson {
    void onHTTPSuccess(String jsonData);
    void onHTTPFail(ErrorDto errorDto);
}
