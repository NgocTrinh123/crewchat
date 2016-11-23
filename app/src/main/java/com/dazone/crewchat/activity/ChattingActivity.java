package com.dazone.crewchat.activity;

import android.app.Activity;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.PopupMenu;

import com.dazone.crewchat.HTTPs.HttpRequest;
import com.dazone.crewchat.R;
import com.dazone.crewchat.Tree.Dtos.TreeUserDTO;
import com.dazone.crewchat.activity.base.BaseSingleStatusActivity;
import com.dazone.crewchat.constant.Statics;
import com.dazone.crewchat.database.AllUserDBHelper;
import com.dazone.crewchat.dto.AttachDTO;
import com.dazone.crewchat.dto.ChatRoomDTO;
import com.dazone.crewchat.dto.ChattingDto;
import com.dazone.crewchat.dto.ErrorDto;
import com.dazone.crewchat.dto.TreeUserDTOTemp;
import com.dazone.crewchat.dto.UserDto;
import com.dazone.crewchat.fragment.ChattingFragment;
import com.dazone.crewchat.fragment.CurrentChatListFragment;
import com.dazone.crewchat.interfaces.BaseHTTPCallBack;
import com.dazone.crewchat.interfaces.OnFilterMessage;
import com.dazone.crewchat.interfaces.OnGetChatRoom;
import com.dazone.crewchat.libGallery.MediaChooser;
import com.dazone.crewchat.utils.Constant;
import com.dazone.crewchat.utils.CrewChatApplication;
import com.dazone.crewchat.utils.Utils;
import com.nononsenseapps.filepicker.FilePickerActivity;
import com.onegravity.contactpicker.contact.Contact;
import com.onegravity.contactpicker.core.ContactPickerActivity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.dazone.crewchat.constant.Statics.CHATTING_VIEW_TYPE_SELECT_VIDEO;

/**
 * Created by david on 12/24/15.
 * 채팅 리스트 액티비티 입니다.
 */
public class ChattingActivity extends BaseSingleStatusActivity implements View.OnClickListener,SearchView.OnQueryTextListener ,SearchView.OnCloseListener {
    TreeUserDTO dto;
    //ChattingDto chattingDto;

    // 채팅방 내부 플래그먼트
    ChattingFragment fragment;

    private ArrayList<TreeUserDTOTemp> treeUserDTOTempArrayList = AllUserDBHelper.getUser();
    // Uri 객체
    public static Uri uri = null;

    private boolean isFromNotification = false;


    private long roomNo;

    // 채팅방 참여유저 UserNo 리스트
    private ArrayList<Integer> userNos;
    private boolean isOne = false;
    private boolean isShow = true;
    private OnFilterMessage mFilterMessage;
    private String title;
    private long myId;
    public static Uri videoPath = null;
    private ChattingDto mDto = null;

    public void setmFilterMessage(OnFilterMessage mFilterMessage) {
        this.mFilterMessage = mFilterMessage;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        /** ADD OnClick Menu */
        ivCall.setOnClickListener(this);
        ivMore.setOnClickListener(this);
        ivSearch.setOnClickListener(this);
        hideCall();
        setupSearchView();

        IntentFilter imageIntentFilter = new IntentFilter(MediaChooser.IMAGE_SELECTED_ACTION_FROM_MEDIA_CHOOSER);
        imageIntentFilter.addAction(MediaChooser.VIDEO_SELECTED_ACTION_FROM_MEDIA_CHOOSER);
        registerReceiver(imageBroadcastReceiver, imageIntentFilter);


        receiveData();

        // if network is connected, sync all chat rom message and store in local database then display it on chat view
        if (Utils.isNetworkAvailable()) {
            getChatRoomInfo();
        }

        // Set local database for current room, may be launch on new thread
        String roomTitle = "";
        if (mDto != null){
            roomTitle = mDto.getRoomTitle();
            userNos = mDto.getUserNos();
            boolean isExistMe = false;
            for (int u = 0; u < userNos.size(); u++) {
                if (userNos.get(u) == myId){
                    if (!isExistMe) {
                        isExistMe = true;
                    }else{
                        userNos.remove(u);
                    }
                }
            }
            isOne = userNos.size() == 2;
            String subTitle = "";
            if (isOne){ // Get user status
                int userId = 0;
                try {
                    userId = (userNos.get(0) != myId) ? userNos.get(0) : userNos.get(1);
                }catch (Exception e){
                    e.printStackTrace();
                }
                String userStatus = AllUserDBHelper.getAUserStatus(userId);
                if (userStatus != null && userStatus.length() > 0){
                    subTitle = userStatus;
                }

            } else { // set default title
                int roomSize = 0;
                if (mDto.getUserNos() != null){
                    roomSize = userNos.size();
                }
                subTitle = CrewChatApplication.getInstance().getResources().getString(R.string.room_info_participant_count, roomSize);
            }
            setupTitleRoom(mDto.getUserNos(), roomTitle, subTitle);
        }

        // Get room title online if room information was updated
        if (!isFinishing()) {
            addFragment();
        }
    }

    /**
     * RECEIVE DATA FROM INTENT
     */
    private void receiveData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            try {
                isFromNotification = bundle.getBoolean(Constant.KEY_INTENT_FROM_NOTIFICATION, false);
                roomNo = bundle.getLong(Constant.KEY_INTENT_ROOM_NO, 0);
                myId = bundle.getLong(Constant.KEY_INTENT_USER_NO, 0);
                if (myId == 0){
                    myId = Utils.getCurrentId();
                }

                mDto = (ChattingDto) bundle.getSerializable(Constant.KEY_INTENT_ROOM_DTO);

            } catch (Exception e) {
                e.printStackTrace();
                Utils.printLogs("ERROR " + e.toString());
            }
        }
    }

    /**
     * GET CHAT ROOM INFO
     * 채팅방 정보를 가져옵니다.
     */
    private void getChatRoomInfo() {
        HttpRequest.getInstance().GetChatRoom(roomNo, new OnGetChatRoom() {
            @Override
            public void OnGetChatRoomSuccess(ChatRoomDTO chatRoomDTO) {

                Utils.printLogs("Chat room info = "+chatRoomDTO.toString());
                userNos = chatRoomDTO.getUserNos();

                boolean isExistMe = false;
                for (int u = 0; u < userNos.size(); u++) {
                    if (userNos.get(u) == myId){
                        if (!isExistMe) {
                            isExistMe = true;
                        }else{
                            userNos.remove(u);
                        }
                    }
                }
                isOne = userNos.size() == 2;
                String subTitle = "";
                if (isOne){ // Get user status
                    int userId = 0;
                    try {
                        userId = (userNos.get(0) != myId) ? userNos.get(0) : userNos.get(1);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    String userStatus = AllUserDBHelper.getAUserStatus(userId);
                    if (userStatus != null && userStatus.length() > 0){
                        subTitle = userStatus;
                    }

                } else { // set default title
                    int roomSize = 0;
                    if (chatRoomDTO.getUserNos() != null){
                        roomSize = userNos.size();
                    }
                    subTitle = CrewChatApplication.getInstance().getResources().getString(R.string.room_info_participant_count, roomSize);
                }

                setupTitleRoom(chatRoomDTO.getUserNos(), chatRoomDTO.getRoomTitle(), subTitle);

                // May be update unread count
                if (CurrentChatListFragment.fragment != null) {
                    CurrentChatListFragment.fragment.updateRoomUnread(roomNo, chatRoomDTO.getUnReadCount());
                }
            }

            @Override
            public void OnGetChatRoomFail(ErrorDto errorDto) {
                Utils.showMessage(getString(R.string.error_server));
            }
        });
    }

    /**
     * Setup TITLE ROOM
     */
    private void setupTitleRoom(ArrayList<Integer> userNos, String roomTitle, String status) {
        title = roomTitle;
        if (title != null && TextUtils.isEmpty(title.trim())) {
            title = getGroupTitleName(userNos);
        }
        setTitle(title);
        setStatus(status);
    }


    @Override
    protected void onNewIntent(Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            roomNo = bundle.getLong(Constant.KEY_INTENT_ROOM_NO, 0);
            getChatRoomInfo();
            /** Setup FRAGMENT*/
            fragment = new ChattingFragment().newInstance(roomNo, userNos, this);

            /** ADD FRAGMENT TO ACTIVITY */
            Utils.addFragmentToActivity(getSupportFragmentManager(), fragment, R.id.content_base_single_activity, false, fragment.getClass().getSimpleName());
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            CrewChatApplication.activityResumed();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        CrewChatApplication.activityPaused();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(imageBroadcastReceiver);
    }

    private void addFragment() {
        /** Setup FRAGMENT*/
        // 채팅방 내부 Fragment 값을 설정하고 가져옵니다.
        fragment = new ChattingFragment().newInstance(roomNo, userNos, this);

        /** ADD FRAGMENT TO ACTIVITY */
        // 채팅방 내부 Fragment 로 이동합니다.
        Utils.addFragmentToActivity(getSupportFragmentManager(), fragment, R.id.content_base_single_activity, false, fragment.getClass().getSimpleName());
    }

    @Override
    protected void addFragment(Bundle bundle) {
        /*Bundle bundle1 = getIntent().getExtras();
        if (bundle1 != null) {
            dto = (TreeUserDTO) bundle1.getSerializable(Statics.TREE_USER_PC);
            chattingDto = (ChattingDto) bundle1.getSerializable(Statics.CHATTING_DTO);
        }
        if (dto != null) {
            //Utils.printLogs(new Gson().toJson(dto));
            //setUPToolBar(dto.getAllName(), "Working");
        } else {
            String name = "";

            if (chattingDto != null) {
                if (chattingDto.getListTreeUser() != null && chattingDto.getListTreeUser().size() > 0) {
                    for (TreeUserDTOTemp treeUserDTOTemp : chattingDto.getListTreeUser()) {
                        name += treeUserDTOTemp.getName() + ",";
                    }
                } else {
                    if (chattingDto.getUserNos() != null && chattingDto.getUserNos().size() > 0) {
                        for (int id : chattingDto.getUserNos()) {
                            if (UserDBHelper.getUser().Id != id) {
                                TreeUserDTOTemp treeUserDTOTemp = Utils.GetUserFromDatabase(treeUserDTOTempArrayList, id);
                                if (treeUserDTOTemp != null)
                                    name += treeUserDTOTemp.getName() + ",";
                            }
                        }
                    } else {
                        TreeUserDTOTemp treeUserDTOTemp = Utils.GetUserFromDatabase(treeUserDTOTempArrayList, chattingDto.getWriterUserNo());
                        if (treeUserDTOTemp != null)
                            name += treeUserDTOTemp.getName() + ",";
                    }
                }
            }

            if (!TextUtils.isEmpty(name)) {
                setUPToolBar(name.substring(0, name.length() - 1), "Working");
            } else {
                setUPToolBar("", "Working");
            }
        }*/

        //if (bundle == null) {
        /** Setup FRAGMENT*/
        //fragment = new ChattingFragment().newInstance(roomNo,userNos);

        /** ADD FRAGMENT TO ACTIVITY */
        //Utils.addFragmentToActivity(getSupportFragmentManager(), fragment, R.id.content_base_single_activity, false, fragment.getClass().getSimpleName());
        //}
    }

    private String getGroupTitleName(ArrayList<Integer> userNos) {
        String result = "";
        for (int i : userNos) {
            if (i != myId) {
                for (TreeUserDTOTemp treeUserDTOTemp : treeUserDTOTempArrayList) {
                    if (i == treeUserDTOTemp.getUserNo()) {
                        result += treeUserDTOTemp.getName() + ",";
                        break;
                    }
                }
            }
        }
        if (TextUtils.isEmpty(result.trim())) {
            return "Unknown";
        }
        return result.substring(0, result.length() - 1);
    }

    /**
     * ACTIVITY RESULT ADD USER SELECT
     */
    private void activityResultAddUser(Intent data) {
        try {
            Bundle bc = data.getExtras();
            if (bc != null) {
                //int[] list = bc.getIntArray(Statics.CHATTING_DTO_ADD_USER_NEW);
                //int i = bc.getInt(Statics.CHATTING_DTO_ADD_USER_NEW);
                ArrayList<Integer> userNosAdded = bc.getIntegerArrayList(Constant.KEY_INTENT_USER_NO_ARRAY);
                //ArrayList<Integer> userNos = this.userNos;
                if (userNosAdded != null) {
                    for (int i : userNosAdded) {
                        userNos.add(i);
                       /* for (TreeUserDTOTemp treeUserDTOTemp : AllUserDBHelper.getUser()) {
                            if (i == treeUserDTOTemp.getUserNo()) {
                                userNos.add(treeUserDTOTemp);
                                break;
                            }
                        }*/
                    }
                    //chattingDto.setUserNos(userNos);
                    setTitle(getGroupTitleName(userNos));
                    if (ChattingFragment.instance != null) {
                        ChattingFragment.instance.Reload();
                    }
                    if (CurrentChatListFragment.fragment != null) {
                        CurrentChatListFragment.fragment.updateWhenAddUser(roomNo, userNosAdded);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Utils.printLogs(e.toString());
        }
    }

    private void galleryAddPic(String path) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(path);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Utils.printLogs(requestCode + " " + resultCode);
        if (resultCode == Activity.RESULT_OK) {
            if (fragment != null) {
                if (fragment.view != null && fragment.view.selection_lnl.getVisibility() == View.VISIBLE) {
                    fragment.view.selection_lnl.setVisibility(View.GONE);
                }
            }
            switch (requestCode) {
                case Statics.ADD_USER_SELECT:
                    activityResultAddUser(data);
                    break;

                case Statics.IMAGE_ROTATE_CODE:
                    if (data != null) {
                        String path = data.getStringExtra(Statics.CHATTING_DTO_GALLERY_SINGLE);

                        // Add image to gallery album
                        galleryAddPic(path);
                        Utils.printLogs("Image capture stored = "+path);

                        ChattingDto chattingDto = new ChattingDto();
                        chattingDto.setmType(Statics.CHATTING_VIEW_TYPE_SELECT_IMAGE);
                        chattingDto.setAttachFilePath(path);
                        chattingDto.setRoomNo(chattingDto.getRoomNo());
                        chattingDto.setRegDate(chattingDto.getRegDate());
                        chattingDto.setLastedMsgAttachType(Statics.ATTACH_IMAGE);


                        chattingDto.setLastedMsgType(Statics.MESSAGE_TYPE_ATTACH);
                        addNewRow(chattingDto);
                    }

                    break;

                case Statics.CAMERA_CAPTURE_IMAGE_REQUEST_CODE:
                    if (uri != null) {
                        String path = Utils.getPathFromURI(uri, this);
                        // Processing to rotate Image
                        Intent intent = new Intent(this, RotateImageActivity.class);
                        intent.putExtra(Statics.CHATTING_DTO_GALLERY_SINGLE, path);
                        String currentTime = System.currentTimeMillis() + "";
                        intent.putExtra(Statics.CHATTING_DTO_REG_DATE, currentTime);
                        startActivityForResult(intent, Statics.IMAGE_ROTATE_CODE);
                    }
                    //Send(Utils.getPathFromURI(uri, this));
                    break;
                case Statics.CAMERA_VIDEO_REQUEST_CODE:
                    if (data != null) {
                        Uri videoUri = data.getData();
                        if (videoUri != null) {
                            String path = Utils.getPathFromURI(videoUri, this);

                            galleryAddPic(path);
                            Utils.printLogs("Video capture stored = "+path);

                            File file = new File(path);
                            String filename = path.substring(path.lastIndexOf("/") + 1);
                            //ChattingDto chattingDto = this.chattingDto;
                            ChattingDto chattingDto = new ChattingDto();
                            chattingDto.setmType(CHATTING_VIEW_TYPE_SELECT_VIDEO);
                            chattingDto.setAttachFilePath(path);
                            chattingDto.setAttachFileName(filename);
                            chattingDto.setAttachFileSize((int) file.length());
                            addNewRow(chattingDto);
                            //Send(Utils.getPathFromURI(videoUri, this));
                        }
                    } else {
                        if (videoPath != null){
                            Uri videoUri = videoPath;
                            String path = Utils.getPathFromURI(videoUri, this);

                            galleryAddPic(path);
                            Utils.printLogs("Video capture stored = "+path);

                            File file = new File(path);
                            String filename = path.substring(path.lastIndexOf("/") + 1);
                            //ChattingDto chattingDto = this.chattingDto;
                            ChattingDto chattingDto = new ChattingDto();
                            chattingDto.setmType(CHATTING_VIEW_TYPE_SELECT_VIDEO);
                            chattingDto.setAttachFilePath(path);
                            chattingDto.setAttachFileName(filename);
                            chattingDto.setAttachFileSize((int) file.length());
                            addNewRow(chattingDto);
                            //Send(Utils.getPathFromURI(videoUri, this));
                        }
                    }

                    break;
                case Statics.VIDEO_PICKER_SELECT:
                    Uri videoUriPick = data.getData();
                    if (videoUriPick != null) {
                        String path = Utils.getPathFromURI(videoUriPick, this);
                        File file = new File(path);
                        String filename = path.substring(path.lastIndexOf("/") + 1);
                        //ChattingDto chattingDto = this.chattingDto;
                        ChattingDto chattingDto = new ChattingDto();
                        chattingDto.setmType(Statics.CHATTING_VIEW_TYPE_SELECT_VIDEO);
                        chattingDto.setAttachFilePath(path);
                        chattingDto.setAttachFileName(filename);
                        chattingDto.setAttachFileSize((int) file.length());

                        // Add new attach info
                        AttachDTO attachInfo = new AttachDTO();
                        attachInfo.setFileName(filename);
                        chattingDto.setAttachInfo(attachInfo);

                        addNewRow(chattingDto);
                        //Send(Utils.getPathFromURI(videoUriPick, this));
                    }
                    break;
                case Statics.FILE_PICKER_SELECT:
                    Uri pathUri = null;
                    if (data.getBooleanExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false)) {
                        // For JellyBean and above
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            ClipData clip = data.getClipData();
                            if (clip != null) {
                                for (int i = 0; i < clip.getItemCount(); i++) {
                                    pathUri = clip.getItemAt(i).getUri();
                                }
                            }
                            // For Ice Cream Sandwich
                        } else {
                            ArrayList<String> paths = data.getStringArrayListExtra
                                    (FilePickerActivity.EXTRA_PATHS);

                            if (paths != null) {
                                for (String path : paths) {
                                    pathUri = Uri.parse(path);
                                }
                            }
                        }

                    } else {
                        pathUri = data.getData();
                    }

                    if (pathUri != null) {

                        String path = Utils.getPathFromURI(pathUri, this);
                        File file = new File(path);
                        String filename = path.substring(path.lastIndexOf("/") + 1);
                        //ChattingDto chattingDto = this.chattingDto;
                        ChattingDto chattingDto = new ChattingDto();
                        chattingDto.setmType(Statics.CHATTING_VIEW_TYPE_SELECT_FILE);
                        chattingDto.setAttachFilePath(path);
                        chattingDto.setAttachFileName(filename);
                        chattingDto.setLastedMsgAttachType(Statics.ATTACH_FILE);
                        chattingDto.setLastedMsgType(Statics.MESSAGE_TYPE_ATTACH);
                        chattingDto.setAttachFileSize((int) file.length());
                        addNewRow(chattingDto);
                        //Send(path);
                    }
                    break;


                case Statics.CONTACT_PICKER_SELECT:

                    if (data != null && data.hasExtra(ContactPickerActivity.RESULT_CONTACT_DATA)) {


                        //ArrayList<UserDto> listUserDto = (ArrayList<UserDto>) data.getSerializableExtra("PICK_CONTACT");
                        // Loop all contact that user has picked and display it on chat windows

                        // we got a result from the contact picker
                        List<Contact> contacts = (List<Contact>) data.getSerializableExtra(ContactPickerActivity.RESULT_CONTACT_DATA);

                        for (Contact contact : contacts) {

                            UserDto userDto = new UserDto();
                            userDto.setFullName(contact.getDisplayName());
                            userDto.setPhoneNumber(contact.getPhone(0));
                            userDto.setAvatar(contact.getPhotoUri()!= null ? contact.getPhotoUri().toString() : null);

                            Utils.printLogs("Contact info when add to chatting windows = "+userDto.toString());
                            ChattingDto chattingDto = new ChattingDto();
                            chattingDto.setmType(Statics.CHATTING_VIEW_TYPE_CONTACT);
                            chattingDto.setUser(userDto);

                            addNewRow(chattingDto);
                        }

                    }

                    break;
            }

        } else if (resultCode == Constant.INTENT_RESULT_CREATE_NEW_ROOM) {
            try {
                Bundle bc = data.getExtras();
                if (bc != null) {
                    // Update room title


                    ChattingDto chattingDto = (ChattingDto) bc.getSerializable(Constant.KEY_INTENT_CHATTING_DTO);
                    Intent intent = new Intent(this, ChattingActivity.class);
                    intent.putExtra(Statics.CHATTING_DTO, chattingDto);
                    intent.putExtra(Constant.KEY_INTENT_ROOM_NO, chattingDto.getRoomNo());
                    intent.putExtra(Constant.KEY_INTENT_ROOM_TITLE, bc.getStringArrayList(Constant.KEY_INTENT_ROOM_TITLE));
                    startActivity(intent);
                    finish();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static File getOutputMediaFile(int type) {
        //File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),Statics.IMAGE_DIRECTORY_NAME);
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory(),Constant.pathDownload);
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat(Statics.DATE_FORMAT_PICTURE,
                Locale.getDefault()).format(new Date());

        File mediaFile;
        if (type == Statics.MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + Utils.getString(R.string.pre_file_name) + timeStamp + Statics.IMAGE_JPG);
        } else if (type == Statics.MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath(), File.separator
                    + Utils.getString(R.string.pre_file_name) + timeStamp + Statics.VIDEO_MP4);

        } else {
            return null;
        }
        return mediaFile;
    }

    //Get uri from captured
    public static Uri getOutputMediaFileUri(int type) {
        uri = Uri.fromFile(getOutputMediaFile(type));
        return uri;
    }

    private final BroadcastReceiver imageBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (fragment != null) {
                if (fragment.view != null && fragment.view.selection_lnl.getVisibility() == View.VISIBLE) {
                    fragment.view.selection_lnl.setVisibility(View.GONE);
                }
            }

            List<String> listFilePath = intent.getStringArrayListExtra("list");
            if (listFilePath != null && listFilePath.size() > 0) {
                for (int i = 0; i < listFilePath.size(); i++) {
                    String path = listFilePath.get(i);
                    ChattingDto chattingDto = new ChattingDto();

                    if (intent.getAction().equals(MediaChooser.IMAGE_SELECTED_ACTION_FROM_MEDIA_CHOOSER)) {

                        chattingDto.setmType(Statics.CHATTING_VIEW_TYPE_SELECT_IMAGE);
                    } else {
                        chattingDto.setmType(Statics.CHATTING_VIEW_TYPE_SELECT_FILE);
                    }

                    chattingDto.setAttachFilePath(path);
                    chattingDto.setRoomNo(chattingDto.getRoomNo());
                    chattingDto.setRegDate(chattingDto.getRegDate());
                    addNewRow(chattingDto);
                }
            }
        }
    };

    // add new row for attach file, then perform notify data to upload file to server
    private void addNewRow(ChattingDto chattingDto) {


        ChattingFragment.instance.dataSet.add(chattingDto);
        ChattingFragment.instance.adapterList.notifyItemInserted(ChattingFragment.instance.dataSet.size());
        ChattingFragment.instance.layoutManager.scrollToPosition(ChattingFragment.instance.dataSet.size() - 1);


        //Send(path);
        //SendTest(path, ChattingFragment.instance.dataSet.size() - 1);
        //ChattingFragment.instance.(chattingDto, path);
        /*if (ChattingFragment.instance.rvMainList != null) {
            ChattingSelfImageViewHolder vh = (ChattingSelfImageViewHolder) ChattingFragment.instance.rvMainList.findViewHolderForPosition(ChattingFragment.instance.dataSet.size() - 2);
            ProgressBar progressBar = vh.progressBar;
            SendTest(path, progressBar);
        }*/
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                backToListChat();
                break;

        }
        return true;
    }

    @Override
    public void onBackPressed() {
        backToListChat();
    }

    private void backToListChat() {

        if (ChattingFragment.instance != null) {
            int i = ChattingFragment.instance.checkBack();
            if (i != 0) {
                ChattingFragment.instance.hidden(i);
            } else {
                if (MainActivity.active) {
                    finish();
                } else {
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        } else {
            finish();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.call_menu:
                showCallMenu(ivCall);
                break;
            case R.id.more_menu:
                showFilterPopup(ivMore);
                break;

            case R.id.search_menu:
                showSearchView();
                break;
        }
    }

    private void showCallMenu(View v) {
        // POPUP MENU
        /*PopupMenu popup = new PopupMenu(this, v);
        // Inflate the menu from xml
        popup.getMenuInflater().inflate(R.menu.menu_call_in_chatting, popup.getMenu());
        Menu menu = popup.getMenu();
        List<TreeUserDTOTemp> listUser = chattingDto.getListTreeUser();
        for (TreeUserDTOTemp treeUserDTOTemp : listUser) {
            String userName = treeUserDTOTemp.getName();
            String phone = !TextUtils.isEmpty(treeUserDTOTemp.getCellPhone().trim()) ?
                    treeUserDTOTemp.getCellPhone() :
                    !TextUtils.isEmpty(treeUserDTOTemp.getCompanyPhone().trim()) ?
                            treeUserDTOTemp.getCompanyPhone() :
                            "";
            System.out.println("treeUserDTOTemp");
            if (!TextUtils.isEmpty(phone)) {
                menu.add(0, Menu.FIRST + listUser.indexOf(treeUserDTOTemp), Menu.NONE, userName + " (" + phone + ")");
            }
        }*/

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(ChattingActivity.this);
        builderSingle.setTitle("Call");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                ChattingActivity.this,
                R.layout.row_chatting_call);
        Utils.addCallArray(userNos, arrayAdapter);
        /*for (int i : userNos) {
            if (i != UserDBHelper.getUser().Id) {
                TreeUserDTOTemp treeUserDTOTemp = Utils.GetUserFromDatabase(AllUserDBHelper.getUser(), i);
                if (treeUserDTOTemp != null) {
                    String userName = treeUserDTOTemp.getName();
                    String phone = !TextUtils.isEmpty(treeUserDTOTemp.getCellPhone().trim()) ?
                            treeUserDTOTemp.getCellPhone() :
                            !TextUtils.isEmpty(treeUserDTOTemp.getCompanyPhone().trim()) ?
                                    treeUserDTOTemp.getCompanyPhone() :
                                    "";
                    if (!TextUtils.isEmpty(phone)) {
                        arrayAdapter.add(userName + " (" + phone + ")");
                    }
                }
            }
        }*/

        builderSingle.setNegativeButton(
                "cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builderSingle.setAdapter(
                arrayAdapter,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String phoneNumber = GetPhoneNumber(arrayAdapter.getItem(which));
                        Utils.CallPhone(ChattingActivity.this, phoneNumber);
                    }
                });
        AlertDialog dialog = builderSingle.create();
        if (arrayAdapter.getCount() > 0) {
            dialog.show();
        }


        Button b = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        if (b != null) {
            b.setTextColor(ContextCompat.getColor(mContext, R.color.light_black));
        }
    }

    private String GetPhoneNumber(String strPhone) {
        String result = strPhone.split("\\(")[1];
        result = result.split("\\)")[0];
        return result;
    }

    private void showFilterPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        // Inflate the menu from xml
        popup.getMenuInflater().inflate(R.menu.menu_in_chatting, popup.getMenu());
        Menu menu = popup.getMenu();
        if (isOne) {
            menu.findItem(R.id.menu_left_group).setVisible(false);
        } else {
            menu.findItem(R.id.menu_left_group).setVisible(true);
        }
        // Setup menu item selection
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_list_chat:
                       /* Intent intent2 = new Intent(ChattingActivity.this, GroupListUser.class);
                        intent2.putExtra(Constant.KEY_INTENT_USER_NO_ARRAY, userNos);
                        startActivity(intent2);*/

                        Intent intent2 = new Intent(ChattingActivity.this, RoomUserInformationActivity.class);
                        intent2.putExtra("userNos", userNos);
                        intent2.putExtra("roomTitle", title);
                        startActivity(intent2);

                        return true;
                    case R.id.menu_add_chat:
                        final Intent intent = new Intent(ChattingActivity.this, OrganizationActivity.class);
                        intent.putExtra(Constant.KEY_INTENT_ROOM_NO, roomNo);
                        intent.putExtra(Constant.KEY_INTENT_COUNT_MEMBER, userNos);
                        intent.putExtra(Constant.KEY_INTENT_ROOM_TITLE, title);
                        startActivityForResult(intent, Statics.ADD_USER_SELECT);
                        return true;

                    case R.id.menu_left_group:
                        HttpRequest.getInstance().DeleteChatRoomUser(roomNo, myId, new BaseHTTPCallBack() {
                            @Override
                            public void onHTTPSuccess() {
                                Intent intent1 = new Intent(ChattingActivity.this, MainActivity.class);
                                intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent1);
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                            }

                            @Override
                            public void onHTTPFail(ErrorDto errorDto) {
                                Utils.showMessage(getString(R.string.error_server));
                            }
                        });
                        return true;

                    case R.id.menu_send_file:

                        Intent i = new Intent(ChattingActivity.Instance, FilePickerActivity.class);
                        i.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false);
                        i.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, false);
                        i.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_FILE);
                        i.putExtra(FilePickerActivity.EXTRA_START_PATH, Environment.getExternalStorageDirectory().getPath());
                        ChattingActivity.Instance.startActivityForResult(i, Statics.FILE_PICKER_SELECT);

                        return true;


                    case R.id.menu_chat_setting:

                        return true;


                    case R.id.menu_close:
                        finish();
                        return true;


                    default:
                        return false;
                }
            }
        });
        // Handle dismissal with: popup.setOnDismissListener(...);
        // Show the menu
        popup.show();
    }

    /*
    * Show search view to search content in a chat
    * */
    private void showSearchView(){
        if (isShow){
            mSearchView.setIconified(true);
            isShow = true;
        }else{
            mSearchView.setIconified(false);
            isShow = false;
        }

    }


    private void setupSearchView() {
        if (isAlwaysExpanded()) {
            mSearchView.setIconifiedByDefault(false);
        }
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        if (searchManager != null) {
            List<SearchableInfo> searchables = searchManager.getSearchablesInGlobalSearch();
            SearchableInfo info = searchManager.getSearchableInfo(getComponentName());
            for (SearchableInfo inf : searchables) {
                if (inf.getSuggestAuthority() != null
                        && inf.getSuggestAuthority().startsWith("applications")) {
                    info = inf;
                }
            }
            mSearchView.setSearchableInfo(info);
        }
        mSearchView.setOnQueryTextListener(this);
    }

    protected boolean isAlwaysExpanded() {
        return false;
    }

    @Override
    public boolean onClose() {
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        ChattingFragment.instance.adapterList.filter(query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        ChattingFragment.instance.adapterList.filter(newText);
        return false;
    }
}
