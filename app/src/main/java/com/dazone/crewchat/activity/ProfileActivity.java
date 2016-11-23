package com.dazone.crewchat.activity;

import android.os.Bundle;
import com.dazone.crewchat.R;
import com.dazone.crewchat.activity.base.BaseSingleBackActivity;
import com.dazone.crewchat.fragment.ProfileFragment;
import com.dazone.crewchat.interfaces.OnBackCallBack;
import com.dazone.crewchat.utils.Utils;

public class ProfileActivity extends BaseSingleBackActivity implements OnBackCallBack {
    ProfileFragment fragment;

    @Override
    protected void addFragment(Bundle bundle) {
        setTitle("");
        fragment = new ProfileFragment();
        fragment.setmCallback(this);
        /** ADD FRAGMENT TO ACTIVITY */
        Utils.addFragmentNotSuportV4ToActivity(getFragmentManager(), fragment, R.id.content_base_single_activity, false, fragment.getClass().getSimpleName());
    }


    @Override
    public void onBack() {
        finish();
    }
}
