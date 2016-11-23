package com.dazone.crewchat.TestMultiLevelListview;

import com.dazone.crewchat.Tree.Dtos.TreeUserDTO;

public interface NLevelListItem {
	public TreeUserDTO getObject();
	public boolean isExpanded();
	public void toggle();
	public NLevelListItem getParent();
	public int getLevel();
	// public View getView();
}
