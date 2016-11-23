package com.dazone.crewchat.HTTPs;

import android.text.TextUtils;

import com.android.volley.Request;
import com.dazone.crewchat.database.ServerSiteDBHelper;
import com.dazone.crewchat.database.UserDBHelper;
import com.dazone.crewchat.dto.ErrorDto;
import com.dazone.crewchat.dto.UserDto;
import com.dazone.crewchat.interfaces.BaseHTTPCallBack;
import com.dazone.crewchat.interfaces.BaseHTTPCallbackWithJson;
import com.dazone.crewchat.interfaces.OAUTHUrls;
import com.dazone.crewchat.interfaces.OnCheckDevice;
import com.dazone.crewchat.interfaces.Urls;
import com.dazone.crewchat.utils.CrewChatApplication;
import com.dazone.crewchat.utils.Prefs;
import com.dazone.crewchat.utils.TimeUtils;
import com.dazone.crewchat.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HttpOauthRequest {

    private static HttpOauthRequest mInstance;
    private static String root_link;
    public static HttpOauthRequest getInstance() {
        if (null == mInstance) {
            mInstance = new HttpOauthRequest();
        }
        root_link = CrewChatApplication.getInstance().getmPrefs().getServerSite();
        return mInstance;
    }
    public void login(final BaseHTTPCallBack baseHTTPCallBack,String userID, String password,String mobileOSVersion,String server_link) {
        final String url = server_link+ OAUTHUrls.URL_GET_LOGIN;
        Map<String, String> params = new HashMap<>();
        params.put("userID", userID);
        params.put("password", password);
        params.put("mobileOSVersion", mobileOSVersion);
        WebServiceManager webServiceManager = new WebServiceManager();
        webServiceManager.doJsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new WebServiceManager.RequestListener<String>() {
            @Override
            public void onSuccess(String response) {
                Gson gson = new Gson();
                UserDto userDto = gson.fromJson(response, UserDto.class);
                userDto.prefs.putaccesstoken(userDto.session);
                userDto.prefs.putUserNo(userDto.Id);
                userDto.prefs.putUserName(userDto.userID);
                UserDBHelper.addUser(userDto);
                baseHTTPCallBack.onHTTPSuccess();
            }
            @Override
            public void onFailure(ErrorDto error) {
                baseHTTPCallBack.onHTTPFail(error);
            }
        });
    }

    // Login function V2
    public void loginV2(final BaseHTTPCallBack baseHTTPCallBack,String userID, String password,String mobileOSVersion,String subDomain, String server_link) {
        final String url = server_link+ OAUTHUrls.URL_GET_LOGIN_V3;
        Map<String, String> params = new HashMap<>();
        //params.put("companyDomain", subDomain + "." + OAUTHUrls.URL_ROOT_DOMAIN);
        params.put("companyDomain", subDomain);
        params.put("languageCode", Locale.getDefault().getLanguage().toUpperCase());
        params.put("timeZoneOffset", TimeUtils.getTimezoneOffsetInMinutes());
        params.put("userID", userID);
        params.put("password", password);
        params.put("mobileOSVersion", mobileOSVersion);
        WebServiceManager webServiceManager = new WebServiceManager();
        webServiceManager.doJsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new WebServiceManager.RequestListener<String>() {
            @Override
            public void onSuccess(String response) {

                Utils.printLogs("Login V2 response = "+response);

                Gson gson = new Gson();
                UserDto userDto = gson.fromJson(response, UserDto.class);
                userDto.prefs.putaccesstoken(userDto.session);
                userDto.prefs.putUserNo(userDto.Id);
                userDto.prefs.putUserName(userDto.userID);
                userDto.prefs.setAvatarUrl(userDto.avatar);
                userDto.prefs.putCompanyName(userDto.getNameCompany());
                userDto.prefs.setFullName(userDto.getFullName());
                userDto.prefs.putCompanyNo(userDto.getCompanyNo());
                UserDBHelper.addUser(userDto);
                // Set static current user Id
                CrewChatApplication.currentId = userDto.Id;

                Urls.HOST_STATUS = userDto.CrewDDSServer;

                baseHTTPCallBack.onHTTPSuccess();
            }
            @Override
            public void onFailure(ErrorDto error) {
                baseHTTPCallBack.onHTTPFail(error);
            }
        });
    }


    public void getUser(final BaseHTTPCallbackWithJson baseHTTPCallBack, String userNo, String languageCode, String timeZoneOffset, String server_link){
        final String url = server_link+ OAUTHUrls.URL_GET_USER_DETAIL;
        Map<String, String> params = new HashMap<>();
        params.put("sessionId",""+CrewChatApplication.getInstance().getmPrefs().getaccesstoken());
        params.put("userNo", userNo);
        params.put("timeZoneOffset", timeZoneOffset);
        params.put("languageCode", languageCode);
        WebServiceManager webServiceManager = new WebServiceManager();
        webServiceManager.doJsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new WebServiceManager.RequestListener<String>() {
            @Override
            public void onSuccess(String response) {
                baseHTTPCallBack.onHTTPSuccess(response);
            }
            @Override
            public void onFailure(ErrorDto error) {
                baseHTTPCallBack.onHTTPFail(error);
            }
        });

    }

    public void checkLogin(final BaseHTTPCallBack baseHTTPCallBack) {
        final String url = root_link+ OAUTHUrls.URL_CHECK_SESSION;
        Map<String, String> params = new HashMap<>();
        params.put("sessionId",""+CrewChatApplication.getInstance().getmPrefs().getaccesstoken());
        WebServiceManager webServiceManager = new WebServiceManager();
        webServiceManager.doJsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new WebServiceManager.RequestListener<String>() {
            @Override
            public void onSuccess(String response) {
                Gson gson = new Gson();
                UserDto userDto = gson.fromJson(response, UserDto.class);
                userDto.prefs.putaccesstoken(userDto.session);
                userDto.prefs.putUserNo(userDto.Id);
                userDto.prefs.putUserName(userDto.userID);
                UserDBHelper.addUser(userDto);
                if(baseHTTPCallBack!=null) {
                    baseHTTPCallBack.onHTTPSuccess();
                }
            }
            @Override
            public void onFailure(ErrorDto error) {
                if(baseHTTPCallBack!=null) {
                    baseHTTPCallBack.onHTTPFail(error);
                }
            }
        });
    }
    public void logout(final BaseHTTPCallBack baseHTTPCallBack) {
        final String url = root_link+ OAUTHUrls.URL_LOG_OUT;
        Map<String, String> params = new HashMap<>();
        params.put("sessionId",""+CrewChatApplication.getInstance().getmPrefs().getaccesstoken());
        WebServiceManager webServiceManager = new WebServiceManager();
        webServiceManager.doJsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new WebServiceManager.RequestListener<String>() {
            @Override
            public void onSuccess(String response) {
                baseHTTPCallBack.onHTTPSuccess();
            }
            @Override
            public void onFailure(ErrorDto error) {
                baseHTTPCallBack.onHTTPFail(error);
            }
        });
    }

    public void insertPhoneToken() {
        String url = OAUTHUrls.URL_INSERT_PHONE_TOKEN;
        Map<String, String> params = new HashMap<>();
        params.put("PhoneToken", Utils.getUniqueDeviceId(CrewChatApplication.getInstance()));
        params.put("SessionID", CrewChatApplication.getInstance().getmPrefs().getaccesstoken());
        params.put("Domain", root_link);
        WebServiceManager webServiceManager = new WebServiceManager();
        webServiceManager.doJsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new WebServiceManager.RequestListener<String>() {
            @Override
            public void onSuccess(String response) {
            }

            @Override
            public void onFailure(ErrorDto error) {
            }
        });
    }

    public void checkPhoneToken(final OnCheckDevice callBack) {
        String url = OAUTHUrls.URL_CHECK_DEVICE_TOKEN;
        Map<String, String> params = new HashMap<>();
        params.put("PhoneToken",Utils.getUniqueDeviceId(CrewChatApplication.getInstance()));
        WebServiceManager webServiceManager = new WebServiceManager();
        webServiceManager.doJsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new WebServiceManager.RequestListener<String>() {
            @Override
            public void onSuccess(String response) {
                try {
                    Prefs prefs = new Prefs();
                    JSONObject object = new JSONObject(response);
                    String SessionID = object.getString("SessionID");
                    String Domain = object.getString("Domain");
                    if(!TextUtils.isEmpty(SessionID)) {
                        prefs.putaccesstoken(SessionID);
                    }
                    if(!TextUtils.isEmpty(Domain)) {
                        prefs.putServerSite(Domain);
//                        ServerSiteDBHelper.addServerSite(Domain);
                    }
                } catch (Exception e)
                {
                    ErrorDto dto = new ErrorDto();
                    dto.message = "Cannot connect to server";
                    callBack.onHTTPFail(dto);
                }
                callBack.onDeviceSuccess();
            }

            @Override
            public void onFailure(ErrorDto error) {
                callBack.onHTTPFail(error);
            }
        });
    }

    public void getDomain(final BaseHTTPCallBack callBack) {
        String url = OAUTHUrls.URL_GET_DOMAIN;
        Map<String, String> params = new HashMap<>();
        WebServiceManager webServiceManager = new WebServiceManager();
        webServiceManager.doJsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new WebServiceManager.RequestListener<String>() {
            @Override
            public void onSuccess(String response) {
                Type listType = new TypeToken<List<String>>() {
                }.getType();
                List<String> listDomain = new Gson().fromJson(response, listType);
                ServerSiteDBHelper.addServerSites(listDomain);
                if(callBack!=null)
                    callBack.onHTTPSuccess();
            }

            @Override
            public void onFailure(ErrorDto error) {
                if(callBack!=null)
                    callBack.onHTTPFail(error);
            }
        });
    }
}
