package com.dazone.crewchat.adapter;

import android.content.Context;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * Created by david on 5/13/15.
 */
public abstract class DrawerListAdapter<T> extends BaseAdapter {

    protected Context mContext;
    protected List<T> mMenuItems;

    public DrawerListAdapter(Context context, List<T> navItems) {
        mContext = context;
        mMenuItems = navItems;
    }

    @Override
    public int getCount() {
        return mMenuItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mMenuItems.get(position);
    }
}
