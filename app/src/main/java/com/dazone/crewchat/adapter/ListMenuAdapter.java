package com.dazone.crewchat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.dazone.crewchat.R;

import java.util.ArrayList;

/**
 * Created by Admin on 6/13/2016.
 */
public class ListMenuAdapter extends ArrayAdapter<String> {
    private ArrayList<String> mData;

    public class ViewHolder{
        TextView tv;
        View lineTop;
        public ViewHolder(View view){
            tv = (TextView) view.findViewById(R.id.tv_action_name);
            lineTop = view.findViewById(R.id.view_line_top);
        }
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public String getItem(int position) {
        return mData.get(position);
    }

    public ListMenuAdapter(Context context, ArrayList<String> data) {
        super(context,  R.layout.row_chatting_action_menu_user, data);
        this.mData = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        String action_name = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_chatting_action_menu_user, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (position == 0){
            viewHolder.lineTop.setVisibility(View.VISIBLE);
        }else{
            viewHolder.lineTop.setVisibility(View.GONE);
        }
        viewHolder.tv.setText(action_name);
        
        return convertView;
    }
}