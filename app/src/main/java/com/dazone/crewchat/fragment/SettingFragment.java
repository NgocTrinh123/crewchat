package com.dazone.crewchat.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import com.dazone.crewchat.HTTPs.HttpOauthRequest;
import com.dazone.crewchat.HTTPs.HttpRequest;
import com.dazone.crewchat.R;
import com.dazone.crewchat.activity.LoginActivity;
import com.dazone.crewchat.activity.MainActivity;
import com.dazone.crewchat.activity.NotificationSettingActivity;
import com.dazone.crewchat.activity.ProfileActivity;
import com.dazone.crewchat.customs.AlertDialogView;
import com.dazone.crewchat.customs.CustomTextView;
import com.dazone.crewchat.database.*;
import com.dazone.crewchat.dto.ErrorDto;
import com.dazone.crewchat.dto.UserDto;
import com.dazone.crewchat.interfaces.BaseHTTPCallBack;
import com.dazone.crewchat.utils.*;

/**
 * Created by THANHTUNG on 29/02/2016.
 */
public class SettingFragment extends BaseFragment implements View.OnClickListener {

    private View mView;
    private UserDto userDBHelper;
    private Context mContext;
    public Prefs prefs;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userDBHelper = UserDBHelper.getUser();
        prefs = CrewChatApplication.getInstance().getmPrefs();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.chat_setting, container, false);
        initSettingGroup();
        return mView;
    }

    private CustomTextView tvGeneralSetting, tvLogout, tvUserName;
    private CustomTextView tvNotificationSettings;
    private ImageView mAvatar;

    private void initSettingGroup() {

        tvGeneralSetting = (CustomTextView) mView.findViewById(R.id.tv_general_setting);
        tvGeneralSetting.setOnClickListener(this);
        tvLogout = (CustomTextView) mView.findViewById(R.id.tv_logout);
        tvLogout.setOnClickListener(this);
        tvNotificationSettings = (CustomTextView) mView.findViewById(R.id.tv_notification_settings);
        tvNotificationSettings.setOnClickListener(this);
        tvUserName = (CustomTextView) mView.findViewById(R.id.tv_username);
        tvUserName.setOnClickListener(this);

        mAvatar = (ImageView) mView.findViewById(R.id.iv_avatar);
        mAvatar.setOnClickListener(this);


        String url = prefs.getServerSite() + prefs.getAvatarUrl();
        ImageUtils.showCycleImageSquareFromLink(url, mAvatar, R.dimen.button_height);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_username:
                goProfile();
                break;
            case R.id.iv_avatar:
                goProfile();
                break;

            case R.id.tv_notification_settings:
                Intent intent = new Intent(mContext, NotificationSettingActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

            case R.id.tv_general_setting:
                generalSetting();
                break;
            case R.id.tv_logout:
                logout();
                break;
        }
    }

    private void goProfile(){
        Intent intent = new Intent(mContext, ProfileActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Bundle bundle = new Bundle();
        bundle.putInt(Constant.KEY_INTENT_USER_NO, userDBHelper.getId());
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void generalSetting(){
        Utils.printLogs("General setting called");
    }



    private void logout(){
        // Show logout confirm
        AlertDialogView.normalAlertDialogWithCancel(mContext, Utils.getString(R.string.logout_confirm_title),Utils.getString(R.string.logout_confirm), Utils.getString(R.string.no), Utils.getString(R.string.yes) , new AlertDialogView.OnAlertDialogViewClickEvent(){

            @Override
            public void onOkClick(DialogInterface alertDialog) {
                doLogout();
            }

            @Override
            public void onCancelClick() {

            }
        });
    }

    private void doLogout(){

        String ids = new Prefs().getGCMregistrationid();
        if (!TextUtils.isEmpty(ids)) {
            HttpRequest.getInstance().DeleteDevice(ids, new BaseHTTPCallBack() {
                @Override
                public void onHTTPSuccess() {

                    HttpOauthRequest.getInstance().logout(new BaseHTTPCallBack() {
                        @Override
                        public void onHTTPSuccess() {

                            // New thread to clear all cache
                            new Thread(new Runnable() {
                                @Override
                                public void run() {

                                    BelongsToDBHelper.clearBelong();
                                    AllUserDBHelper.clearUser();
                                    ChatRomDBHelper.clearChatRooms();
                                    ChatMessageDBHelper.clearMessages();
                                    DepartmentDBHelper.clearDepartment();
                                    UserDBHelper.clearUser();
                                    FavoriteGroupDBHelper.clearGroups();
                                    FavoriteUserDBHelper.clearFavorites();
                                    // CrewChatApplication.getInstance().getmPrefs().clear();
                                    CrewChatApplication.resetValue();
                                    CrewChatApplication.isLoggedIn = false;

                                }
                            }).start();

                            // Finish current activity to start new activity
                            ((MainActivity) getActivity()).destroyFragment();
                            getActivity().finish();

                            Intent intent = new Intent(mContext, LoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                            startActivity(intent);
                        }

                        @Override
                        public void onHTTPFail(ErrorDto errorDto) {
                            Utils.printLogs("Login failed, please check your server or network and try again");
                            Toast.makeText(mContext, "Logout failed !", Toast.LENGTH_LONG).show();
                        }
                    });

                }

                @Override
                public void onHTTPFail(ErrorDto errorDto) {
                    Toast.makeText(mContext, "Logout failed !", Toast.LENGTH_LONG).show();
                }
            });
        }


    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }
}
