package com.dazone.crewchat.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.dazone.crewchat.HTTPs.HttpRequest;
import com.dazone.crewchat.R;
import com.dazone.crewchat.Tree.Dtos.TreeUserDTO;
import com.dazone.crewchat.activity.MainActivity;
import com.dazone.crewchat.adapter.CompanySearchAdapter;
import com.dazone.crewchat.database.AllUserDBHelper;
import com.dazone.crewchat.dto.ErrorDto;
import com.dazone.crewchat.dto.TreeUserDTOTemp;
import com.dazone.crewchat.interfaces.IGetListDepart;
import com.dazone.crewchat.interfaces.OnGetStatusCallback;
import com.dazone.crewchat.test.OrganizationView2;
import com.dazone.crewchat.utils.Constant;
import com.dazone.crewchat.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by david on 12/23/15.
 */
public class CompanyFragment extends Fragment {

    private List<TreeUserDTO> listData;
    private HttpRequest mHttpRequest;

    private CompanySearchAdapter adapter;

    private OrganizationView2 orgView;
    private ArrayList<TreeUserDTO> selectedPersonList;
    private boolean mIsDisplaySelectedOnly = true;
    private boolean isCreated = false;
    private Activity mContext;

    private OnGetStatusCallback mStatusCallback = new OnGetStatusCallback() {
        @Override
        public void onGetStatusFinish() {
            Utils.printLogs("On status callback from CompanyFragment");

            if (orgView != null && isCreated){
                Utils.printLogs("View is # nll");
                orgView.syncStatus();
            }
        }
    };

    public void setContext(Activity context){
        mContext = context;
    }

    /**
     * VIEW
     */
    private View rootView;
    private LinearLayout mSharePersonContent;
    private RecyclerView rvSearch;
    private ProgressBar progressBar;
    private TextView tvNoData;

    /**
     * INTENT FILTER
     */
    IntentFilter intentFilterSearch;
    BroadcastReceiver receiverSearch = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                ArrayList<TreeUserDTOTemp> arrayListSearch = new ArrayList<>();
                String textSearch = intent.getStringExtra(Constant.KEY_INTENT_TEXT_SEARCH);
                if (!TextUtils.isEmpty(textSearch.trim())) {
                    mSharePersonContent.setVisibility(View.GONE);
                    rvSearch.setVisibility(View.VISIBLE);
                    ArrayList<TreeUserDTOTemp> listTemp = AllUserDBHelper.getUser();
                    for (TreeUserDTOTemp treeUserDTOTemp : listTemp) {
                        if (treeUserDTOTemp.getName().toUpperCase().contains(textSearch.toUpperCase())) {
                            arrayListSearch.add(treeUserDTOTemp);
                        }
                    }
                    if (arrayListSearch.size() == 0) {
                        tvNoData.setVisibility(View.VISIBLE);
                        rvSearch.setVisibility(View.GONE);

                    } else {
                        adapter.updateListData(arrayListSearch);
                        tvNoData.setVisibility(View.GONE);
                    }
                } else {
                    mSharePersonContent.setVisibility(View.VISIBLE);
                    rvSearch.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mHttpRequest = HttpRequest.getInstance();
        listData = new ArrayList<>();
        intentFilterSearch = new IntentFilter(Constant.INTENT_FILTER_SEARCH);

        if (mContext instanceof MainActivity){
            ((MainActivity) mContext).setmGetStatusCallbackCompany(mStatusCallback);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_company, container, false);
        mSharePersonContent = (LinearLayout) rootView.findViewById(R.id.container);

        adapter = new CompanySearchAdapter(getActivity(), new ArrayList<TreeUserDTOTemp>());
        rvSearch = (RecyclerView) rootView.findViewById(R.id.rv_search);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        rvSearch.setLayoutManager(mLayoutManager);
        rvSearch.setItemAnimator(new DefaultItemAnimator());
        rvSearch.setAdapter(adapter);

        tvNoData = (TextView) rootView.findViewById(R.id.tv_no_data);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);


        /*progressBar = (LinearLayout) v.findViewById(R.id.progressBar);
        rvMainList = (RecyclerView) v.findViewById(R.id.rv_main);
        recycler_header = (LinearLayout) v.findViewById(R.id.recycler_header);
        recycler_footer = (LinearLayout) v.findViewById(R.id.recycler_footer);
        list_content_rl = (RelativeLayout) v.findViewById(R.id.list_content_rl);
        no_item_found = (TextView) v.findViewById(R.id.no_item_found);

        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setRefreshing(false);
        swipeRefreshLayout.setEnabled(false);

        setupRecyclerView();
        //initSwipeRefresh();
        initList();*/
        return rootView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser){
            selectedPersonList = new ArrayList<>();
            if (!isCreated) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.VISIBLE);
                    }
                });
                mSharePersonContent.setVisibility(View.VISIBLE);
                rvSearch.setVisibility(View.GONE);
                tvNoData.setVisibility(View.GONE);
                isCreated = true;
                orgView = new OrganizationView2(getActivity(), selectedPersonList, mIsDisplaySelectedOnly, mSharePersonContent, progressBar);
            }

        }
    }

    @Override
    public void onStart() {
        super.onStart();
        getActivity().registerReceiver(receiverSearch, intentFilterSearch);
       /* selectedPersonList = new ArrayList<>();
        if (!isCreated) {
            mSharePersonContent.setVisibility(View.VISIBLE);
            rvSearch.setVisibility(View.GONE);
            tvNoData.setVisibility(View.GONE);
            isCreated = true;
            orgView = new OrganizationView2(getActivity(), selectedPersonList, mIsDisplaySelectedOnly, mSharePersonContent);
        }
        getActivity().registerReceiver(receiverSearch, intentFilterSearch);*/
        //callAPIGetListDepart();
    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().unregisterReceiver(receiverSearch);

    }

    /**
     * GET LIST DEPARTMENT
     */

    private void callAPIGetListDepart() {
        mHttpRequest.GetListDepart(new IGetListDepart() {
            @Override
            public void onGetListDepartSuccess(ArrayList<TreeUserDTO> treeUserDTOs) {
                for (TreeUserDTO treeUserDTO : treeUserDTOs) {
                    System.out.println("aaaaaaaaaaaaaaaa treeUserDTOs " + treeUserDTO.toString());
                }
                convertData(treeUserDTOs);
                for (TreeUserDTO treeUserDTO : listData) {
                    System.out.println("aaaaaaaaaaaaaaaa listData " + treeUserDTO.toString());
                }

                ArrayList<TreeUserDTOTemp> listTemp = AllUserDBHelper.getUser();
                for (TreeUserDTOTemp treeUserDTOTemp : listTemp) {
                    System.out.println("aaaaaaaaaaaaaaaa listTemp " + treeUserDTOTemp.toString());
                }
                //callAPIGetListOrganize();

            }

            @Override
            public void onGetListDepartFail(ErrorDto dto) {

            }
        });
    }

    /**
     * GET LIST ORGANIZE
     */
   /* private void callAPIGetListOrganize() {
        mHttpRequest.GetListOrganize(new IGetListOrganization() {
            @Override
            public void onGetListSuccess(List<TreeUserDTOTemp> treeUserDTOs) {
                for (TreeUserDTOTemp treeUserDTOTemp : treeUserDTOs) {
                    System.out.println("aaaaaaaaaaaaaaaa TreeUserDTOTemp " + treeUserDTOTemp.toString());
                }
            }

            @Override
            public void onGetListFail(ErrorDto dto) {

            }
        });
    }*/


    /**
     * SHOW
     */
    public void updateList() {
        /*if (dto != null && dataSet.size() < 1) {
            progressBar.setVisibility(View.VISIBLE);
            dataSet.add(dto);
            Handler mHandler = new Handler();
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    adapterList.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);
                }
            }, 1000);
        }*/
    }

    /**
     * CONVERT TreeUserDTO
     */
    public void convertData(List<TreeUserDTO> treeUserDTOs) {
        if (treeUserDTOs != null && treeUserDTOs.size() != 0) {
            for (TreeUserDTO dto : treeUserDTOs) {
                if (dto.getSubordinates() != null && dto.getSubordinates().size() > 0) {
                    listData.add(dto);
                    convertData(dto.getSubordinates());
                } else {
                    listData.add(dto);
                }
            }
        }
    }
}
