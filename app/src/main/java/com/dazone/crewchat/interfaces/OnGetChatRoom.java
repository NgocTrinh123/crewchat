package com.dazone.crewchat.interfaces;

import com.dazone.crewchat.dto.ChatRoomDTO;
import com.dazone.crewchat.dto.ErrorDto;


/**
 * Created by Dat on 5/4/2016.
 */
public interface OnGetChatRoom {
    public void OnGetChatRoomSuccess(ChatRoomDTO chatRoomDTO);
    public void OnGetChatRoomFail(ErrorDto errorDto);
}
