package com.dazone.crewchat.adapter;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;
import com.dazone.crewchat.fragment.BaseFavoriteFragment;
import com.dazone.crewchat.fragment.CompanyFragment;
import com.dazone.crewchat.fragment.CurrentChatListFragment;
import com.dazone.crewchat.fragment.SettingFragment;

public class TabPagerAdapter extends FragmentPagerAdapter {
    int count = 4;
    Activity mContext;

    public TabPagerAdapter(FragmentManager fm, int count, Activity context) {
        super(fm);
        this.count = count;
        mContext = context;
    }
    public TabPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    // 해당 위치의 탭 정보( Fragment)
    @Override
    public Fragment getItem(int position) {
        switch (position)
        {
            // 채팅 리스트 탭
            case 0:
                return new CurrentChatListFragment();
            // 조직도 탭
            case 1:
                CompanyFragment companyFragment = new CompanyFragment();
                companyFragment.setContext(mContext);
                return companyFragment;
            // 즐겨찾기 탭
            case 2:
                /*FavoriteListFragment favoriteListFragment = new FavoriteListFragment();
                favoriteListFragment.setContext(mContext);
                return  favoriteListFragment;*/

                BaseFavoriteFragment fragment = new BaseFavoriteFragment();
                fragment.setContext(mContext);

                return fragment;
            // 환경설정 탭
            case 3:
                return new SettingFragment();
            // 기본(채팅리스트)
            default:
                return new CurrentChatListFragment();
        }
    }

    // 해당 탭을 데이터 삭제
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
    }

    // 탭의 전체 갯수
    @Override
    public int getCount() {
        return count;
    }

    // 사용하지 않음
    @Override
    public CharSequence getPageTitle(int position) {
        return null;
    }
}
