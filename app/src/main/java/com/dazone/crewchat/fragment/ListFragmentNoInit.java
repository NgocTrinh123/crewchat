package com.dazone.crewchat.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.dazone.crewchat.HTTPs.HttpRequest;
import com.dazone.crewchat.R;
import com.dazone.crewchat.adapter.PullUpLoadMoreRCVAdapter;

import java.util.ArrayList;
import java.util.List;

public abstract class ListFragmentNoInit<T> extends Fragment {
    public PullUpLoadMoreRCVAdapter adapterList;
    public List<T> dataSet;
    protected HttpRequest mHttpRequest;
    public RecyclerView rvMainList;
    protected LinearLayout progressBar;
    protected LinearLayout recycler_header, recycler_footer;
    protected RelativeLayout list_content_rl;
    protected TextView no_item_found;
    protected SwipeRefreshLayout swipeRefreshLayout;
    protected int limit = 20;
    protected String str_lastID = "";
    protected int lastID = 0;
    public RecyclerView.LayoutManager layoutManager;
    protected Context mContext;
    protected FloatingActionButton fab;


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
        recycler_header = (LinearLayout) v.findViewById(R.id.recycler_header);
        recycler_footer = (LinearLayout) v.findViewById(R.id.recycler_footer);
        list_content_rl = (RelativeLayout) v.findViewById(R.id.list_content_rl);
        no_item_found = (TextView) v.findViewById(R.id.no_item_found);

        fab = (FloatingActionButton) v.findViewById(R.id.fab);

        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setRefreshing(false);
        swipeRefreshLayout.setEnabled(false);

        setupRecyclerView();
        //initSwipeRefresh();
        initList();
        return v;
    }

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

    protected void setupRecyclerView() {
        rvMainList.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
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
