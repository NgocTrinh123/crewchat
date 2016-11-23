package com.dazone.crewchat.interfaces;

import com.dazone.crewchat.dto.ErrorDto;
import com.dazone.crewchat.dto.ProfileUserDTO;

/**
 * Created by Dat on 4/27/2016.
 */
public interface OnGetUserCallBack {
    void onHTTPSuccess(ProfileUserDTO result);

    void onHTTPFail(ErrorDto errorDto);
}
