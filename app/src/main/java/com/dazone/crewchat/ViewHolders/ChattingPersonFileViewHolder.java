package com.dazone.crewchat.ViewHolders;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dazone.crewchat.activity.base.BaseActivity;
import com.dazone.crewchat.activity.ProfileUserActivity;
import com.dazone.crewchat.dto.ChattingDto;
import com.dazone.crewchat.R;
import com.dazone.crewchat.utils.Constant;
import com.dazone.crewchat.utils.ImageUtils;
import com.dazone.crewchat.utils.Utils;

/**
 * Created by david on 12/25/15.
 */
public class ChattingPersonFileViewHolder extends ChattingSelfFileViewHolder {
    TextView user_name_tv;
    TextView tvUnread;
    ImageView avatar_imv;
    ImageView ivFile;

    public ChattingPersonFileViewHolder(View v) {
        super(v);
    }

    @Override
    protected void setup(View v) {
        super.setup(v);
        ivFile = (ImageView)v.findViewById(R.id.file_thumb);
        user_name_tv = (TextView) v.findViewById(R.id.user_name_tv);
        avatar_imv = (ImageView) v.findViewById(R.id.avatar_imv);
        tvUnread = (TextView) v.findViewById(R.id.text_unread);

    }

    @Override
    public void bindData(ChattingDto dto) {
        super.bindData(dto);

        /** Set IMAGE FILE TYPE */
        String fileType = Utils.getFileType(dto.getAttachInfo().getFileName());
        ImageUtils.imageFileType(file_thumb, fileType);

        user_name_tv.setText(dto.getName());
        ImageUtils.showRoundImage(dto, avatar_imv);
        String strUnReadCount = dto.getUnReadCount() + "";
        tvUnread.setText(strUnReadCount);
        tvUnread.setVisibility(dto.getUnReadCount() == 0 ? View.GONE : View.VISIBLE);
        avatar_imv.setTag(dto.getUserNo());
        avatar_imv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    int userNo = (int) v.getTag();
                    Intent intent = new Intent(BaseActivity.Instance, ProfileUserActivity.class);
                    intent.putExtra(Constant.KEY_INTENT_USER_NO, userNo);
                    BaseActivity.Instance.startActivity(intent);
                    BaseActivity.Instance.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
