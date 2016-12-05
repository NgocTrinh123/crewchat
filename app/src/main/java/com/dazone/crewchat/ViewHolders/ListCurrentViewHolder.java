package com.dazone.crewchat.ViewHolders;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dazone.crewchat.HTTPs.HttpRequest;
import com.dazone.crewchat.R;
import com.dazone.crewchat.Views.RoundedImageView;
import com.dazone.crewchat.activity.ChattingActivity;
import com.dazone.crewchat.activity.ProfileUserActivity;
import com.dazone.crewchat.activity.RoomUserInformationActivity;
import com.dazone.crewchat.activity.base.BaseActivity;
import com.dazone.crewchat.constant.Statics;
import com.dazone.crewchat.database.AllUserDBHelper;
import com.dazone.crewchat.database.ChatRomDBHelper;
import com.dazone.crewchat.dto.ChattingDto;
import com.dazone.crewchat.dto.ErrorDto;
import com.dazone.crewchat.dto.TreeUserDTOTemp;
import com.dazone.crewchat.dto.UserDto;
import com.dazone.crewchat.fragment.CurrentChatListFragment;
import com.dazone.crewchat.fragment.RecentFavoriteFragment;
import com.dazone.crewchat.interfaces.BaseHTTPCallBack;
import com.dazone.crewchat.utils.Constant;
import com.dazone.crewchat.utils.CrewChatApplication;
import com.dazone.crewchat.utils.ImageUtils;
import com.dazone.crewchat.utils.Prefs;
import com.dazone.crewchat.utils.TimeUtils;
import com.dazone.crewchat.utils.Utils;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by david on 7/17/15.
 */
public class ListCurrentViewHolder extends ItemViewHolder<ChattingDto> implements View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {
    private CurrentChatListFragment.OnContextMenuSelect mOnContextMenuSelect;

    public ListCurrentViewHolder(View itemView, CurrentChatListFragment.OnContextMenuSelect callback) {
        super(itemView);
        mOnContextMenuSelect = callback;
    }

    public TextView tvUserName, tvDate, tvContent, tvTotalUser;
    public RoundedImageView status_imv;
    private ImageView imgBadge;
    private ImageView imgAvatar;
    private ImageView ivLastedAttach;
    private ImageView ivFavorite;
    private ImageView ivNotification;
    private View view;
    private LinearLayout lnData;

    private String roomTitle = "";
    private long roomNo = -1;
    private boolean isTwoUser = false;
    private ChattingDto tempDto;
    /**
     * Group Avatar
     */
    private RelativeLayout layoutGroupAvatar;
    private ImageView imgGroupAvatar1;
    private ImageView imgGroupAvatar2;
    private ImageView imgGroupAvatar3;
    private ImageView imgGroupAvatar4;
    private TextView tvGroupAvatar;

    private final Resources res = CrewChatApplication.getInstance().getResources();
    private int myId;

    @Override
    protected void setup(final View v) {
        view = v;
        tvUserName = (TextView) v.findViewById(R.id.user_name_tv);
        lnData = (LinearLayout) v.findViewById(R.id.row_current_chat_ln_data);
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
        ivFavorite = (ImageView) v.findViewById(R.id.iv_favorite);
        ivNotification = (ImageView) v.findViewById(R.id.iv_notification);

        //gestureDetector = new GestureDetector(CrewChatApplication.getInstance(), new CustomGestureDetector(view));
        view.setOnCreateContextMenuListener(this);

    }


    @Override
    public void bindData(final ChattingDto dto) {

        myId = Utils.getCurrentId();
        tempDto = dto;

        String name = "";
        // Set total user in current room, if user > 2 display this, else hide it
        boolean isFilter = false;
        int totalUser = 0;
        List<TreeUserDTOTemp> list1 = new ArrayList<>();
        TreeUserDTOTemp treeUserDTOTemp1;
        ArrayList<TreeUserDTOTemp> listUsers = CrewChatApplication.listUsers;
        if (listUsers == null) {
            listUsers = AllUserDBHelper.getUser();
            CrewChatApplication.listUsers = listUsers;
            Utils.printLogs("Get list user from local database");
        }

        if (dto.getListTreeUser() != null && dto.getListTreeUser().size() < dto.getUserNos().size()) {
            totalUser = dto.getListTreeUser().size() + 1;
            isFilter = true;
        } else {


            ArrayList<Integer> users = dto.getUserNos();
            ArrayList<Integer> usersClone = new ArrayList<>(users);
            Utils.removeArrayDuplicate(usersClone);

            for (int i = 0; i < usersClone.size(); i++) {
                if (listUsers != null) {
                    treeUserDTOTemp1 = Utils.GetUserFromDatabase(listUsers, usersClone.get(i));
                    if (treeUserDTOTemp1 != null) {
                        list1.add(treeUserDTOTemp1);
                    }
                }
                /*if (myId != id && listUsers != null) {
                    treeUserDTOTemp1 = Utils.GetUserFromDatabase(listUsers, id);
                    if (treeUserDTOTemp1 != null) {
                        list1.add(treeUserDTOTemp1);
                    }
                }*/
            }

            dto.setListTreeUser(list1);
            if (list1 != null) {
                totalUser = list1.size();
            }
        }

        if (totalUser > 2) {
            tvTotalUser.setVisibility(View.VISIBLE);
            tvTotalUser.setText(String.valueOf(totalUser));
        } else {
            isTwoUser = true;
            tvTotalUser.setVisibility(View.GONE);
        }

        if (dto.isFavorite()) {
            ivFavorite.setVisibility(View.VISIBLE);
        } else {
            ivFavorite.setVisibility(View.GONE);
        }

        if (dto.isNotification()) {
            ivNotification.setVisibility(View.GONE);
        } else {
            ivNotification.setVisibility(View.VISIBLE);
        }

        if (dto.getWriterUserNo() == myId) {
            imgBadge.setVisibility(View.GONE);
            Utils.printLogs("Write user is me #############");
        } else {
            if (dto.getUnReadCount() != 0) {
                imgBadge.setVisibility(View.VISIBLE);
                ImageUtils.showBadgeImage(dto.getUnReadCount(), imgBadge);
            } else {
                imgBadge.setVisibility(View.GONE);
            }
        }

        /** SET TITLE FOR ROOM */
        if (TextUtils.isEmpty(dto.getRoomTitle())) {
            if (dto.getListTreeUser() != null && dto.getListTreeUser().size() > 0) {
                for (TreeUserDTOTemp treeUserDTOTemp : dto.getListTreeUser()) {
                    if (treeUserDTOTemp.getUserNo() != myId) {

                        //Utils.printLogs("User No = "+treeUserDTOTemp.getUserNo()+" # my id = "+myId);

                        if (TextUtils.isEmpty(name)) {
                            name += treeUserDTOTemp.getName();
                        } else {
                            name += "," + treeUserDTOTemp.getName();
                        }
                    } else {
                        //Utils.printLogs("User No == My Id == "+treeUserDTOTemp.getUserNo());
                    }
                }
            }
        } else {
            name = dto.getRoomTitle();
        }

        //Utils.printLogs("Room title = "+name);

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
        switch (dto.getLastedMsgType()) {
            case Statics.MESSAGE_TYPE_NORMAL:
                ivLastedAttach.setVisibility(View.GONE);
                /*if (totalUser > 2){
                    int userNo = dto.getMsgUserNo();
                    TreeUserDTOTemp tempUser = Utils.GetUserFromDatabase(listUsers, userNo);
                    if (tempUser != null) {
                        strLastMsg += tempUser.getName() +": ";
                    }
                }*/
                strLastMsg += dto.getLastedMsg();
                break;

            case Statics.MESSAGE_TYPE_SYSTEM:
                strLastMsg = dto.getLastedMsg();
                ivLastedAttach.setVisibility(View.GONE);
                break;

            case Statics.MESSAGE_TYPE_ATTACH:

                // Attach type switch
                switch (dto.getLastedMsgAttachType()) {
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

        // detect break line
        if (strLastMsg != null && strLastMsg.contains("\n")) {
            String[] mess = strLastMsg.split("\\n");
            String ms = "";
            for (String ss : mess) {
                if (ss != null && ss.trim().length() > 0) {
                    ms = ss;
                    break;
                }
            }
            tvContent.setText(ms);
        } else {
            tvContent.setText(strLastMsg);
        }

        /** Test */
        String tempTimeString = dto.getLastedMsgDate();
        if (!TextUtils.isEmpty(tempTimeString)) {

            // tempTimeString = tempTimeString.replace("/Date(", "");
            //tempTimeString = tempTimeString.replace(")/", "");
            //long time = Long.valueOf(tempTimeString);

            //Utils.printLogs("####LastedMsgDate on ViewHolder ="+time);
            if (Locale.getDefault().getLanguage().toUpperCase().equalsIgnoreCase("KO")) {
                tvDate.setText(TimeUtils.displayTimeWithoutOffset(CrewChatApplication.getInstance().getApplicationContext(), dto.getLastedMsgDate(), 1, TimeUtils.KEY_FROM_SERVER));
            } else {
                tvDate.setText(TimeUtils.displayTimeWithoutOffset(CrewChatApplication.getInstance().getApplicationContext(), dto.getLastedMsgDate(), 0, TimeUtils.KEY_FROM_SERVER));
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
                UserDto userDto = Utils.getCurrentUser();
                String url1 = new Prefs().getServerSite();
                if (userDto != null) {
                    url1 += userDto.avatar;
                }
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


                        /*Glide.with(CrewChatApplication.getInstance())
                                .load(url1)
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .bitmapTransform(new RoundedCornersTransformation(CrewChatApplication.getInstance(), 75, 12, RoundedCornersTransformation.CornerType.LEFT ))
                                .into(imgGroupAvatar1);

                        Glide.with(CrewChatApplication.getInstance())
                                .load(url2)
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .bitmapTransform(new RoundedCornersTransformation(CrewChatApplication.getInstance(), 150, 12, RoundedCornersTransformation.CornerType.RIGHT ))
                                .into(imgGroupAvatar2);*/

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



        /*view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                return true;
            }
        });*/

        final boolean finalIsFilter = isFilter;
        tvTotalUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ArrayList<Integer> uNos = new ArrayList<>();
                uNos.add(myId);
                if (finalIsFilter) {

                    for (TreeUserDTOTemp tree : dto.getListTreeUser()) {
                        uNos.add(tree.getUserNo());
                    }
                } else {
                    for (int id : dto.getUserNos()) {
                        if (myId != id) {
                            uNos.add(id);
                        }
                    }
                }

                Intent intent = new Intent(BaseActivity.Instance, RoomUserInformationActivity.class);
                intent.putIntegerArrayListExtra("userNos", uNos);
                intent.putExtra("roomTitle", roomTitle);
                BaseActivity.Instance.startActivity(intent);
            }
        });

        layoutGroupAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<Integer> uNos = new ArrayList<>();
                uNos.add(myId);
                if (finalIsFilter) {

                    for (TreeUserDTOTemp tree : dto.getListTreeUser()) {
                        uNos.add(tree.getUserNo());
                    }
                } else {
                    for (int id : dto.getUserNos()) {
                        if (myId != id) {
                            uNos.add(id);
                        }
                    }
                }

                Intent intent = new Intent(BaseActivity.Instance, RoomUserInformationActivity.class);
                intent.putIntegerArrayListExtra("userNos", uNos);
                intent.putExtra("roomTitle", roomTitle);
                BaseActivity.Instance.startActivity(intent);
            }
        });
        imgAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(BaseActivity.Instance, ProfileUserActivity.class);
                intent.putExtra(Constant.KEY_INTENT_USER_NO, dto.getListTreeUser().get(0).getUserNo());
                BaseActivity.Instance.startActivity(intent);

            }
        });

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        Resources res = CrewChatApplication.getInstance().getResources();
        MenuItem roomRename = menu.add(0, Statics.ROOM_RENAME, 0, res.getString(R.string.room_name));

        MenuItem roomAddFavorite;
        if (tempDto.isFavorite()) {
            roomAddFavorite = menu.add(0, Statics.ROOM_REMOVE_FROM_FAVORITE, 0, res.getString(R.string.room_remove_favorite));
        } else {
            roomAddFavorite = menu.add(0, Statics.ROOM_ADD_TO_FAVORITE, 0, res.getString(R.string.room_favorite));
        }

        MenuItem roomAlarmOnOff;
        if (!tempDto.isNotification()) {
            roomAlarmOnOff = menu.add(0, Statics.ROOM_ALARM_ON, 0, res.getString(R.string.alarm_on));
        } else {
            roomAlarmOnOff = menu.add(0, Statics.ROOM_ALARM_OFF, 0, res.getString(R.string.alarm_off));
        }

        roomRename.setOnMenuItemClickListener(this);
        roomAddFavorite.setOnMenuItemClickListener(this);
        roomAlarmOnOff.setOnMenuItemClickListener(this);

        MenuItem roomOut = menu.add(0, Statics.ROOM_LEFT, 0, res.getString(R.string.room_left));
        roomOut.setOnMenuItemClickListener(this);

    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        Utils.printLogs("On menu item1 click listener ######### ID=" + item.getItemId());
        Bundle roomInfo = null;
        switch (item.getItemId()) {
            case Statics.ROOM_RENAME:

                roomInfo = new Bundle();
                roomInfo.putInt(Statics.ROOM_NO, (int) roomNo);
                roomInfo.putString(Statics.ROOM_TITLE, roomTitle);
                mOnContextMenuSelect.onSelect(Statics.ROOM_RENAME, roomInfo);

                break;
            case Statics.ROOM_OPEN:
                roomInfo = new Bundle();
                roomInfo.putInt(Statics.ROOM_NO, (int) roomNo);
                roomInfo.putSerializable(Constant.KEY_INTENT_ROOM_DTO, tempDto);

                mOnContextMenuSelect.onSelect(Statics.ROOM_OPEN, roomInfo);

                break;

            case Statics.ROOM_REMOVE_FROM_FAVORITE:


                /*final Bundle finalRoomInfo = roomInfo;
                mOnContextMenuSelect.onSelect(Statics.ROOM_ADD_TO_FAVORITE, finalRoomInfo);*/
                Utils.printLogs("Remove room =" + roomNo);

                HttpRequest.getInstance().removeFromFavorite(roomNo, new BaseHTTPCallBack() {
                    @Override
                    public void onHTTPSuccess() {
                        ivFavorite.setVisibility(View.GONE);

                        Utils.printLogs("Remove room =" + roomNo + " success");

                        ChatRomDBHelper.updateChatRoomFavorite(roomNo, false);
                        tempDto.setFavorite(false);

                        if (RecentFavoriteFragment.instance != null) {
                            RecentFavoriteFragment.instance.removeFavorite(roomNo);
                        }
                    }

                    @Override
                    public void onHTTPFail(ErrorDto errorDto) {
                        Toast.makeText(CrewChatApplication.getInstance(), res.getString(R.string.favorite_remove_failed), Toast.LENGTH_LONG).show();
                    }
                });

                break;

            case Statics.ROOM_ADD_TO_FAVORITE:
                /*roomInfo = new Bundle();
                roomInfo.putInt(Statics.ROOM_NO, (int) roomNo);*/
                /*final Bundle finalRoomInfo = roomInfo;
                mOnContextMenuSelect.onSelect(Statics.ROOM_ADD_TO_FAVORITE, finalRoomInfo);*/

                HttpRequest.getInstance().addRoomToFavorite(roomNo, new BaseHTTPCallBack() {
                    @Override
                    public void onHTTPSuccess() {
                        Toast.makeText(CrewChatApplication.getInstance(), res.getString(R.string.favorite_add_success), Toast.LENGTH_LONG).show();
                        ivFavorite.setVisibility(View.VISIBLE);

                        ChatRomDBHelper.updateChatRoomFavorite(roomNo, true);
                        if (RecentFavoriteFragment.instance != null) {
                            RecentFavoriteFragment.instance.addFavorite(tempDto);
                        }
                        tempDto.setFavorite(true);
                    }

                    @Override
                    public void onHTTPFail(ErrorDto errorDto) {
                        Toast.makeText(CrewChatApplication.getInstance(), res.getString(R.string.favorite_add_success), Toast.LENGTH_LONG).show();
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
                roomInfo.putInt(Statics.ROOM_NO, (int) roomNo);
                mOnContextMenuSelect.onSelect(Statics.ROOM_LEFT, roomInfo);

                break;
        }
        return false;
    }
}
