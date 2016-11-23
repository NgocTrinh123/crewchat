package com.dazone.crewchat.activity;

import android.content.Intent;
import android.os.Bundle;
import com.dazone.crewchat.R;
import com.dazone.crewchat.activity.base.BaseSingleBackTick;
import com.dazone.crewchat.constant.Statics;
import com.dazone.crewchat.fragment.RenameRoomFragmen;
import com.dazone.crewchat.interfaces.OnTickCallbackSuccess;
import com.dazone.crewchat.utils.Utils;

public class RenameRoomActivity extends BaseSingleBackTick {

    private String roomTitle;
    private int roomNo;
    OnTickCallbackSuccess mOnTickSuccess = new OnTickCallbackSuccess() {
        @Override
        public void onTickSuccess(int roomNo, String roomTitle) {
            Intent intent = new Intent();
            intent.putExtra(Statics.ROOM_NO, roomNo);
            intent.putExtra(Statics.ROOM_TITLE, roomTitle);
            setResult(RESULT_OK, intent);
            finish();
        }
    };

    RenameRoomFragmen fragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUPToolBar(getString(R.string.room_rename));


    }

    @Override
    protected void addFragment(Bundle bundle) {

        Intent intent = getIntent();
        if (intent != null){
            Bundle b = intent.getExtras();
            roomTitle = b.getString(Statics.ROOM_TITLE, "");
            roomNo = b.getInt(Statics.ROOM_NO, -1);
            Utils.printLogs("Room title = "+roomTitle+" room No = "+roomNo);
        }
        fragment = RenameRoomFragmen.newInstance(roomNo, roomTitle);
        fragment.setmTickSuccessCallback(mOnTickSuccess);
        Utils.addFragmentToActivity(getSupportFragmentManager(), fragment, R.id.content_base_single_activity, false, fragment.getClass().getSimpleName());
    }

    // Need to check
}
