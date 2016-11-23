package com.dazone.crewchat.interfaces;

import com.dazone.crewchat.dto.ErrorDto;

/**
 * Created by Dat on 4/26/2016.
 */
public interface OnGetMessageUnreadCountCallBack {
    void onHTTPSuccess(String result);

    void onHTTPFail(ErrorDto errorDto);
}
