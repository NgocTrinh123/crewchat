package com.dazone.crewchat.interfaces;

import com.dazone.crewchat.dto.ChattingDto;
import com.dazone.crewchat.dto.ErrorDto;

/**
 * Created by THANHTUNG on 17/02/2016.
 */
public interface ICreateOneUserChatRom {
    public void onICreateOneUserChatRomSuccess(ChattingDto chattingDto);
    public void onICreateOneUserChatRomFail(ErrorDto errorDto);
}
