package com.dazone.crewchat.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.dazone.crewchat.R;
import com.dazone.crewchat.Tree.Dtos.TreeUserDTO;
import com.dazone.crewchat.ViewHolders.FavoriteListViewHolder;
import com.dazone.crewchat.ViewHolders.ItemViewHolder;
import com.dazone.crewchat.ViewHolders.ProgressViewHolder;
import com.dazone.crewchat.interfaces.OnDeleteFavoriteGroup;

import java.util.HashMap;
import java.util.List;

/**
 * Created by david on 12/23/15.
 */
public class FavoriteListAdapter extends PullUpLoadMoreRCVAdapter<TreeUserDTO> {
    private HashMap<Integer, ImageView> mStatusViewMap;
    private OnDeleteFavoriteGroup mDeleteCallback;

    public FavoriteListAdapter(Context context, List<TreeUserDTO> myDataSet, RecyclerView recyclerView) {
        super(context,myDataSet, recyclerView);
    }

    public FavoriteListAdapter(Context context, List<TreeUserDTO> myDataSet, RecyclerView recyclerView, HashMap<Integer, ImageView> statusViewMap, OnDeleteFavoriteGroup deleteCallback) {
        super(context,myDataSet, recyclerView);
        this.mStatusViewMap = statusViewMap;
        this.mDeleteCallback = deleteCallback;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        if(viewType==VIEW_ITEM) {

            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_favorite_list, parent, false);
            vh =  new FavoriteListViewHolder(v, mStatusViewMap, mDeleteCallback);
        }else {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.progress_load_more_item, parent, false);

            vh = new ProgressViewHolder(v);
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof FavoriteListViewHolder){
            final TreeUserDTO item = mDataset.get(position);
            ItemViewHolder viewHolder = (FavoriteListViewHolder) holder;
            viewHolder.bindData(item);
        }else{
            ((ProgressViewHolder)holder).progressBar.setIndeterminate(true);
        }
    }
}
