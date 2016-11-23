package com.dazone.crewchat.ViewHolders;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.dazone.crewchat.R;
import com.dazone.crewchat.activity.ProfileUserActivity;
import com.dazone.crewchat.activity.base.BaseActivity;
import com.dazone.crewchat.dto.ChattingDto;
import com.dazone.crewchat.dto.UserDto;
import com.dazone.crewchat.utils.Constant;
import com.dazone.crewchat.utils.CrewChatApplication;
import com.dazone.crewchat.utils.ImageUtils;

/**
 * Created by david on 12/25/15.
 */
public class ChattingContactViewHolder extends BaseChattingHolder {
    TextView tv_contact_name, tv_contact_number;
    TextView tvUnread;
    LinearLayout lnContact;

    public ChattingContactViewHolder(View v) {
        super(v);
    }

    @Override
    protected void setup(View v) {

        tv_contact_name = (TextView) v.findViewById(R.id.tv_contact_name);
        tv_contact_number = (TextView) v.findViewById(R.id.tv_contact_number);
        tvUnread = (TextView) v.findViewById(R.id.text_unread);
        lnContact = (LinearLayout) v.findViewById(R.id.lnContact);
    }

    @Override
    public void bindData(ChattingDto dto) {

        UserDto userDto = dto.getUser();

        tv_contact_name.setText(userDto.getFullName());
        tv_contact_number.setText(userDto.getPhoneNumber());
        //ImageUtils.showRoundImage(userDto.getAvatar(), avatar_imv);

        String strUnReadCount = dto.getUnReadCount() + "";
        tvUnread.setText(strUnReadCount);
        tvUnread.setVisibility(dto.getUnReadCount() == 0 ? View.GONE : View.VISIBLE);
        lnContact.setTag(userDto.getPhoneNumber());
        lnContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = (String) v.getTag();
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" +phoneNumber));
                BaseActivity.Instance.startActivity(intent);
                BaseActivity.Instance.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
    }
}
