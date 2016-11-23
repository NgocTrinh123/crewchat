package com.dazone.crewchat.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dazone.crewchat.R;
import com.dazone.crewchat.Tree.Dtos.TreeUserDTO;

/**
 * A simple {@link Fragment} subclass.
 */
public class TestFragment extends ListFragment<TreeUserDTO> {


    public TestFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_test, container, false);
    }

    @Override
    protected void initAdapter() {

    }

    @Override
    protected void reloadContentPage() {

    }

    @Override
    protected void addMoreItem() {

    }

    @Override
    protected void initList() {

    }

}
