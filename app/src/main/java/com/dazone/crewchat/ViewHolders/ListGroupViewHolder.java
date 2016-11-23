package com.dazone.crewchat.ViewHolders;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.dazone.crewchat.R;
import com.dazone.crewchat.dto.TreeUserDTOTemp;
import com.dazone.crewchat.utils.DialogUtils;
import com.dazone.crewchat.utils.ImageUtils;
import com.dazone.crewchat.utils.Prefs;
import com.dazone.crewchat.utils.Utils;

/**
 * Created by THANHTUNG on 04/03/2016.
 */
public class ListGroupViewHolder extends ItemViewHolder<TreeUserDTOTemp> {

    private RelativeLayout layoutMain;
    private TextView tvUserName, tvPosition;
    private ImageView ivAvatar;

    public ListGroupViewHolder(View v) {
        super(v);
    }

    @Override
    protected void setup(View v) {
        layoutMain = (RelativeLayout) v.findViewById(R.id.layout_main);
        tvUserName = (TextView) v.findViewById(R.id.tv_username);
        ivAvatar = (ImageView) v.findViewById(R.id.iv_avatar);
        tvPosition = (TextView) v.findViewById(R.id.tv_position);
    }

    @Override
    public void bindData(TreeUserDTOTemp treeUserDTOTemp) {
        if (treeUserDTOTemp != null) {
            tvUserName.setText(treeUserDTOTemp.getName());
            tvPosition.setText(treeUserDTOTemp.getPosition());

            //ImageLoader.getInstance().displayImage(new Prefs().getServerSite() + treeUserDTOTemp.getAvatarUrl(), ivAvatar, Statics.options2);
            ImageUtils.showCycleImageFromLink(new Prefs().getServerSite() + treeUserDTOTemp.getAvatarUrl(), ivAvatar, R.dimen.button_height);

            layoutMain.setTag(treeUserDTOTemp);
            layoutMain.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    TreeUserDTOTemp user = (TreeUserDTOTemp) v.getTag();
                    String strName = user.getName();
                    String strPhoneNumber = user.getCellPhone();
                    String strCompanyNumber = user.getCompanyPhone();
                    int userNo = user.getUserNo();
                    Utils.printLogs(user.getName() + " " + user.getCellPhone() + " " + user.getCompanyPhone());
                    DialogUtils.showDialogUser(strName, strPhoneNumber, strCompanyNumber, userNo);
                    return false;
                }
            });
        }
    }
}
