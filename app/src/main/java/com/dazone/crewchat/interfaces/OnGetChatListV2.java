package com.dazone.crewchat.interfaces;

import com.dazone.crewchat.dto.ChatRoomDTO;
import com.dazone.crewchat.dto.ErrorDto;

import java.util.List;

/**
 * Created by Admin on 5/23/2016.
 */
public interface OnGetChatListV2 {
    public void OnGetChatListSuccess(List<ChatRoomDTO> list);
    public void OnGetChatListFail(ErrorDto errorDto);
}
