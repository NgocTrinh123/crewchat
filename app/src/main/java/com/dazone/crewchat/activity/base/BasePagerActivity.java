package com.dazone.crewchat.activity.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import com.dazone.crewchat.HTTPs.HttpOauthRequest;
import com.dazone.crewchat.HTTPs.HttpRequest;
import com.dazone.crewchat.R;
import com.dazone.crewchat.activity.LoginActivity;
import com.dazone.crewchat.adapter.TabPagerAdapter;
import com.dazone.crewchat.dto.ErrorDto;
import com.dazone.crewchat.interfaces.BaseHTTPCallBack;
import com.dazone.crewchat.interfaces.OnClickCallback;
import com.dazone.crewchat.utils.Constant;
import com.dazone.crewchat.utils.Prefs;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;

public abstract class BasePagerActivity extends BaseActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    protected TabPagerAdapter tabAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    protected ViewPager mViewPager;
    public TabLayout tabLayout;
    protected FloatingActionButton fab;

    /**
     * MENU ITEM
     */
    protected MenuItem menuItemSearch;
    protected MenuItem menuItemMore;
    protected SearchView searchView;

    protected ImageView ivSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_base_pager);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        tabAdapter = new TabPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(tabAdapter);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        ivSearch = (ImageView) findViewById(R.id.iv_search);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        hidePAB();
       /* fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                *//*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*//*
                Intent intent = new Intent(BasePagerActivity.this, OrganizationActivity.class);
                startActivity(intent);
            }
        });*/
        init();
    }

    // Show topmenubar saerch icon
    // 탑 메뉴바의 검색 아이콘을 표시
    public void showSearchIcon(final OnClickCallback callback){
        if (ivSearch != null){
            ivSearch.setVisibility(View.VISIBLE);
            ivSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   callback.onClick();
                }
            });
        }
    }

    // Hide topmenubar search icon(default)
    // 탑 메뉴바의 검색 아이콘을 숨김(기본)
    public void hideSearchIcon(){
        if (ivSearch != null){
            ivSearch.setVisibility(View.GONE);
        }
    }

    // FloatingActionButton Show
    // 하단 플로팅 액션 버튼을 보이게 설정
    public void showPAB(final OnClickCallback callback) {
        if (fab != null) {
            fab.setVisibility(View.VISIBLE);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    callback.onClick();
                }
            });
        }
    }

    // FloatingActionButton Hide
    // 하단 플로팅 액션 버튼을 안보이게 설정
    public void hidePAB() {
        if (fab != null) {
            fab.setVisibility(View.GONE);
        }
    }

    protected abstract void init();

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_base_pager, menu);
        menuItemSearch = menu.findItem(R.id.action_search);
        menuItemMore = menu.findItem(R.id.action_status);
        searchView = (SearchView) menuItemSearch.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent intentFinish = new Intent(Constant.INTENT_FILTER_SEARCH);
                intentFinish.putExtra(Constant.KEY_INTENT_TEXT_SEARCH, query);
                BasePagerActivity.this.sendBroadcast(intentFinish);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Intent intentFinish = new Intent(Constant.INTENT_FILTER_SEARCH);
                intentFinish.putExtra(Constant.KEY_INTENT_TEXT_SEARCH, newText);
                BasePagerActivity.this.sendBroadcast(intentFinish);
                return false;
            }
        });
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.action_logout) {
            String ids = new Prefs().getGCMregistrationid();
            if (!TextUtils.isEmpty(ids)) {
                HttpRequest.getInstance().DeleteDevice(ids, new BaseHTTPCallBack() {
                    @Override
                    public void onHTTPSuccess() {
                        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(getBaseContext());
                        try {

                            gcm.unregister();
                        } catch (IOException e) {
                            System.out.println("Error Message: " + e.getMessage());
                        }
                        new Prefs().setGCMregistrationid("");
                        HttpOauthRequest.getInstance().logout(new BaseHTTPCallBack() {
                            @Override
                            public void onHTTPSuccess() {
                                Intent intent = new Intent(BasePagerActivity.this, LoginActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                            }

                            @Override
                            public void onHTTPFail(ErrorDto errorDto) {

                            }
                        });
                    }

                    @Override
                    public void onHTTPFail(ErrorDto errorDto) {

                    }
                });
            } else {
                HttpOauthRequest.getInstance().logout(new BaseHTTPCallBack() {
                    @Override
                    public void onHTTPSuccess() {
                        Intent intent = new Intent(BasePagerActivity.this, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    }

                    @Override
                    public void onHTTPFail(ErrorDto errorDto) {

                    }
                });
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

   public void destroyFragment(){
       tabAdapter.destroyItem(mViewPager, 0, tabAdapter.getItem(0));
       tabAdapter.destroyItem(mViewPager, 1, tabAdapter.getItem(1));
       tabAdapter.destroyItem(mViewPager, 2, tabAdapter.getItem(2));
       tabAdapter.destroyItem(mViewPager, 3, tabAdapter.getItem(3));
   }
}
