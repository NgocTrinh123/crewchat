package com.dazone.crewchat.ViewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.dazone.crewchat.utils.CrewChatApplication;

public abstract class ItemViewHolder<T> extends RecyclerView.ViewHolder {
    protected final String mRootLink;
    public ItemViewHolder(View v) {
        super(v);
        mRootLink = CrewChatApplication.getInstance().getmPrefs().getServerSite();
        setup(v);
    }
    protected abstract void setup(View v);
    public abstract void bindData(T t);
    @Override
    public String toString() {
        return super.toString();
    }
}
