package com.dazone.crewchat.interfaces;

import com.dazone.crewchat.dto.ErrorDto;

/**
 * Created by Admin on 6/8/2016.
 */
public interface OnSetNotification {
    public void OnSuccess();
    public void OnFail(ErrorDto errorDto);
}
