package com.dazone.crewchat.TestMultiLevelListview;

import com.dazone.crewchat.Tree.Dtos.TreeUserDTO;

import java.util.ArrayList;

/**
 * Created by Admin on 8/3/2016.
 */
public class DumpyData {

    public static ArrayList<TreeUserDTO> getData(){
        ArrayList<TreeUserDTO> data = new ArrayList<>();

        data.add(new TreeUserDTO("Dazone Tech", "Dazone tech", "","", "", 1, 1, 0, 0));

        ArrayList<TreeUserDTO> subItem = new ArrayList<>();
        subItem.add(new TreeUserDTO("Quyet", "Quyet", "" , "", "Staff", 2, 1, 11, 0));

        data.get(0).setSubordinates(subItem);

        return data;
    }
}
