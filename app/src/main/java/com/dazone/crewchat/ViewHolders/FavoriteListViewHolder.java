package com.dazone.crewchat.ViewHolders;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.dazone.crewchat.Class.TreeOfficeView;
import com.dazone.crewchat.Class.TreeView;
import com.dazone.crewchat.R;
import com.dazone.crewchat.Tree.Dtos.TreeUserDTO;
import com.dazone.crewchat.interfaces.OnDeleteFavoriteGroup;

import java.util.HashMap;

/**
 * Created by david on 7/17/15.
 */
public class FavoriteListViewHolder extends ItemViewHolder<TreeUserDTO> {
    private HashMap<Integer, ImageView> mStatusViewMap;
    private OnDeleteFavoriteGroup mDeleteCallback;

    public FavoriteListViewHolder(View itemView) {
        super(itemView);
    }
    public FavoriteListViewHolder(View itemView, HashMap<Integer, ImageView> statusViewMap , OnDeleteFavoriteGroup deleteCallback) {
        super(itemView);
        this.mStatusViewMap = statusViewMap;
        this.mDeleteCallback = deleteCallback;
    }

    public LinearLayout favorite_lnl;

    @Override
    protected void setup(View v) {
        favorite_lnl = (LinearLayout)v.findViewById(R.id.favorite_lnl);
    }

    @Override
    public void bindData(TreeUserDTO dto) {
        TreeView tree = new TreeOfficeView(favorite_lnl.getContext(),dto, mStatusViewMap, -1, mDeleteCallback);
        tree.addToView(favorite_lnl);
    }
}
