package com.dazone.crewchat.interfaces;

import com.dazone.crewchat.dto.ErrorDto;
import com.dazone.crewchat.dto.TreeUserDTOTemp;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by maidinh on 8/19/2015.
 */
public interface IGetListOrganization {
    public void onGetListSuccess(ArrayList<TreeUserDTOTemp> treeUserDTOs);
    public void onGetListFail(ErrorDto dto);
}
