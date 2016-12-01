package com.dazone.crewchat.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dazone.crewchat.HTTPs.HttpRequest;
import com.dazone.crewchat.R;
import com.dazone.crewchat.activity.MainActivity;
import com.dazone.crewchat.adapter.PullUpLoadMoreRCVAdapter;
import com.dazone.crewchat.constant.Statics;
import com.dazone.crewchat.interfaces.OnClickCallback;
import com.dazone.crewchat.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public abstract class ListFragment<T> extends Fragment {
    public PullUpLoadMoreRCVAdapter adapterList;
    public List<T> dataSet;
    protected HttpRequest mHttpRequest;
    public RecyclerView rvMainList;
    public RelativeLayout rlNewMessage;
    public TextView tvUserNameMessage;
    public ImageView ivScrollDown;
    protected LinearLayout progressBar;
    protected LinearLayout recycler_header, recycler_footer;
    protected RelativeLayout list_content_rl;
    protected TextView no_item_found;
    protected SwipeRefreshLayout swipeRefreshLayout;
    protected int limit = 20;
    protected String str_lastID = "";
    protected int lastID = 0;
    public LinearLayoutManager layoutManager;
    protected Context mContext;
    protected FloatingActionButton fab;
    protected EditText mInputSearch;


    public void setmContext(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHttpRequest = HttpRequest.getInstance();
        dataSet = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_list, container, false);
        progressBar = (LinearLayout) v.findViewById(R.id.progressBar);
        rvMainList = (RecyclerView) v.findViewById(R.id.rv_main);
        rlNewMessage = (RelativeLayout) v.findViewById(R.id.rl_new_message);
        tvUserNameMessage = (TextView) v.findViewById(R.id.tv_user_message);
        ivScrollDown = (ImageView) v.findViewById(R.id.iv_scroll_down);
        recycler_header = (LinearLayout) v.findViewById(R.id.recycler_header);
        recycler_footer = (LinearLayout) v.findViewById(R.id.recycler_footer);
        list_content_rl = (RelativeLayout) v.findViewById(R.id.list_content_rl);
        no_item_found = (TextView) v.findViewById(R.id.no_item_found);

        fab = (FloatingActionButton) v.findViewById(R.id.fab);
        mInputSearch = (EditText) v.findViewById(R.id.inputSearch);

        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setRefreshing(false);
        swipeRefreshLayout.setEnabled(false);

        mInputSearch.addTextChangedListener(mWatcher);

        setupRecyclerView();
        //initSwipeRefresh();
        initList();
        return v;
    }


    TextWatcher mWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (adapterList != null) {
                adapterList.filterRecentFavorite(s.toString());
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (adapterList != null) {
                adapterList.filterRecentFavorite(s.toString());
            }
        }
    };

    protected void showPAB() {
        if (fab != null) {
            fab.setVisibility(View.VISIBLE);
        }
    }

    protected void hidePAB() {
        if (fab != null) {
            fab.setVisibility(View.GONE);
        }
    }

    protected void hideIcon() {
        if (getActivity() != null) {
            ((MainActivity) getActivity()).hideSearchIcon();
        }
    }

    boolean isShowIcon = false;

    protected void showIcon() {
        if (getActivity() != null) {
            ((MainActivity) getActivity()).showSearchIcon(new OnClickCallback() {
                @Override
                public void onClick() {
                    // Send broadcast to show search view input
                    if (!isShowIcon) {
                        Utils.printLogs("On search icon clicked");
                        Intent intent = new Intent(Statics.ACTION_SHOW_SEARCH_INPUT_IN_CURRENT_CHAT);
                        getActivity().sendBroadcast(intent);
                        isShowIcon = true;
                    } else {
                        hideSearchInput();
                        isShowIcon = false;
                        Intent intent = new Intent(Statics.ACTION_HIDE_SEARCH_INPUT_IN_CURRENT_CHAT);
                        getActivity().sendBroadcast(intent);
                    }
                }
            });
        }
    }

    protected void showSearchInput() {
        if (mInputSearch != null) {
            mInputSearch.setVisibility(View.VISIBLE);
            mInputSearch.post(new Runnable() {
                @Override
                public void run() {
                    mInputSearch.requestFocus();
                    InputMethodManager imgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imgr.showSoftInput(mInputSearch, InputMethodManager.SHOW_IMPLICIT);
                }
            });
        }
    }

    protected void hideSearchInput() {
        if (mInputSearch != null) {
            mInputSearch.setText("");
            mInputSearch.setVisibility(View.GONE);
            if (getActivity() != null) {
                Utils.hideKeyboard(getActivity());
            }
        }
    }

    protected void setupRecyclerView() {
        rvMainList.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
//        layoutManager.setStackFromEnd(true);
        rvMainList.setLayoutManager(layoutManager);
        initAdapter();
        rvMainList.setAdapter(adapterList);
    }

    public void enableLoadingMore() {
        adapterList.setOnLoadMoreListener(new PullUpLoadMoreRCVAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                reloadContentPage();
            }
        });
    }

    public void disableSwipeRefresh() {
        swipeRefreshLayout.setEnabled(false);
    }

    /*protected void initSwipeRefresh() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                str_lastID = "";
                lastID = 0;
                dataSet.clear();
                adapterList.notifyDataSetChanged();
                adapterList.setLoaded();
                reloadContentPage();
            }
        });
    }*/
    protected abstract void initAdapter();

    protected abstract void reloadContentPage();

    protected abstract void addMoreItem();

    protected abstract void initList();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }
}
