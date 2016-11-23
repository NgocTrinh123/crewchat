package com.dazone.crewchat.fragment;

import android.os.Bundle;
import android.view.View;

import com.dazone.crewchat.Tree.Dtos.TreeUserDTO;
import com.dazone.crewchat.adapter.GroupListAdapter;
import com.dazone.crewchat.constant.Statics;
import com.dazone.crewchat.database.AllUserDBHelper;
import com.dazone.crewchat.database.UserDBHelper;
import com.dazone.crewchat.dto.ChattingDto;
import com.dazone.crewchat.dto.TreeUserDTOTemp;
import com.dazone.crewchat.dto.UserDto;
import com.dazone.crewchat.utils.Constant;
import com.dazone.crewchat.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * Created by THANHTUNG on 04/03/2016.
 */
public class GroupListFragment extends ListFragment<TreeUserDTOTemp> implements View.OnClickListener {

    //ChattingDto chattingDto;
    private ArrayList<Integer> userNos;

    public GroupListFragment instance(ArrayList<Integer> userNos) {
        GroupListFragment groupListFragment = new GroupListFragment();
        Bundle bundle = new Bundle();
        bundle.putIntegerArrayList(Constant.KEY_INTENT_USER_NO_ARRAY, userNos);
        //bundle.putSerializable(Statics.CHATTING_DTO_FOR_GROUP_LIST, chattingDto);
        groupListFragment.setArguments(bundle);
        return groupListFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        receiveBundle();
    }

    public void receiveBundle() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            userNos = bundle.getIntegerArrayList(Constant.KEY_INTENT_USER_NO_ARRAY);
            //chattingDto = (ChattingDto) bundle.getSerializable(Statics.CHATTING_DTO_FOR_GROUP_LIST);
        }
    }

    @Override
    protected void initAdapter() {
        adapterList = new GroupListAdapter(mContext,dataSet, rvMainList);
        enableLoadingMore();

    }

    @Override
    protected void reloadContentPage() {
        dataSet.add(null);
        adapterList.notifyItemInserted(dataSet.size() - 1);
    }

    @Override
    protected void addMoreItem() {

    }

    @Override
    protected void initList() {
        if (userNos != null && userNos.size() > 0) {
            for (int i : userNos) {
                if (i != UserDBHelper.getUser().Id) {
                    TreeUserDTOTemp treeUserDTOTemp = Utils.GetUserFromDatabase(AllUserDBHelper.getUser(), i);
                    if (treeUserDTOTemp != null) {
                        dataSet.add(treeUserDTOTemp);
                    }
                    adapterList.notifyItemChanged(userNos.indexOf(i));
                }
            }
        }
    }

   /* @Override
    protected void initSwipeRefresh() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }*/

    @Override
    public void onClick(View v) {

    }
}
