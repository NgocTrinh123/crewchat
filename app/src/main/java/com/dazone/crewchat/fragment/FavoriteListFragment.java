package com.dazone.crewchat.fragment;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.dazone.crewchat.HTTPs.HttpRequest;
import com.dazone.crewchat.R;
import com.dazone.crewchat.Tree.Dtos.TreeUserDTO;
import com.dazone.crewchat.Tree.Org_tree;
import com.dazone.crewchat.Tree.Org_treeOffline;
import com.dazone.crewchat.activity.MainActivity;
import com.dazone.crewchat.activity.base.OrganizationFavoriteActivity;
import com.dazone.crewchat.adapter.FavoriteListAdapter;
import com.dazone.crewchat.constant.Statics;
import com.dazone.crewchat.customs.AlertDialogView;
import com.dazone.crewchat.database.AllUserDBHelper;
import com.dazone.crewchat.database.DepartmentDBHelper;
import com.dazone.crewchat.database.FavoriteGroupDBHelper;
import com.dazone.crewchat.database.UserDBHelper;
import com.dazone.crewchat.dto.ErrorDto;
import com.dazone.crewchat.dto.TreeUserDTOTemp;
import com.dazone.crewchat.dto.userfavorites.FavoriteGroupDto;
import com.dazone.crewchat.dto.userfavorites.FavoriteUserDto;
import com.dazone.crewchat.interfaces.*;
import com.dazone.crewchat.utils.Constant;
import com.dazone.crewchat.utils.CrewChatApplication;
import com.dazone.crewchat.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.*;

/**
 * Created by david on 12/23/15.
 * 즐겨찾기 플레그먼트
 */
public class FavoriteListFragment extends ListFragment<TreeUserDTO> {

    private TreeUserDTOTemp tempUser = null;
    public static List<TreeUserDTO> listFa;
    private Activity mContext;
    private Context context;
    private boolean isCreated = false;
    int myId = UserDBHelper.getUser().Id;
    public void setContext(Activity context){
        mContext = context;
    }
    private HashMap<Integer, ImageView> mStatusViewMap = new HashMap<>();
    public ArrayList<TreeUserDTO> tempDepartOffline;
    private View rootView;
    ArrayList<TreeUserDTOTemp> list;

    protected final android.os.Handler mHandler = new android.os.Handler() {
        public void handleMessage(Message msg) {
            if(msg.what == 1) {
                progressBar.setVisibility(View.GONE);
                adapterList.notifyDataSetChanged();
            }else if(msg.what == 2){
                progressBar.setVisibility(View.GONE);
            }else if(msg.what == 3){
                // Send handler to update UI
                adapterList.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }else if (msg.what == 4){

                progressBar.setVisibility(View.GONE);
                // Has got favorite user data, just build favorite group data

                Bundle args = msg.getData();
                if(args != null) {
                    ArrayList<FavoriteGroupDto> groups = args.getParcelableArrayList("groupList");
                    if (groups != null){
                        // Print result for check
                        for (FavoriteGroupDto group : groups){
                            Utils.printLogs("Group receive on handler server = "+group.toString());
                        }

                        if (msg.arg1 == 1){ // add new group
                            buildTree(groups, 1);
                        }else if (msg.arg1 == 0){ // start app
                            buildTree(groups, 0);
                        } else { // When get data from server
                            for (TreeUserDTO t : listFa){
                                Utils.printLogs("Temp sau khi get tu server = "+t.toString());
                            }
                            buildTree(groups, 2);
                        }
                        // Custom build tree when add a new node
                    }
                }
            } else if(msg.what == 5){ // build data offline without favorite group
                progressBar.setVisibility(View.GONE);
                buildTree();
            } else if (msg.what == 6){ // update stuatus
                // List user when get success from local database
                Bundle args = msg.getData();
                ArrayList<TreeUserDTOTemp> users = args.getParcelableArrayList("listUsers");
                updateStatus(users);
            }
        }
    };


    private OnGetStatusCallback mStatusCallback = new OnGetStatusCallback() {
        @Override
        public void onGetStatusFinish() {
            Utils.printLogs("On status callback in Favorite fragment ");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    ArrayList<TreeUserDTOTemp> listTemp = AllUserDBHelper.getUser();

                    Message message = Message.obtain();
                    message.what = 6;

                    Bundle args = new Bundle();
                    args.putParcelableArrayList("listUsers", listTemp);
                    message.setData(args);
                    mHandler.sendMessage(message);
                }
            }).start();

        }

    };


    private void updateStatus(List<TreeUserDTOTemp> users){
        if (mStatusViewMap.size() > 0){
            // Compare status and update view
            for(TreeUserDTOTemp user : users){
                for(Map.Entry<Integer, ImageView> u : mStatusViewMap.entrySet()){
                    if(user.getUserNo() == u.getKey()){
                        // set image resource for this view
                        int status = user.getStatus();
                        ImageView ivStatus = u.getValue();
                        if (status == Statics.USER_LOGIN){
                            ivStatus.setImageResource(R.drawable.home_big_status_01);
                        }else if(status == Statics.USER_AWAY){
                            ivStatus.setImageResource(R.drawable.home_big_status_02);
                        }else{
                            ivStatus.setImageResource(R.drawable.home_big_status_03);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_favorite_list_fragment_test, container, false);
        progressBar = (LinearLayout) rootView.findViewById(R.id.progressBar);
        rvMainList = (RecyclerView) rootView.findViewById(R.id.rv_main);
        /* Add header view for tree item */

        // 하단 플로팅 버튼
        FloatingActionButton btnFab = (FloatingActionButton) rootView.findViewById(R.id.fab_new_group);
        btnFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Resources res = getResources();
                String groupName = res.getString(R.string.group_name);
                String confirm = res.getString(R.string.confirm);
                String cancel = res.getString(R.string.cancel);

                Utils.printLogs("Group name ="+groupName+" confirm="+confirm+" cancel="+cancel);

                AlertDialogView.alertDialogComfirmWithEdittext(context, groupName, groupName, "", confirm, cancel, new AlertDialogView.onAlertDialogViewClickEventData() {
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
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                list = AllUserDBHelper.getUser();
                tempDepartOffline = DepartmentDBHelper.getDepartments();
            }
        }).start();

        rvMainList.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        rvMainList.setLayoutManager(layoutManager);
        initAdapter();
        rvMainList.setAdapter(adapterList);

        if (getActivity() instanceof MainActivity){
            ((MainActivity) getActivity()).setmGetStatusCallbackFavorite(mStatusCallback);
        }

        progressBar.setVisibility(View.VISIBLE);

        return rootView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser){
            if (!isCreated){
                isCreated = true;
                initData();
            } else {
                if (progressBar != null) progressBar.setVisibility(View.GONE);
            }
        }
    }

    /* Function request to server to add new favorite group */
    // 새로운 즐겨찾기 그룹을 추가합니다.
    private void addFavoriteGroup(String groupName){

        HttpRequest.getInstance().insertFavoriteGroup(groupName, new BaseHTTPCallbackWithJson() {
            @Override
            public void onHTTPSuccess(String jsonData) {

                Toast.makeText(CrewChatApplication.getInstance(), "Under construction", Toast.LENGTH_LONG).show();

                /*Type listType = new TypeToken<FavoriteGroupDto>() {}.getType();
                FavoriteGroupDto group = new Gson().fromJson(jsonData, listType);

                // Add favorite group to local database
                FavoriteGroupDBHelper.addGroup(group); // Store to data base
                // get favorite group before add a new item
                ArrayList<FavoriteGroupDto> favoriteGroupDtos = FavoriteGroupDBHelper.getFavoriteGroup();

                // include list group get from local
                Message message = Message.obtain();
                message.what = 4;
                message.arg1 = 1;
                message.obj = myId;

                Bundle args = new Bundle();
                args.putParcelableArrayList("groupList", favoriteGroupDtos);
                message.setData(args);
                mHandler.sendMessage(message);*/

            }

            @Override
            public void onHTTPFail(ErrorDto errorDto) {
                // If add a new group is failed, nothing to do
            }
        });
    }
    /* Function to request to server to update favorite group */
    private void updateFavoriteGroup(long groupNo, String groupNam, int sortNo){

        HttpRequest.getInstance().updateFavoriteGroup(groupNo, groupNam, sortNo, new BaseHTTPCallbackWithJson() {
            @Override
            public void onHTTPSuccess(String jsonData) {
                // refresh view agian
            }

            @Override
            public void onHTTPFail(ErrorDto errorDto) {

            }
        });
    }

    // Build list user

    private void initData(){
        setUpData(list);
    }

    @Override
    protected void initList() {

    }

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

    private ArrayList<TreeUserDTO> temp = new ArrayList<>();
    private ArrayList<TreeUserDTO> mPersonList = new ArrayList<>();

    // xem ky lai cho nay
    private void buildDataSet(List<TreeUserDTOTemp> listUser, List<TreeUserDTO> listDepart){

        final TreeUserDTO[] dto = {null};

        convertData(listDepart);

        for (TreeUserDTO treeUserDTO : temp) {
            if (treeUserDTO.getSubordinates() != null && treeUserDTO.getSubordinates().size() > 0) {
                treeUserDTO.setSubordinates(null);
            }
        }

        // sort data by order
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
        });

        for (TreeUserDTOTemp treeUserDTOTemp : listUser) {
            TreeUserDTO treeUserDTO = new TreeUserDTO(
                    treeUserDTOTemp.getName(),
                    treeUserDTOTemp.getNameEN(),
                    treeUserDTOTemp.getCellPhone(),
                    treeUserDTOTemp.getAvatarUrl(),
                    treeUserDTOTemp.getPosition(),
                    treeUserDTOTemp.getType(),
                    treeUserDTOTemp.getStatus(),
                    treeUserDTOTemp.getUserNo(),
                    treeUserDTOTemp.getDepartNo(),
                    treeUserDTOTemp.getUserStatusString()
            );
            treeUserDTO.setCompanyNumber(treeUserDTOTemp.getCompanyPhone());
            temp.add(treeUserDTO);
        }

        mPersonList = new ArrayList<>(temp);

        try {
            dto[0] = Org_tree.buildTree(mPersonList);
        } catch (Exception e) {
            e.printStackTrace();
        }


        if (dto[0] != null) {
            for (TreeUserDTO treeUserDTO : dto[0].getSubordinates()) {
                treeUserDTO.setParent(dto[0].getId());
                addChild(treeUserDTO);
            }
        }

    }

    public void getDataOffline(final List<TreeUserDTOTemp> treeUserDTOList){

        for (TreeUserDTOTemp treeUserDTOTemp : treeUserDTOList) {
            if (treeUserDTOTemp.getUserNo() == myId) {
                tempUser = treeUserDTOTemp;
                break;
            }
        }
        // Set update offline
        if (tempUser != null){

            listFa = new ArrayList<>();

            if (tempDepartOffline == null || tempDepartOffline.size() == 0){
                mHandler.obtainMessage(2).sendToTarget();
                HttpRequest.getInstance().GetListDepart(new IGetListDepart() {
                    @Override
                    public void onGetListDepartSuccess(final ArrayList<TreeUserDTO> treeUserDTOs) {

                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        DepartmentDBHelper.addDepartment(treeUserDTOs);
                                    }
                                }).start();


                                if (tempDepartOffline == null){
                                    tempDepartOffline = new ArrayList<>();
                                    tempDepartOffline.addAll(treeUserDTOs);
                                }


                                if (treeUserDTOs != null) {

                                   buildDataSet(treeUserDTOList, treeUserDTOs);

                                }

                                // Get all department here and build tree
                                // tempDepartOffline = DepartmentDBHelper.getDepartments();
                                convertDataOffline(treeUserDTOs);

                                /*for (TreeUserDTOTemp treeUserDTOTemp : treeUserDTOList) {
                                    if (treeUserDTOTemp.getDepartNo() == tempUser.getDepartNo()) {
                                        TreeUserDTO treeUserDTO = new TreeUserDTO(treeUserDTOTemp.getName(), treeUserDTOTemp.getNameEN(), treeUserDTOTemp.getCellPhone(), treeUserDTOTemp.getAvatarUrl(),
                                                treeUserDTOTemp.getPosition(), treeUserDTOTemp.getType(), treeUserDTOTemp.getStatus(), treeUserDTOTemp.getUserNo(), treeUserDTOTemp.getDepartNo());
                                        listFa.add(treeUserDTO);
                                    }
                                }*/


                                for (TreeUserDTO t : listFa){
                                    Utils.printLogs("Temp sau khi get online tu server = "+t.toString());
                                }


                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        // get group here and build it on new thread
                                        ArrayList<FavoriteGroupDto> groupArr = FavoriteGroupDBHelper.getFavoriteGroup();
                                        if (groupArr.size() > 0){ // try to request to server

                                            Message message = Message.obtain();
                                            message.what = 4;
                                            message.arg1 = 0;
                                            message.obj = myId;

                                            Bundle args = new Bundle();
                                            args.putParcelableArrayList("groupList", groupArr);
                                            message.setData(args);
                                            mHandler.sendMessage(message);

                                        } else{

                                            Message message = Message.obtain();
                                            message.what = 5;
                                            mHandler.sendMessage(message);
                                        }
                                    }
                                }).start();
                                // always call get group from server
                                getGroupFromServer();
                            }
                        }).start();

                    }

                    @Override
                    public void onGetListDepartFail(ErrorDto dto) {
                        mHandler.obtainMessage(2).sendToTarget();
                    }
                });
            }else{

                // rebuild tree when get data offline
                buildDataSet(treeUserDTOList, tempDepartOffline);
                // build data set here

                convertDataOffline(tempDepartOffline);



                for (TreeUserDTOTemp treeUserDTOTemp : treeUserDTOList) {
                    if (treeUserDTOTemp.getDepartNo() == tempUser.getDepartNo()) {
                        TreeUserDTO treeUserDTO = new TreeUserDTO(treeUserDTOTemp.getName(), treeUserDTOTemp.getNameEN(), treeUserDTOTemp.getCellPhone(), treeUserDTOTemp.getAvatarUrl(),
                                treeUserDTOTemp.getPosition(), treeUserDTOTemp.getType(), treeUserDTOTemp.getStatus(), treeUserDTOTemp.getUserNo(), treeUserDTOTemp.getDepartNo());
                        listFa.add(treeUserDTO);
                    }
                }

                Utils.printLogs("Add data offline ###");


                // prepare list Fa with data get from local


                // get group here and build it on new thread with favorite group

                ArrayList<FavoriteGroupDto> groupArr = FavoriteGroupDBHelper.getFavoriteGroup();
                if (groupArr.size() > 0){ // try to request to server

                    Message message = Message.obtain();
                    message.what = 4;
                    message.arg1 = 0;
                    message.obj = myId;

                    Bundle args = new Bundle();
                    args.putParcelableArrayList("groupList", groupArr);
                    message.setData(args);
                    mHandler.sendMessage(message);

                } else{
                    mHandler.obtainMessage(5).sendToTarget();
                }

                getGroupFromServer();

            }


        }else {
            mHandler.obtainMessage(2).sendToTarget();
        }
    }

    private void sendMessageUpdateToHandler(ArrayList<TreeUserDTOTemp> users){
        // When get success, send update to mHandler
        Message message = Message.obtain();
        message.what = 6;

        Bundle args = new Bundle();
        args.putParcelableArrayList("listUsers", users);
        message.setData(args);
        mHandler.sendMessage(message);
    }

    private void buildTree(){

        rvMainList.invalidate();

        TreeUserDTO tempTree = null;
        for (TreeUserDTO tempe : listFa){
            if (tempe.getId() == 1000){
                tempTree = tempe;
                break;
            }
        }

        tempFavorite.clear();
        if (tempTree == null){
            tempTree = new TreeUserDTO(1000,1, tempFavorite,"Favorites", "Favorites", 0, 1);
            listFa.add(tempTree);
        }

        TreeUserDTO treeUserDTO = null;
        try {
            treeUserDTO = Org_treeOffline.buildTree(listFa);
        } catch (Exception e) {
            e.printStackTrace();
        }

        dataSet.clear();
        dataSet.add(treeUserDTO);
        adapterList.notifyDataSetChanged();

        // the last step, send message to update user status
        sendMessageUpdateToHandler(list);
    }

    //
    private void buildTree(ArrayList<FavoriteGroupDto> groupArr,int addNewGroup ){
        rvMainList.invalidate();

        if (addNewGroup == 1 || addNewGroup == 2){
            for (Iterator<TreeUserDTO> iterator = listFa.iterator(); iterator.hasNext(); ) {
                TreeUserDTO value = iterator.next();
                if (value.getType() == 2) {
                    iterator.remove();
                }
            }
        }

        /*Utils.printLogs("Build lan thu "+time);
        time ++;

        for (TreeUserDTO t : dataSet){
            Utils.printLogs("Temp data set before build = "+t.toString());
            if (t != null){
                for (TreeUserDTO a : t.getSubordinates()){
                    Utils.printLogs("Temp data set child  ="+a.toString());
                }
            }
        }*/

        convertFavoriteGroup(groupArr);


      /*  for (TreeUserDTO t : listFa){
            Utils.printLogs("Temp sau khi convert Group = "+t.toString());
        }

        for (TreeUserDTO t : tempFavorite){
            Utils.printLogs("Temp favorite luc nay = "+t.toString());
        }*/

        TreeUserDTO tempTree = null;
        for (TreeUserDTO tempe : listFa){
            if (tempe.getId() == 1000){
                tempTree = tempe;
                break;
            }
        }

        if (tempTree == null){
            tempTree = new TreeUserDTO(1000,1, tempFavorite,"Favorites", "Favorites", 0, 1);
            listFa.add(tempTree);
        }



        /*for (TreeUserDTO t : listFa){
            Utils.printLogs("Fa item truoc khi build = "+t.toString());
        }*/

        TreeUserDTO treeUserDTO = null;
        try {
            treeUserDTO = Org_treeOffline.buildTree(listFa);
        } catch (Exception e) {
            e.printStackTrace();
        }


        // Clear dataSet before add new node
        dataSet.clear();
        dataSet.add(treeUserDTO);

        /*for (TreeUserDTO dto : dataSet){
            Utils.printLogs("Temp dataset luc nay = "+dto.toString());
            if (dto.getSubordinates() != null && dto.getSubordinates().size() > 0){
                for (TreeUserDTO dto1 : dto.getSubordinates()){
                    Utils.printLogs("Temp child dataset luc nay = "+dto1.toString());
                }
            }

        }*/


        // Send handler to update UI
        if (addNewGroup == 1){
            if (dataSet.size() > 0){
                int lastIndex = dataSet.size() - 1;
                Utils.printLogs("Temp last index = "+lastIndex);
                adapterList.notifyItemChanged(lastIndex);

            }
        }else if (addNewGroup == 2){
            Utils.printLogs("Temp get from server and update");

            if (dataSet.size() > 0){
                Utils.printLogs("Data set after get from server = "+dataSet.size());
                int lastIndex = dataSet.size() - 1;
                Utils.printLogs("Temp last index = "+lastIndex);
                 adapterList.notifyItemChanged(lastIndex);
            }

        }else{
            Utils.printLogs("Temp data when start");
            adapterList.notifyDataSetChanged();
        }


        /*for (TreeUserDTO t : dataSet){
            Utils.printLogs("Temp data set after build = "+t.toString());
            if (t != null){
                for (TreeUserDTO a : t.getSubordinates()){
                    Utils.printLogs("Temp data set child  ="+a.toString());
                }
            }
        }*/

        // the last step, send message to update user status
        sendMessageUpdateToHandler(list);
    }


    ArrayList<FavoriteUserDto> listFavoriteTop = null;
    private void getFavoriteTopGroup(){
        // Get top group and data
        HttpRequest.getInstance().getFavotiteTopGroupAndData(new BaseHTTPCallbackWithJson() {
            @Override
            public void onHTTPSuccess(String jsonData) {
                Type listType = new TypeToken<ArrayList<FavoriteUserDto>>() {}.getType();
                // Add data from local before get all from local database --> it may perform slow
                listFavoriteTop = new Gson().fromJson(jsonData, listType);
            }

            @Override
            public void onHTTPFail(ErrorDto errorDto) {

            }
        });
    }

    private void getFavoriteGroup(){
        // Get main favorite group and data
        HttpRequest.getInstance().getFavotiteGroupAndData(new BaseHTTPCallbackWithJson() {
            @Override
            public void onHTTPSuccess(String json) {

                Utils.printLogs("########### Get main data success");

                Type listType = new TypeToken<ArrayList<FavoriteGroupDto>>() {}.getType();
                // Add data from local before get all from local database --> it may perform slow
                ArrayList<FavoriteGroupDto> listFromServer = new Gson().fromJson(json, listType);
                FavoriteGroupDBHelper.addGroups(listFromServer);

                // get all favorite group
                ArrayList<FavoriteGroupDto> listFromLocal = FavoriteGroupDBHelper.getFavoriteGroup();

                Message message = Message.obtain();
                message.what = 4;
                message.arg1 = 2;
                message.obj = myId;

                Bundle args = new Bundle();
                args.putParcelableArrayList("groupList", listFromLocal);
                message.setData(args);
                mHandler.sendMessage(message);
            }

            @Override
            public void onHTTPFail(ErrorDto errorDto) {
                Utils.printLogs("Error when get group from server");
            }
        });
    }

    private void getGroupFromServer(){

        new Thread(new Runnable() {
            @Override
            public void run() {


                // Wait for build favorite tree ok
                //getFavoriteTopGroup();
                getFavoriteGroup();
            }
        }).start();

    }

    ArrayList<TreeUserDTO> tempFavorite = new ArrayList<>();
    public void convertFavoriteGroup(List<FavoriteGroupDto> favoriteGroupDtos) {
        // Clear temp favorite tree when start to build
        tempFavorite.clear();

        if (listFavoriteTop != null){
            for (FavoriteUserDto favoriteUserDto : listFavoriteTop){
                TreeUserDTO user = new TreeUserDTO("", "", "", "", "", 2 , 1, favoriteUserDto.getUserNo(), 1);
                tempDepartOffline.add(user);
            }
        }

        if (favoriteGroupDtos != null && favoriteGroupDtos.size() != 0) {
            // add favorite user again
            for (FavoriteGroupDto dto : favoriteGroupDtos) {

                Utils.printLogs("Favorite group num = "+dto.getGroupNo());
                if (dto.getUserList() != null && dto.getUserList().size() > 0) {

                    ArrayList<TreeUserDTO> userList = new ArrayList<>();

                    for (FavoriteUserDto u : dto.getUserList()){
                        TreeUserDTO user = new TreeUserDTO("", "", "", "", "", 2 , 1, u.getUserNo(), dto.getGroupNo());

                        userList.add(user);
                    }


                    TreeUserDTO user = new TreeUserDTO(dto.getName(), dto.getName(), "", "", "", 1, 1, dto.getGroupNo(), 1000);
                    user.setSubordinates(userList);

                    tempFavorite.add(user);

                } else {
                    TreeUserDTO user = new TreeUserDTO(dto.getName(), dto.getName(), "", "", "", 1, 1, dto.getGroupNo(), 1000);
                    tempFavorite.add(user);
                }
            }
        }
    }

    private void saveDataToLocal(final List<FavoriteGroupDto> groups){
        // Save data to local
        // sync data and store to local database
        Utils.printLogs("Saving group data to local ###");
        new Thread(new Runnable() {
            @Override
            public void run() {
                // just test, not run now
                FavoriteGroupDBHelper.addGroups(groups);
                // Update table ChatRoom
            }
        }).start();

    }


    private void addChild(TreeUserDTO treeUserDTO){

        if (treeUserDTO.getType() != 2) {
            DepartmentDBHelper.addDepartment(treeUserDTO);
        }

        if (treeUserDTO.getSubordinates() != null && treeUserDTO.getSubordinates().size() != 0) {
            // sort data by order
            boolean hasType2 = false;
            boolean hasType0 = false;
            for (TreeUserDTO dto : treeUserDTO.getSubordinates()){
                if (dto.getType() == 2){
                    hasType2 = true;
                }
                if (dto.getType() == 0){
                    hasType0 = true;
                }
            }

            if (hasType2 && hasType0){
                Collections.sort(treeUserDTO.getSubordinates(), new Comparator<TreeUserDTO>() {
                    @Override
                    public int compare(TreeUserDTO r1, TreeUserDTO r2) {
                        return r1.getmSortNo() - r2.getmSortNo();
                    }
                });
            }

            for (TreeUserDTO dto1 : treeUserDTO.getSubordinates()) {
                dto1.setParent(treeUserDTO.getId());
                addChild(dto1);
            }
        }

    }

    public void getDataOnline(final List<TreeUserDTOTemp> treeUserDTOList){
        for (TreeUserDTOTemp treeUserDTOTemp : treeUserDTOList) {
            if (treeUserDTOTemp.getUserNo() == myId) {
                tempUser = treeUserDTOTemp;
                break;
            }
        }

        if (tempUser != null)
            mHttpRequest.GetListDepart(new IGetListDepart() {
                @Override
                public void onGetListDepartSuccess(ArrayList<TreeUserDTO> treeUserDTOs) {

                    for (TreeUserDTO tree : treeUserDTOs){
                        Utils.printLogs("Tree user DTO XXX = "+tree.toString());
                    }


                    TreeUserDTO dto = null;
                    listFa = new ArrayList<>();
                    convertData(treeUserDTOs);

                    for (TreeUserDTOTemp treeUserDTOTemp : treeUserDTOList) {
                        if (treeUserDTOTemp.getDepartNo() == tempUser.getDepartNo()) {
                            TreeUserDTO treeUserDTO = new TreeUserDTO(treeUserDTOTemp.getName(), treeUserDTOTemp.getNameEN(), treeUserDTOTemp.getCellPhone(), treeUserDTOTemp.getAvatarUrl(),
                                    treeUserDTOTemp.getPosition(), treeUserDTOTemp.getType(), treeUserDTOTemp.getStatus(), treeUserDTOTemp.getUserNo(), treeUserDTOTemp.getDepartNo());
                            listFa.add(treeUserDTO);
                            Utils.printLogs("Tree user DTO = "+treeUserDTOTemp.toString());
                        }
                    }

                    try {
                        dto = Org_tree.buildTree(listFa);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (dto == null){
                        getDataOnline(treeUserDTOList);
                    }else{
                        dataSet.add(dto);
                        adapterList.notifyDataSetChanged();
                    }


                }

                @Override
                public void onGetListDepartFail(ErrorDto dto) {

                }
            });
    }

    public void setUpData(final List<TreeUserDTOTemp> treeUserDTOList) {
        if (treeUserDTOList == null || treeUserDTOList.size() == 0){
            HttpRequest.getInstance().GetListOrganize(new IGetListOrganization() {
                @Override
                public void onGetListSuccess(final ArrayList<TreeUserDTOTemp> treeUserDTOs) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            // AllUserDBHelper.addUser(treeUserDTOs);
                            getDataOffline(treeUserDTOs);
                        }
                    }).start();

                }

                @Override
                public void onGetListFail(ErrorDto dto) {
                    mHandler.obtainMessage(2).sendToTarget();
                }
            });
        } else{
            getDataOffline(treeUserDTOList);
        }
    }

    /* Function call API delete favorite group */
    // 그룹삭제
    private void onDeleteGroup(final long groupNo){
        HttpRequest.getInstance().deleteFavoriteGroup(groupNo, new BaseHTTPCallBack() {
            @Override
            public void onHTTPSuccess() {
                Utils.printLogs("Da xoa group thanh cong");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        FavoriteGroupDBHelper.deleteFavoriteGroup(groupNo);
                    }
                }).start();

                // Update dataset, update database and build again
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //
                    }
                }).start();
            }

            @Override
            public void onHTTPFail(ErrorDto errorDto) {

            }
        });
    }

    private OnDeleteFavoriteGroup mOnDeleteCallback = new OnDeleteFavoriteGroup() {
        @Override
        public void onDelete(final long groupNo) {
            // Confirm before call API delete
            // Show logout confirm
            AlertDialogView.normalAlertDialogWithCancel(getActivity(), Utils.getString(R.string.app_name),Utils.getString(R.string.favorite_group_delete_warning), Utils.getString(R.string.no), Utils.getString(R.string.yes) , new AlertDialogView.OnAlertDialogViewClickEvent(){
                @Override
                public void onOkClick(DialogInterface alertDialog) {
                    onDeleteGroup(groupNo);
                }

                @Override
                public void onCancelClick() {

                }
            });
        }

        @Override
        public void onEdit(final long groupNo, String oldGroupName) {
            Resources res = getResources();
            String groupName = res.getString(R.string.group_name);
            String confirm = res.getString(R.string.confirm);
            String cancel = res.getString(R.string.cancel);

            Utils.printLogs("Old groupName on Callback = "+oldGroupName);

            AlertDialogView.alertDialogComfirmWithEdittext(context, groupName, groupName, oldGroupName , confirm, cancel, new AlertDialogView.onAlertDialogViewClickEventData() {
                @Override
                public void onOkClick(String groupName) {
                    // Call API to add group
                    int sortNo = 0;
                    updateFavoriteGroup(groupNo, groupName, sortNo);
                }

                @Override
                public void onCancelClick() {
                    // Dismiss dialog
                }
            });
        }

        @Override
        public void onAdd(long groupNo, ArrayList<TreeUserDTO> list) {

            ArrayList<Integer> uNos = new ArrayList<>();
            for (TreeUserDTO u : list){
                uNos.add(u.getId());
            }

            final Intent intent = new Intent(getActivity(), OrganizationFavoriteActivity.class);
            intent.putExtra(Constant.KEY_INTENT_GROUP_NO, groupNo);
            intent.putIntegerArrayListExtra(Constant.KEY_INTENT_COUNT_MEMBER, uNos);

            startActivityForResult(intent, Statics.ADD_USER_TO_FAVORITE);
        }
    };


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK){
            if (requestCode == Statics.ADD_USER_TO_FAVORITE){
                if (data != null){
                    //String content = data.getStringExtra(Constant.KEY_INTENT_SELECT_USER_RESULT);
                    Toast.makeText(CrewChatApplication.getInstance(),"Add user to favorite group successfully", Toast.LENGTH_LONG ).show();
                }

            }
        }
    }

    @Override
    protected void initAdapter() {
        if (mStatusViewMap != null) {
            mStatusViewMap = new HashMap<>();
        }

        adapterList = new FavoriteListAdapter(mContext,dataSet, rvMainList, mStatusViewMap, mOnDeleteCallback);
        //enableLoadingMore();
    }

    @Override
    protected void reloadContentPage() {
        dataSet.add(null);
        adapterList.notifyItemInserted(dataSet.size() - 1);
//        mHttpRequest.getAllEmployeesSort(this, millis,limit,userNo,sortType);
    }

    @Override
    protected void addMoreItem() {
//        mHttpRequest.getAllEmployeesSort(this, millis,limit,userNo,sortType);
    }

//    @Override
//    public void onHTTPSuccess(List<CurrentChatDto> dtos) {
//        if(dataSet==null) {
//            return;
//        }
//        int dataSetSize= dataSet.size();
//        if(dataSetSize>0) {
//            dataSet.remove(dataSet.size() - 1);
//            adapterList.notifyItemRemoved(dataSet.size());
//        }
//        dataSet.addAll(dtos);
////        if(dataSet!=null&&dataSet.size()>0) {
////            lastID = (current_Task.get(current_Task.size() - 1)).userno;
////        }
//        adapterList.notifyItemChanged(dataSetSize, dataSet.size());
//        if(dataSetSize+limit<=dataSet.size())
//        {
//            adapterList.setLoaded();
//        }
//    }
//
//    @Override
//    public void onHTTPFail(ErrorDto errorDto) {
//
//    }

    public void convertDataOffline(List<TreeUserDTO> treeUserDTOs) {

        /*if (treeUserDTOs != null && treeUserDTOs.size() != 0) {
            for (TreeUserDTO dto : treeUserDTOs) {
                Utils.printLogs("TreeUser on convert = "+dto.toString());
                if (dto.getId() == tempUser.getDepartNo()) {
                    dto.setParent(0);
                    listFa.add(dto);
                    break;
                }
            }
        }*/

        if (treeUserDTOs != null && treeUserDTOs.size() != 0) {
            for (TreeUserDTO dto : treeUserDTOs) {
                if (dto.getSubordinates() != null && dto.getSubordinates().size() > 0) {
                    if (dto.getId() == tempUser.getDepartNo()) {
                        dto.setParent(0);
                        listFa.add(dto);
                        break;
                    }
                    convertDataOffline(dto.getSubordinates());
                } else {
                    if (dto.getId() == tempUser.getDepartNo()) {
                        dto.setParent(0);
                        listFa.add(dto);
                        break;
                    }
                }
            }
        }
    }

  /*  public void convertData(List<TreeUserDTO> treeUserDTOs) {
        if (treeUserDTOs != null && treeUserDTOs.size() != 0) {
            for (TreeUserDTO dto : treeUserDTOs) {
                if (dto.getSubordinates() != null && dto.getSubordinates().size() > 0) {
                    if (dto.getId() == temp.getDepartNo()) {
                        dto.setParent(0);
                        listFa.add(dto);
                        break;
                    }
                    convertData(dto.getSubordinates());
                } else {
                    if (dto.getId() == temp.getDepartNo()) {
                        dto.setParent(0);
                        listFa.add(dto);
                        break;
                    }
                }
            }
        }
    }
*/


   /* //change late when have api
    @Override
    protected void initSwipeRefresh() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }*/

}
