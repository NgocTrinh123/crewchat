package com.dazone.crewchat.adapter;

import android.support.v7.widget.RecyclerView;

import com.dazone.crewchat.dto.SelectionPlusDto;
import com.dazone.crewchat.ViewHolders.SelectionChattingViewHolder;

import java.util.List;

/**
 * Created by david on 1/5/16.
 */
public class SelectionPlusAdapter extends SelectionAdapter<SelectionPlusDto> {
    public SelectionPlusAdapter(List<SelectionPlusDto> dataSet) {
        super(dataSet);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                final SelectionPlusDto item = dataSet.get(position);
        SelectionChattingViewHolder viewHolder = (SelectionChattingViewHolder) holder;
        viewHolder.bindData(item);
    }
}
