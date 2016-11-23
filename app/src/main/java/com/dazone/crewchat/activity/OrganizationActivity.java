package com.dazone.crewchat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import com.dazone.crewchat.HTTPs.HttpRequest;
import com.dazone.crewchat.R;
import com.dazone.crewchat.Tree.Dtos.TreeUserDTO;
import com.dazone.crewchat.activity.base.BaseSingleActivity;
import com.dazone.crewchat.constant.Statics;
import com.dazone.crewchat.database.AllUserDBHelper;
import com.dazone.crewchat.database.UserDBHelper;
import com.dazone.crewchat.dto.ChattingDto;
import com.dazone.crewchat.dto.ErrorDto;
import com.dazone.crewchat.dto.TreeUserDTOTemp;
import com.dazone.crewchat.interfaces.BaseHTTPCallBack;
import com.dazone.crewchat.interfaces.ICreateOneUserChatRom;
import com.dazone.crewchat.test.OrganizationFragment;
import com.dazone.crewchat.utils.Constant;
import com.dazone.crewchat.utils.Prefs;
import com.dazone.crewchat.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by THANHTUNG on 22/02/2016.
 */
public class OrganizationActivity extends BaseSingleActivity {
    OrganizationFragment fragment;
    private long task = -1;
    private int countMember = 0;
    private ArrayList<Integer> userNos;
    private String oldTitle = "";
    int currentUserNo = 0;

    static {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    @Override
    protected void addFragment(Bundle bundle) {
        Bundle bundle1 = getIntent().getExtras();
        if (bundle1 != null) {
            try {
                task = bundle1.getLong(Constant.KEY_INTENT_ROOM_NO);
                userNos = bundle1.getIntegerArrayList(Constant.KEY_INTENT_COUNT_MEMBER);
                oldTitle = bundle1.getString(Constant.KEY_INTENT_ROOM_TITLE);
            } catch (Exception e) {
                task = -1;
                countMember = 0;
                e.printStackTrace();
            }
        }
        fragment = OrganizationFragment.newInstance(userNos, false);
        if (bundle == null) {
            Utils.addFragmentToActivity(getSupportFragmentManager(), fragment, R.id.content_base_single_activity, false);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showSave();
        HiddenTitle();
        currentUserNo = new Prefs().getUserNo();
        if (currentUserNo == 0) {
            currentUserNo =  UserDBHelper.getUser().Id;
        }
        ivMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fragment != null) {
                    if (task == -1) {
                        fragment.callChat();
                        finish();
                    } else {
                        ArrayList<TreeUserDTO> list = fragment.getListUser();
                        if (list != null && list.size() > 0) {
                            if (userNos != null && userNos.size() == 2) {
                                List<Integer> listUserNos = new ArrayList<>();
                                for (int i : userNos) {
                                    if (i != currentUserNo ) {
                                        listUserNos.add(i);
                                    }
                                }
                                for (TreeUserDTO treeUserDTO : list) {
                                    boolean idAdd = true;
                                    for (int i : listUserNos) {
                                        if (i == treeUserDTO.getId()) {
                                            idAdd = false;
                                            break;
                                        }
                                    }
                                    if (idAdd) {
                                        listUserNos.add(treeUserDTO.getId());

                                        // Combine title for new user added to group
                                        TreeUserDTOTemp temp = AllUserDBHelper.getAUser(treeUserDTO.getId());
                                        if (temp != null && treeUserDTO.getId() != currentUserNo){
                                            oldTitle += "," + temp.getName();
                                        }
                                    }
                                }
                                ivMore.setOnClickListener(null);
                                if (listUserNos.size() == 1) {
                                    Utils.showMessageShort("User has been added");
                                    finish();
                                } else {

                                    HttpRequest.getInstance().CreateGroupChatRoom(listUserNos, new ICreateOneUserChatRom() {
                                        @Override
                                        public void onICreateOneUserChatRomSuccess(ChattingDto chattingDto) {

                                            // Update room title here
                                            HttpRequest.getInstance().updateChatRoomInfo((int) chattingDto.getRoomNo(), oldTitle, new BaseHTTPCallBack() {
                                                @Override
                                                public void onHTTPSuccess() {

                                                }

                                                @Override
                                                public void onHTTPFail(ErrorDto errorDto) {
                                                    Utils.printLogs("On update failed ###");
                                                }
                                            });

                                            // Start new activity
                                            Intent intent = new Intent();
                                            intent.putExtra(Constant.KEY_INTENT_CHATTING_DTO, chattingDto);
                                            intent.putExtra(Constant.KEY_INTENT_ROOM_TITLE, oldTitle);
                                            setResult(Constant.INTENT_RESULT_CREATE_NEW_ROOM, intent);
                                            finish();
                                        }

                                        @Override
                                        public void onICreateOneUserChatRomFail(ErrorDto errorDto) {
                                            Utils.showMessageShort("Fail");
                                        }
                                    });
                                }
                            } else {
                                if (userNos != null) {
                                    for (int i : userNos) {
                                        for (TreeUserDTO treeUserDTO : list) {
                                            if (treeUserDTO.getId() == i) {
                                                list.remove(treeUserDTO);
                                                break;
                                            }
                                        }
                                    }
                                    if (list.size() > 0) {
                                        final ArrayList<Integer> test = new ArrayList<>();

                                        for (TreeUserDTO treeUserDTO : list) {
                                            test.add(treeUserDTO.getId());
                                        }
                                        HttpRequest.getInstance().AddChatRoomUser(list, task, new BaseHTTPCallBack() {
                                            @Override
                                            public void onHTTPSuccess() {
                                                Bundle conData = new Bundle();
                                                conData.putInt(Statics.CHATTING_DTO_ADD_USER_NEW, 1);
                                                conData.putIntegerArrayList(Constant.KEY_INTENT_USER_NO_ARRAY, test);
                                                Intent intent = new Intent();
                                                intent.putExtras(conData);
                                                setResult(RESULT_OK, intent);
                                                finish();
                                            }

                                            @Override
                                            public void onHTTPFail(ErrorDto errorDto) {
                                                Utils.showMessage("Now, Only apply for group!");
                                            }
                                        });
                                    } else {
                                        Utils.showMessage("Added.");
                                        finish();
                                    }
                                } else {
                                    Utils.showMessage("Now, Only apply for group!");
                                    finish();
                                }
                            }
                        }
                    }
                }

            }
        });
    }
}
