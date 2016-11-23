package com.dazone.crewchat.interfaces;

import com.dazone.crewchat.dto.ChattingDto;
import com.dazone.crewchat.dto.ErrorDto;

import java.util.List;

/**
 * Created by THANHTUNG on 18/02/2016.
 */
public interface OnGetChatList {
    public void OnGetChatListSuccess(List<ChattingDto> list);
    public void OnGetChatListFail(ErrorDto errorDto);
}
