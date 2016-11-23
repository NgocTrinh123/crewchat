package com.dazone.crewchat.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dazone.crewchat.dto.MenuDrawItem;
import com.dazone.crewchat.R;
import com.dazone.crewchat.utils.ImageUtils;

import java.util.List;

/**
 * Created by david on 12/18/15.
 */
public class SelectListAdapter extends DrawerListAdapter<MenuDrawItem> {

    public SelectListAdapter(Context context, List<MenuDrawItem> navItems) {
        super(context, navItems);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        MenuDrawItem item = mMenuItems.get(position);
        if (item.isHide())
        {
            view = inflater.inflate(R.layout.row_null,null);;
        }
        else {
            view = inflater.inflate(R.layout.row_menu, null);
            TextView titleView = (TextView) view.findViewById(R.id.title);
            ImageView iconView = (ImageView) view.findViewById(R.id.ic_folder);

            titleView.setText(item.getStringTitle());
            titleView.setTextColor(Color.BLACK);
            ImageUtils.showImage(item, iconView);
        }
        return view;
    }

    @Override
    public long getItemId(int position) {
        return mMenuItems.get(position).getItemID();
    }
}
