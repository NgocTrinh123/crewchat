package com.dazone.crewchat.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import com.dazone.crewchat.R;
import com.dazone.crewchat.activity.base.BaseSingleBackActivity;
import com.dazone.crewchat.fragment.SettingNotificationFragment;

public class NotificationSettingActivity extends BaseSingleBackActivity {

    private SettingNotificationFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUPToolBar(getString(R.string.settings_notification));
    }

    @Override
    protected void addFragment(Bundle bundle) {
        /** Setup FRAGMENT*/
        fragment = new SettingNotificationFragment();
        /** ADD FRAGMENT TO ACTIVITY */
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.content_base_single_activity, fragment, fragment.getClass().getSimpleName());
        transaction.commit();
        //Utils.addFragmentToActivity(getSupportFragmentManager(), fragment, R.id.content_base_single_activity, false, fragment.getClass().getSimpleName());
    }
}
