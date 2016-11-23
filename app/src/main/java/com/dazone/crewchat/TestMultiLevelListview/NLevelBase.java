package com.dazone.crewchat.TestMultiLevelListview;

import com.dazone.crewchat.Tree.Dtos.TreeUserDTO;

import java.io.Serializable;

/**
 * Created by Admin on 8/3/2016.
 */
public class NLevelBase implements NLevelListItem, Serializable{

    private TreeUserDTO wrappedObject;
    private NLevelItem parent;
    private NLevelView nLevelView;
    private boolean isExpanded = false;
    private int mLevelIndex = 0;

    public NLevelBase(TreeUserDTO wrappedObject, NLevelItem parent, int levelIndex) {
        this.wrappedObject = wrappedObject;
        this.parent = parent;
        this.mLevelIndex = levelIndex;
    }

    @Override
    public TreeUserDTO getObject() {
        return wrappedObject;
    }

    @Override
    public boolean isExpanded() {
        return isExpanded;
    }

    @Override
    public void toggle() {
        isExpanded = !isExpanded;
    }

    @Override
    public NLevelListItem getParent() {
        return parent;
    }

    @Override
    public int getLevel() {
        return mLevelIndex;
    }
}
