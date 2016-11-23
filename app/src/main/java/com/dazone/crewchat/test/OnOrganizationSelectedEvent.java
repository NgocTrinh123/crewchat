package com.dazone.crewchat.test;

import com.dazone.crewchat.Tree.Dtos.TreeUserDTO;

/**
 * Created by Sherry on 12/31/15.
 */
public interface OnOrganizationSelectedEvent {
    void onOrganizationCheck(boolean isCheck, TreeUserDTO personData);
//    void onOrganizationCollapseExpand(int isHide,PersonData selectedObject);
}
