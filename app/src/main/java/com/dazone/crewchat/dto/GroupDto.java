package com.dazone.crewchat.dto;

import com.dazone.crewchat.constant.Statics;
import com.dazone.crewchat.utils.Utils;

import java.util.List;

/**
 * Created by david on 12/25/15.
 */
public class GroupDto extends ChattingDto {
    private List<UserDto> group;

    public GroupDto(List<UserDto> group) {
        this.group = group;
        setmType(Statics.CHATTING_VIEW_TYPE_GROUP);
    }

    @Override
    public String toString() {
        return "GroupDto{" +
                "group=" + group +
                '}';
    }

    @Override
    public String getName() {
        if(group==null||group.size()==0)
            return "";
        String temp = "";
        for(UserDto dto: group) {
            Utils.printLogs("###Member info ="+dto.toString());
            temp = temp+dto.FullName+",";
        }
        return temp.substring(0,temp.length()-1);
    }

    public List<UserDto> getGroup() {
        return group;
    }

    public void setGroup(List<UserDto> group) {
        this.group = group;
    }
}
