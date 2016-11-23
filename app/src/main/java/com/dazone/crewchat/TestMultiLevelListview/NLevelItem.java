package com.dazone.crewchat.TestMultiLevelListview;

import com.dazone.crewchat.Tree.Dtos.TreeUserDTO;

public class NLevelItem extends NLevelBase {
	
	private TreeUserDTO wrappedObject;
	private NLevelItem parent;
	private boolean isExpanded = true;
	
	public NLevelItem(TreeUserDTO wrappedObject, NLevelItem parent, int levelIndex) {
		super(wrappedObject, parent, levelIndex);
		this.wrappedObject = wrappedObject;
		this.parent = parent;
	}
	
	public Object getWrappedObject() {
		return wrappedObject;
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
	public NLevelListItem getParent() {
		return parent;
	}

	@Override
	public void toggle() {
		isExpanded = !isExpanded;
	}
}
