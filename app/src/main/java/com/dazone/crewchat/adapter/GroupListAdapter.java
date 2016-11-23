package com.dazone.crewchat.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dazone.crewchat.dto.TreeUserDTOTemp;
import com.dazone.crewchat.dto.UserDto;
import com.dazone.crewchat.R;
import com.dazone.crewchat.ViewHolders.ItemViewHolder;
import com.dazone.crewchat.ViewHolders.ListGroupViewHolder;
import com.dazone.crewchat.ViewHolders.ProgressViewHolder;

import java.util.List;

/**
 * Created by THANHTUNG on 04/03/2016.
 */
public class GroupListAdapter extends PullUpLoadMoreRCVAdapter<TreeUserDTOTemp> {

    public GroupListAdapter(Context context, List<TreeUserDTOTemp> mDataSet, RecyclerView view) {
        super(context, mDataSet, view);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        if (viewType == VIEW_ITEM) {

            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_group_list, parent, false);
            vh = new ListGroupViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.progress_load_more_item, parent, false);

            vh = new ProgressViewHolder(v);
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ListGroupViewHolder) {
            final TreeUserDTOTemp item = mDataset.get(position);
            ItemViewHolder viewHolder = (ListGroupViewHolder) holder;
            viewHolder.bindData(item);
        } else {
            ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        }
    }
}
