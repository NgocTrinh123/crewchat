package com.dazone.crewchat.fragment;

import android.content.Context;
import android.support.v4.app.Fragment;

/**
 * Created by david on 12/23/15.
 */
public class BaseFragment extends Fragment {
    protected Context mContext;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }
}
