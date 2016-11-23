package com.dazone.crewchat.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.dazone.crewchat.HTTPs.HttpRequest;
import com.dazone.crewchat.R;
import com.dazone.crewchat.activity.RenameRoomActivity;
import com.dazone.crewchat.activity.base.BaseActivity;
import com.dazone.crewchat.constant.Statics;
import com.dazone.crewchat.dto.ErrorDto;
import com.dazone.crewchat.interfaces.BaseHTTPCallBack;
import com.dazone.crewchat.interfaces.OnTickCallback;
import com.dazone.crewchat.interfaces.OnTickCallbackSuccess;
import com.dazone.crewchat.utils.CrewChatApplication;
import com.dazone.crewchat.utils.Utils;

/**
 * A simple {@link Fragment} subclass.
 */
public class RenameRoomFragmen extends BaseFragment {

    private View rootView;
    private TextView tvOldTitle, tvRemainCharacterCount;
    private EditText et_title;
    private Button btnClear;

    private String roomTitle;
    private int roomNo;

    private OnTickCallbackSuccess mTickSuccessCallback;
    public void setmTickSuccessCallback(OnTickCallbackSuccess mTickSuccessCallback) {
        this.mTickSuccessCallback = mTickSuccessCallback;
    }

    public RenameRoomFragmen() {
        // Required empty public constructor
    }

    public static RenameRoomFragmen newInstance(int roomNo, String roomTitle) {
        RenameRoomFragmen myFragment = new RenameRoomFragmen();
        Bundle args = new Bundle();
        args.putInt(Statics.ROOM_NO, roomNo);
        args.putString(Statics.ROOM_TITLE, roomTitle);
        myFragment.setArguments(args);
        return myFragment;
    }

    OnTickCallback callback = new OnTickCallback() {
        @Override
        public void onTick() {
            // request to API
            final String titleChanged = et_title.getText().toString().trim();
            if (roomTitle.compareTo(titleChanged) == 0){
                Toast.makeText(mContext, CrewChatApplication.getInstance().getResources().getString(R.string.warning_change_title), Toast.LENGTH_LONG).show();
                return;
            }

            HttpRequest.getInstance().updateChatRoomInfo(roomNo, titleChanged, new BaseHTTPCallBack() {
                @Override
                public void onHTTPSuccess() {
                    if (mTickSuccessCallback != null){
                        mTickSuccessCallback.onTickSuccess(roomNo, titleChanged);
                    }
                }

                @Override
                public void onHTTPFail(ErrorDto errorDto) {
                    Utils.printLogs("On update failed ###");
                }
            });
        }
    };


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((RenameRoomActivity)getActivity()).setmCallback(callback);
        // Get all bundle is parsed to here
        Bundle args = getArguments();
        if (args != null){
            roomTitle = args.getString(Statics.ROOM_TITLE, "");
            roomNo = args.getInt(Statics.ROOM_NO, -1);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_rename_room, container, false);
        initView();
        initData();
        return rootView;
    }

    private void initView(){
        tvRemainCharacterCount = (TextView) rootView.findViewById(R.id.tv_remain_character_count);
        et_title = (EditText) rootView.findViewById(R.id.et_title);
        btnClear = (Button) rootView.findViewById(R.id.btn_clear);
    }

    private void initData(){
        et_title.setText("");
        et_title.append(roomTitle);
        et_title.requestFocus();

        if (!TextUtils.isEmpty(et_title.getText().toString().trim())){
            btnClear.setVisibility(View.VISIBLE);
        }
        //set on text change listener for edittext
        et_title.addTextChangedListener(textWatcher());
        //set event for clear button
        btnClear.setOnClickListener(onClickListener());
    }

    private View.OnClickListener onClickListener() {
        return new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                et_title.setText(""); //clear edittext
            }
        };
    }

    private TextWatcher textWatcher() {
        return new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!et_title.getText().toString().equals("")) { //if edittext include text
                    btnClear.setVisibility(View.VISIBLE);
                    int remain = 50 - et_title.getText().length();
                    String remainText = remain+"/50";
                    tvRemainCharacterCount.setText(remainText);
                } else { //not include text
                    btnClear.setVisibility(View.GONE);
                }

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
    }
}
