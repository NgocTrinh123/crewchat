package com.dazone.crewchat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.dazone.crewchat.R;
import com.dazone.crewchat.Tree.Dtos.TreeUserDTO;
import com.dazone.crewchat.activity.base.BaseActivity;
import com.dazone.crewchat.adapter.RoomUserInfoAdapter;
import com.dazone.crewchat.database.AllUserDBHelper;
import com.dazone.crewchat.database.UserDBHelper;
import com.dazone.crewchat.dto.BelongDepartmentDTO;
import com.dazone.crewchat.dto.TreeUserDTOTemp;
import com.dazone.crewchat.utils.CrewChatApplication;
import com.dazone.crewchat.utils.Prefs;
import com.dazone.crewchat.utils.Utils;

import java.util.ArrayList;

public class RoomUserInformationActivity extends BaseActivity {

    protected TextView toolbar_title;
    protected ImageView ivBack;
    private ArrayList<Integer> userNos;
    private RoomUserInfoAdapter mAdapter;
    public RecyclerView rvMainList;
    public RecyclerView.LayoutManager layoutManager;
    private ArrayList<TreeUserDTO> temp = new ArrayList<>();

    private String roomTitle = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_user_information);
        init();

        // Get bundle data
        Intent intent = getIntent();
        if (intent != null){

            roomTitle = intent.getStringExtra("roomTitle");
            userNos = intent.getIntegerArrayListExtra("userNos");

            ArrayList<TreeUserDTOTemp> users;
            if (CrewChatApplication.listUsers != null && CrewChatApplication.listUsers.size() > 0){
                users = CrewChatApplication.listUsers;
            } else {
                users = AllUserDBHelper.getUser();
            }
            int myID = Utils.getCurrentId();
            // init data
            if (userNos != null){
                String subtitle = CrewChatApplication.getInstance().getResources().getString(R.string.room_info_participant_count, userNos.size());
                toolbar_title.setText(subtitle);

                TreeUserDTO myUser = null;
                for (Integer userId : userNos){

                    for (TreeUserDTOTemp treeUserDTOTemp : users) {

                            if (userId == treeUserDTOTemp.getUserNo()){

                                Utils.printLogs("Add to Room ############");

                                if (treeUserDTOTemp.getBelongs() != null){

                                    String positionName = "";
                                    for (BelongDepartmentDTO belong : treeUserDTOTemp.getBelongs()){
                                        if (TextUtils.isEmpty(positionName)){
                                            positionName += belong.getPositionName();
                                        }else {
                                            positionName += ","+ belong.getPositionName();
                                        }
                                    }

                                    TreeUserDTO treeUserDTO = new TreeUserDTO(
                                            treeUserDTOTemp.getName(),
                                            treeUserDTOTemp.getNameEN(),
                                            treeUserDTOTemp.getCellPhone(),
                                            treeUserDTOTemp.getAvatarUrl(),
                                            positionName,
                                            treeUserDTOTemp.getType(),
                                            treeUserDTOTemp.getStatus(),
                                            treeUserDTOTemp.getUserNo(),
                                            treeUserDTOTemp.getDepartNo(),
                                            treeUserDTOTemp.getUserStatusString()
                                    );
                                    treeUserDTO.setCompanyNumber(treeUserDTOTemp.getCompanyPhone());

                                    if (userId == myID){
                                        if (myUser != null){
                                            temp.add(myUser);
                                            myUser = treeUserDTO;
                                        } else {
                                            myUser = treeUserDTO;
                                        }
                                    } else {
                                        temp.add(treeUserDTO);
                                    }
                                }

                            }

                    }

                }

                if (myUser != null){
                    temp.add(0, myUser);
                }

                //toolbar_title.setText(roomTitle);
                mAdapter = new RoomUserInfoAdapter(this, temp, rvMainList );
                rvMainList.setAdapter(mAdapter);
            }

        }

    }

    private void init(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        ivBack = (ImageView) findViewById(R.id.iv_back);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        toolbar_title = (TextView) findViewById(R.id.toolbar_title);
        rvMainList = (RecyclerView) findViewById(R.id.rv_main);

        rvMainList.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        rvMainList.setLayoutManager(layoutManager);

    }
}
