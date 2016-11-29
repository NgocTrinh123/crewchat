package com.dazone.crewchat.activity;

import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.dazone.crewchat.HTTPs.GetUserStatus;
import com.dazone.crewchat.HTTPs.HttpRequest;
import com.dazone.crewchat.R;
import com.dazone.crewchat.activity.base.BasePagerActivity;
import com.dazone.crewchat.adapter.TabPagerAdapter;
import com.dazone.crewchat.constant.Statics;
import com.dazone.crewchat.database.AllUserDBHelper;
import com.dazone.crewchat.database.UserDBHelper;
import com.dazone.crewchat.dto.ErrorDto;
import com.dazone.crewchat.dto.StatusDto;
import com.dazone.crewchat.dto.StatusItemDto;
import com.dazone.crewchat.dto.TreeUserDTOTemp;
import com.dazone.crewchat.dto.UserInfoDto;
import com.dazone.crewchat.fragment.CompanyFragment;
import com.dazone.crewchat.interfaces.OnClickCallback;
import com.dazone.crewchat.interfaces.OnGetStatusCallback;
import com.dazone.crewchat.interfaces.OnGetUserInfo;
import com.dazone.crewchat.interfaces.Urls;
import com.dazone.crewchat.services.SyncStatusService;
import com.dazone.crewchat.utils.CrewChatApplication;
import com.dazone.crewchat.utils.Prefs;
import com.dazone.crewchat.utils.Utils;

import java.util.ArrayList;

/**
 * Created by david on 12/23/15.
 */
public class MainActivity extends BasePagerActivity implements ViewPager.OnPageChangeListener, ServiceConnection {

    private boolean doubleBackToExitPressedOnce = false;
    /**
     * Handler
     */
    protected Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            boolean aResponse = msg.getData().getBoolean("is_update");
            if (aResponse) {
                Utils.printLogs("Handler is received a message from another thread");

                int tab = msg.getData().getInt("tab");
                if (mGetStatusCallbackCompany != null && tab == TAB_COMPANY) {
                    mGetStatusCallbackCompany.onGetStatusFinish();
                }

                if (mGetStatusCallbackFavorite != null && tab == TAB_FAVORITE) {
                    mGetStatusCallbackFavorite.onGetStatusFinish();
                }
            }

        }
    };

    static boolean active = false;

    public static int TAB_CHAT = 0;
    public static int TAB_COMPANY = 1;
    public static int TAB_FAVORITE = 2;
    public static int TAB_SETTING = 3;
    private int currentUserNo = 0;

    private SyncStatusService syncStatusService = null;
    private boolean isBound = false;
    private int companyNo;

    private OnGetStatusCallback mGetStatusCallbackCompany, mGetStatusCallbackFavorite;


    public void setmGetStatusCallbackCompany(OnGetStatusCallback mGetStatusCallback) {
        this.mGetStatusCallbackCompany = mGetStatusCallback;
    }

    public void setmGetStatusCallbackFavorite(OnGetStatusCallback mGetStatusCallback) {
        this.mGetStatusCallbackFavorite = mGetStatusCallback;
    }

    @Override
    protected void init() {

        currentUserNo = Utils.getCurrentId();
        companyNo = new Prefs().getCompanyNo();

        active = true;
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        tabAdapter = new TabPagerAdapter(getSupportFragmentManager(), Statics.MAIN_ACTIVITY_TAB_COUNT, this);
        mViewPager.setAdapter(tabAdapter);
        mViewPager.setOffscreenPageLimit(4);
        tabLayout.setupWithViewPager(mViewPager);
        setupTab();
        setupViewPager();

        // Load client data after login
        if (!CrewChatApplication.isLoggedIn) {
            CrewChatApplication.getInstance().syncData();
            CrewChatApplication.getInstance().loadStaticLocalData();
            CrewChatApplication.isLoggedIn = true;
            CrewChatApplication.currentId = currentUserNo;
        }
        // Show FAB button at the first time
        showPAB(mAddUserCallback);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Bind service to sync status data
        Intent objIntent = new Intent(this, SyncStatusService.class);
        bindService(objIntent, this, Context.BIND_AUTO_CREATE);
    }

    protected void setupViewPager() {
        mViewPager.addOnPageChangeListener(this);
    }

    protected void setupTab() {
        if (tabLayout == null)
            return;
        View view = LayoutInflater.from(this).inflate(R.layout.custom_tab_view, null);
        tabLayout.getTabAt(0).setCustomView(view);
        tabLayout.getTabAt(1).setIcon(R.drawable.tabbar_group_ic);
        tabLayout.getTabAt(2).setIcon(R.drawable.nav_favorite_ic);
        tabLayout.getTabAt(3).setIcon(R.drawable.nav_mnu_hol_ic);
    }

    /**
     * OnBackPressed
     */
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, getResources().getString(R.string.press_again_to_exit_message), Toast.LENGTH_SHORT).show();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Utils.printLogs("Handler hold on event onBackPressed ###");
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    /**
     * OnPageChangeListener
     * 탭 화면이 스크롤 되어 질 때에 이벤트 처리(좌우), 화면에 보이는 검색 아이콘등을 설정
     */
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        Utils.hideKeyboard(this);
        Fragment page = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.container + ":" + mViewPager.getCurrentItem());
        // based on the current position you can then cast the page to the correct
        // class and call the method:
        // 탭 화면이 스크롤 되어 질 때에 이벤트 처리(좌우), 화면에 보이는 검색 아이콘등을 설정
        if (mViewPager.getCurrentItem() == TAB_CHAT && page != null) {
            if (menuItemSearch != null) {
                searchView.setIconified(true);
                searchView.setVisibility(View.GONE);
                menuItemMore.setVisible(false);
                menuItemSearch.collapseActionView();
                menuItemSearch.setVisible(false);
            }
        } else if (mViewPager.getCurrentItem() == TAB_COMPANY && page != null) {
            ((CompanyFragment) page).updateList();
            if (menuItemSearch != null && menuItemMore != null) {
                menuItemSearch.setVisible(true);
                searchView.setVisibility(View.VISIBLE);
            }
        } else if (mViewPager.getCurrentItem() == TAB_FAVORITE && page != null) {
            // todo something
            if (menuItemSearch != null && menuItemMore != null) {
                menuItemSearch.collapseActionView();
                menuItemSearch.setVisible(false);
            }
        } else {
            if (menuItemSearch != null && menuItemMore != null) {
                searchView.setIconified(true);
                searchView.setVisibility(View.GONE);
                menuItemMore.setVisible(false);
                menuItemSearch.collapseActionView();
                menuItemSearch.setVisible(false);
            }
        }
    }


    public void showMenuSearch(OnClickCallback callback) {
        if (menuItemSearch != null) {
            menuItemSearch.setVisible(true);
            searchView.setVisibility(View.VISIBLE);
        }
    }

    public void hideMenuSearch() {
        if (menuItemSearch != null) {
            searchView.setIconified(true);
            searchView.setVisibility(View.GONE);
            menuItemMore.setVisible(false);
            menuItemSearch.collapseActionView();
            menuItemSearch.setVisible(false);
        }
    }

    OnClickCallback mAddUserCallback = new OnClickCallback() {
        @Override
        public void onClick() {
            Intent intent = new Intent(MainActivity.this, OrganizationActivity.class);
            startActivity(intent);
        }
    };

    public int getCurrentTab() {
        return mViewPager.getCurrentItem();
    }


    // 탭을 직접 선택 했을 경우 이벤트 처리
    @Override
    public void onPageSelected(final int position) {
        Utils.hideKeyboard(this);
        if (position == TAB_CHAT) {
            showPAB(mAddUserCallback);
        } else if (position == TAB_COMPANY || position == TAB_SETTING) {
            hideSearchIcon();
            hidePAB();
        }
        // Get all user ID
        Utils.printLogs("Page position =" + position);
        if (position == TAB_COMPANY || position == TAB_FAVORITE) {
            // resuming data to get user status
            // New thread to update user status here, status running in background

            final ArrayList<TreeUserDTOTemp> users = AllUserDBHelper.getUser();
            boolean isStaticList = false;
            if (CrewChatApplication.listUsers != null && CrewChatApplication.listUsers.size() > 0) {
                isStaticList = true;
            }

            // 유저 상태정보 처리(온라인/자리비움등)
            final boolean finalIsStaticList = isStaticList;
            new Thread(new Runnable() {
                @Override
                public void run() {

                    // 상태값을 가져옵니다.
                    final StatusDto status = new GetUserStatus().getStatusOfUsers(Urls.HOST_STATUS, companyNo);
                    // Need to improve it
                    if (status != null) {
                        for (final TreeUserDTOTemp u : users) {
                            boolean isUpdate = false;
                            for (final StatusItemDto sItem : status.getItems()) {
                                if (sItem.getUserID().equals(u.getUserID())) {
                                    // Thread to update status
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            AllUserDBHelper.updateStatus(u.getDBId(), sItem.getStatus());

                                            if (finalIsStaticList) {
                                                boolean xUpdate = false;
                                                TreeUserDTOTemp temp = null;
                                                for (TreeUserDTOTemp uu : CrewChatApplication.listUsers) {
                                                    temp = uu;
                                                    if (sItem.getUserID().equals(uu.getUserID())) {
                                                        uu.setStatus(sItem.getStatus());
                                                        xUpdate = true;
                                                        break;
                                                    }

                                                }

                                                if (!xUpdate) {
                                                    if (temp != null) {
                                                        temp.setStatus(Statics.USER_LOGOUT);
                                                    }
                                                }

                                            }

                                        }
                                    }).start();

                                    isUpdate = true;
                                    break;
                                }
                            }

                            if (!isUpdate) {
                                if (u.getUserNo() == currentUserNo) {
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            // 내 상태값을 바꾼다.
                                            UserDBHelper.updateStatus(u.getUserNo(), Statics.USER_LOGOUT);
                                        }
                                    }).start();
                                }
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        // 나 이외의 사용자 상태값을 바꾼다.
                                        AllUserDBHelper.updateStatus(u.getDBId(), Statics.USER_LOGOUT);
                                    }
                                }).start();

                            }
                        }

                        // Send message to main thread after update status
                        Message msgObj = mHandler.obtainMessage();
                        Bundle b = new Bundle();
                        b.putBoolean("is_update", true);
                        if (position == TAB_COMPANY) {
                            b.putInt("tab", TAB_COMPANY);
                        } else {
                            b.putInt("tab", TAB_FAVORITE);
                        }

                        msgObj.setData(b);
                        mHandler.sendMessage(msgObj);
                    }

                }
            }).start();

            // 유저 상태메시지 처리
            HttpRequest.getInstance().getAllUserInfo(new OnGetUserInfo() {
                @Override
                public void OnSuccess(final ArrayList<UserInfoDto> userInfo) {

                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            // If list user is #null then update user status string
                            if (users == null) {
                                ArrayList<TreeUserDTOTemp> tempUsers = AllUserDBHelper.getUser();
                                for (TreeUserDTOTemp sItem : tempUsers) {
                                    for (UserInfoDto u : userInfo) {
                                        if (sItem.getUserNo() == u.getUserNo()) {
                                            AllUserDBHelper.updateStatusString(sItem.getDBId(), u.getStateMessage());
                                        }
                                    }
                                }
                            } else {
                                for (TreeUserDTOTemp sItem : users) {
                                    for (UserInfoDto u : userInfo) {
                                        if (sItem.getUserNo() == u.getUserNo()) {
                                            AllUserDBHelper.updateStatusString(sItem.getDBId(), u.getStateMessage());
                                            sItem.setUserStatusString(u.getStateMessage());
                                        }
                                    }

                                }
                            }

                            // Send message to main thread after update status
                            Message msgObj = mHandler.obtainMessage();
                            Bundle b = new Bundle();
                            b.putBoolean("is_update", true);
                            if (position == TAB_COMPANY) {
                                b.putInt("tab", TAB_COMPANY);
                            } else {
                                b.putInt("tab", TAB_FAVORITE);
                            }

                            msgObj.setData(b);
                            mHandler.sendMessage(msgObj);

                        }
                    }).start();


                }

                @Override
                public void OnFail(ErrorDto errorDto) {
                    Utils.printLogs("On get list user failed");
                }
            });
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public static void cancelAllNotification(Context ctx) {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager nMgr = (NotificationManager) ctx.getSystemService(ns);
        nMgr.cancelAll();
    }

    @Override
    protected void onDestroy() {
        cancelAllNotification(CrewChatApplication.getInstance());

        // if this service is not stop by self , let's stop it
        if (isBound) {
            unbindService(this);
        }

        super.onDestroy();
        active = false;
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        SyncStatusService.Binder binder = (SyncStatusService.Binder) service;
        syncStatusService = binder.getMyService();
        new Thread(new Runnable() {
            @Override
            public void run() {
                syncStatusService.syncData();
                syncStatusService.syncStatusString();
            }
        }).start();

        isBound = true;
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        isBound = false;
    }
}
