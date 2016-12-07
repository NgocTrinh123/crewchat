package com.dazone.crewchat.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.dazone.crewchat.HTTPs.HttpRequest;
import com.dazone.crewchat.R;
import com.dazone.crewchat.activity.ChattingActivity;
import com.dazone.crewchat.activity.MainActivity;
import com.dazone.crewchat.activity.RenameRoomActivity;
import com.dazone.crewchat.activity.base.BaseActivity;
import com.dazone.crewchat.adapter.RecentFavoriteAdapter;
import com.dazone.crewchat.constant.Statics;
import com.dazone.crewchat.database.AllUserDBHelper;
import com.dazone.crewchat.database.ChatRomDBHelper;
import com.dazone.crewchat.dto.ChattingDto;
import com.dazone.crewchat.dto.CurrentChatDto;
import com.dazone.crewchat.dto.ErrorDto;
import com.dazone.crewchat.dto.TreeUserDTOTemp;
import com.dazone.crewchat.dto.userfavorites.FavoriteChatRoomDto;
import com.dazone.crewchat.interfaces.BaseHTTPCallBack;
import com.dazone.crewchat.interfaces.IGetListOrganization;
import com.dazone.crewchat.interfaces.OnGetCurrentChatCallBack;
import com.dazone.crewchat.interfaces.OnGetFavoriteChatRoom;
import com.dazone.crewchat.utils.Constant;
import com.dazone.crewchat.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class RecentFavoriteFragment extends ListFragment<ChattingDto> implements OnGetCurrentChatCallBack {
    private View rootView;
    public boolean isUpdate = false;
    public static CurrentChatListFragment fragment;
    public static RecentFavoriteFragment instance;

    private List<TreeUserDTOTemp> treeUserDTOTempList;
    private EditText etInputSearch;
    public boolean isActive = false;


    private void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Statics.ACTION_SHOW_SEARCH_INPUT);
        filter.addAction(Statics.ACTION_HIDE_SEARCH_INPUT);
        getActivity().registerReceiver(mReceiverShowSearchInput, filter);
    }

    private void unregisterReceiver() {
        getActivity().unregisterReceiver(mReceiverShowSearchInput);
    }

    private BroadcastReceiver mReceiverShowSearchInput = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Statics.ACTION_SHOW_SEARCH_INPUT)) {
                if (etInputSearch != null) {
                    etInputSearch.setVisibility(View.VISIBLE);
                    etInputSearch.post(new Runnable() {
                        @Override
                        public void run() {
                            etInputSearch.requestFocus();
                            InputMethodManager imgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imgr.showSoftInput(etInputSearch, InputMethodManager.SHOW_IMPLICIT);
                        }
                    });
                }
            } else if (intent.getAction().equals(Statics.ACTION_HIDE_SEARCH_INPUT)) {
                etInputSearch.setText("");
                etInputSearch.setVisibility(View.GONE);
                if (getActivity() != null) {
                    Utils.hideKeyboard(getActivity());
                }
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHttpRequest = HttpRequest.getInstance();
        dataSet = new ArrayList<>();
        instance = this;
        registerReceiver();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (getActivity() != null) {
            ((MainActivity) getActivity()).hideSearchIcon();
            ((MainActivity) getActivity()).hideMenuSearch();
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unregisterReceiver();
    }

    public void removeFavorite(long roomNo) {
        for (ChattingDto chat : dataSet) {
            if (chat.getRoomNo() == roomNo) {
                dataSet.remove(chat);
                adapterList.notifyDataSetChanged();
                break;
            }
        }
    }

    public void addFavorite(ChattingDto dto) {
        boolean isExist = false;
        for (ChattingDto chat : dataSet) {
            if (chat.getRoomNo() == dto.getRoomNo()) {
                chat.setFavorite(true);
                adapterList.notifyDataSetChanged();
                isExist = true;
                break;
            }
        }
        if (!isExist) {
            dataSet.add(dto);
            Collections.sort(dataSet, new Comparator<ChattingDto>() {
                public int compare(ChattingDto chattingDto1, ChattingDto chattingDto2) {
                    if (chattingDto1.getLastedMsgDate() == null || chattingDto2.getLastedMsgDate() == null) {
                        return -1;
                    }
                    return chattingDto2.getLastedMsgDate().compareToIgnoreCase(chattingDto1.getLastedMsgDate());
                }
            });
            adapterList.notifyDataSetChanged();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_recent_favorite, container, false);

        progressBar = (LinearLayout) rootView.findViewById(R.id.progressBar);
        rvMainList = (RecyclerView) rootView.findViewById(R.id.rv_main);

        etInputSearch = (EditText) rootView.findViewById(R.id.inputSearch);
        etInputSearch.addTextChangedListener(new TextWatcher() {
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

            }
        });

        rvMainList.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        rvMainList.setLayoutManager(layoutManager);
        initAdapter();
        rvMainList.setAdapter(adapterList);


        Utils.printLogs("On Create view in " + this.getClass().getSimpleName());

        initList();

        return rootView;
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && !isActive) {
            isActive = true;

        }
        if (isVisibleToUser) {
            if (getActivity() != null && getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).hidePAB();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null) {
            ((MainActivity) getActivity()).hideMenuSearch();
        }
    }

    @Override
    protected void initAdapter() {
        adapterList = new RecentFavoriteAdapter(mContext, dataSet, rvMainList, mOnContextMenuSelect);
        Utils.printLogs("Item size oh lalala");
    }

    @Override
    protected void reloadContentPage() {

    }

    @Override
    protected void addMoreItem() {

    }

    @Override
    protected void initList() {

        progressBar.setVisibility(View.VISIBLE);
        final ArrayList<TreeUserDTOTemp> listOfUsers = Utils.getUsers();
        // If list user is not null, load data from client at first
        if (listOfUsers != null && listOfUsers.size() > 0) {
            treeUserDTOTempList = listOfUsers;
            // If list user is exist get data from client, the run a new thread to get data from
            getDataFromClient(listOfUsers);

        } else {
            // Get user list from server
            HttpRequest.getInstance().GetListOrganize(new IGetListOrganization() {
                @Override
                public void onGetListSuccess(final ArrayList<TreeUserDTOTemp> treeUserDTOs) {
                    // Get list user to send message here
                    // Get list success and store it to Sqlite databas
                    Utils.printLogs(" onGetListSuccess " + RecentFavoriteFragment.this.getClass().getSimpleName());
                    for (TreeUserDTOTemp tem : treeUserDTOs) {
                        Utils.printLogs("Chat = " + tem.toString());
                    }
                    treeUserDTOTempList = treeUserDTOs;

                    // get newest data from server, using auto sync and then store offline
                    // get data from server just get and store to local storage
                    getDataFromServer();


                }

                @Override
                public void onGetListFail(ErrorDto dto) {
                    Utils.printLogs(" onGetListFail " + RecentFavoriteFragment.this.getClass().getSimpleName());
                    // Hide progressBar when error -> on Network is disconnected
                    progressBar.setVisibility(View.GONE);

                    // GET LIST USER FAILED, DISPLAY SOME ERROR MESSAGE
                    // GET LIST USER FROM CLIENT, TO DISPLAY IT
                    getDataFromClient(listOfUsers);
                }
            });

        }
    }

    @Override
    public void onHTTPSuccess(List<CurrentChatDto> dtos) {

    }

    @Override
    public void onHTTPFail(ErrorDto errorDto) {

    }

    /*
    * Custom function
    * */
    public interface OnContextMenuSelect {
        public void onSelect(int type, Bundle bundle);
    }

    private OnContextMenuSelect mOnContextMenuSelect = new OnContextMenuSelect() {
        @Override
        public void onSelect(int type, Bundle bundle) {

            Intent intent = null;
            final long roomNo = bundle.getLong(Statics.ROOM_NO, 0);

            switch (type) {
                case Statics.ROOM_RENAME:

                    intent = new Intent(getActivity(), RenameRoomActivity.class);
                    intent.putExtras(bundle);
                    startActivityForResult(intent, 1001);

                    break;

                case Statics.ROOM_OPEN:

                    intent = new Intent(BaseActivity.Instance, ChattingActivity.class);
                    intent.putExtra(Constant.KEY_INTENT_ROOM_NO, roomNo);
                    startActivity(intent);

                    break;

                case Statics.ROOM_REMOVE_FROM_FAVORITE:

                    for (ChattingDto chat : dataSet) {
                        if (chat.getRoomNo() == roomNo) {
                            dataSet.remove(chat);
                            adapterList.notifyDataSetChanged();
                            break;
                        }
                    }

                    break;

                case Statics.ROOM_ADD_TO_FAVORITE:

                    final Resources res = mContext.getResources();
                    HttpRequest.getInstance().addRoomToFavorite(roomNo, new BaseHTTPCallBack() {
                        @Override
                        public void onHTTPSuccess() {
                            Toast.makeText(mContext, res.getString(R.string.favorite_add_success), Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onHTTPFail(ErrorDto errorDto) {
                            Toast.makeText(mContext, res.getString(R.string.favorite_add_success), Toast.LENGTH_LONG).show();
                        }
                    });

                    break;

                case Statics.ROOM_LEFT:

                    int myId = Utils.getCurrentId();

                    HttpRequest.getInstance().DeleteChatRoomUser(roomNo, myId, new BaseHTTPCallBack() {
                        @Override
                        public void onHTTPSuccess() {
                            Utils.printLogs("User is left a room #### success");
                            try {
                                for (int i = 0; i < dataSet.size(); i++) {
                                    Utils.printLogs("Current room No = " + dataSet.get(i).getRoomNo());
                                    if (dataSet.get(i).getRoomNo() == roomNo) {
                                        dataSet.remove(i);
                                        adapterList.notifyDataSetChanged();
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onHTTPFail(ErrorDto errorDto) {
                            Utils.printLogs("User is left a room #### failed");
                        }
                    });

                    break;
            }
        }
    };


    private void getDataFromClient(List<TreeUserDTOTemp> listOfUsers) {

        int myId = Utils.getCurrentId();
        List<TreeUserDTOTemp> list1;
        TreeUserDTOTemp treeUserDTOTemp1;
        ArrayList<ChattingDto> listChat = ChatRomDBHelper.getFavoriteChatRooms();

        Collections.sort(listChat, new Comparator<ChattingDto>() {
            public int compare(ChattingDto chattingDto1, ChattingDto chattingDto2) {
                return chattingDto2.getLastedMsgDate().compareToIgnoreCase(chattingDto1.getLastedMsgDate());
            }
        });

        for (ChattingDto chattingDto : listChat) {

            Utils.printLogs("Chat client = " + chattingDto.getUnreadTotalCount());

            if (!Utils.checkChat(chattingDto, myId)) {
                list1 = new ArrayList<>();
                for (int id : chattingDto.getUserNos()) {

                    if (myId != id) {
                        treeUserDTOTemp1 = Utils.GetUserFromDatabase(listOfUsers, id);

                        if (treeUserDTOTemp1 != null) {
                            list1.add(treeUserDTOTemp1);
                        }
                    }
                }
                chattingDto.setListTreeUser(list1);
                dataSet.add(chattingDto);

            }
        }

        if (dataSet != null && dataSet.size() > 0) {
            countDataFromServer(true);
            adapterList.notifyDataSetChanged();
        } else {
            countDataFromServer(false);
        }

        /*if (Utils.isNetworkAvailable()){
            getDataFromServer();
        }*/
    }

    public void getDataFromServer() {

        final ArrayList<TreeUserDTOTemp> list = Utils.getUsers();
        // If user list is empty, get list user from server
        if (list == null || (list.size() == 0)) {
            HttpRequest.getInstance().GetListOrganize(new IGetListOrganization() {
                @Override
                public void onGetListSuccess(final ArrayList<TreeUserDTOTemp> treeUserDTOs) {
                    AllUserDBHelper.addUser(treeUserDTOs);
                    // New Thread to get chat list
                    getChatList(treeUserDTOs);

                }

                @Override
                public void onGetListFail(ErrorDto dto) {

                }
            });
        } else {

            // New Thread to get chat list
            // If list user is not null, then get chat list
            getChatList(list);

        }
    }

    private void getChatList(final List<TreeUserDTOTemp> listOfUsers) {

        HttpRequest.getInstance().getGetFavoriteChatRoom(new OnGetFavoriteChatRoom() {
            @Override
            public void OnGetChatRoomSuccess(final List<FavoriteChatRoomDto> list) {

                progressBar.setVisibility(View.GONE);
                // Get current chat room and compare with favorite room
                List<ChattingDto> listChat = ChatRomDBHelper.getChatRooms();
                Collections.sort(listChat, new Comparator<ChattingDto>() {
                    public int compare(ChattingDto chattingDto1, ChattingDto chattingDto2) {
                        return chattingDto2.getLastedMsgDate().compareToIgnoreCase(chattingDto1.getLastedMsgDate());
                    }
                });

                int myId = Utils.getCurrentId();
                List<TreeUserDTOTemp> list1;
                TreeUserDTOTemp treeUserDTOTemp1;

                for (ChattingDto dto : listChat) {
                    for (FavoriteChatRoomDto chat : list) {

                        if (dto.getRoomNo() == chat.getRoomNo()) {
                            list1 = new ArrayList<>();
                            for (int id : dto.getUserNos()) {

                                if (myId != id) {
                                    treeUserDTOTemp1 = Utils.GetUserFromDatabase(listOfUsers, id);
                                    if (treeUserDTOTemp1 != null) {
                                        list1.add(treeUserDTOTemp1);
                                    }
                                }
                            }
                            dto.setListTreeUser(list1);
                            dataSet.add(dto);

                        }

                    }
                }

                adapterList.notifyDataSetChanged();
            }

            @Override
            public void OnGetChatRoomFail(ErrorDto errorDto) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void countDataFromServer(boolean isHaveData) {
        if (isHaveData) {
            progressBar.setVisibility(View.GONE);
            rvMainList.setVisibility(View.VISIBLE);
            //no_item_found.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            rvMainList.setVisibility(View.GONE);
            //no_item_found.setVisibility(View.VISIBLE);
            //no_item_found.setText("No Data");
        }
    }
}
