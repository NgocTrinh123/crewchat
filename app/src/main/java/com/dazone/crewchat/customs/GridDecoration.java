package com.dazone.crewchat.customs;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by david on 1/6/16.
 */
public class GridDecoration extends RecyclerView.ItemDecoration {
    private int offset;

    public GridDecoration(int offset) {
        this.offset = offset;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent, RecyclerView.State state) {
        outRect.left = offset;
        outRect.right = offset;
        outRect.bottom = offset;
//        if (parent.getChildAdapterPosition(view) == 0) {
//            outRect.top = offset;
//        }

    }
}

