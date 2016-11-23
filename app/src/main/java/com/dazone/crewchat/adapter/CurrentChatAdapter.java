package com.dazone.crewchat.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dazone.crewchat.ViewHolders.*;
import com.dazone.crewchat.constant.Statics;
import com.dazone.crewchat.dto.ChattingDto;
import com.dazone.crewchat.R;
import com.dazone.crewchat.fragment.CurrentChatListFragment;

import java.util.List;

/**
 * Created by david on 12/23/15.
 */
public class CurrentChatAdapter extends PullUpLoadMoreRCVAdapter<ChattingDto> {
    private CurrentChatListFragment.OnContextMenuSelect mOnContextMenuSelect;
    public CurrentChatAdapter(Context context, List<ChattingDto> myDataSet, RecyclerView recyclerView,CurrentChatListFragment.OnContextMenuSelect callback ) {
        super(context, myDataSet, recyclerView);
        this.mOnContextMenuSelect = callback;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        if (viewType == VIEW_ITEM) {

            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_current_chat, parent, false);
            vh = new ListCurrentViewHolder(v, this.mOnContextMenuSelect);
        } else if(viewType == Statics.CHATTING_VIEW_TYPE_EMPTY){

            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_chatting_empty, parent, false);
            vh = new EmptyViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.progress_load_more_item, parent, false);

            vh = new ProgressViewHolder(v);
        }
        return vh;
    }


    @Override
    public int getItemCount() {
        if (mDataset.size() == 0 && !CurrentChatListFragment.fragment.isFirstTime){
            return 1;
        }
        return mDataset.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (getItemCount() == 1 && mDataset.size() == 0){
            return Statics.CHATTING_VIEW_TYPE_EMPTY;
        }
        return super.getItemViewType(position);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof EmptyViewHolder){
            ItemViewHolder viewHolder = (EmptyViewHolder) holder;
        }else if (holder instanceof ListCurrentViewHolder) {
            final ChattingDto item = mDataset.get(position);
            ItemViewHolder viewHolder = (ListCurrentViewHolder) holder;
            viewHolder.bindData(item);
        } else {
            ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        }
    }
}
