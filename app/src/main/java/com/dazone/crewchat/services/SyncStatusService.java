package com.dazone.crewchat.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.dazone.crewchat.HTTPs.GetUserStatus;
import com.dazone.crewchat.HTTPs.HttpRequest;
import com.dazone.crewchat.constant.Statics;
import com.dazone.crewchat.database.AllUserDBHelper;
import com.dazone.crewchat.database.UserDBHelper;
import com.dazone.crewchat.dto.ErrorDto;
import com.dazone.crewchat.dto.StatusDto;
import com.dazone.crewchat.dto.StatusItemDto;
import com.dazone.crewchat.dto.TreeUserDTOTemp;
import com.dazone.crewchat.dto.UserInfoDto;
import com.dazone.crewchat.interfaces.OnGetUserInfo;
import com.dazone.crewchat.interfaces.Urls;
import com.dazone.crewchat.utils.CrewChatApplication;
import com.dazone.crewchat.utils.Prefs;
import com.dazone.crewchat.utils.Utils;

import java.util.ArrayList;

/**
 * Created by Admin on 8/31/2016.
 */
public class SyncStatusService extends Service{
    private int currentUserNo;
    private ArrayList<TreeUserDTOTemp> users;
    private boolean isStaticList = false;
    private int companyNo;

    private final IBinder mBinder = new Binder();
    public class Binder extends android.os.Binder{
        public SyncStatusService getMyService(){
            return SyncStatusService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate(){
        super.onCreate();

        Prefs prefs = new Prefs();
        currentUserNo = prefs.getUserNo();
        companyNo = prefs.getCompanyNo();

        users = CrewChatApplication.listUsers;
        if (users == null){
            users = AllUserDBHelper.getUser();
        }

        if (CrewChatApplication.listUsers != null && CrewChatApplication.listUsers.size() > 0){
            isStaticList = true;
        }
    }

    public void syncStatusString(){

        HttpRequest.getInstance().getAllUserInfo(new OnGetUserInfo() {
            @Override
            public void OnSuccess(final ArrayList<UserInfoDto> userInfo) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            for (TreeUserDTOTemp sItem : users){
                                for (UserInfoDto u : userInfo){
                                    if (sItem.getUserNo() == u.getUserNo()){
                                        AllUserDBHelper.updateStatusString(sItem.getDBId(), u.getStateMessage());
                                        sItem.setUserStatusString(u.getStateMessage());
                                    }
                                }

                            }
                        } catch (Exception e){
                            e.printStackTrace();
                        } finally {
                            Utils.printLogs("Update status string finished ###");
                        }

                    }
                }).start();

            }

            @Override
            public void OnFail(ErrorDto errorDto) {
                Utils.printLogs("On get list user failed");
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return START_STICKY;
    }

    public void syncData(){
        final StatusDto status = new GetUserStatus().getStatusOfUsers(Urls.HOST_STATUS, companyNo);
        // Need to improve it
        if (status != null) {
            for (final TreeUserDTOTemp u : users) {
                boolean isUpdate = false;
                for (final StatusItemDto sItem : status.getItems()) {
                    if (sItem.getUserID().equals(u.getUserID())) {
                        // Thread to update status
                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                                AllUserDBHelper.updateStatus(u.getDBId(), sItem.getStatus());
                                boolean xUpdate = false;
                                TreeUserDTOTemp temp = null;
                                for (TreeUserDTOTemp uu : users) {
                                    temp = uu;
                                    if (sItem.getUserID().equals(uu.getUserID())) {
                                        uu.setStatus(sItem.getStatus());
                                        xUpdate = true;
                                        break;
                                    }

                                }

                                if (!xUpdate) {
                                    if (temp != null) {
                                        temp.setStatus(Statics.USER_LOGOUT);
                                    }
                                }

                            }
                        }).start();

                        isUpdate = true;
                        break;
                    }
                }

                if (!isUpdate) {
                    if (u.getUserNo() == currentUserNo) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                UserDBHelper.updateStatus(u.getUserNo(), Statics.USER_LOGOUT);
                            }
                        }).start();
                    }
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            AllUserDBHelper.updateStatus(u.getDBId(), Statics.USER_LOGOUT);
                        }
                    }).start();

                }
            }
        }
    }

    // Destroy
    @Override
    public void onDestroy() {
        // Release the resources
        super.onDestroy();
    }
}

