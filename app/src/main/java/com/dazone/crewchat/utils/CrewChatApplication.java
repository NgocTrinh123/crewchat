package com.dazone.crewchat.utils;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.text.TextUtils;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.dazone.crewchat.BuildConfig;
import com.dazone.crewchat.HTTPs.GetUserStatus;
import com.dazone.crewchat.HTTPs.HttpRequest;
import com.dazone.crewchat.Tree.Dtos.TreeUserDTO;
import com.dazone.crewchat.constant.Statics;
import com.dazone.crewchat.database.*;
import com.dazone.crewchat.dto.*;
import com.dazone.crewchat.dto.userfavorites.FavoriteGroupDto;
import com.dazone.crewchat.dto.userfavorites.FavoriteUserDto;
import com.dazone.crewchat.interfaces.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.io.File;
import java.lang.reflect.Type;
import java.util.*;

public class CrewChatApplication extends MultiDexApplication {
    private static final String TAG = "EmcorApplication";

    private static CrewChatApplication _instance;
    private RequestQueue mRequestQueue;

    private static Prefs mPrefs;

    private HashMap<Object, Object> _data = new HashMap<Object, Object>();

    public ImageLoader imageLoader = ImageLoader.getInstance();

    // Define some static var here
    public static ArrayList<TreeUserDTOTemp> listUsers = null;
    public static ArrayList<TreeUserDTO> listDeparts = null;
    public static int currentId = 0;
    public static ArrayList<FavoriteGroupDto> listFavoriteGroup = null;
    public static ArrayList<FavoriteUserDto> listFavoriteTop = null;
    public static UserDto currentUser = null;
    private int companyNo;

    public static boolean isLoggedIn = false;

    @Override
    public void onCreate() {
        super.onCreate();
        //Fabric.with(this, new Crashlytics());
        _instance = this;
        init();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .threadPoolSize(5)
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new WeakMemoryCache())
                .build();
        imageLoader.init(config);

        companyNo = mPrefs.getCompanyNo();
        // Sync data from server
        // Check session here
        if (Utils.checkStringValue(mPrefs.getaccesstoken()) && !mPrefs.getBooleanValue(Statics.PREFS_KEY_SESSION_ERROR, false)) {
            isLoggedIn = true;

            syncData();
            // Thread to Load data from local
            loadStaticLocalData();
            // Thread to Update status from server
            if (listUsers != null){
                updateAllAppInfo();
            }
        } else {
            Utils.printLogs("User is not logged in");
            isLoggedIn = false;
        }

        Utils.printLogs("### isLoggedIn = "+isLoggedIn);
    }

    public void syncData(){
        CrewChatApplication.isLoggedIn = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                getDepartmentFromServer();
                getListUserFromServer();
                getFavoriteGroupFromServer();
            }
        }).start();
    }


    private void getStatusString(){

    }

    private void getTopFavorite(){
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
            }

            @Override
            public void onHTTPFail(ErrorDto errorDto) {

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

            }
            @Override
            public void onGetListFail(ErrorDto dto) {

            }
        });
    }
    private void getDepartmentFromServer(){
        HttpRequest.getInstance().GetListDepart(new IGetListDepart() {
            @Override
            public void onGetListDepartSuccess(final ArrayList<TreeUserDTO> treeUserDTOs) {
                // Get department
                temp.clear();
                convertData(treeUserDTOs);

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

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        DepartmentDBHelper.addDepartment(temp);
                    }
                }).start();

                CrewChatApplication.listDeparts = temp;
            }
            @Override
            public void onGetListDepartFail(ErrorDto dto) {

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

    private void getFavoriteGroupFromServer(){
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

            }

            @Override
            public void onHTTPFail(ErrorDto errorDto) {
                Utils.printLogs("Error when get group from server");
            }
        });
    }

    public void loadStaticLocalData(){

        new Thread(new Runnable() {
            @Override
            public void run() {
                Utils.printLogs("Start new thread get local data ###");
                listUsers = AllUserDBHelper.getUser();
                listDeparts = DepartmentDBHelper.getDepartments();
                currentId = Utils.getCurrentId();
                listFavoriteGroup = FavoriteGroupDBHelper.getFavoriteGroup();
                listFavoriteTop = FavoriteUserDBHelper.getFavoriteTop();
                currentUser = UserDBHelper.getUser();
            }
        }).start();
    }

    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }


    public static synchronized CrewChatApplication getInstance() {
        return _instance;
    }

    public void updateAllAppInfo(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Update user status
                updateStatus(listUsers, currentId);
                // Update user status string
                updateStatusString(listUsers);
            }
        }).start();

    }

    /* Get all status string of user */
    private void updateStatusString(final List<TreeUserDTOTemp> users){
            HttpRequest.getInstance().getAllUserInfo(new OnGetUserInfo() {
                @Override
                public void OnSuccess(final ArrayList<UserInfoDto> userInfo) {

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            for (TreeUserDTOTemp sItem : users){
                                for (UserInfoDto u : userInfo){
                                    if (sItem.getUserNo() == u.getUserNo()){
                                        AllUserDBHelper.updateStatusString(sItem.getDBId(), u.getStateMessage());
                                        Utils.printLogs(this.getClass().getSimpleName()+" updating user status string");
                                    }
                                }
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

    /*Get all status of user */
    private void updateStatus(final List<TreeUserDTOTemp> users,final int currentID ){

        new Thread(new Runnable() {
            @Override
            public void run() {

                final StatusDto status = new GetUserStatus().getStatusOfUsers(Urls.HOST_STATUS, companyNo);
                // Need to improve it
                if (status != null){
                    for (final TreeUserDTOTemp u : users){
                        boolean isUpdate = false;
                        for (final StatusItemDto sItem : status.getItems()){
                            if (sItem.getUserID().equals(u.getUserID())){
                                // Thread to update status
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        AllUserDBHelper.updateStatus(u.getDBId(), sItem.getStatus());
                                    }
                                }).start();

                                isUpdate = true;
                                break;
                            }
                        }

                        if (!isUpdate){
                            if (u.getUserNo() == currentID){
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        UserDBHelper.updateStatus(u.getUserNo(),Statics.USER_LOGOUT);
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
        }).start();
    }


    private static void init() {
        mPrefs = new Prefs();

        // Check version code, if is update
        int old_version = mPrefs.getIntValue(Statics.VERSION_CODE, 0);
        int versionCode = BuildConfig.VERSION_CODE;
        Utils.printLogs("Old version = "+old_version);

        if(old_version < versionCode){
            // upgrade
            mPrefs.putIntValue(Statics.VERSION_CODE, versionCode);
        }

        // init default value to enable/ disable notification settings
        if (!mPrefs.isContainKey(Statics.ENABLE_NOTIFICATION)){
            mPrefs.putBooleanValue(Statics.ENABLE_NOTIFICATION, true);

            Map<String, Object> params = new HashMap<>();
            params.put("enabled",true);
            params.put("sound",true);
            params.put("vibrate",true);
            params.put("notitime",true);
            params.put("starttime",Statics.DEFAULT_START_NOTIFICATION_TIME + ":00");
            params.put("endtime", Statics.DEFAULT_END_NOTIFICATION_TIME + ":00");
            params.put("confirmonline",true);


            HttpRequest.getInstance().setNotification(Urls.URL_INSERT_DEVICE,
                    mPrefs.getGCMregistrationid(),
                    params,
                    new OnSetNotification() {
                        @Override
                        public void OnSuccess() {
                            Utils.printLogs("On insert device in Crewchat Application success");
                        }

                        @Override
                        public void OnFail(ErrorDto errorDto) {
                            Utils.printLogs("On insert device in Crewchat Application failed");
                        }
                    }
            );

        }
        if (!mPrefs.isContainKey(Statics.ENABLE_SOUND)) {
            mPrefs.putBooleanValue(Statics.ENABLE_SOUND, true);
        }
        if (!mPrefs.isContainKey(Statics.ENABLE_VIBRATE)){
            mPrefs.putBooleanValue(Statics.ENABLE_VIBRATE, true);
        }
        if (!mPrefs.isContainKey(Statics.ENABLE_TIME)){
            mPrefs.putBooleanValue(Statics.ENABLE_TIME, false);
        }

        if (!mPrefs.isContainKey(Statics.ENABLE_NOTIFICATION_WHEN_USING_PC_VERSION)){
            mPrefs.putBooleanValue(Statics.ENABLE_NOTIFICATION_WHEN_USING_PC_VERSION, true);
        }

        // Default value for notification time
        if (!mPrefs.isContainKey(Statics.START_NOTIFICATION_HOUR)) {
            mPrefs.putIntValue(Statics.START_NOTIFICATION_HOUR, Statics.DEFAULT_START_NOTIFICATION_TIME);
        }
        if (!mPrefs.isContainKey(Statics.START_NOTIFICATION_MINUTES)) {
            mPrefs.putIntValue(Statics.START_NOTIFICATION_MINUTES, 0);
        }
        if (!mPrefs.isContainKey(Statics.END_NOTIFICATION_HOUR)) {
            mPrefs.putIntValue(Statics.END_NOTIFICATION_HOUR, Statics.DEFAULT_END_NOTIFICATION_TIME);
        }
        if (!mPrefs.isContainKey(Statics.END_NOTIFICATION_MINUTES)) {
            mPrefs.putIntValue(Statics.END_NOTIFICATION_MINUTES, 0);
        }
    }

    public Prefs getmPrefs() {
        return mPrefs;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setRetryPolicy(new DefaultRetryPolicy(Statics.REQUEST_TIMEOUT_MS, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setRetryPolicy(new DefaultRetryPolicy(Statics.REQUEST_TIMEOUT_MS, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    public void clearCache() {
        getRequestQueue().getCache().clear();
    }

    public void putData(Object key, Object value) {
        try {
            _data.put(key, value);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            System.gc();
        }

    }

    public void removeData(Object key) {
        if (getData(key) != null)
            _data.remove(key);
    }

    public Object getData(Object key) {
        return _data.get(key);
    }

    public static boolean isActivityVisible() {
        return activityVisible;
    }

    public static void activityResumed() {
        try {
            activityVisible = true;
        }catch (NullPointerException e){
            // don't know why
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void activityPaused() {
        activityVisible = false;
    }

    private static boolean activityVisible;

    public static long currentRoomNo = 0;
    public static long currentNotification = 0;


    public static void resetValue(){
        if (listUsers != null){
            listUsers.clear();
            listUsers = null;
        }

        if (listDeparts != null){
            listDeparts.clear();
            listDeparts = null;
        }

        currentId = 0;

        if (listFavoriteGroup != null){
            listFavoriteGroup.clear();
            listFavoriteGroup = null;
        }

        if (listFavoriteTop != null){
            listFavoriteTop.clear();
            listFavoriteTop = null;
        }
    }
    /* Define a function to clear disk cache */
    public static void clearApplicationData() {
        File cache = getInstance().getCacheDir();
        File appDir = new File(cache.getParent());
        if (appDir.exists()) {
            String[] children = appDir.list();
            for (String s : children) {
                if (!s.equals("lib")) {
                    deleteDir(new File(appDir, s));

                }
            }
        }
    }
    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }

        return dir.delete();
    }
}
