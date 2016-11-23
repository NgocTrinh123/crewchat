package com.dazone.crewchat.interfaces;

import com.dazone.crewchat.dto.ErrorDto;
import com.dazone.crewchat.dto.userfavorites.FavoriteChatRoomDto;

import java.util.List;

/**
 * Created by THANHTUNG on 18/02/2016.
 */
public interface OnGetFavoriteChatRoom {
    public void OnGetChatRoomSuccess(List<FavoriteChatRoomDto> list);
    public void OnGetChatRoomFail(ErrorDto errorDto);
}
