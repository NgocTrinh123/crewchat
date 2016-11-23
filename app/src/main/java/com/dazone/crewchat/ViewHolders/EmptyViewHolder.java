package com.dazone.crewchat.ViewHolders;

import android.view.View;
import com.dazone.crewchat.dto.ChattingDto;

/**
 * Created by david on 12/25/15.
 */
public class EmptyViewHolder extends BaseChattingHolder {

    public EmptyViewHolder(View v) {
        super(v);
    }

    @Override
    protected void setup(View v) {
    }

    @Override
    public void bindData(ChattingDto dto) {
    }
}
