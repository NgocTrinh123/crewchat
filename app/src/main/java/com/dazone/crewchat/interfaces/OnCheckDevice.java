package com.dazone.crewchat.interfaces;


import com.dazone.crewchat.dto.ErrorDto;

/**
 * Created by david on 9/25/15.
 */
public interface OnCheckDevice {
    public void onDeviceSuccess();
    public void onHTTPFail(ErrorDto errorDto);
}
