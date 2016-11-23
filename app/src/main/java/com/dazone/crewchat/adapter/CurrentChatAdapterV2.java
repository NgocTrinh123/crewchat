package com.dazone.crewchat.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.dazone.crewchat.R;
import com.dazone.crewchat.ViewHolders.ItemViewHolder;
import com.dazone.crewchat.ViewHolders.ListCurrentViewHolder;
import com.dazone.crewchat.ViewHolders.ProgressViewHolder;
import com.dazone.crewchat.dto.ChatRoomDTO;
import com.dazone.crewchat.dto.ChattingDto;

import java.util.List;

/**
 * Created by david on 12/23/15.
 */
public class CurrentChatAdapterV2 extends PullUpLoadMoreRCVAdapter<ChatRoomDTO> {

    public CurrentChatAdapterV2(Context context, List<ChatRoomDTO> myDataSet, RecyclerView recyclerView) {
        super(context,myDataSet, recyclerView);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh = null;
        if (viewType == VIEW_ITEM) {

            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_current_chat, parent, false);
            //vh = new ListCurrentViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.progress_load_more_item, parent, false);

            vh = new ProgressViewHolder(v);
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ListCurrentViewHolder) {
            final ChatRoomDTO item = mDataset.get(position);
            ItemViewHolder viewHolder = (ListCurrentViewHolder) holder;
            viewHolder.bindData(item);
        } else {
            ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        }
    }
}
