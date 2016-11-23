package com.dazone.crewchat.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.dazone.crewchat.R;
import com.dazone.crewchat.Tree.Dtos.TreeUserDTO;
import com.dazone.crewchat.ViewHolders.ItemViewHolder;
import com.dazone.crewchat.ViewHolders.ProgressViewHolder;
import com.dazone.crewchat.ViewHolders.RoomUserInfoViewHolder;

import java.util.List;

/**
 * Created by Admin on 6/27/2016.
 */
public class RoomUserInfoAdapter extends PullUpLoadMoreRCVAdapter<TreeUserDTO>{
    private Context mContext;
    public RoomUserInfoAdapter(Context context, List<TreeUserDTO> myDataSet, RecyclerView recyclerView) {
        super(context, myDataSet, recyclerView);
        this.mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        if (viewType == VIEW_ITEM) {

            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_room_user_information, parent, false);

            RoomUserInfoViewHolder viewHolder = new RoomUserInfoViewHolder(v);
            viewHolder.setContext(mContext);
            vh = viewHolder;

        } else {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.progress_load_more_item, parent, false);

            vh = new ProgressViewHolder(v);
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof RoomUserInfoViewHolder) {
            final TreeUserDTO item = mDataset.get(position);
            ItemViewHolder viewHolder = (RoomUserInfoViewHolder) holder;
            viewHolder.bindData(item);
        } else {
            ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        }
    }
}
