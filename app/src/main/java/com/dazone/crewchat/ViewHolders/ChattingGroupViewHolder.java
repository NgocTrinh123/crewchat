package com.dazone.crewchat.ViewHolders;

import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dazone.crewchat.dto.ChattingDto;
import com.dazone.crewchat.R;

/**
 * Created by david on 12/25/15.
 */
public class ChattingGroupViewHolder extends BaseChattingHolder {
    TextView group_name;
    ProgressBar progressBar;


    public ChattingGroupViewHolder(View v) {
        super(v);
    }

    @Override
    protected void setup(View v) {
        group_name = (TextView) v.findViewById(R.id.group_name);
        progressBar = (ProgressBar) v.findViewById(R.id.progressBar);

    }

    @Override
    public void bindData(ChattingDto dto) {
        if (dto.getId() != 0) {
            progressBar.setVisibility(View.VISIBLE);
            group_name.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            group_name.setVisibility(View.VISIBLE);
            group_name.setText(dto.getName());
        }
    }
}
