package com.dazone.crewchat.ViewHolders;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.*;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.dazone.crewchat.HTTPs.HttpRequest;
import com.dazone.crewchat.R;
import com.dazone.crewchat.Views.RoundedImageView;
import com.dazone.crewchat.activity.ChattingActivity;
import com.dazone.crewchat.activity.RoomUserInformationActivity;
import com.dazone.crewchat.activity.base.BaseActivity;
import com.dazone.crewchat.constant.Statics;
import com.dazone.crewchat.database.ChatRomDBHelper;
import com.dazone.crewchat.database.UserDBHelper;
import com.dazone.crewchat.dto.ChattingDto;
import com.dazone.crewchat.dto.ErrorDto;
import com.dazone.crewchat.dto.TreeUserDTOTemp;
import com.dazone.crewchat.fragment.RecentFavoriteFragment;
import com.dazone.crewchat.interfaces.BaseHTTPCallBack;
import com.dazone.crewchat.utils.*;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.Locale;

/**
 * Created by david on 7/17/15.
 */
public class RecentFavoriteViewHolder extends ItemViewHolder<ChattingDto> implements View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener{
    private RecentFavoriteFragment.OnContextMenuSelect mOnContextMenuSelect;
    public RecentFavoriteViewHolder(View itemView, RecentFavoriteFragment.OnContextMenuSelect callback) {
        super(itemView);
        mOnContextMenuSelect = callback;
    }

    public TextView tvUserName, tvDate, tvContent, tvTotalUser;
    public RoundedImageView status_imv;
    private ImageView imgBadge;
    private ImageView imgAvatar;
    private ImageView ivLastedAttach;
    private View view;

    private String roomTitle = "";
    private long roomNo = -1;
    private boolean isTwoUser = false;
    /**
     * Group Avatar
     */
    private RelativeLayout layoutGroupAvatar;
    private ImageView imgGroupAvatar1;
    private ImageView imgGroupAvatar2;
    private ImageView imgGroupAvatar3;
    private ImageView imgGroupAvatar4;
    private TextView tvGroupAvatar;
    private ImageView ivNotification;
    private ChattingDto tempDto;

    private int myId;

    @Override
    protected void setup(View v) {
        view = v;
        tvUserName = (TextView) v.findViewById(R.id.user_name_tv);
        tvDate = (TextView) v.findViewById(R.id.date_tv);
        tvContent = (TextView) v.findViewById(R.id.content_tv);
        imgAvatar = (ImageView) v.findViewById(R.id.avatar_imv);
        imgBadge = (ImageView) v.findViewById(R.id.image_badge);
        ivLastedAttach = (ImageView) v.findViewById(R.id.iv_lasted_attach);
        tvTotalUser = (TextView) v.findViewById(R.id.tv_user_total);

        layoutGroupAvatar = (RelativeLayout) v.findViewById(R.id.avatar_group);
        imgGroupAvatar1 = (ImageView) v.findViewById(R.id.avatar_group_1);
        imgGroupAvatar2 = (ImageView) v.findViewById(R.id.avatar_group_2);
        imgGroupAvatar3 = (ImageView) v.findViewById(R.id.avatar_group_3);
        imgGroupAvatar4 = (ImageView) v.findViewById(R.id.avatar_group_4);
        tvGroupAvatar = (TextView) v.findViewById(R.id.avatar_group_number);
        ivNotification = (ImageView) v.findViewById(R.id.iv_notification);

        view.setOnCreateContextMenuListener(this);
    }

    @Override
    public void bindData(final ChattingDto dto) {

        myId = Utils.getCurrentId();
        tempDto = dto;

        String name = "";
        // Set total user in current room, if user > 2 display this, else hide it
        int totalUser = dto.getUserNos().size();
        if (totalUser > 2){
            tvTotalUser.setVisibility(View.VISIBLE);
            tvTotalUser.setText(String.valueOf(totalUser));
        }else{
            isTwoUser = true;
            tvTotalUser.setVisibility(View.GONE);
        }


        if (dto.getUnReadCount() != 0) {
            imgBadge.setVisibility(View.VISIBLE);
            ImageUtils.showBadgeImage(dto.getUnReadCount(), imgBadge);
        } else {
            imgBadge.setVisibility(View.GONE);
        }

        if (dto.isNotification()){
            ivNotification.setVisibility(View.GONE);
        }else{
            ivNotification.setVisibility(View.VISIBLE);
        }

        /** SET TITLE FOR ROOM */
        if (TextUtils.isEmpty(dto.getRoomTitle())) {
            if (dto.getListTreeUser() != null && dto.getListTreeUser().size() > 0) {
                for (TreeUserDTOTemp treeUserDTOTemp : dto.getListTreeUser()) {
                    name += treeUserDTOTemp.getName() + ",";
                }
                if (name.length() != 0) {
                    name = name.substring(0, name.length() - 1);
                }
            }
        } else {
            name = dto.getRoomTitle();
        }

        // Global value
        roomTitle = name;
        roomNo = dto.getRoomNo();

        if (dto.getListTreeUser() == null || dto.getListTreeUser().size() == 0) {
            tvUserName.setTextColor(ContextCompat.getColor(CrewChatApplication.getInstance(), R.color.gray));
            tvUserName.setText(CrewChatApplication.getInstance().getResources().getString(R.string.unknown));
        } else {
            tvUserName.setTextColor(ContextCompat.getColor(CrewChatApplication.getInstance(), R.color.black));
            tvUserName.setText(name);
        }


        /** SET LAST MESSAGE */
        String strLastMsg = "";
        Resources res = CrewChatApplication.getInstance().getResources();
        switch (dto.getLastedMsgType()){
            case Statics.MESSAGE_TYPE_NORMAL:
                ivLastedAttach.setVisibility(View.GONE);
                strLastMsg = dto.getLastedMsg();
                break;

            case Statics.MESSAGE_TYPE_SYSTEM:
                strLastMsg = dto.getLastedMsg();
                ivLastedAttach.setVisibility(View.GONE);
                break;

            case Statics.MESSAGE_TYPE_ATTACH:

                // Attach type switch
                switch (dto.getLastedMsgAttachType()){
                    case Statics.ATTACH_NONE:
                        strLastMsg = dto.getLastedMsg();
                        ivLastedAttach.setVisibility(View.GONE);
                        break;

                    case Statics.ATTACH_IMAGE:
                        strLastMsg = res.getString(R.string.attach_image);
                        ivLastedAttach.setImageResource(R.drawable.home_attach_ic_images);
                        break;

                    case Statics.ATTACH_FILE:
                        strLastMsg = res.getString(R.string.attach_file);
                        ivLastedAttach.setImageResource(R.drawable.home_attach_ic_file);
                        break;
                }
                ivLastedAttach.setVisibility(View.VISIBLE);
                break;
        }
        tvContent.setText(strLastMsg);

        /** Test */
        String tempTimeString = dto.getLastedMsgDate();
        if (!TextUtils.isEmpty(tempTimeString)){
            long time;

            if (tempTimeString.contains("(")) {
                tempTimeString = tempTimeString.replace("/Date(", "");
                int plusIndex = tempTimeString.indexOf("+");

                if (plusIndex != -1) {
                    time = Long.valueOf(tempTimeString.substring(0, plusIndex));
                } else {
                    time = Long.valueOf(tempTimeString.substring(0, tempTimeString.indexOf(")")));
                }
            } else {
                time = Long.valueOf(tempTimeString);
            }

            if (Locale.getDefault().getLanguage().toUpperCase().equalsIgnoreCase("KO")) {
                tvDate.setText(TimeUtils.displayTimeWithoutOffset(CrewChatApplication.getInstance().getApplicationContext(), time, 1));
            } else {
                tvDate.setText(TimeUtils.displayTimeWithoutOffset(CrewChatApplication.getInstance().getApplicationContext(), time, 0));
            }
        }

        if (dto.getListTreeUser() != null && dto.getListTreeUser().size() > 0) {
            if (dto.getListTreeUser().size() < 2) {
                layoutGroupAvatar.setVisibility(View.GONE);
                imgAvatar.setVisibility(View.VISIBLE);
                ImageUtils.showRoundImage(dto.getListTreeUser().get(0), imgAvatar);
            } else {
                layoutGroupAvatar.setVisibility(View.VISIBLE);
                imgAvatar.setVisibility(View.GONE);
                String url1 = new Prefs().getServerSite() + UserDBHelper.getUser().avatar;
                String url2;
                String url3;
                String url4;


                switch (dto.getListTreeUser().size()) {
                    case 2:
                        imgGroupAvatar1.getLayoutParams().height = RelativeLayout.LayoutParams.MATCH_PARENT;
                        imgGroupAvatar1.getLayoutParams().width = (int) CrewChatApplication.getInstance().getResources().getDimension(R.dimen.common_avatar_group);
                        imgGroupAvatar2.getLayoutParams().height = RelativeLayout.LayoutParams.MATCH_PARENT;
                        imgGroupAvatar2.setVisibility(View.VISIBLE);
                        imgGroupAvatar3.setVisibility(View.GONE);
                        imgGroupAvatar4.setVisibility(View.GONE);
                        tvGroupAvatar.setVisibility(View.GONE);
                        url1 = new Prefs().getServerSite() + dto.getListTreeUser().get(0).getAvatarUrl();
                        url2 = new Prefs().getServerSite() + dto.getListTreeUser().get(1).getAvatarUrl();
                        ImageLoader.getInstance().displayImage(url1, imgGroupAvatar1, Statics.avatarGroupTL);
                        ImageLoader.getInstance().displayImage(url2, imgGroupAvatar2, Statics.avatarGroupTR);
                        break;
                    case 3:
                        imgGroupAvatar1.getLayoutParams().width = RelativeLayout.LayoutParams.MATCH_PARENT;
                        imgGroupAvatar2.setVisibility(View.GONE);
                        imgGroupAvatar3.setVisibility(View.VISIBLE);
                        imgGroupAvatar4.setVisibility(View.VISIBLE);
                        tvGroupAvatar.setVisibility(View.GONE);
                        url1 = new Prefs().getServerSite() + dto.getListTreeUser().get(0).getAvatarUrl();
                        url3 = new Prefs().getServerSite() + dto.getListTreeUser().get(1).getAvatarUrl();
                        url4 = new Prefs().getServerSite() + dto.getListTreeUser().get(2).getAvatarUrl();
                        ImageLoader.getInstance().displayImage(url1, imgGroupAvatar1, Statics.avatarGroupTOP);
                        ImageLoader.getInstance().displayImage(url3, imgGroupAvatar3, Statics.avatarGroupBL);
                        ImageLoader.getInstance().displayImage(url4, imgGroupAvatar4, Statics.avatarGroupBR);
                        break;
                    case 4:
                        imgGroupAvatar1.getLayoutParams().height = (int) CrewChatApplication.getInstance().getResources().getDimension(R.dimen.common_avatar_group);
                        imgGroupAvatar1.getLayoutParams().width = (int) CrewChatApplication.getInstance().getResources().getDimension(R.dimen.common_avatar_group);
                        imgGroupAvatar2.getLayoutParams().height = (int) CrewChatApplication.getInstance().getResources().getDimension(R.dimen.common_avatar_group);
                        imgGroupAvatar2.setVisibility(View.VISIBLE);
                        imgGroupAvatar3.setVisibility(View.VISIBLE);
                        imgGroupAvatar4.setVisibility(View.VISIBLE);
                        tvGroupAvatar.setVisibility(View.GONE);
                        url1 = new Prefs().getServerSite() + dto.getListTreeUser().get(0).getAvatarUrl();
                        url2 = new Prefs().getServerSite() + dto.getListTreeUser().get(1).getAvatarUrl();
                        url3 = new Prefs().getServerSite() + dto.getListTreeUser().get(2).getAvatarUrl();
                        url4 = new Prefs().getServerSite() + dto.getListTreeUser().get(3).getAvatarUrl();
                        ImageLoader.getInstance().displayImage(url1, imgGroupAvatar1, Statics.avatarGroupTL);
                        ImageLoader.getInstance().displayImage(url2, imgGroupAvatar2, Statics.avatarGroupTR);
                        ImageLoader.getInstance().displayImage(url3, imgGroupAvatar3, Statics.avatarGroupBL);
                        ImageLoader.getInstance().displayImage(url4, imgGroupAvatar4, Statics.avatarGroupBR);
                        break;
                    default:
                        imgGroupAvatar1.getLayoutParams().height = (int) CrewChatApplication.getInstance().getResources().getDimension(R.dimen.common_avatar_group);
                        imgGroupAvatar1.getLayoutParams().width = (int) CrewChatApplication.getInstance().getResources().getDimension(R.dimen.common_avatar_group);
                        imgGroupAvatar2.getLayoutParams().height = (int) CrewChatApplication.getInstance().getResources().getDimension(R.dimen.common_avatar_group);
                        imgGroupAvatar2.setVisibility(View.VISIBLE);
                        imgGroupAvatar3.setVisibility(View.VISIBLE);
                        imgGroupAvatar4.setVisibility(View.VISIBLE);

                        tvGroupAvatar.setVisibility(View.VISIBLE);
                        String strNumber = dto.getListTreeUser().size() - 3 + "";
                        tvGroupAvatar.setText(strNumber);
                        url1 = new Prefs().getServerSite() + dto.getListTreeUser().get(0).getAvatarUrl();
                        url2 = new Prefs().getServerSite() + dto.getListTreeUser().get(1).getAvatarUrl();
                        url3 = new Prefs().getServerSite() + dto.getListTreeUser().get(2).getAvatarUrl();
                        url4 = "drawable://" + R.drawable.avatar_group_bg;
                        ImageLoader.getInstance().displayImage(url1, imgGroupAvatar1, Statics.avatarGroupTL);
                        ImageLoader.getInstance().displayImage(url2, imgGroupAvatar2, Statics.avatarGroupTR);
                        ImageLoader.getInstance().displayImage(url3, imgGroupAvatar3, Statics.avatarGroupBL);
                        ImageLoader.getInstance().displayImage(url4, imgGroupAvatar4, Statics.avatarGroupBR);
                        break;
                }


            }
        }

        if (dto.getListTreeUser() == null || dto.getListTreeUser().size() == 0) {
            layoutGroupAvatar.setVisibility(View.GONE);
            imgAvatar.setVisibility(View.VISIBLE);
            String url = "drawable://" + R.drawable.avatar_l;
            ImageLoader.getInstance().displayImage(url, imgAvatar, Statics.options2);
        }
        view.setTag(dto.getRoomNo());

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Utils.printLogs("On click listener #########");
                long roomNo = (long) v.getTag();
                Intent intent = new Intent(BaseActivity.Instance, ChattingActivity.class);


                Bundle args = new Bundle();
                args.putLong(Constant.KEY_INTENT_ROOM_NO, roomNo);
                args.putLong(Constant.KEY_INTENT_USER_NO, myId);
                args.putSerializable(Constant.KEY_INTENT_ROOM_DTO, tempDto);

                intent.putExtras(args);

                BaseActivity.Instance.startActivity(intent);
            }
        });

        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Utils.printLogs("On Long click listener #########");
                 v.showContextMenu();
                return true;
            }
        });

        tvTotalUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(BaseActivity.Instance, RoomUserInformationActivity.class);
                intent.putIntegerArrayListExtra("userNos", dto.getUserNos());
                intent.putExtra("roomTitle", roomTitle);
                BaseActivity.Instance.startActivity(intent);
            }
        });

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

        Resources res = CrewChatApplication.getInstance().getResources();
        MenuItem roomRename = menu.add(0, Statics.ROOM_RENAME, 0, res.getString(R.string.room_rename));

        MenuItem roomOpen = menu.add(0, Statics.ROOM_OPEN, 0, res.getString(R.string.room_open));

        MenuItem roomAlarmOnOff;
        if(!tempDto.isNotification()){
            roomAlarmOnOff = menu.add(0, Statics.ROOM_ALARM_ON, 0, res.getString(R.string.alarm_on));
        } else{
            roomAlarmOnOff = menu.add(0, Statics.ROOM_ALARM_OFF, 0, res.getString(R.string.alarm_off));
        }

        MenuItem roomOut = menu.add(0, Statics.ROOM_LEFT, 0, res.getString(R.string.room_left));
        MenuItem roomRemoveFavorite = menu.add(0,Statics.ROOM_REMOVE_FROM_FAVORITE, 0, res.getString(R.string.room_remove_favorite));


        roomRename.setOnMenuItemClickListener(this);
        roomOpen.setOnMenuItemClickListener(this);
        roomAlarmOnOff.setOnMenuItemClickListener(this);
        roomRemoveFavorite.setOnMenuItemClickListener(this);
        roomOut.setOnMenuItemClickListener(this);

    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        Utils.printLogs("On menu item1 click listener ######### ID="+item.getItemId());
        Bundle roomInfo = null;
        switch (item.getItemId()){
            case Statics.ROOM_RENAME:

                roomInfo = new Bundle();
                roomInfo.putLong(Statics.ROOM_NO, roomNo);
                roomInfo.putString(Statics.ROOM_TITLE, roomTitle);
                mOnContextMenuSelect.onSelect(Statics.ROOM_RENAME, roomInfo);

                break;
            case Statics.ROOM_OPEN:
                roomInfo = new Bundle();
                roomInfo.putLong(Statics.ROOM_NO, roomNo);
                mOnContextMenuSelect.onSelect(Statics.ROOM_OPEN, roomInfo);

                break;

            case Statics.ROOM_REMOVE_FROM_FAVORITE:

                final Resources res = CrewChatApplication.getInstance().getResources();
                Utils.printLogs("Remove room ="+roomNo);
                HttpRequest.getInstance().removeFromFavorite(roomNo, new BaseHTTPCallBack() {
                    @Override
                    public void onHTTPSuccess() {
                        // update to current chat list
                        ChatRomDBHelper.updateChatRoomFavorite(roomNo, false);
                        tempDto.setFavorite(false);

                        // Send broadcast to reload list current data
                        Intent intentBroadcast = new Intent(Constant.INTENT_FILTER_NOTIFY_ADAPTER);
                        intentBroadcast.putExtra("roomNo", tempDto.getRoomNo());
                        intentBroadcast.putExtra("type", Constant.TYPE_ACTION_FAVORITE);
                        CrewChatApplication.getInstance().sendBroadcast(intentBroadcast);

                        // Clear self from current favorite list
                        Bundle args = new Bundle();
                        args.putLong(Statics.ROOM_NO, tempDto.getRoomNo());
                        mOnContextMenuSelect.onSelect(Statics.ROOM_REMOVE_FROM_FAVORITE, args);
                    }

                    @Override
                    public void onHTTPFail(ErrorDto errorDto) {
                        Toast.makeText(CrewChatApplication.getInstance(), res.getString(R.string.favorite_remove_failed) , Toast.LENGTH_LONG).show();
                    }
                });

                break;


            case Statics.ROOM_ALARM_ON:

                HttpRequest.getInstance().updateChatRoomNotification(roomNo, true, new BaseHTTPCallBack() {
                    @Override
                    public void onHTTPSuccess() {
                        ivNotification.setVisibility(View.GONE);
                        tempDto.setNotification(true);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                ChatRomDBHelper.updateChatRoomNotification(roomNo, true);
                            }
                        }).start();

                        // Send broadcast to reload list current data
                        Intent intentBroadcast = new Intent(Constant.INTENT_FILTER_NOTIFY_ADAPTER);
                        intentBroadcast.putExtra("roomNo", tempDto.getRoomNo());
                        intentBroadcast.putExtra("type", Constant.TYPE_ACTION_ALARM_ON);
                        CrewChatApplication.getInstance().sendBroadcast(intentBroadcast);
                    }

                    @Override
                    public void onHTTPFail(ErrorDto errorDto) {

                    }
                });

                break;
            case Statics.ROOM_ALARM_OFF:

                HttpRequest.getInstance().updateChatRoomNotification(roomNo, false, new BaseHTTPCallBack() {
                    @Override
                    public void onHTTPSuccess() {
                        ivNotification.setVisibility(View.VISIBLE);
                        tempDto.setNotification(false);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                ChatRomDBHelper.updateChatRoomNotification(roomNo, false);

                                // Send broadcast to reload list current data
                                Intent intentBroadcast = new Intent(Constant.INTENT_FILTER_NOTIFY_ADAPTER);
                                intentBroadcast.putExtra("roomNo", tempDto.getRoomNo());
                                intentBroadcast.putExtra("type", Constant.TYPE_ACTION_ALARM_OFF);
                                CrewChatApplication.getInstance().sendBroadcast(intentBroadcast);
                            }
                        }).start();
                    }

                    @Override
                    public void onHTTPFail(ErrorDto errorDto) {

                    }
                });

                break;
            case Statics.ROOM_LEFT:

                roomInfo = new Bundle();
                roomInfo.putLong(Statics.ROOM_NO, roomNo);
                mOnContextMenuSelect.onSelect(Statics.ROOM_LEFT, roomInfo);

                break;
        }
        return false;
    }
}
