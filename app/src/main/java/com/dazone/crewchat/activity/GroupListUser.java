package com.dazone.crewchat.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;

import com.dazone.crewchat.activity.base.BaseSingleActivity;
import com.dazone.crewchat.constant.Statics;
import com.dazone.crewchat.database.AllUserDBHelper;
import com.dazone.crewchat.database.UserDBHelper;
import com.dazone.crewchat.dto.ChattingDto;
import com.dazone.crewchat.dto.TreeUserDTOTemp;
import com.dazone.crewchat.dto.UserDto;
import com.dazone.crewchat.fragment.GroupListFragment;
import com.dazone.crewchat.R;
import com.dazone.crewchat.utils.Constant;
import com.dazone.crewchat.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by THANHTUNG on 04/03/2016.
 */
public class GroupListUser extends BaseSingleActivity implements View.OnClickListener {
    ChattingDto chattingDto;
    GroupListFragment fragment = new GroupListFragment();
    private ArrayList<TreeUserDTOTemp> listTemp = AllUserDBHelper.getUser();

    private ArrayList<Integer> userNos = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }

    private void initToolBar() {
        setTitle("Group List");
        HideBtnMore();
        HideStatus();

        /** MENU ITEM CALL */
        ivCall.setOnClickListener(this);
        ivCall.setVisibility(View.GONE);

        /** SETUP CALL VISIBLE */
        ivCall.setVisibility(Utils.isCallVisible(userNos) ? View.VISIBLE : View.GONE);

    }

    @Override
    protected void addFragment(Bundle bundle) {
        Bundle bundle1 = getIntent().getExtras();
        if (bundle1 != null) {
            try {
                userNos = bundle1.getIntegerArrayList(Constant.KEY_INTENT_USER_NO_ARRAY);
                chattingDto = (ChattingDto) bundle1.getSerializable(Statics.CHATTING_DTO_FOR_GROUP_LIST);
            } catch (Exception e) {
                chattingDto = null;
                e.printStackTrace();
            }
        }

        if (bundle == null) {
            Utils.addFragmentToActivity(getSupportFragmentManager(), fragment.instance(userNos), R.id.content_base_single_activity, false);
        }
        initToolBar();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.call_menu:
                showCallList();
                break;
        }
    }

    /**
     * SHOW CALL MENU
     */
    private void showCallList() {

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(GroupListUser.this);
        builderSingle.setTitle("Call");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                GroupListUser.this,
                R.layout.row_chatting_call);

        Utils.addCallArray(userNos, arrayAdapter);

        builderSingle.setNegativeButton(
                "cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builderSingle.setAdapter(
                arrayAdapter,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String phoneNumber = GetPhoneNumber(arrayAdapter.getItem(which));
                        Utils.CallPhone(GroupListUser.this, phoneNumber);
                    }
                });
        AlertDialog dialog = builderSingle.create();
        if (arrayAdapter.getCount() > 0) {
            dialog.show();
        }


        Button b = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        if (b != null) {
            b.setTextColor(ContextCompat.getColor(mContext, R.color.light_black));
        }
    }

    private String GetPhoneNumber(String strPhone) {
        String result = strPhone.split("\\(")[1];
        result = result.split("\\)")[0];
        return result;
    }

}
