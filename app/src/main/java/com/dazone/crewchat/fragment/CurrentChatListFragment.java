package com.dazone.crewchat.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;

import com.dazone.crewchat.HTTPs.HttpRequest;
import com.dazone.crewchat.Tree.Dtos.TreeUserDTO;
import com.dazone.crewchat.activity.ChattingActivity;
import com.dazone.crewchat.activity.RenameRoomActivity;
import com.dazone.crewchat.activity.base.BaseActivity;
import com.dazone.crewchat.adapter.CurrentChatAdapter;
import com.dazone.crewchat.constant.Statics;
import com.dazone.crewchat.database.AllUserDBHelper;
import com.dazone.crewchat.database.ChatRomDBHelper;
import com.dazone.crewchat.dto.ChattingDto;
import com.dazone.crewchat.dto.CurrentChatDto;
import com.dazone.crewchat.dto.ErrorDto;
import com.dazone.crewchat.dto.TreeUserDTOTemp;
import com.dazone.crewchat.interfaces.BaseHTTPCallBack;
import com.dazone.crewchat.interfaces.IGetListOrganization;
import com.dazone.crewchat.interfaces.OnGetChatList;
import com.dazone.crewchat.interfaces.OnGetCurrentChatCallBack;
import com.dazone.crewchat.utils.Constant;
import com.dazone.crewchat.utils.CrewChatApplication;
import com.dazone.crewchat.utils.Prefs;
import com.dazone.crewchat.utils.TimeUtils;
import com.dazone.crewchat.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by david on 12/23/15.
 */
public class CurrentChatListFragment extends ListFragment<ChattingDto> implements OnGetCurrentChatCallBack {

    public boolean isUpdate = false;
    public static CurrentChatListFragment fragment;
    private List<TreeUserDTOTemp> treeUserDTOTempList;

    public boolean isActive = false;
    private int myId;
    public boolean isFirstTime = true;

    private void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Statics.ACTION_SHOW_SEARCH_INPUT_IN_CURRENT_CHAT);
        filter.addAction(Statics.ACTION_HIDE_SEARCH_INPUT_IN_CURRENT_CHAT);
        getActivity().registerReceiver(mReceiverShowSearchInput, filter);
    }

    private void unregisterReceiver() {
        if (getActivity() != null) {
            getActivity().unregisterReceiver(mReceiverShowSearchInput);
        }
    }


    private BroadcastReceiver mReceiverShowSearchInput = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Statics.ACTION_SHOW_SEARCH_INPUT_IN_CURRENT_CHAT)) {
                showSearchInput();
            } else if (intent.getAction().equals(Statics.ACTION_HIDE_SEARCH_INPUT_IN_CURRENT_CHAT)) {
                // to do something
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragment = this;
        registerReceiver();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unregisterReceiver();
    }

    private ArrayList<TreeUserDTO> temp = new ArrayList<>();

    public void convertData(List<TreeUserDTO> treeUserDTOs) {
        if (treeUserDTOs != null && treeUserDTOs.size() != 0) {
            for (TreeUserDTO dto : treeUserDTOs) {
                if (dto.getSubordinates() != null && dto.getSubordinates().size() > 0) {
                    temp.add(dto);
                    convertData(dto.getSubordinates());
                } else {
                    temp.add(dto);
                }
            }
        }
    }

    @Override
    protected void initList() {
        myId = new Prefs().getUserNo();

        progressBar.setVisibility(View.VISIBLE);
        Utils.printLogs("" + this.getClass().getSimpleName());
        ArrayList<TreeUserDTOTemp> listOfUsers = CrewChatApplication.listUsers;
        if (listOfUsers == null) {
            listOfUsers = AllUserDBHelper.getUser();
        }
        // If list user is not null, load data from client at first
        if (listOfUsers != null && listOfUsers.size() > 0) {
            treeUserDTOTempList = listOfUsers;

            // If list user is exist get data from client, the run a new thread to get data from
            getDataFromClient(listOfUsers);

        } else {
            // Get user list from server
            final ArrayList<TreeUserDTOTemp> finalListOfUsers = listOfUsers;
            HttpRequest.getInstance().GetListOrganize(new IGetListOrganization() {
                @Override
                public void onGetListSuccess(final ArrayList<TreeUserDTOTemp> treeUserDTOs) {
                    getChatList(treeUserDTOs);
                    // Get list user to send message here
                    // Get list success and store it to Sqlite databas
                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            Utils.printLogs(" onGetListSuccess " + CurrentChatListFragment.this.getClass().getSimpleName());
                            for (TreeUserDTOTemp tem : treeUserDTOs) {
                                Utils.printLogs("Chat = " + tem.toString());
                            }
                            treeUserDTOTempList = treeUserDTOs;

                            // get newest data from server, using auto sync and then store offline
                            // get data from server just get and store to local storage
                            AllUserDBHelper.addUser(treeUserDTOs);

                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    CrewChatApplication.listUsers = AllUserDBHelper.getUser();
                                }
                            }).start();
                            // New Thread to get chat list
                        }
                    }).start();


                }

                @Override
                public void onGetListFail(ErrorDto dto) {
                    Utils.printLogs(" onGetListFail " + CurrentChatListFragment.this.getClass().getSimpleName());
                    // Hide progressBar when error -> on Network is disconnected
                    progressBar.setVisibility(View.GONE);

                    // GET LIST USER FAILED, DISPLAY SOME ERROR MESSAGE
                    // GET LIST USER FROM CLIENT, TO DISPLAY IT
                    getDataFromClient(finalListOfUsers);
                }
            });

        }
        /*List<TreeUserDTOTemp> treeUserDTOTempArrayList = AllUserDBHelper.getUser();

        if (treeUserDTOTempArrayList == null || treeUserDTOTempArrayList.size() == 0) {

        } else {
            getDataFromServer(treeUserDTOTempArrayList);
        }*/
    }


    /*
        * @return list of data from local storage
        * Get list chat has stored before
        * */
    private void getDataFromClient(List<TreeUserDTOTemp> listOfUsers) {
        dataSet.clear();
        List<TreeUserDTOTemp> list1;
        TreeUserDTOTemp treeUserDTOTemp1;
        ArrayList<ChattingDto> listChat = ChatRomDBHelper.getChatRooms();

        Collections.sort(listChat, new Comparator<ChattingDto>() {
            public int compare(ChattingDto chattingDto1, ChattingDto chattingDto2) {
                return chattingDto2.getLastedMsgDate().compareToIgnoreCase(chattingDto1.getLastedMsgDate());
            }
        });

        for (ChattingDto chattingDto : listChat) {

            Utils.printLogs("Chat client = " + chattingDto.getUnreadTotalCount());

            if (!Utils.checkChat(chattingDto, myId)) {
                list1 = new ArrayList<>();

                ArrayList<Integer> cloneArr = new ArrayList<>(chattingDto.getUserNos());
                Utils.removeArrayDuplicate(cloneArr);

                Utils.printLogs("-->######################################");
                for (int id : cloneArr) {

                    Utils.printLogs("-->ID = " + id);
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

        // display data from local first
        if (dataSet != null && dataSet.size() > 0) {
            countDataFromServer(true);
            adapterList.notifyDataSetChanged();
        }


        // Get data from server if network is avaiable
        if (Utils.isNetworkAvailable()) {
            getDataFromServer();
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }


    public interface OnContextMenuSelect {
        public void onSelect(int type, Bundle bundle);
    }

    private OnContextMenuSelect mOnContextMenuSelect = new OnContextMenuSelect() {
        @Override
        public void onSelect(int type, Bundle bundle) {

            Intent intent = null;
            final long roomNo = bundle.getInt(Statics.ROOM_NO, 0);

            switch (type) {
                case Statics.ROOM_RENAME:

                    intent = new Intent(getActivity(), RenameRoomActivity.class);
                    intent.putExtras(bundle);
                    startActivityForResult(intent, 1001);

                    break;

                case Statics.ROOM_OPEN:

                    intent = new Intent(BaseActivity.Instance, ChattingActivity.class);
                    ChattingDto dto = (ChattingDto) bundle.getSerializable(Constant.KEY_INTENT_ROOM_DTO);

                    Bundle args = new Bundle();
                    args.putLong(Constant.KEY_INTENT_ROOM_NO, roomNo);
                    args.putSerializable(Constant.KEY_INTENT_CHATTING_DTO, dto);

                    intent.putExtras(args);

                    startActivity(intent);

                    break;

                case Statics.ROOM_ADD_TO_FAVORITE:


                   /* final Resources res = mContext.getResources();
                    HttpRequest.getInstance().addRoomToFavorite(roomNo, new BaseHTTPCallBack() {
                        @Override
                        public void onHTTPSuccess() {
                            Toast.makeText(mContext, res.getString(R.string.favorite_add_success) , Toast.LENGTH_LONG).show();
                            for (ChattingDto chat : dataSet){
                                if (chat.getRoomNo() == roomNo){

                                }
                            }
                        }

                        @Override
                        public void onHTTPFail(ErrorDto errorDto) {
                            Toast.makeText(mContext, res.getString(R.string.favorite_add_success) , Toast.LENGTH_LONG).show();
                        }
                    });*/

                    break;

                case Statics.ROOM_LEFT:


                    HttpRequest.getInstance().DeleteChatRoomUser(roomNo, myId, new BaseHTTPCallBack() {
                        @Override
                        public void onHTTPSuccess() {
                            Utils.printLogs("User is left a room #### success");
                            try {
                                for (int i = 0; i < dataSet.size(); i++) {
                                    Utils.printLogs("Current room No = " + dataSet.get(i).getRoomNo());
                                    if (dataSet.get(i).getRoomNo() == roomNo) {

                                        // remove from favorite list
                                        if (RecentFavoriteFragment.instance != null) {
                                            if (dataSet.get(i).isFavorite()) {
                                                RecentFavoriteFragment.instance.removeFavorite(roomNo);
                                            }
                                        }
                                        // remove from data list
                                        dataSet.remove(i);
                                        // remove chat room from local database
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                ChatRomDBHelper.deleteChatRoom(roomNo);
                                            }
                                        }).start();

                                        // notify adapter
                                        adapterList.notifyDataSetChanged();

                                        break;
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


    @Override
    protected void initAdapter() {
        adapterList = new CurrentChatAdapter(mContext, dataSet, rvMainList, mOnContextMenuSelect);
        //enableLoadingMore();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        showIcon();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        hideIcon();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case 1001:
                    if (data != null) {
                        final int roomNo = data.getIntExtra(Statics.ROOM_NO, 0);
                        final String roomTitle = data.getStringExtra(Statics.ROOM_TITLE);
                        // Update current chat list

                        for (ChattingDto a : dataSet) {
                            if (roomNo == a.getRoomNo()) {
                                a.setRoomTitle(roomTitle);
                                adapterList.notifyDataSetChanged();
                                break;
                            }

                        }
                        // Start new thread to update local database
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                ChatRomDBHelper.updateChatRoom(roomNo, roomTitle);
                            }
                        }).start();
                    }
                    break;

            }
        }
    }

    @Override
    protected void reloadContentPage() {
        //dataSet.add(null);
        //adapterList.notifyItemInserted(dataSet.size() - 1);
//        mHttpRequest.getAllEmployeesSort(this, millis,limit,userNo,sortType);
        initList();
    }

    @Override
    protected void addMoreItem() {
//        mHttpRequest.getAllEmployeesSort(this, millis,limit,userNo,sortType);
    }

    @Override
    public void onHTTPSuccess(List<CurrentChatDto> dtos) {
       /* if(dataSet==null) {
            return;
        }
        int dataSetSize= dataSet.size();
        if(dataSetSize>0) {
            dataSet.remove(dataSet.size() - 1);
            adapterList.notifyItemRemoved(dataSet.size());
        }
        dataSet.addAll(dtos);
//        if(dataSet!=null&&dataSet.size()>0) {
//            lastID = (current_Task.get(current_Task.size() - 1)).userno;
//        }
        adapterList.notifyItemChanged(dataSetSize, dataSet.size());
        if(dataSetSize+limit<=dataSet.size())
        {
            adapterList.setLoaded();
        }*/
    }

    @Override
    public void onHTTPFail(ErrorDto errorDto) {
        progressBar.setVisibility(View.GONE);
    }

    /***/
    public void reloadDataSet() {
        getDataFromServer();
    }

    //change late when have api
    /*@Override
    protected void initSwipeRefresh() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }*/
    public void updateRoomUnread(long roomNo) {
        for (ChattingDto chattingDto : dataSet) {
            if (chattingDto.getRoomNo() == roomNo) {
                chattingDto.setUnReadCount(0);
                adapterList.updateData(dataSet, dataSet.indexOf(chattingDto));
                break;
            }
        }
    }

    public void updateRoomUnread(long roomNo, int count) {
        for (ChattingDto chattingDto : dataSet) {
            if (chattingDto.getRoomNo() == roomNo) {
                chattingDto.setUnReadCount(count);
                adapterList.updateData(dataSet, dataSet.indexOf(chattingDto));
                break;
            }
        }
    }

    public void updateFavoriteStatus(long roomNo) {
        for (ChattingDto chattingDto : dataSet) {
            if (chattingDto.getRoomNo() == roomNo) {
                chattingDto.setFavorite(true);
                break;
            }
        }
    }

    public void updateDataSet(ChattingDto dto) {
        boolean isContains = false;
        for (ChattingDto chattingDto : dataSet) {
            if (chattingDto.getRoomNo() == dto.getRoomNo()) {

                chattingDto.setLastedMsg(dto.getMessage());
                chattingDto.setLastedMsgType(dto.getLastedMsgType());
                chattingDto.setLastedMsgAttachType(dto.getLastedMsgAttachType());
                chattingDto.setLastedMsgDate(dto.getLastedMsgDate());
                chattingDto.setRegDate(dto.getRegDate());

                chattingDto.setUnreadTotalCount(dto.getUnreadTotalCount());
                chattingDto.setWriterUserNo(dto.getWriterUserNo());

                chattingDto.setRoomNo(dto.getRoomNo());
                chattingDto.setMessage(dto.getMessage());
                chattingDto.setMessageNo(dto.getMessageNo());

                chattingDto.setAttachNo(dto.getAttachNo());
                chattingDto.setAttachFileName(dto.getAttachFileName());
                chattingDto.setAttachFileType(dto.getAttachFileType());
                chattingDto.setAttachFilePath(dto.getAttachFilePath());
                chattingDto.setAttachFileSize(dto.getAttachFileSize());


                if (dto.getLastedMsgDate() != null) {
                    String time = TimeUtils.convertTimeDeviceToTimeServerDefault(dto.getLastedMsgDate());
                    chattingDto.setLastedMsgDate(time);
                } else if (dto.getRegDate() != null) {
                    String time = TimeUtils.convertTimeDeviceToTimeServerDefault(dto.getRegDate());
                    chattingDto.setLastedMsgDate(time);
                    Utils.printLogs("####LastedMsgDate = " + chattingDto.getLastedMsgDate());
                }


                if (ChattingFragment.instance == null) {
                    chattingDto.setUnReadCount(chattingDto.getUnReadCount() + 1);

                } else {
                    if (ChattingFragment.instance.roomNo != CrewChatApplication.currentRoomNo) {
                        chattingDto.setUnReadCount(chattingDto.getUnReadCount() + 1);

                    }
                }
                isContains = true;

                break;
            }
        }

        if (!isContains) {
            HttpRequest.getInstance().GetChatList(new OnGetChatList() {
                @Override
                public void OnGetChatListSuccess(List<ChattingDto> list) {
                    dataSet.clear();


                    List<TreeUserDTOTemp> list1;
                    TreeUserDTOTemp treeUserDTOTemp1;

                    // Sort a gain
                    Collections.sort(list, new Comparator<ChattingDto>() {
                        public int compare(ChattingDto chattingDto1, ChattingDto chattingDto2) {
                            if (chattingDto1.getLastedMsgDate() == null || chattingDto2.getLastedMsgDate() == null)
                                return -1;
                            return chattingDto2.getLastedMsgDate().compareToIgnoreCase(chattingDto1.getLastedMsgDate());
                        }
                    });

                    for (ChattingDto chattingDto : list) {
                        if (!Utils.checkChat(chattingDto, myId)) {
                            if (!Utils.checkChatId198(chattingDto)) {
                                list1 = new ArrayList<>();
                                for (int id : chattingDto.getUserNos()) {
                                    if (myId != id) {
                                        treeUserDTOTemp1 = Utils.GetUserFromDatabase(treeUserDTOTempList, id);
                                        if (treeUserDTOTemp1 != null) {
                                            list1.add(treeUserDTOTemp1);
                                        }
                                    }
                                }
                                chattingDto.setListTreeUser(list1);
                                dataSet.add(chattingDto);
                            }
                        }
                    }
                    //adapterList.updateData(dataSet);
                }

                @Override
                public void OnGetChatListFail(ErrorDto errorDto) {

                }
            });
        } else {

            // Sort by date

            Collections.sort(dataSet, new Comparator<ChattingDto>() {
                public int compare(ChattingDto chattingDto1, ChattingDto chattingDto2) {
                    if (chattingDto1.getLastedMsgDate() == null || chattingDto2.getLastedMsgDate() == null)
                        return -1;
                    return chattingDto2.getLastedMsgDate().compareToIgnoreCase(chattingDto1.getLastedMsgDate());
                }
            });

            /*Collections.sort(dataSet, new Comparator<ChattingDto>() {
                public int compare(ChattingDto chattingDto1, ChattingDto chattingDto2) {
                    return chattingDto2.getUnReadCount() - chattingDto1.getUnReadCount();
                }
            });*/

            //adapterList.updateData(dataSet);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            showIcon();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        showIcon();
        isActive = true;
        registerGCMReceiver();
        if (isUpdate) {
            isUpdate = false;
            adapterList.updateData(dataSet);
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        isActive = false;
        unregisterGCMReceiver();
    }

    private void registerGCMReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Statics.ACTION_RECEIVER_NOTIFICATION);
        filter.addAction(Constant.INTENT_FILTER_GET_MESSAGE_UNREAD_COUNT);
        filter.addAction(Constant.INTENT_FILTER_ADD_USER);
        filter.addAction(Constant.INTENT_FILTER_NOTIFY_ADAPTER);
        getActivity().registerReceiver(mReceiverNewAssignTask, filter);
    }

    private void unregisterGCMReceiver() {
        getActivity().unregisterReceiver(mReceiverNewAssignTask);
    }

    private BroadcastReceiver mReceiverNewAssignTask = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(Statics.ACTION_RECEIVER_NOTIFICATION)) {
                adapterList.updateData(dataSet);
                isUpdate = false;
            } else if (intent.getAction().equals(Constant.INTENT_FILTER_ADD_USER)) {
                getDataFromServer();
            } else if (intent.getAction().equals(Constant.INTENT_FILTER_NOTIFY_ADAPTER)) {
                // get action
                long roomNo = intent.getLongExtra("roomNo", 0);
                int type = intent.getIntExtra("type", 0);
                // Search roomNo
                int pos = 0;
                for (ChattingDto chat : dataSet) {
                    if (chat.getRoomNo() == roomNo) {
                        if (type == Constant.TYPE_ACTION_ALARM_ON) {
                            chat.setNotification(true);
                            Utils.printLogs("Receive broad cast ############### ALARM ON");
                        } else if (type == Constant.TYPE_ACTION_ALARM_OFF) {
                            chat.setNotification(false);
                            Utils.printLogs("Receive broad cast ############### ALARM OFF");
                        } else if (type == Constant.TYPE_ACTION_FAVORITE) {
                            chat.setFavorite(false);
                            Utils.printLogs("Receive broad cast ############### FAVORITE");
                        }

                        // Notify database
                        final int finalPos = pos;
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (adapterList != null) {
                                    adapterList.notifyItemChanged(finalPos);
                                }
                            }
                        });

                        break;
                    }

                    // increase position
                    pos++;
                }

            } else if (intent.getAction().equals(Constant.INTENT_FILTER_GET_MESSAGE_UNREAD_COUNT)) {

                long roomNo = intent.getLongExtra(Constant.KEY_INTENT_ROOM_NO, 0);
                int unreadCount = intent.getIntExtra(Constant.KEY_INTENT_UNREAD_TOTAL_COUNT, 0);
                long userNo = intent.getLongExtra(Constant.KEY_INTENT_USER_NO, 0);

                // update roomNo and total unread count
                Utils.printLogs("Unread count = " + unreadCount + " at room =" + roomNo);
                int pos = 0;
                for (ChattingDto dto : dataSet) {
                    if (dto.getRoomNo() == roomNo) {
                        dto.setUnreadTotalCount(unreadCount);
                        // fix unread total count
                        if (userNo != 0) {
                            dto.setUnReadCount(0);
                        }

                        final int finalPos = pos;
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapterList.notifyItemChanged(finalPos);
                            }
                        });

                        break;
                    }
                    pos++;
                }
            }
        }
    };

    /**
     * RECEIVE NOTIFICATION ADD USER
     */
   /* private void receiveNotificationAddUser(long roomNo) {
        HttpRequest.getInstance().GetChatRoom(roomNo, new OnGetChatRoom() {
            @Override
            public void OnGetChatRoomSuccess(ChatRoomDTO chatRoomDTO) {
                ChattingDto chattingDto = new ChattingDto();
                chattingDto.setRoomNo(chatRoomDTO.getRoomNo());
                chattingDto.setMakeUserNo(chatRoomDTO.getMakeUserNo());
                chattingDto.setLastedMsg(Utils.getString(R.string.notification_add_user));
                chattingDto.setLastedMsgDate(chatRoomDTO.getLastedMsgDate());
                chattingDto.setUnReadCount(chatRoomDTO.getUnReadCount());
                chattingDto.setUserNos(chatRoomDTO.getUserNos());
                //ChattingDto.
                dataSet.add(chattingDto);
                adapterList.updateData(dataSet, dataSet.indexOf(chattingDto));
            }

            @Override
            public void OnGetChatRoomFail(ErrorDto errorDto) {

            }
        });
    }*/

    /*public void updateCurrentChatList() {
        *//*for (int i = 0; i < dataSet.size(); i++) {
            ChattingDto chattingDto = dataSet.get(i);
            if (chattingDto.getRoomNo() == dataDto.getRoomNo()) {
                chattingDto.setLastedMsg(dataDto.getMessage());
                chattingDto.setLastedMsgDate(dataDto.getRegDate());
                adapterList.notifyItemChanged(i);
                break;
            }
        }
        for (ChattingDto chattingDto : dataSet) {
            if (chattingDto.getRoomNo() == dataDto.getRoomNo()) {
                //int temp = chattingDto.getUnReadCount();
                //chattingDto.setUnReadCount(temp + 1);
                chattingDto.setLastedMsg(dataDto.getMessage());
                chattingDto.setRegDate(dataDto.getRegDate());

                adapterList.notifyDataSetChanged();
            }
        }*//*
        *//*String data = new Prefs().getStringValue(Statics.KEY_DATA_CURRENT_CHAT_LIST, "");
        if (!TextUtils.isEmpty(data)) {
            dataSet.clear();
            List<ChattingDto> list = Utils.convertStringToListChatting(data);
            Iterator<ChattingDto> it = list.iterator();
            while (it.hasNext()) {
                ChattingDto chattingDto = it.next();
                if (!Utils.checkChat(chattingDto)) {
                    if (!Utils.checkChatId198(chattingDto)) {
                        List<TreeUserDTOTemp> list1 = new ArrayList<>();
                        for (int id : chattingDto.getUserNos()) {
                            if (UserDBHelper.getUser().Id != id) {
                                TreeUserDTOTemp treeUserDTOTemp1 = Utils.GetUserFromDatabase(treeUserDTOTempList, id);
                                if (treeUserDTOTemp1 != null) {
                                    list1.add(treeUserDTOTemp1);
                                }
                            }
                        }
                        chattingDto.setListTreeUser(list1);
                        dataSet.add(chattingDto);
                    } else {
                        it.remove();
                    }
                } else {
                    it.remove();
                }
            }
            Collections.sort(dataSet, new Comparator<ChattingDto>() {
                public int compare(ChattingDto chattingDto1, ChattingDto chattingDto2) {
                    return chattingDto2.getLastedMsgDate().compareToIgnoreCase(chattingDto1.getLastedMsgDate());
                }
            });
            Collections.sort(dataSet, new Comparator<ChattingDto>() {
                public int compare(ChattingDto chattingDto1, ChattingDto chattingDto2) {
                    return chattingDto2.getUnReadCount() - chattingDto1.getUnReadCount();
                }
            });
            adapterList.updateData(dataSet);
        }
*//*

        *//*HttpRequest.getInstance().GetChatList(new OnGetChatList() {
            @Override
            public void OnGetChatListSuccess(List<ChattingDto> list) {
                dataSet.clear();
                Iterator<ChattingDto> it = list.iterator();
                while (it.hasNext()) {
                    ChattingDto chattingDto = it.next();
                    if (!Utils.checkChat(chattingDto)) {
                        if (!Utils.checkChatId198(chattingDto)) {
                            List<TreeUserDTOTemp> list1 = new ArrayList<>();
                            for (int id : chattingDto.getUserNos()) {
                                if (UserDBHelper.getUser().Id != id) {
                                    TreeUserDTOTemp treeUserDTOTemp1 = Utils.GetUserFromDatabase(treeUserDTOTempList, id);
                                    if (treeUserDTOTemp1 != null) {
                                        list1.add(treeUserDTOTemp1);
                                    }
                                }
                            }
                            chattingDto.setListTreeUser(list1);
                            dataSet.add(chattingDto);
                        } else {
                            it.remove();
                        }
                    } else {
                        it.remove();
                    }
                }
                adapterList.notifyDataSetChanged();
            }

            @Override
            public void OnGetChatListFail(ErrorDto errorDto) {

            }
        });*//*
    }*/

    /*
    * description : after data get from server, data will be stored on local storage
    * */
    private void storeListChatRoomToLocal(final ChattingDto data) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ChatRomDBHelper.addChatRoom(data);
            }
        }).start();
    }

    /**
     * GET DATA FROM SERVER
     */
    public void getDataFromServer() {
        HttpRequest.getInstance().GetListOrganize(new IGetListOrganization() {
            @Override
            public void onGetListSuccess(final ArrayList<TreeUserDTOTemp> treeUserDTOs) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        AllUserDBHelper.addUser(treeUserDTOs);
                    }
                }).start();

                // New Thread to get chat list
                getChatList(treeUserDTOs);

            }

            @Override
            public void onGetListFail(ErrorDto dto) {
                countDataFromServer(true);
            }
        });

    }

    private void getChatList(final List<TreeUserDTOTemp> listOfUsers) {

        HttpRequest.getInstance().GetChatList(new OnGetChatList() {
            @Override
            public void OnGetChatListSuccess(List<ChattingDto> list) {

                for (ChattingDto chattingDto : list) {
                    Utils.printLogs("From chatList = " + chattingDto.toString());
                }

                // Hide progress
                progressBar.setVisibility(View.GONE);

                List<ChattingDto> listChat = ChatRomDBHelper.getChatRooms();
                int localSize = listChat.size();
                int severSize = list.size();

                Collections.sort(list, new Comparator<ChattingDto>() {
                    public int compare(ChattingDto chattingDto1, ChattingDto chattingDto2) {
                        return chattingDto2.getLastedMsgDate().compareToIgnoreCase(chattingDto1.getLastedMsgDate());
                    }
                });

                /*Collections.sort(list, new Comparator<ChattingDto>() {
                    public int compare(ChattingDto chattingDto1, ChattingDto chattingDto2) {
                        return chattingDto2.getUnReadCount() - chattingDto1.getUnReadCount();
                    }
                });*/
                isFirstTime = false;

                if (localSize != severSize) {
                    ChatRomDBHelper.clearChatRooms();
                    dataSet.clear();
                    adapterList.notifyDataSetChanged();

                    List<TreeUserDTOTemp> list1;
                    TreeUserDTOTemp treeUserDTOTemp1;

                    // Sort before display it

                    for (ChattingDto chattingDto : list) {
                        if (!Utils.checkChat(chattingDto, myId)) {
                            if (!Utils.checkChatId198(chattingDto)) {
                                list1 = new ArrayList<>();


                                // remove duplicate user
                                ArrayList<Integer> cloneArr = new ArrayList<>(chattingDto.getUserNos());
                                Utils.removeArrayDuplicate(cloneArr);

                                for (int id : cloneArr) {
                                    if (myId != id) {
                                        treeUserDTOTemp1 = Utils.GetUserFromDatabase(listOfUsers, id);

                                        if (treeUserDTOTemp1 != null) {
                                            list1.add(treeUserDTOTemp1);
                                        }
                                    }
                                }
                                chattingDto.setListTreeUser(list1);
                                dataSet.add(chattingDto);

                                // store data to local database
                                storeListChatRoomToLocal(chattingDto);
                            }
                        }
                    }

                    // After get data, show it on view
                    /*for (ChattingDto chattingDto : list) {
                        Utils.printLogs("From chatList = "+chattingDto.toString());
                    }*/

                    if (dataSet != null && dataSet.size() > 0) {
                        adapterList.notifyDataSetChanged();
                    }

                } else {

                    for (ChattingDto dto : list) {

                        //Utils.printLogs("From chatList update = "+dto.toString());

                        dto.setUnreadTotalCount(dto.getUnReadCount());
                        //Utils.printLogs("Chat server = "+dto.getUnreadTotalCount());
                        ChatRomDBHelper.updateChatRoom(dto.getRoomNo(), dto.getLastedMsg(), dto.getLastedMsgType(), dto.getLastedMsgAttachType(), dto.getLastedMsgDate(), dto.getUnreadTotalCount(), dto.getUnReadCount(), dto.getMsgUserNo());

                        for (ChattingDto chat : dataSet) {
                            if (chat.getRoomNo() == dto.getRoomNo()) {
                                chat.setLastedMsg(dto.getLastedMsg());
                                chat.setRoomTitle(dto.getRoomTitle());
                                chat.setLastedMsgType(dto.getLastedMsgType());
                                chat.setLastedMsgAttachType(dto.getLastedMsgAttachType());
                                chat.setAttachFileName(dto.getAttachFileName());
                                chat.setLastedMsgDate(dto.getLastedMsgDate());
                                chat.setMsgUserNo(dto.getMsgUserNo());
                                chat.setUnreadTotalCount(dto.getUnreadTotalCount());
                                chat.setUnReadCount(dto.getUnReadCount());
                                chat.setWriterUserNo(dto.getWriterUserNo());
                                chat.setMsgUserNo(dto.getMsgUserNo());
                                break;
                            }
                        }
                    }

                    Collections.sort(dataSet, new Comparator<ChattingDto>() {
                        public int compare(ChattingDto chattingDto1, ChattingDto chattingDto2) {
                            return chattingDto2.getLastedMsgDate().compareToIgnoreCase(chattingDto1.getLastedMsgDate());
                        }
                    });

                    adapterList.notifyDataSetChanged();

                }

                countDataFromServer(true);
            }

            @Override
            public void OnGetChatListFail(ErrorDto errorDto) {
                progressBar.setVisibility(View.GONE);
                countDataFromServer(true);
            }
        });
    }

    /**
     * UPDATE WHEN ADD USER
     */
    public void updateWhenAddUser(long roomNo, ArrayList<Integer> userNosAdd) {
        for (ChattingDto chattingDto : dataSet) {
            if (chattingDto.getRoomNo() == roomNo) {
                ArrayList<Integer> chattingDTOUserNos = chattingDto.getUserNos();
                for (int i : userNosAdd) {
                    chattingDTOUserNos.add(i);
                    for (TreeUserDTOTemp treeUserDTOTemp : treeUserDTOTempList) {
                        if (i == treeUserDTOTemp.getUserNo()) {
                            chattingDto.getListTreeUser().add(treeUserDTOTemp);
                            break;
                        }
                    }
                }
                chattingDto.setUserNos(chattingDTOUserNos);
                adapterList.notifyItemChanged(dataSet.indexOf(chattingDto));
                break;
            }
        }
    }

    /**
     * CHECK HAVE DATA FROM SERVER
     */
    private void countDataFromServer(boolean isHaveData) {
        if (isHaveData) {
            rvMainList.setVisibility(View.VISIBLE);
            no_item_found.setVisibility(View.GONE);
        } else {
            rvMainList.setVisibility(View.GONE);
            no_item_found.setVisibility(View.VISIBLE);
            no_item_found.setText("No Data");
        }
    }

    /**
     * UPDATE DATA
     */
    public void updateData(ChattingDto dto, boolean isAddUnread) {
        boolean isContains = false;
        for (ChattingDto chattingDto : dataSet) {
            if (chattingDto.getRoomNo() == dto.getRoomNo()) {
                chattingDto.setLastedMsg(dto.getMessage());
                chattingDto.setLastedMsgType(dto.getLastedMsgType());
                chattingDto.setLastedMsgAttachType(dto.getLastedMsgAttachType());
                chattingDto.setAttachFileName(dto.getAttachFileName());
                chattingDto.setLastedMsgDate(dto.getRegDate());

                if (isAddUnread) {
                    chattingDto.setUnReadCount(chattingDto.getUnReadCount() + 1);
                }
                isContains = true;
                break;
            }
        }

        if (!isContains) {
            HttpRequest.getInstance().GetChatList(new OnGetChatList() {
                @Override
                public void OnGetChatListSuccess(List<ChattingDto> list) {
                    dataSet.clear();
                    List<TreeUserDTOTemp> list1;
                    TreeUserDTOTemp treeUserDTOTemp1;

                    for (ChattingDto chattingDto : list) {
                        if (!Utils.checkChat(chattingDto, myId)) {
                            if (!Utils.checkChatId198(chattingDto)) {
                                list1 = new ArrayList<>();
                                for (int id : chattingDto.getUserNos()) {
                                    if (myId != id) {
                                        treeUserDTOTemp1 = Utils.GetUserFromDatabase(treeUserDTOTempList, id);
                                        if (treeUserDTOTemp1 != null) {
                                            list1.add(treeUserDTOTemp1);
                                        }
                                    }
                                }
                                chattingDto.setListTreeUser(list1);
                                dataSet.add(chattingDto);
                            }
                        }
                    }
                    if (dataSet != null && dataSet.size() > 0) {
                        countDataFromServer(true);
                        adapterList.updateData(dataSet);

                    } else {
                        countDataFromServer(false);
                    }
                }

                @Override
                public void OnGetChatListFail(ErrorDto errorDto) {
                    countDataFromServer(false);
                }
            });
        } else {


            Collections.sort(dataSet, new Comparator<ChattingDto>() {
                public int compare(ChattingDto chattingDto1, ChattingDto chattingDto2) {
                    if (chattingDto1.getLastedMsgDate() == null || chattingDto2.getLastedMsgDate() == null) {
                        return -1;
                    }
                    return chattingDto2.getLastedMsgDate().compareToIgnoreCase(chattingDto1.getLastedMsgDate());
                }
            });

           /* Collections.sort(dataSet, new Comparator<ChattingDto>() {
                public int compare(ChattingDto chattingDto1, ChattingDto chattingDto2) {
                    return chattingDto2.getUnReadCount() - chattingDto1.getUnReadCount();
                }
            });*/

            adapterList.updateData(dataSet);
        }
    }

    public void ReloadList() {
        if (adapterList != null) {
            adapterList.notifyDataSetChanged();
        }
    }

}
