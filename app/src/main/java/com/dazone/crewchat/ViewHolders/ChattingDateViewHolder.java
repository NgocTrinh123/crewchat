package com.dazone.crewchat.ViewHolders;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.dazone.crewchat.constant.Statics;
import com.dazone.crewchat.dto.ChattingDto;
import com.dazone.crewchat.R;
import com.dazone.crewchat.utils.TimeUtils;
import com.dazone.crewchat.utils.Utils;

/**
 * Created by david on 12/25/15.
 */
public class ChattingDateViewHolder extends BaseChattingHolder {
    TextView time;

    public ChattingDateViewHolder(View v) {
        super(v);
    }

    @Override
    protected void setup(View v) {
        time = (TextView) v.findViewById(R.id.time);
    }

    @Override
    public void bindData(ChattingDto dto) {
        if (TextUtils.isEmpty(dto.getRegDate())) {
            time.setText(TimeUtils.showTimeWithoutTimeZone(dto.getTime(), Statics.DATE_FORMAT_YYYY_MM_DD));
        } else {
            if (dto.getRegDate().equalsIgnoreCase(Utils.getString(R.string.today))) {
                time.setText(Utils.getString(R.string.today));
            } else {
                time.setText(TimeUtils.displayTimeWithoutOffset(dto.getRegDate()));
            }
        }
    }
}
