package com.dazone.crewchat.interfaces;

import com.dazone.crewchat.dto.ChatRoomDTO;
import com.dazone.crewchat.dto.ErrorDto;
import com.dazone.crewchat.dto.UserInfoDto;

import java.util.ArrayList;


/**
 * Created by Dat on 5/4/2016.
 */
public interface OnGetUserInfo {
    public void OnSuccess(ArrayList<UserInfoDto> userInfo);
    public void OnFail(ErrorDto errorDto);
}
