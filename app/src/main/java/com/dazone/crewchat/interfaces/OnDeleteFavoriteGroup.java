package com.dazone.crewchat.interfaces;

import com.dazone.crewchat.Tree.Dtos.TreeUserDTO;

import java.util.ArrayList;

/**
 * Created by Admin on 7/19/2016.
 */
public interface OnDeleteFavoriteGroup {
    public void onDelete(long groupNo);
    public void onEdit(long groupNo, String groupName);
    public void onAdd(long groupNo, ArrayList<TreeUserDTO> list);
}
