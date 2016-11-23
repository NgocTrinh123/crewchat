package com.dazone.crewchat.ViewHolders;

import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dazone.crewchat.dto.ChattingDto;
import com.dazone.crewchat.R;

/**
 * Created by THANHTUNG on 24/02/2016.
 */
public class ChattingGroupViewHolderNew extends BaseChattingHolder {
    TextView group_name;
    ProgressBar progressBar;

    public ChattingGroupViewHolderNew(View v) {
        super(v);
    }

    @Override
    protected void setup(View v) {
        group_name = (TextView) v.findViewById(R.id.group_name);
        progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
    }

    @Override
    public void bindData(ChattingDto dto) {
        progressBar.setVisibility(View.GONE);
        group_name.setText(dto.getMessage());

    }
}
