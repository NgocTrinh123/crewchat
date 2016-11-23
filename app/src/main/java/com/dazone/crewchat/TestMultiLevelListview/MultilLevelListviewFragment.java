package com.dazone.crewchat.TestMultiLevelListview;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import com.dazone.crewchat.HTTPs.HttpRequest;
import com.dazone.crewchat.R;
import com.dazone.crewchat.Tree.Dtos.TreeUserDTO;
import com.dazone.crewchat.activity.MainActivity;
import com.dazone.crewchat.activity.base.OrganizationFavoriteActivity;
import com.dazone.crewchat.constant.Statics;
import com.dazone.crewchat.customs.AlertDialogView;
import com.dazone.crewchat.database.AllUserDBHelper;
import com.dazone.crewchat.database.DepartmentDBHelper;
import com.dazone.crewchat.database.FavoriteGroupDBHelper;
import com.dazone.crewchat.database.FavoriteUserDBHelper;
import com.dazone.crewchat.dto.BelongDepartmentDTO;
import com.dazone.crewchat.dto.ErrorDto;
import com.dazone.crewchat.dto.TreeUserDTOTemp;
import com.dazone.crewchat.dto.userfavorites.FavoriteGroupDto;
import com.dazone.crewchat.dto.userfavorites.FavoriteUserDto;
import com.dazone.crewchat.interfaces.*;
import com.dazone.crewchat.utils.Constant;
import com.dazone.crewchat.utils.CrewChatApplication;
import com.dazone.crewchat.utils.Prefs;
import com.dazone.crewchat.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class MultilLevelListviewFragment extends Fragment {

    public static MultilLevelListviewFragment instance;

    private List<NLevelItem> list = new CopyOnWriteArrayList<>();
    private View rootView;
    private Context mContext;

    private RecyclerView rvMain;
    public RecyclerView.LayoutManager layoutManager;
    private TreeUserDTOTemp tempUser = null;
    private int myId;
    private boolean isCreated = false;
    private NLevelRecycleAdapter mAdapter = null;
    private EditText mInputSearch;

    // Local variable
    // Define some static var here
    private ArrayList<TreeUserDTOTemp> mListUsers = null;
    private ArrayList<TreeUserDTO> mListDeparts = null;
    private ArrayList<FavoriteGroupDto> mListFavoriteGroup = null;
    private ArrayList<FavoriteUserDto> mListFavoriteTop = null;
    private NLevelItem favoriteRoot = null;

    private final int FAVORITE_BUILD = 1;


    // Addition search favorite user
    private void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Statics.ACTION_SHOW_SEARCH_FAVORITE_INPUT);
        filter.addAction(Statics.ACTION_HIDE_SEARCH_FAVORITE_INPUT);
        getActivity().registerReceiver(mReceiverShowSearchInput, filter);
    }

    private void unregisterReceiver() {
        getActivity().unregisterReceiver(mReceiverShowSearchInput);
    }

    private BroadcastReceiver mReceiverShowSearchInput = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Statics.ACTION_SHOW_SEARCH_FAVORITE_INPUT)) {
                if (mInputSearch != null){
                   showSearchInput();
                }
            } else if (intent.getAction().equals(Statics.ACTION_HIDE_SEARCH_FAVORITE_INPUT)){
                hideSearchInput();
            }
        }
    };

    protected void showSearchInput(){
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

    protected void hideSearchInput(){
        if (mInputSearch != null){
            mInputSearch.setText("");
            mInputSearch.setVisibility(View.GONE);
            if (getActivity() != null){
                Utils.hideKeyboard(getActivity());
            }
        }
    }

    // End - Addition favorite search

    /*IntentFilter intentFilterSearch;
    BroadcastReceiver receiverSearch = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                String textSearch = intent.getStringExtra(Constant.KEY_INTENT_TEXT_SEARCH);
                if (mAdapter != null){
                    mAdapter.getFilter().filterUser(textSearch);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };*/

    public void showFAB(){
        if (getActivity() != null && getActivity() instanceof MainActivity){
            Utils.printLogs("Activity name ="+getActivity().getClass().getSimpleName());
            ((MainActivity)getActivity()).showPAB(mAddFavoriteGroup);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    public static MultilLevelListviewFragment newInstance() {
        // Required empty public constructor
        if (instance == null){
            instance = new MultilLevelListviewFragment();
        }
        return instance;
    }

    protected final android.os.Handler mHandler = new android.os.Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == FAVORITE_BUILD) {
                if (mAdapter != null) {
                    mAdapter.reloadData();
                }
            }

        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //intentFilterSearch = new IntentFilter(Constant.INTENT_FILTER_SEARCH);
    }

    @Override
    public void onStart() {
        super.onStart();
        /*if (getActivity() != null){
            getActivity().registerReceiver(receiverSearch, intentFilterSearch);
        }*/
        registerReceiver();
    }

    @Override
    public void onStop() {
        super.onStop();
        /*if (getActivity() != null){
            getActivity().unregisterReceiver(receiverSearch);
        }*/
        unregisterReceiver();
    }

    boolean isShowSreachIcon = false;
    @Override
    public void onResume() {
        super.onResume();
        Utils.printLogs("On resume ####");
        if (getActivity() != null){
            if (((MainActivity)getActivity()).getCurrentTab() == 2){

                ((MainActivity) getActivity()).showSearchIcon(new OnClickCallback() {
                    @Override
                    public void onClick() {
                        if (!isShowSreachIcon){
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

                /*((MainActivity)getActivity()).hideSearchIcon();
                ((MainActivity)getActivity()).showMenuSearch(new OnClickCallback() {
                    @Override
                    public void onClick() {
                        // to do something
                    }
                });*/
            }
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser && favoriteRoot != null) {
            Utils.printLogs("User is visible ####");
             getFavoriteGroup(favoriteRoot);
        }

        if (isVisibleToUser){
            if (getActivity() != null && getActivity() instanceof MainActivity)
            ((MainActivity)getActivity()).showPAB(mAddFavoriteGroup);
        }
    }

    public void addNewFavorite(FavoriteUserDto userDto){
        // find parent
        Iterator<NLevelItem> iter = list.iterator();
        int index = -1;
        while (iter.hasNext()) {
            NLevelItem item = iter.next();
            TreeUserDTO temp = item.getObject();
            index ++;
            if (temp.getId() == userDto.getGroupNo() && temp.getType() != Statics.TYPE_USER){ // Got parent == item

                // process to get an user from data base
                TreeUserDTOTemp tempU = AllUserDBHelper.getAUser(userDto.getUserNo());
                TreeUserDTO user = new TreeUserDTO(tempU.getName(), tempU.getNameEN(), tempU.getCellPhone() ,tempU.getAvatarUrl(), tempU.getPosition(), 2 , 1, userDto.getUserNo(), userDto.getGroupNo());
                user.setStatus(tempU.getStatus());
                user.setStatusString(tempU.getUserStatusString());
                NLevelItem childItem = new NLevelItem(user, item, item.getLevel()+1);

                list.add(index + 1, childItem);

                if (mAdapter != null) mAdapter.reloadData();
                Utils.printLogs("Add user from favorite successfully");
                break;
            }
        }
        // create new user and add to list

        // reload data
    }

    public void removeFavoriteUser(int userNo){
        // remove favorite from current dataset
        Utils.printLogs("Remove user from favorite is called");
        Iterator<NLevelItem> iter = list.iterator();
        while (iter.hasNext()) {
            NLevelItem item = iter.next();
            TreeUserDTO temp = item.getObject();
            if (temp.getId() == userNo && temp.getType() == Statics.TYPE_USER){
                iter.remove();
                if (mAdapter != null) mAdapter.reloadData();
                Utils.printLogs("Remove user from favorite successfully");
            }

        }
    }

    private OnGetStatusCallback mStatusCallback = new OnGetStatusCallback() {
        @Override
        public void onGetStatusFinish() {
            Utils.printLogs("On status callback in Favorite fragment ");
            if (!rvMain.isComputingLayout()) {

                // Need to update status
                if (mListUsers != null){
                    for(TreeUserDTOTemp user : mListUsers){
                        for(NLevelItem item : list){

                            TreeUserDTO dto = item.getObject();
                            if (dto.getType() == Statics.TYPE_USER) {

                                if (user.getUserNo() == dto.getId()){

                                    Utils.printLogs("Status on update TAB = "+user.getStatus());
                                    dto.setStatus(user.getStatus());
                                    dto.setStatusString(user.getUserStatusString());
                                }

                            }
                        }
                    }
                    // Notify data set after set status string
                    if (mAdapter != null){
                        Utils.printLogs("Status Reload data on click to TAB");
                        mAdapter.reloadData();
                    }
                }
            }
        }

    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_multil_level_listview, container, false);

        rvMain = (RecyclerView) rootView.findViewById(R.id.rv_main);

        // Addition - Search favorite
        mInputSearch = (EditText) rootView.findViewById(R.id.inputSearch);
        mInputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mAdapter != null){
                    Utils.printLogs("User name input = "+s);
                    mAdapter.getFilter().filterUser(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // End addition - favorite search

        rvMain.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        rvMain.setLayoutManager(layoutManager);
        list = new ArrayList<>();


        if (CrewChatApplication.listDeparts != null && CrewChatApplication.listDeparts.size() > 0 && CrewChatApplication.listUsers != null && CrewChatApplication.listUsers.size() > 0) {

            mListDeparts = CrewChatApplication.listDeparts;
            mListUsers = CrewChatApplication.listUsers;
            buildTree();
        } else {

            // Done change preference
            CrewChatApplication.listDeparts = DepartmentDBHelper.getDepartments();
            CrewChatApplication.listUsers = AllUserDBHelper.getUser();

            mListDeparts = CrewChatApplication.listDeparts;
            mListUsers = CrewChatApplication.listUsers;

            if (mListDeparts != null && mListUsers != null && mListDeparts.size() > 0 && mListUsers.size() > 0){
                buildTree();
            } else {
                getDepartmentFromServer();
            }

        }

        initView();

        if (getActivity() instanceof MainActivity){
            ((MainActivity) getActivity()).setmGetStatusCallbackFavorite(mStatusCallback);
            ((MainActivity)getActivity()).showPAB(mAddFavoriteGroup);
        }

        return rootView;
    }


    OnClickCallback mAddFavoriteGroup = new OnClickCallback() {
        @Override
        public void onClick() {

            Resources res = getResources();
            String groupName = res.getString(R.string.group_name);
            String confirm = res.getString(R.string.confirm);
            String cancel = res.getString(R.string.cancel);

            Utils.printLogs("Group name ="+groupName+" confirm="+confirm+" cancel="+cancel);

            AlertDialogView.alertDialogComfirmWithEdittext(mContext, groupName, groupName, "", confirm, cancel, new AlertDialogView.onAlertDialogViewClickEventData() {
                @Override
                public void onOkClick(String groupName) {
                    // Call API to add group
                    addFavoriteGroup(groupName);
                }

                @Override
                public void onCancelClick() {
                    // Dismiss dialog
                }
            });
        }
    };

    private void initView(){

        /*FloatingActionButton btnFab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        btnFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Resources res = getResources();
                String groupName = res.getString(R.string.group_name);
                String confirm = res.getString(R.string.confirm);
                String cancel = res.getString(R.string.cancel);

                Utils.printLogs("Group name ="+groupName+" confirm="+confirm+" cancel="+cancel);

                AlertDialogView.alertDialogComfirmWithEdittext(mContext, groupName, groupName, "", confirm, cancel, new AlertDialogView.onAlertDialogViewClickEventData() {
                    @Override
                    public void onOkClick(String groupName) {
                        // Call API to add group
                        addFavoriteGroup(groupName);
                    }

                    @Override
                    public void onCancelClick() {
                        // Dismiss dialog
                    }
                });
            }
        });*/
    }

    ArrayList<FavoriteGroupDto> temFavorite = new ArrayList<>();
    private void getFavoriteGroup(final NLevelItem parent){
        // Get main favorite group and data
        HttpRequest.getInstance().getFavotiteGroupAndData(new BaseHTTPCallbackWithJson() {
            @Override
            public void onHTTPSuccess(String json) {
                Type listType = new TypeToken<ArrayList<FavoriteGroupDto>>() {}.getType();
                // Add data from local before get all from local database --> it may perform slow
                final ArrayList<FavoriteGroupDto> listFromServer = new Gson().fromJson(json, listType);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        FavoriteGroupDBHelper.addGroups(listFromServer);
                    }
                }).start();

                // foreach to compare
                if (mListFavoriteGroup != null){
                    if (mListFavoriteGroup.size() == 0 && CrewChatApplication.listFavoriteGroup != null && CrewChatApplication.listFavoriteGroup.size() > 0){
                        mListFavoriteGroup = CrewChatApplication.listFavoriteGroup;
                    }
                    // Loop and add favorite to current list, sync data
                    // Thread to sync data
                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            temFavorite.clear();
                            for (FavoriteGroupDto serverDto : listFromServer){
                                boolean isExist = false;
                                for (FavoriteGroupDto clientDto : mListFavoriteGroup){
                                    if (serverDto.getGroupNo() == clientDto.getGroupNo()){
                                        isExist = true;

                                        ArrayList<FavoriteUserDto> listUsersServer = serverDto.getUserList();
                                        if (listUsersServer != null) {
                                            for (FavoriteUserDto userServer : listUsersServer){
                                                boolean isChildExist = false;
                                                ArrayList<FavoriteUserDto> listUsersClient = clientDto.getUserList();
                                                for (FavoriteUserDto userClient: listUsersClient){
                                                    if (userServer.getUserNo() == userClient.getUserNo()){
                                                       isChildExist = true;
                                                        break;
                                                    }

                                                }
                                                // Add child to current list
                                                if (!isChildExist) {
                                                    listUsersClient.add(userServer);
                                                    // Find parent
                                                    int i = 0;
                                                    for (NLevelItem item : list){
                                                        i++;
                                                        if (item.getObject().getId() == userServer.getGroupNo()){
                                                            TreeUserDTOTemp tempU = AllUserDBHelper.getAUser(userServer.getUserNo());
                                                            if (tempU != null){
                                                                TreeUserDTO user = new TreeUserDTO(tempU.getName(), tempU.getNameEN(), tempU.getCellPhone() ,tempU.getAvatarUrl(), tempU.getPosition(), 2 , 1, userServer.getUserNo(), userServer.getGroupNo());
                                                                NLevelItem newItem = new NLevelItem(user, item, item.getLevel() + 1);
                                                                list.add(i, newItem);

                                                                Message message = Message.obtain();
                                                                message.what = FAVORITE_BUILD;
                                                                mHandler.sendMessage(message);
                                                            }
                                                            break;
                                                        }
                                                    }
                                                    // Create new view

                                                }

                                            }
                                        }

                                        break;
                                    }
                                }
                                if (!isExist) {
                                    temFavorite.add(serverDto);
                                }
                            }

                            // build favorite group
                            if (temFavorite.size() > 0){
                                 reBuildFavoriteGroup();
                            }
                        }
                    }).start();

                } else {
                    CrewChatApplication.listFavoriteGroup = listFromServer;
                    mListFavoriteGroup = CrewChatApplication.listFavoriteGroup;
                    buildFavoriteGroup(parent);
                    if (mAdapter != null) {
                        mAdapter.reloadData();
                    }
                }
            }

            @Override
            public void onHTTPFail(ErrorDto errorDto) {
                Utils.printLogs("Error when get group from server");
            }
        });
    }

    private void getFavoriteGroup(){
        // Get main favorite group and data
        HttpRequest.getInstance().getFavotiteGroupAndData(new BaseHTTPCallbackWithJson() {
            @Override
            public void onHTTPSuccess(String json) {
                Type listType = new TypeToken<ArrayList<FavoriteGroupDto>>() {}.getType();
                // Add data from local before get all from local database --> it may perform slow
                final ArrayList<FavoriteGroupDto> listFromServer = new Gson().fromJson(json, listType);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        FavoriteGroupDBHelper.addGroups(listFromServer);
                    }
                }).start();

                CrewChatApplication.listFavoriteGroup = listFromServer;
                mListFavoriteGroup = listFromServer;

                buildTree();
            }

            @Override
            public void onHTTPFail(ErrorDto errorDto) {
                Utils.printLogs("Error when get group from server");
            }
        });
    }

    private void getListUserFromServer(){
        HttpRequest.getInstance().GetListOrganize(new IGetListOrganization() {
            @Override
            public void onGetListSuccess(final ArrayList<TreeUserDTOTemp> treeUserDTOs) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        AllUserDBHelper.addUser(treeUserDTOs);
                    }
                }).start();

                mListUsers = treeUserDTOs;
                CrewChatApplication.listUsers = treeUserDTOs;

                getFavoriteGroup();
            }
            @Override
            public void onGetListFail(ErrorDto dto) {

            }
        });
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

    private void getDepartmentFromServer(){
        HttpRequest.getInstance().GetListDepart(new IGetListDepart() {
            @Override
            public void onGetListDepartSuccess(final ArrayList<TreeUserDTO> treeUserDTOs) {
                // Thread to store database to local

                // Get department
                temp.clear();
                convertData(treeUserDTOs);

                /*// sort data by order
                Collections.sort(temp, new Comparator<TreeUserDTO>() {
                    @Override
                    public int compare(TreeUserDTO r1, TreeUserDTO r2) {
                        if (r1.getmSortNo() > r2.getmSortNo()) {
                            return 1;
                        } else if (r1.getmSortNo() == r2.getmSortNo()) {
                            return 0;
                        } else {
                            return -1;
                        }
                    }
                });*/

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        DepartmentDBHelper.addDepartment(treeUserDTOs);
                    }
                }).start();

                CrewChatApplication.listDeparts = temp;
                mListDeparts = CrewChatApplication.listDeparts;

                getListUserFromServer();

            }
            @Override
            public void onGetListDepartFail(ErrorDto dto) {

            }
        });
    }

    private void buildTree(){
        // Using asyn task to get local database here

        int mCurrentId = new Prefs().getUserNo();
        //int mCurrentId = UserDBHelper.getUser().Id;
        /*if (CrewChatApplication.currentId == 0){
            CrewChatApplication.currentId = UserDBHelper.getUser().Id;
            mCurrentId = CrewChatApplication.currentId;
        }*/

        for (TreeUserDTOTemp temp : mListUsers){
            if (temp.getUserNo() == mCurrentId) {
                tempUser = temp;
                Utils.printLogs("### Tim thay user temp");
                break;
            }
        }
        // perform add user to list department that's formatted above
        // The first time , level is 0, level will increase with each level
        list.clear();
        convertDataV2(null);

        // Need to get favorite group android user to
        TreeUserDTO favorite = new TreeUserDTO("Favorites", "Favorites", "", "", "", 1, 1, 1000, 0);

        favoriteRoot = new NLevelItem(favorite, null, 0);
        list.add(favoriteRoot);

        // build favorite top from local first, else get it online
        if(CrewChatApplication.listFavoriteTop != null && CrewChatApplication.listFavoriteTop.size() > 0){
            mListFavoriteTop = CrewChatApplication.listFavoriteTop;

            buidFavoriteTop(mListFavoriteTop, favoriteRoot);
            buildFavoriteGroup(favoriteRoot);

            if (mAdapter != null) mAdapter.reloadData();

        } else { // if main is null

            mListFavoriteTop = FavoriteUserDBHelper.getFavoriteTop();
            if (mListFavoriteTop != null && mListFavoriteTop.size() > 0){ // Local data is null, get from server

                buidFavoriteTop(mListFavoriteTop, favoriteRoot);
                buildFavoriteGroup(favoriteRoot);

                if (mAdapter != null) mAdapter.reloadData();

            } else { // get from local data and build it

                getTopFavorite(favoriteRoot);
            }
        }

        // build favorite group
        // Init adapter after get all user
        init();
    }

    private void getTopFavorite(final NLevelItem favoriteRoot){
        HttpRequest.getInstance().getTopFavotiteGroupAndData(new BaseHTTPCallbackWithJson() {
            @Override
            public void onHTTPSuccess(String jsonData) {
                Type listType = new TypeToken<ArrayList<FavoriteUserDto>>() {}.getType();
                // Add data from local before get all from local database --> it may perform slow
                final ArrayList<FavoriteUserDto> listTop = new Gson().fromJson(jsonData, listType);

                for (FavoriteUserDto dto : listTop){
                    dto.setIsTop(1);
                }

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        FavoriteUserDBHelper.addUsers(listTop);
                    }
                }).start();

                CrewChatApplication.listFavoriteTop = listTop;

                buidFavoriteTop(listTop, favoriteRoot);

                if (mListFavoriteGroup != null && mListFavoriteGroup.size() > 0) {
                    buildFavoriteGroup(favoriteRoot);
                    if (mAdapter != null) {
                        mAdapter.reloadData();
                    }
                } else {
                    getFavoriteGroup(favoriteRoot);
                }


            }

            @Override
            public void onHTTPFail(ErrorDto errorDto) {

                if (mListFavoriteGroup != null && mListFavoriteGroup.size() > 0) {
                    buildFavoriteGroup(favoriteRoot);
                    if (mAdapter != null) {
                        mAdapter.reloadData();
                    }
                } else {
                    getFavoriteGroup(favoriteRoot);
                }

            }
        });
    }

    private void buidFavoriteTop(ArrayList<FavoriteUserDto> listTop, NLevelItem parent){

        for (FavoriteUserDto u : listTop){

            TreeUserDTOTemp tempU = AllUserDBHelper.getAUser(u.getUserNo());
            if (tempU != null){
                String position = "";
                ArrayList<BelongDepartmentDTO> belongs = tempU.getBelongs();
                if (belongs != null){
                    for (BelongDepartmentDTO belong : belongs){
                        if (TextUtils.isEmpty(position)) {
                            position += belong.getPositionName();
                        } else {
                            position += "," +belong.getPositionName();
                        }
                    }
                }

                TreeUserDTO user = new TreeUserDTO(tempU.getName(), tempU.getNameEN(), tempU.getCellPhone() ,tempU.getAvatarUrl(), position, 2 , 1, u.getUserNo(), u.getGroupNo());
                user.setStatus(tempU.getStatus());
                user.setStatusString(tempU.getUserStatusString());
                NLevelItem childItem = new NLevelItem(user, favoriteRoot, favoriteRoot.getLevel()+1);
                list.add(childItem);
            }

        }
    }

    public void buildFavoriteGroup(NLevelItem parent){

        if (mListFavoriteGroup == null){
            mListFavoriteGroup = CrewChatApplication.listFavoriteGroup;
        }

        if (mListFavoriteGroup != null && mListFavoriteGroup.size() > 0){

            Utils.printLogs("List favorite data is getting successfully ####");

            // sort data by order

            for (FavoriteGroupDto fa : mListFavoriteGroup){
                TreeUserDTO folder = new TreeUserDTO(fa.getName(), fa.getName(), "", "", "", 1 , 1, fa.getGroupNo(), favoriteRoot.getObject().getId());
                NLevelItem folderItem = new NLevelItem(folder, parent, parent.getLevel()+1);
                list.add(folderItem);
                // for folder
                if(fa.getUserList() != null && fa.getUserList().size() > 0){

                    for (FavoriteUserDto u : fa.getUserList()){
                        // Get list user and build tree
                        TreeUserDTOTemp tempU = AllUserDBHelper.getAUser(u.getUserNo());
                        if (tempU != null) {

                            String position = "";
                            ArrayList<BelongDepartmentDTO> belongs = tempU.getBelongs();
                            if (belongs != null){
                                for (BelongDepartmentDTO belong : belongs){
                                    if (TextUtils.isEmpty(position)) {
                                        position += belong.getPositionName();
                                    } else {
                                        position += "," +belong.getPositionName();
                                    }
                                }
                            }

                            TreeUserDTO user = new TreeUserDTO(tempU.getName(), tempU.getNameEN(), tempU.getCellPhone() ,tempU.getAvatarUrl(), position, 2 , 1, u.getUserNo(), u.getGroupNo());
                            user.setStatus(tempU.getStatus());
                            user.setStatusString(tempU.getUserStatusString());
                            user.mIsFavoriteUser = true;
                            NLevelItem childItem = new NLevelItem(user, folderItem, folderItem.getLevel()+1);
                            list.add(childItem);
                        }
                    }
                }
            }

        }
    }

    public void reBuildFavoriteGroup(){
        if (temFavorite.size() > 0){
            for (FavoriteGroupDto fa : temFavorite){
                TreeUserDTO folder = new TreeUserDTO(fa.getName(), fa.getName(), "", "", "", 1 , 1, fa.getGroupNo(), favoriteRoot.getObject().getId());
                NLevelItem folderItem = new NLevelItem(folder, favoriteRoot, favoriteRoot.getLevel()+1);
                list.add(folderItem);
                // for folder
                if(fa.getUserList() != null && fa.getUserList().size() > 0){

                    for (FavoriteUserDto u : fa.getUserList()){
                        // Get list user and build tree
                        TreeUserDTOTemp tempU = AllUserDBHelper.getAUser(u.getUserNo());
                        if (tempU != null) {

                            String position = "";
                            ArrayList<BelongDepartmentDTO> belongs = tempU.getBelongs();
                            if (belongs != null){
                                for (BelongDepartmentDTO belong : belongs){
                                    if (TextUtils.isEmpty(position)) {
                                        position += belong.getPositionName();
                                    } else {
                                        position += "," +belong.getPositionName();
                                    }
                                }
                            }

                            TreeUserDTO user = new TreeUserDTO(tempU.getName(), tempU.getNameEN(), tempU.getCellPhone() ,tempU.getAvatarUrl(), position, 2 , 1, u.getUserNo(), u.getGroupNo());
                            user.setStatus(tempU.getStatus());
                            user.setStatusString(tempU.getUserStatusString());
                            NLevelItem childItem = new NLevelItem(user, folderItem, folderItem.getLevel()+1);
                            list.add(childItem);
                        }
                    }
                }
            }

            Message message = Message.obtain();
            message.what = FAVORITE_BUILD;
            mHandler.sendMessage(message);
        }
    }

    // Convert data for favorite list
    public void convertDataV2(NLevelItem parent){
        if (mListDeparts == null) return;
        // Sort by Parent id
        if (parent == null){
            TreeUserDTO root = new TreeUserDTO("Dazone", "Dazone", "", "", "", 1, 1, 0, 0);
            NLevelItem item = new NLevelItem(root, null, 0);
            convertDataV2(item);
        } else {

            for (TreeUserDTO dto : mListDeparts){
                if (dto.getParent() == parent.getObject().getId()){

                    NLevelItem item = new NLevelItem(dto, parent, parent.getLevel());
                    convertDataV2(item);

                    // Show all current user departments
                    if (tempUser != null){
                        ArrayList<BelongDepartmentDTO> belongs = tempUser.getBelongs();
                        if (belongs != null){
                            for (BelongDepartmentDTO belong : belongs){

                                if (dto.getId() == belong.getDepartNo()) {
                                    list.add(item);
                                    convertUser(item, parent.getLevel()+1, dto);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Build list tree for department
    public void convertDataV2Backup(NLevelItem parent){
        // Sort by Parent id
        if (parent == null){
            TreeUserDTO root = new TreeUserDTO("Dazone", "Dazone", "", "", "", 1, 1, 0, 0);
            NLevelItem item = new NLevelItem(root, null, -1);
            convertDataV2(item);
        } else {

            for (TreeUserDTO dto : CrewChatApplication.listDeparts){
                if (dto.getParent() == parent.getObject().getId()){

                    NLevelItem item = new NLevelItem(dto, parent, parent.getLevel() + 1);
                    list.add(item);
                    convertUser(item, parent.getLevel()+1, dto);
                    convertDataV2(item);
                }
            }
        }
    }

    private void convertUser(NLevelItem parent, int level, TreeUserDTO sub) {
        for (TreeUserDTOTemp user : mListUsers){

            ArrayList<BelongDepartmentDTO> belongs = user.getBelongs();
            if (belongs != null) {
                for (BelongDepartmentDTO belong : belongs) {
                    if (belong.getDepartNo() == sub.getId()){
                        TreeUserDTO treeUserDTO = new TreeUserDTO(
                                user.getName(),
                                user.getNameEN(),
                                user.getCellPhone(),
                                user.getAvatarUrl(),
                                belong.getPositionName(),
                                user.getType(),
                                user.getStatus(),
                                user.getUserNo(),
                                belong.getDepartNo(),
                                user.getUserStatusString()
                        );
                        treeUserDTO.setCompanyNumber(user.getCompanyPhone());
                        NLevelItem item = new NLevelItem(treeUserDTO, parent, level);
                        list.add(item);
                    }
                }
            }
        }
    }

    private void init(){
        int left20dp = Utils.getDimenInPx(R.dimen.dimen_20_40);
        mAdapter = new NLevelRecycleAdapter(getActivity(), list, left20dp, mOnshowCallback);
        rvMain.setAdapter(mAdapter);
    }

    @Override
    public void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("nLevelItem",  mCurrentItemContext);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            //probably orientation change
            mCurrentItemContext = (NLevelItem) savedInstanceState.getSerializable("nLevelItem");
        }
    }

    // 유저 추가 화면을 띄웁니다.(Show AddUser Activity)
    private OnGroupShowContextMenu mOnshowCallback = new OnGroupShowContextMenu() {
        @Override
        public void onShow(NLevelItem item, ArrayList<Integer> uNos) {

            Utils.printLogs("Current item = "+item.getObject().toString());

            mCurrentItemContext = item;

            TreeUserDTO dto = item.getObject();
            final Intent intent = new Intent(getActivity(), OrganizationFavoriteActivity.class);
            intent.putExtra(Constant.KEY_INTENT_GROUP_NO,(long) dto.getId());
            intent.putIntegerArrayListExtra(Constant.KEY_INTENT_COUNT_MEMBER, uNos);
            startActivityForResult(intent, Statics.ADD_USER_TO_FAVORITE);
        }
    };

    private NLevelItem mCurrentItemContext = null;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK){
            if (requestCode ==  Statics.ADD_USER_TO_FAVORITE){
                Bundle args = data.getExtras();

                long groupNo = args.getLong(Constant.KEY_INTENT_GROUP_NO, 0);
                ArrayList<TreeUserDTO> selectedArr = null;
                try{
                    selectedArr = (ArrayList<TreeUserDTO>) args.getSerializable(Constant.KEY_INTENT_SELECT_USER_RESULT);
                }catch (Exception e){
                    e.printStackTrace();
                }
                if (selectedArr != null && selectedArr.size() > 0) {
                    for (TreeUserDTO u : selectedArr){
                        insertFavoriteUser(groupNo, u.getId());
                    }
                } else {
                    Utils.printLogs("Nothing selected ###");
                }

            }
        }
    }

    private void insertFavoriteUser(final long groupNo, final long userNo){

        if (mCurrentItemContext != null) {

            Utils.printLogs("NLevelItem on insert user = "+mCurrentItemContext.getObject().toString());

            final TreeUserDTO dto = mCurrentItemContext.getObject();
            HttpRequest.getInstance().insertFavoriteUser(dto.getId(), userNo, new BaseHTTPCallbackWithJson() {
                @Override
                public void onHTTPSuccess(String jsonData) {

                    Utils.printLogs("Json user add successfully = "+jsonData);

                    Utils.printLogs("Current parent item = "+mCurrentItemContext.getObject().toString());

                    Type listType = new TypeToken<FavoriteUserDto>() {}.getType();
                    final FavoriteUserDto userDto = new Gson().fromJson(jsonData, listType);

                    // add to local data
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            FavoriteUserDBHelper.addFavoriteUser(userDto);
                        }
                    }).start();

                    // create new item to add, by find user in local data
                    TreeUserDTOTemp tempU = AllUserDBHelper.getAUser(userNo);
                    if (tempU != null) {

                        TreeUserDTO newUser = new TreeUserDTO(tempU.getName(), tempU.getNameEN(), tempU.getCellPhone() ,tempU.getAvatarUrl(), tempU.getPosition(), 2 , 1, tempU.getUserNo(), tempU.getDepartNo());

                        NLevelItem childItem = new NLevelItem(newUser, mCurrentItemContext, mCurrentItemContext.getLevel()+1);

                        int indexOf = list.indexOf(mCurrentItemContext);
                        if (indexOf != -1){
                            list.add(indexOf + 1,childItem);
                            mAdapter.reloadData();
                        } else {
                            Utils.printLogs("Index of = -1");
                        }

                    }

                    // Notify data set to reload data

                }

                @Override
                public void onHTTPFail(ErrorDto errorDto) {

                }
            });
        }


    }

    // Addition
     /* Function request to server to add new favorite group */
    private void addFavoriteGroup(String groupName){

        HttpRequest.getInstance().insertFavoriteGroup(groupName, new BaseHTTPCallbackWithJson() {
            @Override
            public void onHTTPSuccess(String jsonData) {

                Type listType = new TypeToken<FavoriteGroupDto>() {}.getType();
                FavoriteGroupDto group = new Gson().fromJson(jsonData, listType);
                // Add favorite group to local database
                FavoriteGroupDBHelper.addGroup(group); // Store to data base
                // Need to update current data set

                // 1. Find Parent
                // 2. Build node
                if (favoriteRoot != null){
                    TreeUserDTO newGroup = new TreeUserDTO(group.getName(), group.getName(), "", "", "", 1 , 1, group.getGroupNo(), favoriteRoot.getObject().getId());
                    NLevelItem newItem = new NLevelItem(newGroup, favoriteRoot, favoriteRoot.getLevel() + 1);
                    list.add(newItem);
                    mAdapter.reloadData();
                } else {
                    Utils.printLogs("Parent of new group not found");
                }


            }

            @Override
            public void onHTTPFail(ErrorDto errorDto) {
                // If add a new group is failed, nothing to do
            }
        });
    }

}
