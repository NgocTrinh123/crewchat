package com.dazone.crewchat.interfaces;

import com.dazone.crewchat.dto.ChattingDto;
import com.dazone.crewchat.dto.ErrorDto;

/**
 * Created by THANHTUNG on 18/02/2016.
 */
public interface SendChatMessage {
    public void onSenChatMessageSuccess(ChattingDto chattingDto);
    public void onSenChatMessageFail(ErrorDto errorDto, String url);
}
