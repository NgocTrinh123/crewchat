package com.dazone.crewchat.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.dazone.crewchat.Class.ChatInputView;
import com.dazone.crewchat.Enumeration.ChatMessageType;
import com.dazone.crewchat.HTTPs.HttpRequest;
import com.dazone.crewchat.R;
import com.dazone.crewchat.Tree.Dtos.TreeUserDTO;
import com.dazone.crewchat.activity.ChatViewImageActivity;
import com.dazone.crewchat.adapter.ChattingAdapter;
import com.dazone.crewchat.constant.Statics;
import com.dazone.crewchat.customs.EmojiView;
import com.dazone.crewchat.database.AllUserDBHelper;
import com.dazone.crewchat.database.ChatMessageDBHelper;
import com.dazone.crewchat.database.ChatRomDBHelper;
import com.dazone.crewchat.database.UserDBHelper;
import com.dazone.crewchat.dto.AttachDTO;
import com.dazone.crewchat.dto.ChattingDto;
import com.dazone.crewchat.dto.ErrorDto;
import com.dazone.crewchat.dto.GroupDto;
import com.dazone.crewchat.dto.MessageUnreadCountDTO;
import com.dazone.crewchat.dto.TreeUserDTOTemp;
import com.dazone.crewchat.dto.UserDto;
import com.dazone.crewchat.interfaces.IGetListOrganization;
import com.dazone.crewchat.interfaces.OnGetChatMessage;
import com.dazone.crewchat.interfaces.OnGetMessageUnreadCountCallBack;
import com.dazone.crewchat.interfaces.SendChatMessage;
import com.dazone.crewchat.interfaces.Urls;
import com.dazone.crewchat.socket.NetClient;
import com.dazone.crewchat.utils.Constant;
import com.dazone.crewchat.utils.CrewChatApplication;
import com.dazone.crewchat.utils.ImageUtils;
import com.dazone.crewchat.utils.Prefs;
import com.dazone.crewchat.utils.TimeUtils;
import com.dazone.crewchat.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static com.dazone.crewchat.database.ChatMessageDBHelper.addMessage;

/**
 * Created by david on 12/24/15.
 * 채팅방 내부 Fragment 입니다.
 * 채팅방의 내부 UI 를 구성합니다.
 */
public class ChattingFragment extends ListFragment<ChattingDto> implements View.OnClickListener, EmojiView.EventListener, View.OnLayoutChangeListener {
    private TreeUserDTO dto;

    public long roomNo;
    private ArrayList<Integer> userNos;
    public boolean isActive = false;


    public ChatInputView view;
    private ArrayList<TreeUserDTOTemp> listTemp = null;
    private int userID;
    public static ChattingFragment instance;
    private List<ChattingDto> dataFromServer = new ArrayList<>();
    private boolean isScrolling = false;
    public boolean isVisible = false;
    public boolean isUpdate = false;

    /**
     * LoadMore
     */
    private boolean isLoading = false;
    private boolean isLoadMore = true;
    private boolean isLoaded = false;
    private boolean hasLoadMore = false;
    private boolean isGetFromServer = false;

    private int mTotalScrolled = 0;
    private boolean isFromNotification = false;

    private boolean isShowNewMessage = false;
    private Activity mActivity;
    UserDto temp = null;

    // 메시지 핸들러 코드값
    private int WHAT_CODE_HIDE_PROCESS = 0;
    private int WHAT_CODE_NOTIFY_DATASET = 1;
    private int WHAT_CODE_ADD_NEW_DATA = 2;
    private int WHAT_CODE_EMPTY = 3;
    private int WHAT_CODE_HAS_INIT = 4;

    private String addNewData = "AddNewData";

    public ChattingFragment() {

    }

    public ChattingFragment(Activity activity) {
        this.mActivity = activity;
    }

    protected final android.os.Handler mHandler = new android.os.Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == WHAT_CODE_HIDE_PROCESS) {
                progressBar.setVisibility(View.GONE);
            } else if (msg.what == WHAT_CODE_NOTIFY_DATASET) {

                adapterList.notifyDataSetChanged();
                if (!isLoadMore) {
                    layoutManager.scrollToPosition(dataSet.size() - 1);
                }
            } else if (msg.what == WHAT_CODE_ADD_NEW_DATA) {
                Bundle args = msg.getData();
                dataFromServer = (ArrayList<ChattingDto>) args.getSerializable(addNewData);

                // prepare this
                boolean hasInit = false;
                if (msg.arg1 == WHAT_CODE_HAS_INIT) {
                    hasInit = true;
                }
                addData(dataFromServer, hasInit);

            } else if (msg.what == WHAT_CODE_EMPTY) {
                initData();
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        userID = Utils.getCurrentId();

        temp = CrewChatApplication.currentUser;
        if (temp == null) {
            temp = UserDBHelper.getUser();
        }

        Bundle bundle = getArguments();
        userID = Utils.getCurrentId();
        if (bundle != null) {
            roomNo = bundle.getLong(Constant.KEY_INTENT_ROOM_NO, 0);
            userNos = bundle.getIntegerArrayList(Constant.KEY_INTENT_USER_NO_ARRAY);
        }
    }

    // 로컬 DB에 저장된 채팅 메시지 내역을 가져옵니다.
    private void loadClientData() {
        isLoaded = true;
//        progressBar.setVisibility(View.VISIBLE);
        // Load chat message section from database and init first
        final ArrayList<ChattingDto> listChatMessage = ChatMessageDBHelper.getMsgSession(roomNo, 0, ChatMessageDBHelper.FIRST);
        if (listChatMessage != null) {
            for (ChattingDto chat : listChatMessage) {
                Utils.printLogs("Message = " + roomNo + " message = " + chat.toString());
            }
        }

        dataFromServer = listChatMessage;
        //int chatsize = 0;
        if (listChatMessage != null && listChatMessage.size() > 0) {
            //chatsize = listChatMessage.size();
            initData(listChatMessage);
        } // do nothing if chat list == 0

        // perform to get online data if network is available
        // on client data init finished
        if (Utils.isNetworkAvailable()) {
            // if delay time the result will be wrong
            getOnlineData(roomNo, listChatMessage);
        } else {
            progressBar.setVisibility(View.GONE);
            if (listChatMessage != null && listChatMessage.size() == 0) {
                initData();
            }
        }
    }

    private void updateUnreadCount(long roomNo, long startNo) {
        HttpRequest.getInstance().UpdateMessageUnreadCount(roomNo, userID, startNo);
    }

    // Thread to get data from server
    private void getOnlineData(final long roomNo, final ArrayList<ChattingDto> listChatMessage) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                long startNo = 0;
                final int listMessageSize = listChatMessage.size();
                if (listMessageSize > 0) {
                    int last_index = 1;
                    int unreadCount = ChatRomDBHelper.getUnreadCount(roomNo);
                    if (unreadCount > 0) {
                        last_index += unreadCount;
                    }
                    int index = listMessageSize - last_index;
                    if (index < 0) {
                        index = 0;
                    }
                    startNo = listChatMessage.get(index).getMessageNo();
                }
                Utils.printLogs("Latest local messageNo =" + startNo);

                // Call API to update this message
                updateUnreadCount(roomNo, startNo);

                int mesType = startNo == 0 ? ChatMessageDBHelper.FIRST : ChatMessageDBHelper.AFTER;
                // Get all message from standard message
                HttpRequest.getInstance().GetChatMsgSection(roomNo, startNo, mesType, new OnGetChatMessage() {
                    @Override
                    public void OnGetChatMessageSuccess(List<ChattingDto> listNew) {

                        // hide progressBar when loading data from server is success
                        mHandler.obtainMessage(WHAT_CODE_HIDE_PROCESS).sendToTarget();

                        Utils.printLogs("Sync new data from server ");
                        for (ChattingDto chat : listNew) {
                            Utils.printLogs("Chat ->" + chat.toString());
                        }
                        isLoaded = true;
                        // perform thread to sync data server with client

                        // add to current data list and notify dataset
                        ArrayList<ChattingDto> newDataFromServer = new ArrayList<>();
                        if (listNew.size() > 0) {

                        /*
                        * Change follow --> just update unReadCount when loading image success
                        * */
                            // Update unread count to server
                            long startMsgNo = listNew.get(listNew.size() - 1).getMessageNo();
                            // Update unRead count message
                            updateUnreadCount(roomNo, startMsgNo);

                            isGetFromServer = true;

                            if (CurrentChatListFragment.fragment != null) {
                                CurrentChatListFragment.fragment.updateRoomUnread(roomNo);
                            }

                            // Save online data to local data
                            for (ChattingDto chat : listNew) {
                                boolean isExist = false;
                                for (ChattingDto dto : dataFromServer) {
                                    if (chat.getMessageNo() == dto.getMessageNo()) {
                                        // My be update something
                                        isExist = true;
                                        break;
                                    }
                                }

                                // Check if message is exist
                                if (!isExist) {
                                    // add to server and save to local database
                                    dataFromServer.add(chat);
                                    newDataFromServer.add(chat);
                                    addMessage(chat);
                                }
                            }

                            // notify database
                            if (newDataFromServer.size() > 0) {
                                // Need to send array list object via handler
                                Message message = Message.obtain();
                                message.what = WHAT_CODE_ADD_NEW_DATA;
                                if (listMessageSize > 0) {
                                    message.arg1 = WHAT_CODE_HAS_INIT;
                                }

                                Bundle args = new Bundle();
                                args.putSerializable(addNewData, newDataFromServer);
                                message.setData(args);
                                mHandler.sendMessage(message);
                            } else {
                                if (listChatMessage.size() == 0) {
                                    mHandler.obtainMessage(WHAT_CODE_EMPTY).sendToTarget();
                                }
                            }
                        }
                    }

                    @Override
                    public void OnGetChatMessageFail(ErrorDto errorDto) {
                        isLoaded = true;
                        mHandler.obtainMessage(WHAT_CODE_HIDE_PROCESS).sendToTarget();
                        if (listChatMessage.size() == 0) {
                            mHandler.obtainMessage(WHAT_CODE_EMPTY).sendToTarget();
                        }
                    }
                });

            }
        }).start();
    }

    @Override
    public void onStart() {
        super.onStart();

        listTemp = CrewChatApplication.listUsers;
        if (listTemp == null) {
            listTemp = AllUserDBHelper.getUser();
        }

        if (listTemp != null && listTemp.size() == 0) {
            HttpRequest.getInstance().GetListOrganize(new IGetListOrganization() {
                @Override
                public void onGetListSuccess(final ArrayList<TreeUserDTOTemp> treeUserDTOs) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            AllUserDBHelper.addUser(treeUserDTOs);
                            listTemp.addAll(treeUserDTOs);
                        }
                    }).start();

                }

                @Override
                public void onGetListFail(ErrorDto dto) {

                }
            });
        }
        // Load client data at first, then call load online data on new thread
        // Just load on the first time
        if (!isLoaded) {
            loadClientData();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        CrewChatApplication.currentRoomNo = 0;
    }

    /**
     * Set ARGUMENTS FRAGMENT
     * Fragment를 초기화하며 해당 정보를 넘깁니다. 방번호와 참여유저No 정보가 세팅됩니다.
     */
    public ChattingFragment newInstance(long roomNo, ArrayList<Integer> userNos, Activity activity) {

        ChattingFragment fragment = new ChattingFragment(activity);
        Bundle args = new Bundle();
        args.putLong(Constant.KEY_INTENT_ROOM_NO, roomNo);
        args.putIntegerArrayList(Constant.KEY_INTENT_USER_NO_ARRAY, userNos);
        fragment.setArguments(args);
        return fragment;
    }

    public ChattingFragment newInstance(TreeUserDTO dto1, ChattingDto chattingDto1, Activity activity) {
        ChattingFragment fragment = new ChattingFragment(activity);
        Bundle args = new Bundle();
        args.putSerializable(Statics.TREE_USER_PC, dto1);
        args.putSerializable(Statics.CHATTING_DTO, chattingDto1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        hideNewMessage();
    }

    @Override
    protected void initAdapter() {

//        rvMainList.addOnScrollListener(new EndlessRecyclerOnScrollListener() {
//
//            @Override
//            public void onScrolledUp() {
//                super.onScrolledUp();
//                isShowNewMessage = true;
//                //Utils.printLogs("Scroll up");
//            }
//
//            @Override
//            public void onScrolledDown() {
//                super.onScrolledDown();
//                //isShowNewMessage = true;
//                //Utils.printLogs("Scroll down");
//            }
//
//            @Override
//            public void onScrolledToBottom() {
//                super.onScrolledToBottom();
//                hideNewMessage();
//                isShowNewMessage = false;
//                //Utils.printLogs("Scroll to bottom");
//            }
//
//            @Override
//            public void onScrolledToTop() {
//                super.onScrolledToTop();
//                isShowNewMessage = true;
//                loadMoreData();
//                //Utils.printLogs("Scroll to top");
//            }
//        });
        adapterList = new ChattingAdapter(mContext, mActivity, dataSet, rvMainList);
        layoutManager.setStackFromEnd(true);

        rvMainList.setLayoutManager(layoutManager);

    }

    @Override
    protected void reloadContentPage() {

    }

    @Override
    protected void addMoreItem() {

    }

    @Override
    protected void initList() {
        initFooter();
    }

    //TextView tv_status;

    private void initFooter() {

        rvMainList.addOnLayoutChangeListener(this);
//        rvMainList.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//                if (!isScrolling) {
//                    mTotalScrolled += dy;
//                }
//            }
//        });
        //recycler_footer.setVisibility(View.VISIBLE);
        //recycler_footer.addView(tv_status = initTextStatus());
        view = new ChatInputView(getContext());
        view.addToView(recycler_footer);
        view.mEmojiView.setEventListener(this);
        view.btnSend.setOnClickListener(this);
        list_content_rl.setBackgroundColor(ImageUtils.getColor(getContext(), R.color.chat_list_bg_color));
        disableSwipeRefresh();
    }

   /* private TextView initTextStatus() {
        TextView tv = (TextView) LayoutInflater.from(getActivity()).inflate(R.layout.row_chat_status_tv, null);
        return tv;
    }*/

    /*
    * This function just read data from local or get from server
    * Then add to object arrays and fill it to database
    * */
    public void addNewChat(ChattingDto chattingDto, boolean isUpdate) {
        UserDto user = null;
        if (dto != null) {
            switch (chattingDto.getType()) {
                case 0:
                    if (chattingDto.getWriterUser() != dto.getId()) {
                        user = new UserDto(String.valueOf(temp.Id), temp.FullName, temp.avatar);
                        boolean isCheck = false;
                        if (dataSet != null && dataSet.size() > 0) {
                            isCheck = Utils.getChattingType(chattingDto, dataSet.get(dataSet.size() - 1));
                        }
                        if (isCheck) {
                            chattingDto.setmType(Statics.CHATTING_VIEW_TYPE_SELF_NOT_SHOW);
                        } else {
                            chattingDto.setmType(Statics.CHATTING_VIEW_TYPE_SELF);
                        }

                    } else {
                        user = new UserDto(String.valueOf(dto.getId()), dto.getName(), dto.getAvatarUrl());
                        boolean isCheck = false;
                        if (dataSet != null && dataSet.size() > 0) {
                            isCheck = Utils.getChattingType(chattingDto, dataSet.get(dataSet.size() - 1));
                        }
                        if (isCheck) {
                            chattingDto.setmType(Statics.CHATTING_VIEW_TYPE_PERSON_NOT_SHOW);
                        } else {
                            chattingDto.setmType(Statics.CHATTING_VIEW_TYPE_PERSON);
                        }
                    }
                    break;
                case 1:
                    if (chattingDto.getWriterUser() != dto.getId()) {
                        user = new UserDto(String.valueOf(temp.Id), temp.FullName, temp.avatar);
                    } else {
                        user = new UserDto(String.valueOf(dto.getId()), dto.getName(), dto.getAvatarUrl());
                    }
                    chattingDto.setmType(Statics.CHATTING_VIEW_TYPE_GROUP_NEW);
                    break;
                case 2:
                    if (chattingDto.getWriterUser() != dto.getId()) {
                        user = new UserDto(String.valueOf(temp.Id), temp.FullName, temp.avatar);
                        chattingDto.setmType(Statics.CHATTING_VIEW_TYPE_SELF_IMAGE);
                    } else {
                        user = new UserDto(String.valueOf(dto.getId()), dto.getName(), dto.getAvatarUrl());
                        boolean isCheck = false;
                        if (dataSet != null && dataSet.size() > 0) {
                            isCheck = Utils.getChattingType(chattingDto, dataSet.get(dataSet.size() - 1));
                        }
                        if (isCheck) {
                            chattingDto.setmType(Statics.CHATTING_VIEW_TYPE_PERSON_IMAGE_NOT_SHOW);
                        } else {
                            chattingDto.setmType(Statics.CHATTING_VIEW_TYPE_PERSON_IMAGE);
                        }
                    }
                    break;
                default:
                    if (chattingDto.getWriterUser() != dto.getId()) {
                        user = new UserDto(String.valueOf(temp.Id), temp.FullName, temp.avatar);
                        chattingDto.setmType(Statics.CHATTING_VIEW_TYPE_SELF);
                    } else {
                        user = new UserDto(String.valueOf(dto.getId()), dto.getName(), dto.getAvatarUrl());
                        chattingDto.setmType(Statics.CHATTING_VIEW_TYPE_PERSON);
                    }
                    break;
            }
        } else {
            switch (chattingDto.getType()) {
                case ChatMessageType.Normal:
                    if (chattingDto.getWriterUser() == userID) {
                        user = new UserDto(String.valueOf(temp.Id), temp.FullName, temp.avatar);
                        boolean isCheck = false;
                        if (dataSet != null && dataSet.size() > 0) {
                            isCheck = Utils.getChattingType(chattingDto, dataSet.get(dataSet.size() - 1));
                        }
                        if (isCheck) {
                            chattingDto.setmType(Statics.CHATTING_VIEW_TYPE_SELF_NOT_SHOW);
                        } else {
                            chattingDto.setmType(Statics.CHATTING_VIEW_TYPE_SELF);
                        }
                    } else {
                        TreeUserDTOTemp treeUserDTOTemp = Utils.GetUserFromDatabase(listTemp, chattingDto.getWriterUser());
                        if (treeUserDTOTemp != null) {
                            user = new UserDto(String.valueOf(treeUserDTOTemp.getUserNo()), treeUserDTOTemp.getName(), treeUserDTOTemp.getAvatarUrl());
                        }

                        boolean isCheck = false;
                        if (dataSet != null && dataSet.size() > 0) {
                            isCheck = Utils.getChattingType(chattingDto, dataSet.get(dataSet.size() - 1));
                        }
                        if (isCheck) {
                            chattingDto.setmType(Statics.CHATTING_VIEW_TYPE_PERSON_NOT_SHOW);
                        } else {
                            chattingDto.setmType(Statics.CHATTING_VIEW_TYPE_PERSON);
                        }
                    }
                    break;
                case ChatMessageType.Group:
                    if (chattingDto.getWriterUser() == userID) {
                        user = new UserDto(String.valueOf(temp.Id), temp.FullName, temp.avatar);
                    } else {
                        TreeUserDTOTemp treeUserDTOTemp = Utils.GetUserFromDatabase(listTemp, chattingDto.getWriterUser());
                        if (treeUserDTOTemp != null)
                            user = new UserDto(String.valueOf(treeUserDTOTemp.getUserNo()), treeUserDTOTemp.getName(), treeUserDTOTemp.getAvatarUrl());
                    }
                    chattingDto.setmType(Statics.CHATTING_VIEW_TYPE_GROUP_NEW);
                    break;
                case ChatMessageType.Attach:
                    if (chattingDto.getWriterUser() == userID) {
                        user = new UserDto(String.valueOf(temp.Id), temp.FullName, temp.avatar);
                        if (chattingDto.getAttachInfo() != null) {
                            if (chattingDto.getAttachInfo().getType() == 1)
                                // 이미지 첨부 파일의 경우
                                chattingDto.setmType(Statics.CHATTING_VIEW_TYPE_SELF_IMAGE);
                            else {
                                // 일반 첨부 파일의 경우
                                Utils.printLogs("Attach file name = " + chattingDto.getAttachFileName() + " attach file type = " + chattingDto.getAttachFileType());
                                // Check is video or normal file
                                String filename = chattingDto.getAttachInfo().getFileName();
                                if (filename == null) {
                                    String filePath = chattingDto.getAttachInfo().getFullPath();
                                    if (filePath != null) {
                                        String pattern = Pattern.quote(System.getProperty("file.separator"));
                                        String[] files = filePath.split(pattern);
                                        if (files.length > 0) {
                                            filename = files[files.length - 1];
                                        }
                                    }
                                }

                                // 비디오 파일 유무체크하여, 타입값을 지정합니다.
                                if (Utils.isVideo(filename)) {
                                    chattingDto.setmType(Statics.CHATTING_VIEW_TYPE_SELF_VIDEO);
                                } else {
                                    chattingDto.setmType(Statics.CHATTING_VIEW_TYPE_SELF_FILE);
                                }
                            }
                        }
                    } else {
                        TreeUserDTOTemp treeUserDTOTemp = Utils.GetUserFromDatabase(listTemp, chattingDto.getWriterUser());
                        if (treeUserDTOTemp != null)
                            user = new UserDto(String.valueOf(treeUserDTOTemp.getUserNo()), treeUserDTOTemp.getName(), treeUserDTOTemp.getAvatarUrl());
                        if (chattingDto.getAttachInfo() != null)
                            if (chattingDto.getAttachInfo().getType() == 1) {
                                boolean isCheck = false;
                                if (dataSet != null && dataSet.size() > 0) {
                                    isCheck = Utils.getChattingType(chattingDto, dataSet.get(dataSet.size() - 1));
                                }
                                if (isCheck) {
                                    chattingDto.setmType(Statics.CHATTING_VIEW_TYPE_PERSON_IMAGE_NOT_SHOW);
                                } else {
                                    chattingDto.setmType(Statics.CHATTING_VIEW_TYPE_PERSON_IMAGE);
                                }
                            } else {
                                boolean isCheck = false;
                                if (dataSet != null && dataSet.size() > 0) {
                                    isCheck = Utils.getChattingType(chattingDto, dataSet.get(dataSet.size() - 1));
                                }
                                if (isCheck) {
                                    //chattingDto.setmType(Statics.CHATTING_VIEW_TYPE_PERSON_FILE_NOT_SHOW);
                                    String filename = chattingDto.getAttachInfo().getFileName();
                                    if (Utils.isVideo(filename)) {
                                        chattingDto.setmType(Statics.CHATTING_VIEW_TYPE_PERSON_VIDEO_NOT_SHOW);
                                    } else {
                                        chattingDto.setmType(Statics.CHATTING_VIEW_TYPE_PERSON_FILE_NOT_SHOW);
                                    }
                                } else {
                                    // Check is video or file
                                    String filename = chattingDto.getAttachInfo().getFileName();
                                    Utils.printLogs("Attach person file name = " + filename);
                                    if (Utils.isVideo(filename)) {
                                        chattingDto.setmType(Statics.CHATTING_VIEW_TYPE_PERSON_VIDEO_NOT_SHOW);
                                    } else {
                                        chattingDto.setmType(Statics.CHATTING_VIEW_TYPE_PERSON_FILE);
                                    }
                                }
                            }
                    }
                    break;
                default:
                    if (chattingDto.getWriterUser() == userID) {
                        user = new UserDto(String.valueOf(temp.Id), temp.FullName, temp.avatar);
                        chattingDto.setmType(Statics.CHATTING_VIEW_TYPE_SELF);
                    } else {
                        TreeUserDTOTemp treeUserDTOTemp = Utils.GetUserFromDatabase(listTemp, chattingDto.getWriterUser());
                        if (treeUserDTOTemp != null)
                            user = new UserDto(String.valueOf(treeUserDTOTemp.getUserNo()), treeUserDTOTemp.getName(), treeUserDTOTemp.getAvatarUrl());
                        chattingDto.setmType(Statics.CHATTING_VIEW_TYPE_PERSON);
                    }
                    break;
            }
        }

        if (!chattingDto.isCheckFromServer()) {
            if (view != null) {
                view.edt_comment.setText("");
            }
        }

        if (chattingDto.getType() == 2) {
            if (chattingDto.getAttachInfo() == null) {
                return;
            }
        }

        if (isUpdate) {
            if (CurrentChatListFragment.fragment != null) {
                CurrentChatListFragment.fragment.updateData(chattingDto, false);
            }
        }
        chattingDto.setUser(user);
        chattingDto.setContent(chattingDto.getMessage());
        dataSet.add(chattingDto);

        adapterList.notifyItemInserted(dataSet.size());
        if (((LinearLayoutManager) layoutManager).findLastCompletelyVisibleItemPosition() == dataSet.size() - 2) {
            layoutManager.scrollToPosition(dataSet.size() - 1);
        }

        if (!isFromNotification) {
            // Scroll to bottom
            if (!hasLoadMore) {
//                rvMainList.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        rvMainList.smoothScrollToPosition(dataSet.size());
//                        hasLoadMore = false;
//                    }
//                }, 1000);
            }

        } else {
            isFromNotification = false;
        }
    }

    // Add Image to ChattingView
    public void addNewChat(ChattingDto chattingDto, int position) {
        UserDto user = null;
        UserDto temp = Utils.getCurrentUser();
        if (dto != null) {
            switch (chattingDto.getType()) {
                case 0:
                    if (chattingDto.getWriterUser() != dto.getId()) {
                        user = new UserDto(String.valueOf(temp.Id), temp.FullName, temp.avatar);
                        boolean isCheck = false;
                        if (dataSet != null && dataSet.size() > 0) {
                            isCheck = Utils.getChattingType(chattingDto, dataSet.get(dataSet.size() - 1));
                        }
                        if (isCheck) {
                            chattingDto.setmType(Statics.CHATTING_VIEW_TYPE_SELF_NOT_SHOW);
                        } else {
                            chattingDto.setmType(Statics.CHATTING_VIEW_TYPE_SELF);
                        }

                    } else {
                        user = new UserDto(String.valueOf(dto.getId()), dto.getName(), dto.getAvatarUrl());
                        boolean isCheck = false;
                        if (dataSet != null && dataSet.size() > 0) {
                            isCheck = Utils.getChattingType(chattingDto, dataSet.get(dataSet.size() - 1));
                        }
                        if (isCheck) {
                            chattingDto.setmType(Statics.CHATTING_VIEW_TYPE_PERSON_NOT_SHOW);
                        } else {
                            chattingDto.setmType(Statics.CHATTING_VIEW_TYPE_PERSON);
                        }
                    }
                    break;
                case 1:
                    if (chattingDto.getWriterUser() != dto.getId()) {
                        user = new UserDto(String.valueOf(temp.Id), temp.FullName, temp.avatar);
                    } else {
                        user = new UserDto(String.valueOf(dto.getId()), dto.getName(), dto.getAvatarUrl());
                    }
                    chattingDto.setmType(Statics.CHATTING_VIEW_TYPE_GROUP_NEW);
                    break;
                case 2:
                    if (chattingDto.getWriterUser() != dto.getId()) {
                        user = new UserDto(String.valueOf(temp.Id), temp.FullName, temp.avatar);
                        chattingDto.setmType(Statics.CHATTING_VIEW_TYPE_SELF_IMAGE);
                    } else {
                        user = new UserDto(String.valueOf(dto.getId()), dto.getName(), dto.getAvatarUrl());
                        boolean isCheck = false;
                        if (dataSet != null && dataSet.size() > 0) {
                            isCheck = Utils.getChattingType(chattingDto, dataSet.get(dataSet.size() - 1));
                        }
                        if (isCheck) {
                            chattingDto.setmType(Statics.CHATTING_VIEW_TYPE_PERSON_IMAGE_NOT_SHOW);
                        } else {
                            chattingDto.setmType(Statics.CHATTING_VIEW_TYPE_PERSON_IMAGE);
                        }
                    }
                    break;
                default:
                    if (chattingDto.getWriterUser() != dto.getId()) {
                        user = new UserDto(String.valueOf(temp.Id), temp.FullName, temp.avatar);
                        chattingDto.setmType(Statics.CHATTING_VIEW_TYPE_SELF);
                    } else {
                        user = new UserDto(String.valueOf(dto.getId()), dto.getName(), dto.getAvatarUrl());
                        chattingDto.setmType(Statics.CHATTING_VIEW_TYPE_PERSON);
                    }
                    break;
            }

        } else {
            switch (chattingDto.getType()) {
                case 0:
                    if (chattingDto.getWriterUser() == userID) {
                        user = new UserDto(String.valueOf(temp.Id), temp.FullName, temp.avatar);
                        boolean isCheck = false;
                        if (dataSet != null && dataSet.size() > 0) {
                            isCheck = Utils.getChattingType(chattingDto, dataSet.get(dataSet.size() - 1));
                        }
                        if (isCheck) {
                            chattingDto.setmType(Statics.CHATTING_VIEW_TYPE_SELF_NOT_SHOW);
                        } else {
                            chattingDto.setmType(Statics.CHATTING_VIEW_TYPE_SELF);
                        }
                    } else {
                        TreeUserDTOTemp treeUserDTOTemp = Utils.GetUserFromDatabase(listTemp, chattingDto.getWriterUser());
                        if (treeUserDTOTemp != null)
                            user = new UserDto(String.valueOf(treeUserDTOTemp.getUserNo()), treeUserDTOTemp.getName(), treeUserDTOTemp.getAvatarUrl());

                        boolean isCheck = false;
                        if (dataSet != null && dataSet.size() > 0) {
                            isCheck = Utils.getChattingType(chattingDto, dataSet.get(dataSet.size() - 1));
                        }
                        if (isCheck) {
                            chattingDto.setmType(Statics.CHATTING_VIEW_TYPE_PERSON_NOT_SHOW);
                        } else {
                            chattingDto.setmType(Statics.CHATTING_VIEW_TYPE_PERSON);
                        }
                    }
                    break;
                case 1:
                    if (chattingDto.getWriterUser() == userID) {
                        user = new UserDto(String.valueOf(temp.Id), temp.FullName, temp.avatar);
                    } else {
                        TreeUserDTOTemp treeUserDTOTemp = Utils.GetUserFromDatabase(listTemp, chattingDto.getWriterUser());
                        if (treeUserDTOTemp != null)
                            user = new UserDto(String.valueOf(treeUserDTOTemp.getUserNo()), treeUserDTOTemp.getName(), treeUserDTOTemp.getAvatarUrl());
                    }
                    chattingDto.setmType(Statics.CHATTING_VIEW_TYPE_GROUP_NEW);
                    break;
                case 2:
                    if (chattingDto.getWriterUser() == userID) {
                        user = new UserDto(String.valueOf(temp.Id), temp.FullName, temp.avatar);
                        if (chattingDto.getAttachInfo() != null) {
                            if (chattingDto.getAttachInfo().getType() == 1)
                                chattingDto.setmType(Statics.CHATTING_VIEW_TYPE_SELF_IMAGE);
                            else {

                                Utils.printLogs("Attach file name = " + chattingDto.getAttachFileName() + " attach file type = " + chattingDto.getAttachFileType());
                                // Check is video or normal file
                                String filename = chattingDto.getAttachInfo().getFileName();
                                if (Utils.isVideo(filename)) {
                                    chattingDto.setmType(Statics.CHATTING_VIEW_TYPE_SELF_VIDEO);
                                } else {
                                    chattingDto.setmType(Statics.CHATTING_VIEW_TYPE_SELF_FILE);
                                }
                            }
                        }
                    } else {
                        TreeUserDTOTemp treeUserDTOTemp = Utils.GetUserFromDatabase(listTemp, chattingDto.getWriterUser());
                        if (treeUserDTOTemp != null)
                            user = new UserDto(String.valueOf(treeUserDTOTemp.getUserNo()), treeUserDTOTemp.getName(), treeUserDTOTemp.getAvatarUrl());
                        if (chattingDto.getAttachInfo() != null)
                            if (chattingDto.getAttachInfo().getType() == 1) {
                                boolean isCheck = false;
                                if (dataSet != null && dataSet.size() > 0) {
                                    isCheck = Utils.getChattingType(chattingDto, dataSet.get(dataSet.size() - 1));
                                }
                                if (isCheck) {
                                    chattingDto.setmType(Statics.CHATTING_VIEW_TYPE_PERSON_IMAGE_NOT_SHOW);
                                } else {
                                    chattingDto.setmType(Statics.CHATTING_VIEW_TYPE_PERSON_IMAGE);
                                }
                            } else {
                                boolean isCheck = false;
                                if (dataSet != null && dataSet.size() > 0) {
                                    isCheck = Utils.getChattingType(chattingDto, dataSet.get(dataSet.size() - 1));
                                }
                                if (isCheck) {
                                    chattingDto.setmType(Statics.CHATTING_VIEW_TYPE_PERSON_FILE_NOT_SHOW);
                                } else {

                                    // Check is video or normal file
                                    String filename = chattingDto.getAttachInfo().getFileName();
                                    Utils.printLogs("Attach person file = " + filename);
                                    if (Utils.isVideo(filename)) {
                                        chattingDto.setmType(Statics.CHATTING_VIEW_TYPE_PERSON_VIDEO_NOT_SHOW);
                                        Utils.printLogs("File is video");
                                    } else {
                                        chattingDto.setmType(Statics.CHATTING_VIEW_TYPE_PERSON_FILE);
                                        Utils.printLogs("File is normal file");
                                    }
                                }
                            }
                    }
                    break;
                default:
                    if (chattingDto.getWriterUser() == userID) {
                        user = new UserDto(String.valueOf(temp.Id), temp.FullName, temp.avatar);
                        chattingDto.setmType(Statics.CHATTING_VIEW_TYPE_SELF);
                    } else {
                        TreeUserDTOTemp treeUserDTOTemp = Utils.GetUserFromDatabase(listTemp, chattingDto.getWriterUser());
                        if (treeUserDTOTemp != null)
                            user = new UserDto(String.valueOf(treeUserDTOTemp.getUserNo()), treeUserDTOTemp.getName(), treeUserDTOTemp.getAvatarUrl());
                        chattingDto.setmType(Statics.CHATTING_VIEW_TYPE_PERSON);
                    }
                    break;
            }
        }

        /*if (!chattingDto.isCheckFromServer())
            if (view != null)
                view.edt_comment.setText("");*/

        if (chattingDto.getType() == 2) { // attach file but attach info is null --> return this
            if (chattingDto.getAttachInfo() == null) {
                return;
            }
        }
        //Utils.saveStringCurrentChatList(chattingDto, false);
        if (CurrentChatListFragment.fragment != null) {
            CurrentChatListFragment.fragment.updateData(chattingDto, false);
        }
        chattingDto.setUser(user);
        chattingDto.setContent(chattingDto.getMessage());
        dataSet.remove(position);
        dataSet.add(position, chattingDto);

        Utils.printLogs("Chatting Dto = " + chattingDto.toString());

        adapterList.notifyItemChanged(position);

        // Go to the bottom
        rvMainList.postDelayed(new Runnable() {
            @Override
            public void run() {
//                rvMainList.scrollToPosition(dataSet.size() - 1);
            }
        }, 1000);
    }

    private void addData(List<ChattingDto> list, boolean hasInit) {
        for (int i = 0; i < list.size(); i++) {
            ChattingDto chattingDto = list.get(i);
            if (i == 0) {
                long isTime = TimeUtils.getTimeForMail(TimeUtils.getTime(chattingDto.getRegDate()));
                if (isTime == -2) {

                    if (!hasInit) {
                        ChattingDto time = new ChattingDto();
                        time.setmType(Statics.CHATTING_VIEW_TYPE_DATE);
                        time.setRegDate(Utils.getString(R.string.today));
                        dataSet.add(time);
                    }
                    addNewChat(chattingDto, false);

                } else {
                    ChattingDto time = new ChattingDto();
                    time.setmType(Statics.CHATTING_VIEW_TYPE_DATE);
                    time.setTime(TimeUtils.getTime(list.get(0).getRegDate()));
                    dataSet.add(time);
                    addNewChat(chattingDto, false);
                }

            } else {
                long noTemp = TimeUtils.getTime((list.get(i - 1).getRegDate()));
                long noTemp2 = TimeUtils.getTime((chattingDto.getRegDate()));
                long isTime = TimeUtils.getTimeForMail(noTemp2);
                if (TimeUtils.compareTime(noTemp, noTemp2)) {
                    addNewChat(chattingDto, false);
                } else {
                    if (isTime == -2) {

                        if (!hasInit) {
                            ChattingDto time = new ChattingDto();
                            time.setmType(Statics.CHATTING_VIEW_TYPE_DATE);
                            time.setRegDate(Utils.getString(R.string.today));
                            dataSet.add(time);
                        }

                    } else {
                        ChattingDto time2 = new ChattingDto();
                        time2.setmType(Statics.CHATTING_VIEW_TYPE_DATE);
                        time2.setRegDate(chattingDto.getRegDate());
                        dataSet.add(time2);
                    }
                    addNewChat(chattingDto, false);
                }
            }
        }
    }

    private void initData(List<ChattingDto> list) {

        List<UserDto> userDtos = new ArrayList<>();
        if (userNos != null && userNos.size() > 0)
            for (int id : userNos) {
                TreeUserDTOTemp treeUserDTOTemp = Utils.GetUserFromDatabase(listTemp, id);
                if (treeUserDTOTemp != null) {
                    userDtos.add(new UserDto(String.valueOf(treeUserDTOTemp.getUserNo()), treeUserDTOTemp.getName(), treeUserDTOTemp.getAvatarUrl()));
                }
            }

        if (userDtos.size() > 0) {
            ChattingDto group = new GroupDto(userDtos);
            dataSet.add(group);
        }

        for (int i = 0; i < list.size(); i++) {
            ChattingDto chattingDto = list.get(i);
            if (i == 0) {
                long isTime = TimeUtils.getTimeForMail(TimeUtils.getTime(chattingDto.getRegDate()));
                if (isTime == -2) {
                    ChattingDto time = new ChattingDto();
                    time.setmType(Statics.CHATTING_VIEW_TYPE_DATE);
                    time.setRegDate(Utils.getString(R.string.today));
                    dataSet.add(time);
                    addNewChat(chattingDto, false);
                } else {
                    ChattingDto time = new ChattingDto();
                    time.setmType(Statics.CHATTING_VIEW_TYPE_DATE);
                    time.setTime(TimeUtils.getTime(list.get(0).getRegDate()));
                    dataSet.add(time);
                    addNewChat(chattingDto, false);
                }
            } else {
                long noTemp = TimeUtils.getTime((list.get(i - 1).getRegDate()));
                long noTemp2 = TimeUtils.getTime((chattingDto.getRegDate()));
                long isTime = TimeUtils.getTimeForMail(noTemp2);
                if (TimeUtils.compareTime(noTemp, noTemp2)) {
                    // 동일한 날짜 일 경우
                    addNewChat(chattingDto, false);
                } else {
                    // 날짜가 틀려졌을 경우
                    if (isTime == -2) {
                        ChattingDto time = new ChattingDto();
                        time.setmType(Statics.CHATTING_VIEW_TYPE_DATE);
                        time.setRegDate(Utils.getString(R.string.today));
                        dataSet.add(time);
                    } else {
                        ChattingDto time2 = new ChattingDto();
                        time2.setmType(Statics.CHATTING_VIEW_TYPE_DATE);
                        time2.setRegDate(chattingDto.getRegDate());
                        dataSet.add(time2);
                    }
                    addNewChat(chattingDto, false);
                }
            }
        }


        // Scroll to bottom
        if (!hasLoadMore) {
//            rvMainList.scrollToPosition(dataSet.size() - 1);
            hasLoadMore = false;
        }


    }


    public void initData() {

        UserDto currentUserDto = Utils.getCurrentUser();

        List<UserDto> userDtos = new ArrayList<>();
        if (dto != null) {
            if (currentUserDto != null)
                userDtos.add(currentUserDto);
            userDtos.add(new UserDto(String.valueOf(dto.getId()), dto.getName(), dto.getAvatarUrl()));
        } else {
            if (userNos != null && userNos.size() > 0)
                for (int id : userNos) {
                    TreeUserDTOTemp treeUserDTOTemp = Utils.GetUserFromDatabase(listTemp, id);
                    if (treeUserDTOTemp != null)
                        userDtos.add(new UserDto(String.valueOf(treeUserDTOTemp.getUserNo()), treeUserDTOTemp.getName(), treeUserDTOTemp.getAvatarUrl()));
                }
        }

        if (userDtos.size() > 0) {
            ChattingDto group = new GroupDto(userDtos);
            dataSet.add(group);
        }

//        layoutManager.scrollToPosition(dataSet.size() - 1);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /*System.out.println("aaaaaaaaaaaaaaaaaaaa onActivityResult Fragment");
        Uri selectedImage = null;
        Utils.printLogs("Activity.RESULT_OK " + Activity.RESULT_OK);
        switch (requestCode) {
            case Statics.CAMERA_CAPTURE_IMAGE_REQUEST_CODE:

                Utils.printLogs("resultCode " + resultCode);
                if (resultCode == Activity.RESULT_OK) {
                    selectedImage = data.getData();
                }

                break;
            case Statics.IMAGE_PICKER_SELECT:

                Utils.printLogs("resultCode " + resultCode);
                if (resultCode == Activity.RESULT_OK) {
                    selectedImage = data.getData();
                }
                break;
        }
        if (selectedImage != null) {
            //String avatarRealPath = Utils.getPathFromURI(selectedImage, getActivity());
            //addNewChat(avatarRealPath,Statics.CHATTING_VIEW_TYPE_SELF_IMAGE);
        }*/
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSend:      // 전송버튼
                String message = view.edt_comment.getText().toString();
                if (!TextUtils.isEmpty(message) && message.length() > 0) {
                    view.edt_comment.setText("");

                    // Add new line for new message, it's may be today
                    String date = "";
                    if (dataSet != null) {
                        if (dataSet.size() > 2) {
                            date = dataSet.get(dataSet.size() - 1).getRegDate();
                        } else if (dataSet.size() > 1) {
                            date = dataSet.get(1).getRegDate();
                        }
                        if (!TextUtils.isEmpty(date)) {
                            if (!date.equalsIgnoreCase(Utils.getString(R.string.today))) {
                                long isTime = TimeUtils.getTimeForMail(TimeUtils.getTime(date));
                                if (isTime != -2) {
                                    ChattingDto time = new ChattingDto();
                                    time.setmType(Statics.CHATTING_VIEW_TYPE_DATE);
                                    time.setRegDate(Utils.getString(R.string.today));
                                    dataSet.add(time);
                                }
                            }
                        }
                    }

                    // Add new chat before send, and resend if it sent failed
                    final ChattingDto newDto = new ChattingDto();


                    // please check solution again
                    // long tempMessageNo = dataFromServer.get(dataFromServer.size() - 1).getMessageNo() + 1;
                    newDto.setMessageNo(Long.MAX_VALUE);


                    newDto.setMessage(message);
                    newDto.setUserNo(userID);
                    newDto.setType(Statics.MESSAGE_TYPE_NORMAL);
                    newDto.setRoomNo(roomNo);
                    newDto.setWriterUser(userID);
                    newDto.setHasSent(false);

                    String currentTime = System.currentTimeMillis() + "";
                    newDto.setRegDate(TimeUtils.convertTimeDeviceToTimeServer(currentTime));
                    addNewChat(newDto, true);

                    dataFromServer.add(newDto);
                    // Save chatting dto just send
                    long lastId = 0;
                    lastId = ChatMessageDBHelper.addSimpleMessage(newDto);
                    // If send success then update
                    final long finalLastId = lastId;

                    // 실제 서버로 메시지 데이터를 보냅니다.
                    HttpRequest.getInstance().SendChatMsg(roomNo, message, new SendChatMessage() {
                        @Override
                        public void onSenChatMessageSuccess(final ChattingDto chattingDto) {
                            // update old chat message model --> messageNo from server
                            newDto.setHasSent(true);
                            newDto.setMessageNo(chattingDto.getMessageNo());
                            newDto.setUnReadCount(chattingDto.getUnReadCount());
                            // perform update when send message success
                            String time = TimeUtils.convertTimeDeviceToTimeServer(chattingDto.getRegDate());
                            newDto.setRegDate(time);
                            // dataFromServer.add(chattingDto);

                            // update current adapter
                            newDto.setRegDate(time);


                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    ChatMessageDBHelper.updateMessage(newDto, finalLastId);
                                }
                            }).start();

                            // Notify current adapter
                            // dataFromServer.add(newDto);

                            adapterList.notifyDataSetChanged();
                            rvMainList.postDelayed(new Runnable() {
                                @Override
                                public void run() {
//                                    rvMainList.scrollToPosition(dataSet.size());
                                }
                            }, 100);

                        }

                        @Override
                        public void onSenChatMessageFail(ErrorDto errorDto, String url) {
                            Toast.makeText(mActivity, "Send message failed !", Toast.LENGTH_LONG).show();
                            Utils.printLogs("Send message failed !");

                        }
                    });
                }
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        instance = null;
    }


    public void updateData(ChattingDto chattingDto) {
        if (chattingDto.getUnreadTotalCount() > 0) {
            Utils.printLogs("Unread total count = " + chattingDto.getUnreadTotalCount());
        } else {
            Utils.printLogs("Unread total count = " + 0);
        }

        chattingDto.setWriterUser(chattingDto.getWriterUserNo());
        chattingDto.setCheckFromServer(true);
        int userNo = Utils.getCurrentId();
        long startMsgNo = chattingDto.getMessageNo();
        HttpRequest.getInstance().UpdateMessageUnreadCount(roomNo, userNo, startMsgNo);
        chattingDto.setRegDate(TimeUtils.convertTimeDeviceToTimeServer(chattingDto.getRegDate()));

        UserDto user = null;
        UserDto temp = Utils.getCurrentUser();
        if (dto != null) {
            switch (chattingDto.getType()) {
                case 0:
                    if (chattingDto.getWriterUser() != dto.getId()) {
                        user = new UserDto(String.valueOf(temp.Id), temp.FullName, temp.avatar);
                        boolean isCheck = false;
                        if (dataSet != null && dataSet.size() > 0) {
                            isCheck = Utils.getChattingType(chattingDto, dataSet.get(dataSet.size() - 1));
                        }
                        if (isCheck) {
                            chattingDto.setmType(Statics.CHATTING_VIEW_TYPE_SELF_NOT_SHOW);
                        } else {
                            chattingDto.setmType(Statics.CHATTING_VIEW_TYPE_SELF);
                        }

                    } else {
                        user = new UserDto(String.valueOf(dto.getId()), dto.getName(), dto.getAvatarUrl());
                        boolean isCheck = false;
                        if (dataSet != null && dataSet.size() > 0) {
                            isCheck = Utils.getChattingType(chattingDto, dataSet.get(dataSet.size() - 1));
                        }
                        if (isCheck) {
                            chattingDto.setmType(Statics.CHATTING_VIEW_TYPE_PERSON_NOT_SHOW);
                        } else {
                            chattingDto.setmType(Statics.CHATTING_VIEW_TYPE_PERSON);
                        }
                    }
                    break;
                case 1:
                    if (chattingDto.getWriterUser() != dto.getId()) {
                        user = new UserDto(String.valueOf(temp.Id), temp.FullName, temp.avatar);
                    } else {
                        user = new UserDto(String.valueOf(dto.getId()), dto.getName(), dto.getAvatarUrl());
                    }
                    chattingDto.setmType(Statics.CHATTING_VIEW_TYPE_GROUP_NEW);
                    break;
                case 2:
                    if (chattingDto.getWriterUser() != dto.getId()) {
                        user = new UserDto(String.valueOf(temp.Id), temp.FullName, temp.avatar);
                        chattingDto.setmType(Statics.CHATTING_VIEW_TYPE_SELF_IMAGE);
                    } else {
                        user = new UserDto(String.valueOf(dto.getId()), dto.getName(), dto.getAvatarUrl());
                        boolean isCheck = false;
                        if (dataSet != null && dataSet.size() > 0) {
                            isCheck = Utils.getChattingType(chattingDto, dataSet.get(dataSet.size() - 1));
                        }
                        if (isCheck) {
                            chattingDto.setmType(Statics.CHATTING_VIEW_TYPE_PERSON_IMAGE_NOT_SHOW);
                        } else {
                            chattingDto.setmType(Statics.CHATTING_VIEW_TYPE_PERSON_IMAGE);
                        }
                    }
                    break;
                default:
                    if (chattingDto.getWriterUser() != dto.getId()) {
                        user = new UserDto(String.valueOf(temp.Id), temp.FullName, temp.avatar);
                        chattingDto.setmType(Statics.CHATTING_VIEW_TYPE_SELF);
                    } else {
                        user = new UserDto(String.valueOf(dto.getId()), dto.getName(), dto.getAvatarUrl());
                        chattingDto.setmType(Statics.CHATTING_VIEW_TYPE_PERSON);
                    }
                    break;
            }

        } else {
            switch (chattingDto.getType()) {
                case 0:
                    if (chattingDto.getWriterUser() == userID) {
                        user = new UserDto(String.valueOf(temp.Id), temp.FullName, temp.avatar);
                        boolean isCheck = false;
                        if (dataSet != null && dataSet.size() > 0) {
                            isCheck = Utils.getChattingType(chattingDto, dataSet.get(dataSet.size() - 1));
                        }
                        if (isCheck) {
                            chattingDto.setmType(Statics.CHATTING_VIEW_TYPE_SELF_NOT_SHOW);
                        } else {
                            chattingDto.setmType(Statics.CHATTING_VIEW_TYPE_SELF);
                        }
                    } else {
                        TreeUserDTOTemp treeUserDTOTemp = Utils.GetUserFromDatabase(listTemp, chattingDto.getWriterUser());
                        if (treeUserDTOTemp != null)
                            user = new UserDto(String.valueOf(treeUserDTOTemp.getUserNo()), treeUserDTOTemp.getName(), treeUserDTOTemp.getAvatarUrl());

                        boolean isCheck = false;
                        if (dataSet != null && dataSet.size() > 0) {
                            isCheck = Utils.getChattingType(chattingDto, dataSet.get(dataSet.size() - 1));
                        }
                        if (isCheck) {
                            chattingDto.setmType(Statics.CHATTING_VIEW_TYPE_PERSON_NOT_SHOW);
                        } else {
                            chattingDto.setmType(Statics.CHATTING_VIEW_TYPE_PERSON);
                        }
                    }
                    break;
                case 1:
                    if (chattingDto.getWriterUser() == userID) {
                        user = new UserDto(String.valueOf(temp.Id), temp.FullName, temp.avatar);
                    } else {
                        TreeUserDTOTemp treeUserDTOTemp = Utils.GetUserFromDatabase(listTemp, chattingDto.getWriterUser());
                        if (treeUserDTOTemp != null)
                            user = new UserDto(String.valueOf(treeUserDTOTemp.getUserNo()), treeUserDTOTemp.getName(), treeUserDTOTemp.getAvatarUrl());
                    }
                    chattingDto.setmType(Statics.CHATTING_VIEW_TYPE_GROUP_NEW);
                    break;
                case 2:
                    if (chattingDto.getWriterUser() == userID) {
                        user = new UserDto(String.valueOf(temp.Id), temp.FullName, temp.avatar);
                        if (chattingDto.getAttachInfo() != null) {
                            if (chattingDto.getAttachInfo().getType() == 1)
                                chattingDto.setmType(Statics.CHATTING_VIEW_TYPE_SELF_IMAGE);
                            else {

                                Utils.printLogs("Attach file name = " + chattingDto.getAttachFileName() + " attach file type = " + chattingDto.getAttachFileType());
                                // Check is video or normal file
                                String filename = chattingDto.getAttachInfo().getFileName();
                                if (Utils.isVideo(filename)) {
                                    chattingDto.setmType(Statics.CHATTING_VIEW_TYPE_SELF_VIDEO);
                                } else {
                                    chattingDto.setmType(Statics.CHATTING_VIEW_TYPE_SELF_FILE);
                                }
                            }
                        }
                    } else {
                        TreeUserDTOTemp treeUserDTOTemp = Utils.GetUserFromDatabase(listTemp, chattingDto.getWriterUser());
                        if (treeUserDTOTemp != null)
                            user = new UserDto(String.valueOf(treeUserDTOTemp.getUserNo()), treeUserDTOTemp.getName(), treeUserDTOTemp.getAvatarUrl());
                        if (chattingDto.getAttachInfo() != null)
                            if (chattingDto.getAttachInfo().getType() == 1) {
                                boolean isCheck = false;
                                if (dataSet != null && dataSet.size() > 0) {
                                    isCheck = Utils.getChattingType(chattingDto, dataSet.get(dataSet.size() - 1));
                                }
                                if (isCheck) {
                                    chattingDto.setmType(Statics.CHATTING_VIEW_TYPE_PERSON_IMAGE_NOT_SHOW);
                                } else {
                                    chattingDto.setmType(Statics.CHATTING_VIEW_TYPE_PERSON_IMAGE);
                                }
                            } else {
                                boolean isCheck = false;
                                if (dataSet != null && dataSet.size() > 0) {
                                    isCheck = Utils.getChattingType(chattingDto, dataSet.get(dataSet.size() - 1));
                                }
                                if (isCheck) {
                                    chattingDto.setmType(Statics.CHATTING_VIEW_TYPE_PERSON_FILE_NOT_SHOW);
                                } else {

                                    // check is video or file
                                    String filename = chattingDto.getAttachInfo().getFileName();
                                    Utils.printLogs("Attach person file = " + filename);
                                    if (Utils.isVideo(filename)) {
                                        chattingDto.setmType(Statics.CHATTING_VIEW_TYPE_PERSON_VIDEO_NOT_SHOW);
                                    } else {
                                        chattingDto.setmType(Statics.CHATTING_VIEW_TYPE_PERSON_FILE);
                                    }
                                }
                            }
                    }
                    break;
                default:
                    if (chattingDto.getWriterUser() == userID) {
                        user = new UserDto(String.valueOf(temp.Id), temp.FullName, temp.avatar);
                        chattingDto.setmType(Statics.CHATTING_VIEW_TYPE_SELF);
                    } else {
                        TreeUserDTOTemp treeUserDTOTemp = Utils.GetUserFromDatabase(listTemp, chattingDto.getWriterUser());
                        if (treeUserDTOTemp != null)
                            user = new UserDto(String.valueOf(treeUserDTOTemp.getUserNo()), treeUserDTOTemp.getName(), treeUserDTOTemp.getAvatarUrl());
                        chattingDto.setmType(Statics.CHATTING_VIEW_TYPE_PERSON);
                    }
                    break;
            }
        }

        if (!chattingDto.isCheckFromServer())
            if (view != null)
                view.edt_comment.setText("");
        if (chattingDto.getType() != 0) {
            if (chattingDto.getAttachInfo() == null) {
                return;
            }
        }
        chattingDto.setUser(user);
        chattingDto.setContent(chattingDto.getMessage());

        isFromNotification = true;
        dataFromServer.add(chattingDto);
    }

    @Override
    public void onResume() {
        super.onResume();
        isVisible = true;
        isActive = true;
        registerGCMReceiver();
        CrewChatApplication.currentRoomNo = roomNo;
        if (isUpdate) {
            isUpdate = false;
            adapterList.updateData(dataFromServer);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        isVisible = false;
        isActive = false;
        unregisterGCMReceiver();
        CrewChatApplication.currentRoomNo = 0;
    }

    private void registerGCMReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Statics.ACTION_RECEIVER_NOTIFICATION);
        filter.addAction(Constant.INTENT_FILTER_GET_MESSAGE_UNREAD_COUNT);
        filter.addAction(Constant.INTENT_FILTER_ADD_USER);
        if (mActivity != null) {
            mActivity.registerReceiver(mReceiverNewAssignTask, filter);
        }
    }

    private void showNewMessage(final ChattingDto dto) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                TreeUserDTOTemp treeUserDTOTemp = Utils.GetUserFromDatabase(listTemp, dto.getWriterUserNo());
                String userName;
                if (treeUserDTOTemp != null) {
                    userName = treeUserDTOTemp.getName();
                } else {
                    userName = "Unknown";
                }

                int textSize1 = getResources().getDimensionPixelSize(R.dimen.text_16_32);
                int textSize2 = getResources().getDimensionPixelSize(R.dimen.text_15_30);

                SpannableString span1 = new SpannableString(userName);
                span1.setSpan(new AbsoluteSizeSpan(textSize1), 0, userName.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                SpannableString span2 = new SpannableString(dto.getMessage());
                span2.setSpan(new AbsoluteSizeSpan(textSize2), 0, dto.getMessage().length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);

                // let's put both spans together with a separator and all
                CharSequence finalText = TextUtils.concat(span1, " : ", span2);
                tvUserNameMessage.setText(finalText);

                rlNewMessage.setVisibility(View.VISIBLE);
                ivScrollDown.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        layoutManager.scrollToPosition(dataSet.size() - 1);
                    }
                });
            }
        });
    }

    private void hideNewMessage() {
        if (mActivity == null) {
            mActivity = getActivity();
        }
        if (mActivity != null) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    rlNewMessage.setVisibility(View.GONE);
                }
            });
        }
    }

    private void unregisterGCMReceiver() {
        try {
            mActivity.unregisterReceiver(mReceiverNewAssignTask);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private BroadcastReceiver mReceiverNewAssignTask = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Statics.ACTION_RECEIVER_NOTIFICATION)) {

                String gcmDto = intent.getStringExtra(Statics.GCM_DATA_NOTIFICATOON);
                final ChattingDto dataDto = new Gson().fromJson(gcmDto, ChattingDto.class);

                Utils.printLogs("Check offline --> Dto nhan duoc = " + dataDto.toString());

                boolean checkNotification = true;
                for (ChattingDto chattingDto : dataSet) {

                    if (chattingDto.getMessageNo() == Long.MAX_VALUE) {
                        Utils.printLogs("Check offline message " + chattingDto.toString());
                    }

                    if (chattingDto.getMessageNo() == dataDto.getMessageNo()) {
                        checkNotification = false;
                        break;
                    }
                }
                if (checkNotification) {
                    // show popup new message
                    boolean isShow = dataDto.getRoomNo() == roomNo;
                    if (isShowNewMessage && isShow) {
                        showNewMessage(dataDto);
                    } else {
                        hideNewMessage();
                    }

                    dataDto.setWriterUser(dataDto.getWriterUserNo());
                    dataDto.setCheckFromServer(true);
                    if (roomNo == dataDto.getRoomNo()) {
                        long startMsgNo = dataDto.getMessageNo();
                        HttpRequest.getInstance().UpdateMessageUnreadCount(dataDto.getRoomNo(), userID, startMsgNo);
                        dataDto.setRegDate(TimeUtils.convertTimeDeviceToTimeServer(dataDto.getRegDate()));
                        isFromNotification = true;
                        if (!TextUtils.isEmpty(dataDto.getMessage()) || dataDto.getAttachNo() != 0) {
                            Utils.printLogs("On receive notification ##### on ChattingFragment");
                            addNewChat(dataDto, true);
                        }
                        if (CurrentChatListFragment.fragment != null) {
                            CurrentChatListFragment.fragment.updateData(dataDto, false);
                        }

                        dataFromServer.add(dataDto);

//                        // Add new line for new message, it's may be today
//                        String date = dataDto.getRegDate();
//                        String last_time = "";
//                        if (dataSet != null) {
//                            if (dataSet.size() > 2) {
//                                last_time = dataSet.get(dataSet.size() - 2).getRegDate();
//                            } else if (dataSet.size() > 1) {
//                                last_time = dataSet.get(1).getRegDate();
//                            }
//                            if (!TextUtils.isEmpty(date) && !TextUtils.isEmpty(last_time)) {
//                                if (!date.equalsIgnoreCase(Utils.getString(R.string.today))) {
//                                    long isTime = TimeUtils.getStttimeMessage(TimeUtils.getTime(date), TimeUtils.getTime(last_time));
//                                    if (isTime != -2) {
//                                        ChattingDto time = new ChattingDto();
//                                        time.setmType(Statics.CHATTING_VIEW_TYPE_DATE);
//                                        time.setRegDate(Utils.getString(R.string.today));
//
//                                        dataSet.add(time);
//
//                                        adapterList.notifyDataSetChanged();
//                                    }
//                                }
//                            }
//                        }


                    } else {
                        dataDto.setRegDate(TimeUtils.convertTimeDeviceToTimeServer(dataDto.getRegDate()));
                        if (CurrentChatListFragment.fragment != null) {
                            CurrentChatListFragment.fragment.updateData(dataDto, true);
                        }
                    }
                }
            }/** RECEIVE CODE 5 GET UNREAD COUNT */
            else if (intent.getAction().equals(Constant.INTENT_FILTER_GET_MESSAGE_UNREAD_COUNT)) {
                final long roomNo = intent.getLongExtra(Constant.KEY_INTENT_ROOM_NO, 0);

                if (roomNo != 0 && dataFromServer.size() > 0) {
                    long msgNo = dataFromServer.get(0).getMessageNo();

                    HttpRequest.getInstance().GetMessageUnreadCount(roomNo, msgNo, new OnGetMessageUnreadCountCallBack() {
                        @Override
                        public void onHTTPSuccess(String result) {
                            Type listType = new TypeToken<List<MessageUnreadCountDTO>>() {
                            }.getType();
                            List<MessageUnreadCountDTO> list = new Gson().fromJson(result, listType);
                            for (final MessageUnreadCountDTO messageUnreadCountDTO : list) {
                                for (int i = dataSet.size() - 1; i > -1; i--) {
                                    final ChattingDto chattingDto = dataSet.get(i);
                                    if (chattingDto.getMessageNo() == messageUnreadCountDTO.getMessageNo()) {
                                        if (chattingDto.getUnReadCount() != messageUnreadCountDTO.getUnreadCount()) {
                                            chattingDto.setUnReadCount(messageUnreadCountDTO.getUnreadCount());

                                            new Thread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    ChatMessageDBHelper.updateMessage(chattingDto.getMessageNo(), messageUnreadCountDTO.getUnreadCount());
                                                }
                                            }).start();

                                            // notify view each item
                                            if (isGetFromServer) {
                                                adapterList.notifyItemChanged(i);
                                            }
                                        }
                                        break;
                                    }
                                }
                            }
                        }

                        @Override
                        public void onHTTPFail(ErrorDto errorDto) {
                            Utils.printLogs(errorDto.toString());
                        }
                    });
                }
            }
            /** RECEIVE CODE 2 ADD USER */
            else if (intent.getAction().equals(Constant.INTENT_FILTER_ADD_USER)) {
                long roomNo = intent.getLongExtra(Constant.KEY_INTENT_ROOM_NO, 0);
                if (roomNo != 0 && roomNo == ChattingFragment.this.roomNo) {
                    Reload();
                }
            }
        }
    };

    //0: finish
    //1: attach
    //2: emoji
    public int checkBack() {
        int i = 0;
        if (view != null) {
            if (view.linearEmoji.getVisibility() == View.VISIBLE)
                i = 2;
            if (view.selection_lnl.getVisibility() == View.VISIBLE)
                i = 1;
        }
        return i;
    }

    public void hidden(int task) {
        if (view != null)
            if (task == 1) {
                view.selection_lnl.setVisibility(View.GONE);
            } else {
                view.linearEmoji.setVisibility(View.GONE);
            }

    }

    public void sendAttachFile(int attachNo, long roomNo, final int position) {
        HttpRequest.getInstance().SendChatAttachFile(roomNo, attachNo, new SendChatMessage() {
            @Override
            public void onSenChatMessageSuccess(ChattingDto dto) {

                progressBar.setVisibility(View.GONE);

                dto.setRegDate(TimeUtils.convertTimeDeviceToTimeServer(dto.getRegDate()));
                if (CurrentChatListFragment.fragment != null) {
                    CurrentChatListFragment.fragment.updateData(dto, false);
                }
                addNewChat(dto, position);
                dataFromServer.add(dto);
            }

            @Override
            public void onSenChatMessageFail(ErrorDto errorDto, String url) {
                progressBar.setVisibility(View.GONE);
                Utils.showMessage("Server_Error... with error code: " + errorDto.code);

            }
        });
    }

    /*public void sendAttachFile(int attachNo, long roomNo) {
        HttpRequest.getInstance().SendChatAttachFile(roomNo, attachNo, new SendChatMessage() {
            @Override
            public void onSenChatMessageSuccess(ChattingDto dto) {
                addNewChat(dto);
            }

            @Override
            public void onSenChatMessageFail(ErrorDto errorDto) {
                Utils.showMessage("Server_Error... with error code: " + errorDto.code);
            }
        });
    }*/

    int sendto = 0;

    public void SendTo(ChattingDto chattingDto, ProgressBar progressBar, int position) {
        sendto++;
        if (view != null) {
            view.selection_lnl.setVisibility(View.GONE);
        }
        SendToServer temp = new SendToServer(chattingDto, progressBar, position);
        temp.execute();
        Utils.printLogs("Send data lan thu = " + sendto);
    }

    /*public void SendTo(ChattingDto chattingDto, String path) {
        SendToServer temp = new SendToServer(path, chattingDto);
        temp.execute();
    }*/

    @Override
    public void onBackspace() {
        view.edt_comment.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
    }


    public String getEmijoByUnicode(int unicode) {
        return new String(Character.toChars(unicode));
    }

    @Override
    public void onEmojiSelected(final String res) {
        // String result =  String.valueOf(Character.toChars(Integer.decode("0x" + res)));

        Utils.printLogs("Hex code = " + res);
        int codePointCopyright = Integer.parseInt(res, 16);
        String headPhoneString2 = new String(Character.toChars(codePointCopyright));

        Utils.printLogs("Character =" + headPhoneString2);

        view.edt_comment.setText(view.edt_comment.getText().append(headPhoneString2).toString());
        view.edt_comment.setSelection(view.edt_comment.getText().length());
    }

    @Override
    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
        if (bottom < oldBottom) {
            rvMainList.postDelayed(new Runnable() {
                @Override
                public void run() {
//                    rvMainList.scrollToPosition(dataSet.size());
                }
            }, 100);
        }

        /*if(layoutManager.findLastCompletelyVisibleItemPosition() == dataSet.size() - 2) {
            layoutManager.setStackFromEnd(true);
        } else {
            layoutManager.setStackFromEnd(false);
        }*/

    }

    // 파일서버로 해당 파일을 전송합니다.
    public class SendToServer extends AsyncTask<Void, Void, Integer> {

        private ChattingDto chattingDto;
        private ProgressBar progressBar;
        private int position;

        public SendToServer(ChattingDto chattingDto, ProgressBar progressBar, int position) {
            this.chattingDto = chattingDto;
            this.progressBar = progressBar;
            this.position = position;
        }

        @Override
        protected Integer doInBackground(Void... params) {

            AttachDTO attachDTO = new AttachDTO();
            attachDTO.setFileName(Utils.getFileName(chattingDto.getAttachFilePath()));
            attachDTO.setFileType(Utils.getFileType(attachDTO.getFileName()));
            attachDTO.setFullPath(chattingDto.getAttachFilePath());

            String siteDomain = new Prefs().getServerSite();

            if (siteDomain.startsWith("http://")) {
                siteDomain = siteDomain.replace("http://", "");
            }

            if (siteDomain.contains(":")) {
                siteDomain = siteDomain.substring(0, siteDomain.indexOf(":"));
            }

            InetAddress ip = null;

            try {
                ip = InetAddress.getByName(siteDomain);
            } catch (Exception e) {
                e.printStackTrace();
            }

            NetClient nc;

            if (ip == null) {
                // ip 값이 없다면 도메인명을 통해 파일서버로 접속하여 전송 처리.
                nc = new NetClient(siteDomain, Urls.FILE_SERVER_PORT);
            } else {
                nc = new NetClient(ip.getHostAddress(), Urls.FILE_SERVER_PORT);
            }

            // 실제 파일 데이터를 전송 처리 합니다.
            nc.sendDataWithStringTest(attachDTO, progressBar);
            return nc.receiveDataFromServer();
        }

        @Override
        protected void onPostExecute(Integer integer) {

            super.onPostExecute(integer);
//            //please check solution again
//            // long tempMessageNo = dataFromServer.get(dataFromServer.size() - 1).getMessageNo() + 1;
//            chattingDto.setMessageNo(Long.MAX_VALUE);
//            chattingDto.setUserNo(userID);
//            chattingDto.setmType(Statics.CHATTING_VIEW_TYPE_SELF_IMAGE);
//            chattingDto.setRoomNo(roomNo);
//            chattingDto.setWriterUser(userID);
//            chattingDto.setHasSent(false);
//
//            String currentTime = System.currentTimeMillis() + "";
//            chattingDto.setRegDate(TimeUtils.convertTimeDeviceToTimeServer(currentTime));
//            addNewChat(chattingDto, true);
//
//            dataFromServer.add(chattingDto);
//
//            ChatMessageDBHelper.addSimpleMessage(chattingDto);

            sendAttachFile(integer, roomNo, position);
        }

    }

    /*public class SendToServer extends AsyncTask<Void, Void, Integer> {

        private String path;
        private ChattingDto dto;

        public SendToServer(String path, ChattingDto dto) {
            this.path = path;
            this.dto = dto;
        }

        @Override
        protected Integer doInBackground(Void... params) {
            AttachDTO attachDTO = new AttachDTO();
            attachDTO.setFileName(Utils.getFileName(path));
            attachDTO.setFileType(Utils.getFileType(attachDTO.getFileName()));
            attachDTO.setFullPath(path);
            NetClient nc = new NetClient(Urls.HOST, Urls.PORT);
            nc.sendDataWithString(attachDTO);
            return nc.receiveDataFromServer();
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            if (dto != null) {
                sendAttachFile(integer, dto.getRoomNo());
            }
        }
    }*/

    public void Reload() {
        str_lastID = "";
        lastID = 0;
        dataSet.clear();
        adapterList.notifyDataSetChanged();
        adapterList.setLoaded();
        progressBar.setVisibility(View.VISIBLE);
        HttpRequest.getInstance().GetChatMsgSection(roomNo, 0, 1, new OnGetChatMessage() {
            @Override
            public void OnGetChatMessageSuccess(List<ChattingDto> listNew) {
                progressBar.setVisibility(View.GONE);
                isLoaded = true;
                //saveUnread();
                dataFromServer = listNew;
                if (listNew.size() > 0) {
                    int userNo = Utils.getCurrentId();
                    long startMsgNo = listNew.get(listNew.size() - 1).getMessageNo();
                    HttpRequest.getInstance().UpdateMessageUnreadCount(roomNo, userNo, startMsgNo);
                    initData(listNew);
                } else {
                    initData();
                }
            }

            @Override
            public void OnGetChatMessageFail(ErrorDto errorDto) {
                progressBar.setVisibility(View.GONE);
                isLoaded = true;
                initData();
            }
        });
    }

    /*
        Load more data will load from Client first, then load it from server if client not found
         by baseMsgNo
         이전 메시지 더 보기 입니다.
    */
    private void loadMoreData() {
        if (!isLoading && isLoadMore && dataSet.size() > 19) {
            isLoading = true;
            hasLoadMore = true;
            long baseMsgNo = dataFromServer.get(0).getMessageNo();
            ChattingDto chattingDto2 = dataSet.get(0);
            chattingDto2.setId(999);
            adapterList.notifyItemChanged(0);

            // Load from Local database
            // if load all local data, will load
            // Get data section with baseMsgNo
            ArrayList<ChattingDto> localData = ChatMessageDBHelper.getMsgSession(roomNo, baseMsgNo, ChatMessageDBHelper.BEFORE);
            if (localData != null && localData.size() > 0) { // Local data is > 0
                // Add to current data set
                dataFromServer.addAll(0, localData);
                dataSet.clear();
                initData(dataFromServer);

                adapterList.notifyDataSetChanged();

                isScrolling = true;

                rvMainList.scrollBy(0, mTotalScrolled);
                isScrolling = false;
                isLoading = false;

            } else { // load from server
                HttpRequest.getInstance().GetChatMsgSection(roomNo, baseMsgNo, 2, new OnGetChatMessage() {
                    @Override
                    public void OnGetChatMessageSuccess(final List<ChattingDto> listNew) {
                        if (listNew.size() > 0) {

                            // foreach and save to local database on new thread
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    for (ChattingDto chat : listNew) {
                                        addMessage(chat);
                                    }
                                }
                            }).start();

                            // Add all new data to current local database
                            dataFromServer.addAll(0, listNew);
                            dataSet.clear();
                            initData(dataFromServer);

                            adapterList.notifyDataSetChanged();

                            // Scroll after change
                            isScrolling = true;
                            rvMainList.scrollBy(0, mTotalScrolled);
                            isScrolling = false;
                            isLoading = false;
                        } else {
                            ChattingDto chattingDto2 = dataSet.get(0);
                            chattingDto2.setId(0);
                            adapterList.notifyItemChanged(0);
                            isLoadMore = false;
                        }
                    }

                    @Override
                    public void OnGetChatMessageFail(ErrorDto errorDto) {

                        ChattingDto chattingDto2 = dataSet.get(0);
                        chattingDto2.setId(0);
                        adapterList.notifyItemChanged(0);
                        isLoadMore = false;
                    }
                });
            }
        }
    }

    // 이미지 파일 크게 보기 입니다.
    public void ViewImageFull(ChattingDto chattingDto) {
        ArrayList<ChattingDto> urls = new ArrayList<>();
        int position = 0;
        for (ChattingDto chattingDto1 : dataFromServer) {
            if (chattingDto1.getAttachInfo() != null && chattingDto1.getAttachInfo().getType() == 1) {
/*                String url = chattingDto1.getAttachInfo().getFullPath().replace("D:", "");
                url = url.replaceAll("\\\\", File.separator);*/
                urls.add(chattingDto1);

            }
        }
        for (ChattingDto chattingDto1 : urls) {
            if (chattingDto.getMessageNo() == chattingDto1.getMessageNo()) {
                position = urls.indexOf(chattingDto);
            }
        }

        Intent intent = new Intent(mActivity, ChatViewImageActivity.class);
        intent.putExtra(Statics.CHATTING_DTO_GALLERY_LIST, urls);
        intent.putExtra(Statics.CHATTING_DTO_GALLERY_POSITION, position);
        mActivity.startActivity(intent);
    }
}