package com.dazone.crewchat.activity.base;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.dazone.crewchat.adapter.TabPagerAdapter;
import com.dazone.crewchat.R;

public abstract class BaseDrawerPagerActivity extends BaseDrawerActivity {


    protected TabPagerAdapter tabAdapter;

    protected ViewPager mViewPager;
    protected TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        content_main.setVisibility(View.GONE);
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setVisibility(View.VISIBLE);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setVisibility(View.VISIBLE);
        init();
    }
    protected abstract void init();
}
