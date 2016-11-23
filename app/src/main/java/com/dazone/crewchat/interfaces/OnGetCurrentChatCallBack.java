package com.dazone.crewchat.interfaces;


import com.dazone.crewchat.dto.CurrentChatDto;
import com.dazone.crewchat.dto.ErrorDto;

import java.util.List;

public interface OnGetCurrentChatCallBack {
    public void onHTTPSuccess(List<CurrentChatDto> dtos);
    public void onHTTPFail(ErrorDto errorDto);
}
