package com.dazone.crewchat.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dazone.crewchat.R;
import com.dazone.crewchat.ViewHolders.SelectionChattingViewHolder;

import java.util.List;

/**
 * Created by david on 1/5/16.
 */
public abstract class SelectionAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    protected List<T> dataSet;

    public SelectionAdapter(List<T> dataSet) {
        super();
        this.dataSet = dataSet;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View  v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.selection_layout, parent, false);
                vh = new SelectionChattingViewHolder(v);
        return vh;
    }

//    @Override
//    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
////        final ChattingDto item = dataSet.get(position);
//        BaseChattingHolder viewHolder = (BaseChattingHolder) holder;
//        viewHolder.bindData(item);
//    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}
