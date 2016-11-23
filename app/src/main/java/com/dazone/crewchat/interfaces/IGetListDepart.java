package com.dazone.crewchat.interfaces;

import com.dazone.crewchat.dto.ErrorDto;
import com.dazone.crewchat.Tree.Dtos.TreeUserDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by THANHTUNG on 17/02/2016.
 */
public interface IGetListDepart {
    public void onGetListDepartSuccess(ArrayList<TreeUserDTO> treeUserDTOs);
    public void onGetListDepartFail(ErrorDto dto);
}
