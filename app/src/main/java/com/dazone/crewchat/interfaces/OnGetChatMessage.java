package com.dazone.crewchat.interfaces;

import com.dazone.crewchat.dto.ChattingDto;
import com.dazone.crewchat.dto.ErrorDto;

import java.util.List;

/**
 * Created by THANHTUNG on 18/02/2016.
 */
public interface OnGetChatMessage {
    public void OnGetChatMessageSuccess(List<ChattingDto> list);
    public void OnGetChatMessageFail(ErrorDto errorDto);
}
