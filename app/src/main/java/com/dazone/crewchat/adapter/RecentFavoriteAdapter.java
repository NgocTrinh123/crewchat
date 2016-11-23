package com.dazone.crewchat.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.dazone.crewchat.R;
import com.dazone.crewchat.ViewHolders.ItemViewHolder;
import com.dazone.crewchat.ViewHolders.ProgressViewHolder;
import com.dazone.crewchat.ViewHolders.RecentFavoriteViewHolder;
import com.dazone.crewchat.dto.ChattingDto;
import com.dazone.crewchat.fragment.RecentFavoriteFragment;
import com.dazone.crewchat.utils.Utils;

import java.util.List;

/**
 * Created by david on 12/23/15.
 */
public class RecentFavoriteAdapter extends PullUpLoadMoreRCVAdapter<ChattingDto> {
    private RecentFavoriteFragment.OnContextMenuSelect mOnContextMenuSelect;
    public RecentFavoriteAdapter(Context context, List<ChattingDto> myDataSet, RecyclerView recyclerView, RecentFavoriteFragment.OnContextMenuSelect callback ) {
        super(context, myDataSet, recyclerView);
        Utils.printLogs("Item size from RecentFavoriteAdapter ="+myDataSet.size());
        this.mOnContextMenuSelect = callback;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        if (viewType == VIEW_ITEM) {

            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_current_chat, parent, false);
            vh = new RecentFavoriteViewHolder(v, this.mOnContextMenuSelect);
        } else {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.progress_load_more_item, parent, false);

            vh = new ProgressViewHolder(v);
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof RecentFavoriteViewHolder) {
            final ChattingDto item = mDataset.get(position);
            ItemViewHolder viewHolder = (RecentFavoriteViewHolder) holder;
            viewHolder.bindData(item);
        } else {
            ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        }
    }
}
