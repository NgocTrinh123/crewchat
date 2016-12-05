package com.dazone.crewchat.fragment;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.dazone.crewchat.R;
import com.dazone.crewchat.TestMultiLevelListview.MultilLevelListviewFragment;
import com.dazone.crewchat.activity.MainActivity;
import com.dazone.crewchat.constant.Statics;
import com.dazone.crewchat.customs.DisableSwipeViewpager;
import com.dazone.crewchat.interfaces.OnClickCallback;
import com.dazone.crewchat.utils.Utils;

/**
 * A simple {@link Fragment} subclass.
 */
public class BaseFavoriteFragment extends Fragment implements TabLayout.OnTabSelectedListener {

    private View rootView;
    DisableSwipeViewpager pager;
    TabLayout tabLayout;
    private Activity mContext;
    private boolean isCreated = false;

    public BaseFavoriteFragment() {
        // Required empty public constructor
    }

    public void setContext(Activity context) {
        mContext = context;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_base_favorite, container, false);

        pager = (DisableSwipeViewpager) rootView.findViewById(R.id.pager);
        tabLayout= (TabLayout) rootView.findViewById(R.id.tabLayout);
            initChildPage();
        // Hide search icon for tab favorite chat room
        if (pager.getCurrentItem() == 0) {
            showIcon();
            if (getActivity() != null && getActivity() instanceof MainActivity)
                ((MainActivity) getActivity()).hidePAB();
            ((MainActivity) getActivity()).hideMenuSearch();
        } else {
            hideIcon();

            if (getActivity() != null && getActivity() instanceof MainActivity)
                ((MainActivity) getActivity()).hidePAB();
            //  on show callback here
            if (MultilLevelListviewFragment.instance != null) {
                MultilLevelListviewFragment.instance.showFAB();
            }

                /*((MainActivity)getActivity()).showMenuSearch(new OnClickCallback() {
                    @Override
                    public void onClick() {
                        // todo something
                    }
                });*/

            showSearchFavorite();

        }
        // select tab favorite chat room
       /* new Handler().postDelayed(
                new Runnable(){
                    @Override
                    public void run() {
                        try {
                            tabLayout.getTabAt(0).select();

                            ImageView icon = (ImageView)tabLayout.getTabAt(1).getCustomView().findViewById(R.id.iv_icon_right);
                            icon.setImageResource(R.drawable.tabbar_group_ic_blue);
                            ImageView iconLeft = (ImageView)tabLayout.getTabAt(0).getCustomView().findViewById(R.id.iv_icon_left);
                            iconLeft.setImageResource(R.drawable.nav_chat_ic);

                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }
                }, 100);*/

        return rootView;
    }

    private void initChildPage() {


        if (!isCreated) {
            isCreated = true;

            FragmentManager manager = getChildFragmentManager();
            PagerAdapter adapter = new PagerAdapter(manager);
            pager.setAdapter(adapter);

            pager.setCurrentItem(0);
            tabLayout.setOnTabSelectedListener(this);
        }

    }

    boolean isShowSreachIcon = false;

//    @Override
//    public void setUserVisibleHint(boolean isVisibleToUser) {
//        super.setUserVisibleHint(isVisibleToUser);
//        if (isVisibleToUser) {
//            initChildPage();
//            // Hide search icon for tab favorite chat room
//            if (pager.getCurrentItem() == 0) {
//                showIcon();
//                if (getActivity() != null && getActivity() instanceof MainActivity)
//                    ((MainActivity) getActivity()).hidePAB();
//                ((MainActivity) getActivity()).hideMenuSearch();
//            } else {
//                hideIcon();
//
//                if (getActivity() != null && getActivity() instanceof MainActivity)
//                    ((MainActivity) getActivity()).hidePAB();
//                //  on show callback here
//                if (MultilLevelListviewFragment.instance != null) {
//                    MultilLevelListviewFragment.instance.showFAB();
//                }
//
//                /*((MainActivity)getActivity()).showMenuSearch(new OnClickCallback() {
//                    @Override
//                    public void onClick() {
//                        // todo something
//                    }
//                });*/
//
//                showSearchFavorite();
//
//            }
//        } else {
//            hideIcon();
//        }
//    }

    private void showSearchFavorite() {
        ((MainActivity) getActivity()).showSearchIcon(new OnClickCallback() {
            @Override
            public void onClick() {
                if (!isShowSreachIcon) {
                    Utils.printLogs("On search icon clicked");
                    Intent intent = new Intent(Statics.ACTION_SHOW_SEARCH_FAVORITE_INPUT);
                    getActivity().sendBroadcast(intent);
                    isShowSreachIcon = true;
                } else {
                    isShowSreachIcon = false;
                    Intent intent = new Intent(Statics.ACTION_HIDE_SEARCH_FAVORITE_INPUT);
                    getActivity().sendBroadcast(intent);
                }

            }
        });
    }


    private void hideIcon() {
        if (getActivity() != null) {
            ((MainActivity) getActivity()).hideSearchIcon();
        }
    }

    boolean isShowIcon = false;

    private void showIcon() {
        if (getActivity() != null) {

            ((MainActivity) getActivity()).showSearchIcon(new OnClickCallback() {
                @Override
                public void onClick() {
                    // Send broadcast to show search view input
                    if (!isShowIcon) {
                        Utils.printLogs("On search icon clicked");
                        Intent intent = new Intent(Statics.ACTION_SHOW_SEARCH_INPUT);
                        getActivity().sendBroadcast(intent);
                        isShowIcon = true;
                    } else {
                        Intent intent = new Intent(Statics.ACTION_HIDE_SEARCH_INPUT);
                        getActivity().sendBroadcast(intent);
                        isShowIcon = false;
                    }
                }
            });
        }
    }


    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        pager.setCurrentItem(tab.getPosition());
        if (tab.getPosition() == 0) {

            // Hide search icon for tab favorite chat room
            showIcon();

            ImageView icon = (ImageView) tab.getCustomView().findViewById(R.id.iv_icon_left);
            icon.setImageResource(R.drawable.nav_chat_ic);

            ((MainActivity) getActivity()).hideMenuSearch();

        } else {
            // Hide search icon for tab favorite chat room
            hideIcon();

            ImageView icon = (ImageView) tab.getCustomView().findViewById(R.id.iv_icon_right);
            icon.setImageResource(R.drawable.tabbar_group_ic);

            if (getActivity() != null) {
                /*((MainActivity)getActivity()).showMenuSearch(new OnClickCallback() {
                    @Override
                    public void onClick() {
                        // to do something
                        // Send broadcast to fragment to use it
                        // return text filter to filter list user
                    }
                });*/

                showSearchFavorite();
            }
        }

    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        if (tab.getPosition() == 0) {
            ImageView icon = (ImageView) tab.getCustomView().findViewById(R.id.iv_icon_left);
            icon.setImageResource(R.drawable.nav_chat_ic_blue);
        } else {
            ImageView icon = (ImageView) tab.getCustomView().findViewById(R.id.iv_icon_right);
            icon.setImageResource(R.drawable.tabbar_group_ic_blue);
        }
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }


    public class PagerAdapter extends FragmentStatePagerAdapter {

        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            switch (position) {
                case 0:
                    fragment = new RecentFavoriteFragment();
                    break;
                case 1:
                    fragment = MultilLevelListviewFragment.newInstance();
                    break;
                default:
                    fragment = new RecentFavoriteFragment();
                    break;
            }

            return fragment;
        }

        @Override
        public int getCount() {

            return 2;
        }
    }

}
