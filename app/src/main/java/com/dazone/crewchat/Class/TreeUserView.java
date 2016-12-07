package com.dazone.crewchat.Class;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Message;
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
import com.dazone.crewchat.Tree.Dtos.TreeUserDTO;
import com.dazone.crewchat.activity.ChattingActivity;
import com.dazone.crewchat.activity.base.BaseActivity;
import com.dazone.crewchat.constant.Statics;
import com.dazone.crewchat.database.AllUserDBHelper;
import com.dazone.crewchat.database.FavoriteUserDBHelper;
import com.dazone.crewchat.dto.ChattingDto;
import com.dazone.crewchat.dto.ErrorDto;
import com.dazone.crewchat.dto.TreeUserDTOTemp;
import com.dazone.crewchat.interfaces.BaseHTTPCallbackWithJson;
import com.dazone.crewchat.interfaces.ICreateOneUserChatRom;
import com.dazone.crewchat.utils.Constant;
import com.dazone.crewchat.utils.CrewChatApplication;
import com.dazone.crewchat.utils.ImageUtils;
import com.dazone.crewchat.utils.Utils;

import java.util.HashMap;

/**
 * Created by david on 1/4/16.
 */
public class TreeUserView extends TreeView implements View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener{

    private ImageView avatar_imv;
    private ImageView status_imv;
    private TextView position,tvPhone1,tvPhone2;
    private LinearLayout lnItemWraper,lnPhone;
    private HashMap<Integer, ImageView> myMap;
    private int marginLeft = 0;
    private TextView tv_work_phone, tv_personal_phone;
    TreeUserDTOTemp user;

    public TreeUserView(Context context, TreeUserDTO dto) {
        super(context, dto);
        setupView();
    }

    public TreeUserView(Context context, TreeUserDTO dto, HashMap<Integer, ImageView> statusViewMap, int marginLeft) {
        super(context, dto);
        this.myMap = statusViewMap;
        this.marginLeft = marginLeft;
        setupView();
    }

    protected final android.os.Handler mHandler = new android.os.Handler() {
        public void handleMessage(Message msg) {
            if(msg.what == 1) {
                if (user != null){
                    dto.setName(user.getName());
                    dto.setNameEN(user.getNameEN());
                    dto.setCompanyNumber(user.getCellPhone());
                    dto.setAvatarUrl(user.getAvatarUrl());
                    dto.setPosition(user.getPosition());
                    dto.setType(user.getType());
                    dto.setStatus(user.getStatus());
                    dto.setId(user.getUserNo());
                    dto.setParent(user.getDepartNo());
                    dto.setCompanyNumber(user.getCompanyPhone());
                    dto.setPhoneNumber(user.getCellPhone());
                    dto.setStatus(user.getStatus());
                    dto.setStatusString(user.getUserStatusString());

                    // Update view
                    ImageUtils.showRoundImage(dto, avatar_imv);

                    title.setText(dto.getItemName());
                    position.setText(dto.getPosition());

                    setupStatusImage();
                    if (TextUtils.isEmpty(dto.getPhoneNumber())) {
                        tvPhone1.setVisibility(View.GONE);
                    } else {
                        tvPhone1.setText(dto.getPhoneNumber());

                    }
                    if (TextUtils.isEmpty(dto.getCompanyNumber())) {
                        tvPhone2.setVisibility(View.GONE);
                    } else {
                        tvPhone2.setText(dto.getCompanyNumber());
                    }
                }
            }
        }
    };

    @Override
    public void setupView() {
        currentView = inflater.inflate(R.layout.tree_user_row, null);
        avatar_imv = (ImageView) currentView.findViewById(R.id.avatar);
        status_imv = (ImageView) currentView.findViewById(R.id.status_imv);
        title = (TextView) currentView.findViewById(R.id.name);
        position = (TextView) currentView.findViewById(R.id.position);
        lnItemWraper = (LinearLayout) currentView.findViewById(R.id.item_org_wrapper);
        //status_tv = (TextView) currentView.findViewById(R.id.status_tv);
        //checkBox = (CheckBox) currentView.findViewById(R.id.row_check);
        tv_work_phone = (TextView) currentView.findViewById(R.id.tv_work_phone);
        tv_personal_phone = (TextView) currentView.findViewById(R.id.tv_personal_phone) ;
        tvPhone1 = (TextView) currentView.findViewById(R.id.tv_phone_1);
        tvPhone2 = (TextView) currentView.findViewById(R.id.tv_phone_2);
        lnPhone = (LinearLayout) currentView.findViewById(R.id.ln_phone);
        main = (RelativeLayout) currentView.findViewById(R.id.mainParent);

        currentView.setOnCreateContextMenuListener(this);
        currentView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                v.showContextMenu();
                return true;
            }
        });

        binData();
    }

    private void binData() {
        if (dto == null)
            return;

     /*   if (dto.getName().length() == 0) { // May be it is favorite user

            user  = AllUserDBHelper.getAUser(dto.getId());

            if (user != null) {
                dto.setName(user.getName());
                dto.setNameEN(user.getNameEN());
                dto.setCompanyNumber(user.getCellPhone());
                dto.setAvatarUrl(user.getAvatarUrl());
                dto.setPosition(user.getPosition());
                dto.setType(user.getType());
                dto.setStatus(user.getStatus());
                dto.setId(user.getUserNo());
                dto.setParent(user.getDepartNo());
                dto.setCompanyNumber(user.getCompanyPhone());
                dto.setPhoneNumber(user.getCellPhone());
                dto.setStatus(user.getStatus());
                dto.setStatusString(user.getUserStatusString());

            }
        }else{
            Utils.printLogs("Hix hix");
        }*/

        user  = AllUserDBHelper.getAUser(dto.getId());

        if (user != null) {
            dto.setName(user.getName());
            dto.setNameEN(user.getNameEN());
            dto.setCompanyNumber(user.getCellPhone());
            dto.setAvatarUrl(user.getAvatarUrl());
            dto.setPosition(user.getPosition());
            dto.setType(user.getType());
            dto.setStatus(user.getStatus());
            dto.setId(user.getUserNo());
            dto.setCompanyNumber(user.getCompanyPhone());
            dto.setPhoneNumber(user.getCellPhone());
            dto.setStatus(user.getStatus());
            dto.setStatusString(user.getUserStatusString());

        }

        fillData();

    }

    private void fillData(){

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) lnItemWraper.getLayoutParams();
        params.leftMargin = marginLeft;

        ImageUtils.showRoundImage(dto, avatar_imv);


        title.setText(dto.getItemName());
        position.setText(dto.getPosition());

        /*if (TextUtils.isEmpty(dto.getCompanyNumber())){
            tv_work_phone.setVisibility(View.GONE);
        }else{
            tv_work_phone.setVisibility(View.VISIBLE);
            tv_work_phone.setText(dto.getCompanyNumber());
        }

        if (TextUtils.isEmpty(dto.getPhoneNumber())){
            tv_personal_phone.setVisibility(View.GONE);
        }else{
            tv_personal_phone.setVisibility(View.VISIBLE);
            tv_personal_phone.setText(dto.getPhoneNumber());
        }*/

//        if (TextUtils.isEmpty(dto.getStatusString())){
//            tv_user_status.setVisibility(View.GONE);
//            Utils.printLogs("Status string = Rong");
//        }else{
//            tv_user_status.setVisibility(View.VISIBLE);
//            Utils.printLogs("Status string = "+dto.getStatusString());
//            tv_user_status.setText(dto.getStatusString());
//        }

        Utils.printLogs("Dto status = "+dto.getStatus());

        setupStatusImage();
        handleItemClick(true);
        avatar_imv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        // hold status view
        if (myMap != null){
            myMap.put(dto.getId(), status_imv);
        }
    }

    private void setupStatusImage() {

        switch (dto.getStatus())
        {
            case Statics.USER_LOGIN:
                status_imv.setImageResource(R.drawable.home_big_status_01);
                break;
            case Statics.USER_AWAY:
                status_imv.setImageResource(R.drawable.home_big_status_02);
                break;
            case Statics.USER_LOGOUT:
                status_imv.setImageResource(R.drawable.home_big_status_03);
                break;
            default:
                status_imv.setImageResource(R.drawable.home_big_status_03);
                break;
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (menu.size() == 0){
            Resources res = CrewChatApplication.getInstance().getResources();
            MenuItem removeFavorite = menu.add(0, Statics.MENU_REMOVE_FROM_FAVORITE, 0, res.getString(R.string.remove_from_favorite));
            MenuItem openChatRoom = menu.add(0, Statics.MENU_OPEN_CHAT_ROOM, 0, res.getString(R.string.open_chat_room));

            removeFavorite.setOnMenuItemClickListener(this);
            openChatRoom.setOnMenuItemClickListener(this);
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case Statics.MENU_REMOVE_FROM_FAVORITE:
                // Call API to remove an user from favorite list
                Utils.printLogs("GroupNo = "+dto.getParent()+" userNo = "+dto.getId()+" name = "+dto.getName());
                HttpRequest.getInstance().deleteFavoriteUser(dto.getParent(), dto.getId(), new BaseHTTPCallbackWithJson() {
                    @Override
                    public void onHTTPSuccess(String jsonData) {
                        FavoriteUserDBHelper.deleteFavoriteUser(dto.getParent(), dto.getId());
                        Utils.printLogs("Delete user from favorite success ###");
                        Toast.makeText(CrewChatApplication.getInstance(), "Has deleted", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onHTTPFail(ErrorDto errorDto) {
                        Utils.printLogs("Delete user from favorite failed ###");
                        Toast.makeText(CrewChatApplication.getInstance(), "Has failed", Toast.LENGTH_LONG).show();
                    }
                });

                break;
            case Statics.MENU_OPEN_CHAT_ROOM:

                if (dto.getId() != Utils.getCurrentId())
                    HttpRequest.getInstance().CreateOneUserChatRoom(dto.getId(), new ICreateOneUserChatRom() {
                        @Override
                        public void onICreateOneUserChatRomSuccess(ChattingDto chattingDto) {
                            Intent intent = new Intent(BaseActivity.Instance, ChattingActivity.class);
                            intent.putExtra(Constant.KEY_INTENT_ROOM_NO, chattingDto.getRoomNo());
                            intent.putExtra(Statics.TREE_USER_PC, dto);
                            intent.putExtra(Statics.CHATTING_DTO, chattingDto);
                            BaseActivity.Instance.startActivity(intent);
                        }

                        @Override
                        public void onICreateOneUserChatRomFail(ErrorDto errorDto) {
                            Utils.showMessageShort("Fail");
                        }
                    });
                else
                    Utils.showMessage(Utils.getString(R.string.can_not_chat));

                break;
        }

        return false;
    }
}
