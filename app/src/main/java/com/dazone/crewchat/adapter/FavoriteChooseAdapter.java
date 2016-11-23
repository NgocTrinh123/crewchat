package com.dazone.crewchat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.dazone.crewchat.R;
import com.dazone.crewchat.Tree.Dtos.TreeUserDTO;

import java.util.ArrayList;

/**
 * Created by Admin on 6/23/2016.
 */
public class FavoriteChooseAdapter extends ArrayAdapter<TreeUserDTO> {

    private Context mContext;
    private ArrayList<TreeUserDTO> mTreeUserDto;

    public FavoriteChooseAdapter(Context context, ArrayList<TreeUserDTO> treeUserDTOs) {
        super(context, 0, treeUserDTOs);
        this.mContext = context;
        this.mTreeUserDto = treeUserDTOs;
    }

    @Override
    public int getCount() {
        return mTreeUserDto.size();
    }

    @Override
    public TreeUserDTO getItem(int position) {
        return mTreeUserDto.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        TreeUserDTO user = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        /*if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_user, parent, false);
        }
        // Lookup view for data population
        TextView tvName = (TextView) convertView.findViewById(R.id.tvName);
        TextView tvHome = (TextView) convertView.findViewById(R.id.tvHome);
        // Populate the data into the template view using the data object
        tvName.setText(user.name);
        tvHome.setText(user.hometown);
        // Return the completed view to render on screen*/
        return convertView;
    }
}
