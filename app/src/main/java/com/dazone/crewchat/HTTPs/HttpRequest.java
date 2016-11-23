package com.dazone.crewchat.HTTPs;

import com.android.volley.Request;
import com.dazone.crewchat.Tree.Dtos.TreeUserDTO;
import com.dazone.crewchat.constant.Statics;
import com.dazone.crewchat.dto.*;
import com.dazone.crewchat.dto.userfavorites.FavoriteChatRoomDto;
import com.dazone.crewchat.interfaces.*;
import com.dazone.crewchat.utils.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.*;

public class HttpRequest {

    private static HttpRequest mInstance;
    private static String root_link;
    private static Prefs prefs;

    public static HttpRequest getInstance() {
        if (null == mInstance) {
            mInstance = new HttpRequest();
        }
        root_link = CrewChatApplication.getInstance().getmPrefs().getServerSite();
        prefs = CrewChatApplication.getInstance().getmPrefs();
        return mInstance;
    }

    public void GetListOrganize(final IGetListOrganization iGetListOrganization) {
        String url = root_link + Urls.URL_GET_ALL_USER_BE_LONGS;
        Map<String, String> params = new HashMap<>();
        params.put("sessionId", "" + CrewChatApplication.getInstance().getmPrefs().getaccesstoken());
        params.put("languageCode", Locale.getDefault().getLanguage().toUpperCase());
        params.put("timeZoneOffset", TimeUtils.getTimezoneOffsetInMinutes());
        WebServiceManager webServiceManager = new WebServiceManager();
        webServiceManager.doJsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new WebServiceManager.RequestListener<String>() {
            @Override
            public void onSuccess(String response) {

                Type listType = new TypeToken<ArrayList<TreeUserDTOTemp>>() {}.getType();
                ArrayList<TreeUserDTOTemp> list = new Gson().fromJson(response, listType);

                if (iGetListOrganization != null)
                    iGetListOrganization.onGetListSuccess(list);
            }

            @Override
            public void onFailure(ErrorDto error) {
                if (iGetListOrganization != null)
                    iGetListOrganization.onGetListFail(error);
            }
        });
    }



    public void GetListDepart(final IGetListDepart iGetListDepart) {
        String url = root_link + Urls.URL_GET_DEPARTMENT;
        Map<String, String> params = new HashMap<>();
        params.put("sessionId", "" + CrewChatApplication.getInstance().getmPrefs().getaccesstoken());
        params.put("languageCode", Locale.getDefault().getLanguage().toUpperCase());
        params.put("timeZoneOffset", TimeUtils.getTimezoneOffsetInMinutes());
        WebServiceManager webServiceManager = new WebServiceManager();
        webServiceManager.doJsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new WebServiceManager.RequestListener<String>() {
            @Override
            public void onSuccess(String response) {
                Type listType = new TypeToken<List<TreeUserDTO>>(){}.getType();
                ArrayList<TreeUserDTO> list = new Gson().fromJson(response, listType);
                if (iGetListDepart != null)
                    iGetListDepart.onGetListDepartSuccess(list);
            }

            @Override
            public void onFailure(ErrorDto error) {
                if (iGetListDepart != null)
                    iGetListDepart.onGetListDepartFail(error);
            }
        });
    }

    public void CreateOneUserChatRoom(int UserNo, final ICreateOneUserChatRom iCreateOneUserChatRom) {
        String url = root_link + Urls.URL_ROOT_2;
        Map<String, String> params = new HashMap<>();
        Map<String, Object> params2 = new HashMap<>();
        params.put("command", "" + Urls.URL_CREATE_ONE_USER_CHAT);
        params.put("sessionId", "" + CrewChatApplication.getInstance().getmPrefs().getaccesstoken());
        params.put("languageCode", Locale.getDefault().getLanguage().toUpperCase());
        params.put("timeZoneOffset", TimeUtils.getTimezoneOffsetInMinutes());
        params2.put("joinNo", UserNo);
        Gson gson = new Gson();
        String js = gson.toJson(params2);
        params.put("reqJson", js);
        WebServiceManager webServiceManager = new WebServiceManager();
        webServiceManager.doJsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new WebServiceManager.RequestListener<String>() {
            @Override
            public void onSuccess(String response) {
                ChattingDto chattingDto = new Gson().fromJson(response, ChattingDto.class);
                if (iCreateOneUserChatRom != null)
                    iCreateOneUserChatRom.onICreateOneUserChatRomSuccess(chattingDto);
            }

            @Override
            public void onFailure(ErrorDto error) {
                if (iCreateOneUserChatRom != null)
                    iCreateOneUserChatRom.onICreateOneUserChatRomFail(error);
            }
        });
    }

    public void CreateGroupChatRoom(List<Integer> userNos, final ICreateOneUserChatRom iCreateOneUserChatRom) {
        String url = root_link + Urls.URL_ROOT_2;
        Map<String, String> params = new HashMap<>();
        Map<String, Object> params2 = new HashMap<>();
        params.put("command", "" + Urls.URL_CREATE_GROUP_USER_CHAT);
        params.put("sessionId", "" + CrewChatApplication.getInstance().getmPrefs().getaccesstoken());
        params.put("languageCode", Locale.getDefault().getLanguage().toUpperCase());
        params.put("timeZoneOffset", TimeUtils.getTimezoneOffsetInMinutes());
        String user = "";
        userNos.toString();
        for (int i : userNos) {
            user += i + ",";
        }
        params2.put("userNos", user.substring(0, user.length() - 1));
        Gson gson = new Gson();
        String js = gson.toJson(params2);
        params.put("reqJson", js);
        WebServiceManager webServiceManager = new WebServiceManager();
        webServiceManager.doJsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new WebServiceManager.RequestListener<String>() {
            @Override
            public void onSuccess(String response) {
                ChattingDto chattingDto = new Gson().fromJson(response, ChattingDto.class);
                if (iCreateOneUserChatRom != null)
                    iCreateOneUserChatRom.onICreateOneUserChatRomSuccess(chattingDto);
            }

            @Override
            public void onFailure(ErrorDto error) {
                if (iCreateOneUserChatRom != null)
                    iCreateOneUserChatRom.onICreateOneUserChatRomFail(error);
            }
        });
    }

    public void CreateGroupChatRoom(ArrayList<TreeUserDTO> list, final ICreateOneUserChatRom iCreateOneUserChatRom) {
        String url = root_link + Urls.URL_ROOT_2;
        Map<String, String> params = new HashMap<>();
        Map<String, Object> params2 = new HashMap<>();
        params.put("command", "" + Urls.URL_CREATE_GROUP_USER_CHAT);
        params.put("sessionId", "" + CrewChatApplication.getInstance().getmPrefs().getaccesstoken());
        params.put("languageCode", Locale.getDefault().getLanguage().toUpperCase());
        params.put("timeZoneOffset", TimeUtils.getTimezoneOffsetInMinutes());
        String user = "";
        for (TreeUserDTO treeUserDTO : list) {
            if (treeUserDTO.getType() == 2)
                user += treeUserDTO.getId() + ",";
        }
        params2.put("userNos", user.substring(0, user.length() - 1));
        Gson gson = new Gson();
        String js = gson.toJson(params2);
        params.put("reqJson", js);
        WebServiceManager webServiceManager = new WebServiceManager();
        webServiceManager.doJsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new WebServiceManager.RequestListener<String>() {
            @Override
            public void onSuccess(String response) {
                ChattingDto chattingDto = new Gson().fromJson(response, ChattingDto.class);
                if (iCreateOneUserChatRom != null)
                    iCreateOneUserChatRom.onICreateOneUserChatRomSuccess(chattingDto);
            }

            @Override
            public void onFailure(ErrorDto error) {
                if (iCreateOneUserChatRom != null)
                    iCreateOneUserChatRom.onICreateOneUserChatRomFail(error);
            }
        });
    }

    public void SendChatMsg(long RoomNo, String message, final SendChatMessage sendChatMessage) {
        final String url = root_link + Urls.URL_ROOT_2;
        Map<String, String> params = new HashMap<>();
        Map<String, Object> params2 = new HashMap<>();
        params.put("command", "" + Urls.URL_SEND_CHAT);
        params.put("sessionId", "" + CrewChatApplication.getInstance().getmPrefs().getaccesstoken());
        params.put("languageCode", Locale.getDefault().getLanguage().toUpperCase());
        params.put("timeZoneOffset", TimeUtils.getTimezoneOffsetInMinutes());
        params2.put("roomNo", RoomNo);
        params2.put("message", message);
        Gson gson = new Gson();
        String js = gson.toJson(params2);
        params.put("reqJson", js);
        WebServiceManager webServiceManager = new WebServiceManager();
        webServiceManager.doJsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new WebServiceManager.RequestListener<String>() {
            @Override
            public void onSuccess(String response) {
                ChattingDto chattingDto = new Gson().fromJson(response, ChattingDto.class);
                if (sendChatMessage != null)
                    sendChatMessage.onSenChatMessageSuccess(chattingDto);
            }

            @Override
            public void onFailure(ErrorDto error) {
                if (sendChatMessage != null)
                    sendChatMessage.onSenChatMessageFail(error, url);
            }
        });
    }

    public void SendChatAttachFileTest(long RoomNo, int attachNo, final SendChatMessage sendChatMessage, int position) {
        final String url = root_link + Urls.URL_ROOT_2;
        Map<String, String> params = new HashMap<>();
        Map<Object, Object> params2 = new HashMap<>();
        params.put("command", "" + Urls.URL_SEND_ATTACH_FILE);
        params.put("sessionId", "" + CrewChatApplication.getInstance().getmPrefs().getaccesstoken());
        params.put("languageCode", Locale.getDefault().getLanguage().toUpperCase());
        params.put("timeZoneOffset", TimeUtils.getTimezoneOffsetInMinutes());
        params2.put("roomNo", RoomNo);
        params2.put("attachNo", attachNo);

        Gson gson = new Gson();
        String js = gson.toJson(params2);
        params.put("reqJson", js);
        WebServiceManager webServiceManager = new WebServiceManager();
        webServiceManager.doJsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new WebServiceManager.RequestListener<String>() {
            @Override
            public void onSuccess(String response) {
                ChattingDto chattingDto = new Gson().fromJson(response, ChattingDto.class);
                if (sendChatMessage != null)
                    sendChatMessage.onSenChatMessageSuccess(chattingDto);
            }

            @Override
            public void onFailure(ErrorDto error) {
                if (sendChatMessage != null)
                    sendChatMessage.onSenChatMessageFail(error, url);
            }
        });
    }

    public void SendChatAttachFile(long RoomNo, int attachNo, final SendChatMessage sendChatMessage) {
        final String url = root_link + Urls.URL_ROOT_2;
        Map<String, String> params = new HashMap<>();
        Map<Object, Object> params2 = new HashMap<>();
        params.put("command", "" + Urls.URL_SEND_ATTACH_FILE);
        params.put("sessionId", "" + CrewChatApplication.getInstance().getmPrefs().getaccesstoken());
        params.put("languageCode", Locale.getDefault().getLanguage().toUpperCase());
        params.put("timeZoneOffset", TimeUtils.getTimezoneOffsetInMinutes());
        params2.put("roomNo", RoomNo);
        params2.put("attachNo", attachNo);

        Gson gson = new Gson();
        String js = gson.toJson(params2);
        params.put("reqJson", js);
        WebServiceManager webServiceManager = new WebServiceManager();
        webServiceManager.doJsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new WebServiceManager.RequestListener<String>() {
            @Override
            public void onSuccess(String response) {
                ChattingDto chattingDto = new Gson().fromJson(response, ChattingDto.class);
                if (sendChatMessage != null)
                    sendChatMessage.onSenChatMessageSuccess(chattingDto);
            }

            @Override
            public void onFailure(ErrorDto error) {
                if (sendChatMessage != null)
                    sendChatMessage.onSenChatMessageFail(error, url);
            }
        });
    }

    /*
    * Get users status
    * */
    public void getAllUserInfo(final OnGetUserInfo callback) {
        String url = root_link + Urls.URL_ROOT_2;
        Map<String, String> params = new HashMap<>();
        Map<Object, Object> params2 = new HashMap<>();
        params.put("command", "" + Urls.URL_GET_USERS_STATUS);
        params.put("sessionId", "" + CrewChatApplication.getInstance().getmPrefs().getaccesstoken());
        params.put("languageCode", Locale.getDefault().getLanguage().toUpperCase());
        params.put("timeZoneOffset", TimeUtils.getTimezoneOffsetInMinutes());
        Gson gson = new Gson();
        String js = gson.toJson(params2);
        params.put("reqJson", js);
        WebServiceManager webServiceManager = new WebServiceManager();
        webServiceManager.doJsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new WebServiceManager.RequestListener<String>() {
            @Override
            public void onSuccess(String response) {
                Utils.printLogs("Get all user info ="+response
                );
                Type listType = new TypeToken<ArrayList<UserInfoDto>>() {
                }.getType();
                ArrayList<UserInfoDto> list = new Gson().fromJson(response, listType);
                callback.OnSuccess(list);
            }

            @Override
            public void onFailure(ErrorDto error) {
                callback.OnFail(error);
            }
        });
    }
    /**
     * GET MESSAGE UNREAD COUNT
     */
    public void GetMessageUnreadCount(long roomNo, long startMsgNo, final OnGetMessageUnreadCountCallBack callBack) {
        String url = root_link + Urls.URL_ROOT_2;
        Map<String, String> params = new HashMap<>();
        Map<String, Object> params2 = new HashMap<>();
        params.put("command", "" + Urls.URL_GET_MESSAGE_UNREAD_COUNT);
        params.put("sessionId", "" + CrewChatApplication.getInstance().getmPrefs().getaccesstoken());
        params.put("languageCode", Locale.getDefault().getLanguage().toUpperCase());
        params.put("timeZoneOffset", TimeUtils.getTimezoneOffsetInMinutes());
        params2.put("roomNo", roomNo);
        params2.put("startMsgNo", startMsgNo);
        Gson gson = new Gson();
        String js = gson.toJson(params2);
        params.put("reqJson", js);
        WebServiceManager webServiceManager = new WebServiceManager();
        webServiceManager.doJsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new WebServiceManager.RequestListener<String>() {
            @Override
            public void onSuccess(String response) {
                callBack.onHTTPSuccess(response);
            }

            @Override
            public void onFailure(ErrorDto error) {
                callBack.onHTTPFail(error);
            }
        });
    }

    /**
     * UPDATE MESSAGE UNREAD COUNT
     */
    public void UpdateMessageUnreadCount(long roomNo, int userNo, long startMsgNo) {
        String url = root_link + Urls.URL_ROOT_2;
        Map<String, String> params = new HashMap<>();
        Map<String, Object> params2 = new HashMap<>();
        params.put("command", "" + Urls.URL_UPDATE_MESSAGE_UNREAD_COUNT);
        params.put("sessionId", "" + CrewChatApplication.getInstance().getmPrefs().getaccesstoken());
        params.put("languageCode", Locale.getDefault().getLanguage().toUpperCase());
        params.put("timeZoneOffset", TimeUtils.getTimezoneOffsetInMinutes());
        params2.put("roomNo", roomNo);
        params2.put("userNo", userNo);
        params2.put("startMsgNo", startMsgNo);
        Gson gson = new Gson();
        String js = gson.toJson(params2);
        params.put("reqJson", js);
        WebServiceManager webServiceManager = new WebServiceManager();
        webServiceManager.doJsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new WebServiceManager.RequestListener<String>() {
            @Override
            public void onSuccess(String response) {

                Utils.printLogs("Update Unread Count = "+response);
            }

            @Override
            public void onFailure(ErrorDto error) {
                Utils.printLogs("Update Unread Count failed");
            }
        });
    }

    /**
     * GET USER By UserNo
     */
    public void GetUser(int userNo, final OnGetUserCallBack callBack) {
        String url = root_link + Urls.URL_GET_USER;
        Map<String, String> params = new HashMap<>();
        params.put("sessionId", "" + CrewChatApplication.getInstance().getmPrefs().getaccesstoken());
        params.put("languageCode", Locale.getDefault().getLanguage().toUpperCase());
        params.put("timeZoneOffset", TimeUtils.getTimezoneOffsetInMinutes());
        params.put("userNo", userNo + "");
        WebServiceManager webServiceManager = new WebServiceManager();
        webServiceManager.doJsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new WebServiceManager.RequestListener<String>() {
            @Override
            public void onSuccess(String response) {
                ProfileUserDTO profileUserDTO = new Gson().fromJson(response, ProfileUserDTO.class);
                if (callBack != null)
                    callBack.onHTTPSuccess(profileUserDTO);
            }

            @Override
            public void onFailure(ErrorDto error) {
                if (callBack != null)
                    callBack.onHTTPFail(error);
            }
        });
    }

    public void GetChatMsgSection(long roomNo, long baseMsgNo, int type, final OnGetChatMessage onGetChatMessage) {
        String url = root_link + Urls.URL_ROOT_2;
        Map<String, String> params = new HashMap<>();
        Map<String, Object> params2 = new HashMap<>();
        params.put("command", "" + Urls.URL_GET_CHAT_MSG_SECTION);
        params.put("sessionId", "" + CrewChatApplication.getInstance().getmPrefs().getaccesstoken());
        params.put("languageCode", Locale.getDefault().getLanguage().toUpperCase());
        params.put("timeZoneOffset", TimeUtils.getTimezoneOffsetInMinutes());
        params2.put("roomNo", roomNo);
        params2.put("baseMsgNo", baseMsgNo);
        params2.put("getType", type);
        Gson gson = new Gson();
        String js = gson.toJson(params2);
        params.put("reqJson", js);
        WebServiceManager webServiceManager = new WebServiceManager();
        webServiceManager.doJsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new WebServiceManager.RequestListener<String>() {
            @Override
            public void onSuccess(String response) {
                Type listType = new TypeToken<List<ChattingDto>>() {
                }.getType();
                List<ChattingDto> list = new Gson().fromJson(response, listType);
                if (onGetChatMessage != null)
                    onGetChatMessage.OnGetChatMessageSuccess(list);
            }

            @Override
            public void onFailure(ErrorDto error) {
                if (onGetChatMessage != null)
                    onGetChatMessage.OnGetChatMessageFail(error);
            }
        });
    }

    public void GetChatMsg(long RoomNo, final OnGetChatMessage onGetChatMessage) {
        String url = root_link + Urls.URL_ROOT_2;
        Map<String, String> params = new HashMap<>();
        Map<String, Object> params2 = new HashMap<>();
        params.put("command", "" + Urls.URL_GET_CHAT_MSG);
        params.put("sessionId", "" + CrewChatApplication.getInstance().getmPrefs().getaccesstoken());
        params.put("languageCode", Locale.getDefault().getLanguage().toUpperCase());
        params.put("timeZoneOffset", TimeUtils.getTimezoneOffsetInMinutes());
        params2.put("roomNo", RoomNo);
        Gson gson = new Gson();
        String js = gson.toJson(params2);
        params.put("reqJson", js);
        WebServiceManager webServiceManager = new WebServiceManager();
        webServiceManager.doJsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new WebServiceManager.RequestListener<String>() {
            @Override
            public void onSuccess(String response) {
                Type listType = new TypeToken<List<ChattingDto>>() {
                }.getType();
                List<ChattingDto> list = new Gson().fromJson(response, listType);
                if (onGetChatMessage != null)
                    onGetChatMessage.OnGetChatMessageSuccess(list);
            }

            @Override
            public void onFailure(ErrorDto error) {
                if (onGetChatMessage != null)
                    onGetChatMessage.OnGetChatMessageFail(error);
            }
        });
    }


    /**
     * GET CHAT ROOM INFO
     */
    public void GetChatRoom(long roomNo, final OnGetChatRoom callBack) {
        String url = root_link + Urls.URL_ROOT_2;
        Map<String, String> params = new HashMap<>();
        Map<String, Object> params2 = new HashMap<>();
        params.put("command", "" + Urls.URL_GET_CHAT_ROOM);
        params.put("sessionId", "" + CrewChatApplication.getInstance().getmPrefs().getaccesstoken());
        params.put("languageCode", Locale.getDefault().getLanguage().toUpperCase());
        params.put("timeZoneOffset", TimeUtils.getTimezoneOffsetInMinutes());
        params2.put("roomNo", roomNo);
        Gson gson = new Gson();
        String js = gson.toJson(params2);
        params.put("reqJson", js);
        WebServiceManager webServiceManager = new WebServiceManager();
        webServiceManager.doJsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new WebServiceManager.RequestListener<String>() {
            @Override
            public void onSuccess(String response) {
                Type listType = new TypeToken<ChatRoomDTO>() {
                }.getType();
                ChatRoomDTO chatRoomDTO = new Gson().fromJson(response, listType);
                if (callBack != null) {
                    callBack.OnGetChatRoomSuccess(chatRoomDTO);
                }
            }

            @Override
            public void onFailure(ErrorDto error) {
                if (callBack != null) {
                    callBack.OnGetChatRoomFail(error);
                }
            }
        });
    }

    public void GetChatList(final OnGetChatList onGetChatList) {
        String url = root_link + Urls.URL_ROOT_2;
        Map<String, String> params = new HashMap<>();
        params.put("command", "" + Urls.URL_GET_CHAT_LIST);
        params.put("sessionId", "" + CrewChatApplication.getInstance().getmPrefs().getaccesstoken());
        params.put("languageCode", Locale.getDefault().getLanguage().toUpperCase());
        params.put("timeZoneOffset", TimeUtils.getTimezoneOffsetInMinutes());
        params.put("reqJson", "");
        WebServiceManager webServiceManager = new WebServiceManager();
        webServiceManager.doJsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new WebServiceManager.RequestListener<String>() {
            @Override
            public void onSuccess(String response) {
                //System.out.println("Response" + response);
                new Prefs().putStringValue(Statics.KEY_DATA_CURRENT_CHAT_LIST, response);
                Type listType = new TypeToken<List<ChattingDto>>() {
                }.getType();
                List<ChattingDto> list = new Gson().fromJson(response, listType);
                if (onGetChatList != null)
                    onGetChatList.OnGetChatListSuccess(list);
            }

            @Override
            public void onFailure(ErrorDto error) {
                if (onGetChatList != null)
                    onGetChatList.OnGetChatListFail(error);
            }
        });
    }

    /*
    * Notification setting function
    * Type InsertDevice, Update Device
    * */

    public void setNotification(String command,String deviceId, Map<String, Object> notificationParams, final OnSetNotification callback) {
        String url = root_link + Urls.URL_ROOT_2;

        Map<String, Object> params = new HashMap<>();
        Map<String, Object> jsonParam = new HashMap<>();

        jsonParam.put("DeviceType",Statics.DEVICE_TYPE);
        jsonParam.put("DeviceID",deviceId);


        Gson gson = new Gson();
        jsonParam.put("NotifcationOptions",notificationParams);

        params.put("command", command);
        params.put("sessionId", "" + CrewChatApplication.getInstance().getmPrefs().getaccesstoken());
        params.put("languageCode", Locale.getDefault().getLanguage().toUpperCase());
        params.put("timeZoneOffset", TimeUtils.getTimezoneOffsetInMinutes());


        //Gson gson = new Gson();
        String js = gson.toJson(jsonParam);
        params.put("reqJson", js);

        WebServiceManager webServiceManager = new WebServiceManager();
        webServiceManager.doJsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new WebServiceManager.RequestListener<String>() {
            @Override
            public void onSuccess(String response) {

                Utils.printLogs("On Set notification #### " + response);
                callback.OnSuccess();
            }

            @Override
            public void onFailure(ErrorDto error) {
                callback.OnFail(error);
            }
        });
    }

    public void GetChatListV2(final OnGetChatListV2 onGetChatList) {
        String url = root_link + Urls.URL_ROOT_2;
        Map<String, String> params = new HashMap<>();
        params.put("command", "" + Urls.URL_GET_CHAT_LIST);
        params.put("sessionId", "" + CrewChatApplication.getInstance().getmPrefs().getaccesstoken());
        params.put("languageCode", Locale.getDefault().getLanguage().toUpperCase());
        params.put("timeZoneOffset", TimeUtils.getTimezoneOffsetInMinutes());
        params.put("reqJson", "");
        WebServiceManager webServiceManager = new WebServiceManager();
        webServiceManager.doJsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new WebServiceManager.RequestListener<String>() {
            @Override
            public void onSuccess(String response) {
                System.out.println("Get chatRoom V2: " + response);
                new Prefs().putStringValue(Statics.KEY_DATA_CURRENT_CHAT_LIST, response);
                Type listType = new TypeToken<List<ChatRoomDTO>>() {}.getType();
                List<ChatRoomDTO> list = new Gson().fromJson(response, listType);
                for (ChatRoomDTO chattingDto : list) {
                    System.out.println("aaaaaaaaaaaaaaaaaa " + chattingDto.toString());

                }
                if (onGetChatList != null)
                    onGetChatList.OnGetChatListSuccess(list);
            }

            @Override
            public void onFailure(ErrorDto error) {
                if (onGetChatList != null)
                    onGetChatList.OnGetChatListFail(error);
            }
        });
    }

    public void DeleteChatRoomUser(long RoomNo, long UserNo, final BaseHTTPCallBack baseHTTPCallBack) {
        String url = root_link + Urls.URL_ROOT_2;
        Map<String, String> params = new HashMap<>();
        Map<String, Object> params2 = new HashMap<>();
        params.put("command", "" + Urls.URL_DELETE_LIST);
        params.put("sessionId", "" + CrewChatApplication.getInstance().getmPrefs().getaccesstoken());
        params.put("languageCode", Locale.getDefault().getLanguage().toUpperCase());
        params.put("timeZoneOffset", TimeUtils.getTimezoneOffsetInMinutes());
        params2.put("roomNo", RoomNo);
        params2.put("userNo", UserNo);
        Gson gson = new Gson();
        String js = gson.toJson(params2);
        params.put("reqJson", js);
        WebServiceManager webServiceManager = new WebServiceManager();
        webServiceManager.doJsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new WebServiceManager.RequestListener<String>() {
            @Override
            public void onSuccess(String response) {
                if (baseHTTPCallBack != null)
                    baseHTTPCallBack.onHTTPSuccess();
            }

            @Override
            public void onFailure(ErrorDto error) {
                if (baseHTTPCallBack != null)
                    baseHTTPCallBack.onHTTPFail(error);
            }
        });
    }

    /*
    * Function to add an user to favorite group
    * */

    public void insertFavoriteUser(long groupNo, long UserNo, final BaseHTTPCallbackWithJson baseHTTPCallBack) {
        String url = root_link + Urls.URL_ROOT_2;

        Map<String, String> params = new HashMap<>();
        Map<String, Object> params2 = new HashMap<>();

        params.put("command", "" + Urls.URL_INSERT_FAVORITE);
        params.put("sessionId", "" + CrewChatApplication.getInstance().getmPrefs().getaccesstoken());
        params.put("languageCode", Locale.getDefault().getLanguage().toUpperCase());
        params.put("timeZoneOffset", TimeUtils.getTimezoneOffsetInMinutes());

        params2.put("groupNo", groupNo);
        params2.put("userNo", UserNo);

        Gson gson = new Gson();
        String js = gson.toJson(params2);

        params.put("reqJson", js);

        WebServiceManager webServiceManager = new WebServiceManager();
        webServiceManager.doJsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new WebServiceManager.RequestListener<String>() {
            @Override
            public void onSuccess(String response) {
                if (baseHTTPCallBack != null)
                    baseHTTPCallBack.onHTTPSuccess(response);
            }

            @Override
            public void onFailure(ErrorDto error) {
                if (baseHTTPCallBack != null)
                    baseHTTPCallBack.onHTTPFail(error);
            }
        });
    }


     /*
    * Function to add an user to favorite group
    * */

    public void deleteFavoriteUser(long groupNo, long UserNo, final BaseHTTPCallbackWithJson baseHTTPCallBack) {
        String url = root_link + Urls.URL_ROOT_2;

        Map<String, String> params = new HashMap<>();
        Map<String, Object> params2 = new HashMap<>();

        params.put("command", "" + Urls.URL_DELETE_FAVORITE);
        params.put("sessionId", "" + CrewChatApplication.getInstance().getmPrefs().getaccesstoken());
        params.put("languageCode", Locale.getDefault().getLanguage().toUpperCase());
        params.put("timeZoneOffset", TimeUtils.getTimezoneOffsetInMinutes());

        params2.put("groupNo", groupNo);
        params2.put("userNo", UserNo);

        Gson gson = new Gson();
        String js = gson.toJson(params2);

        params.put("reqJson", js);

        WebServiceManager webServiceManager = new WebServiceManager();
        webServiceManager.doJsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new WebServiceManager.RequestListener<String>() {
            @Override
            public void onSuccess(String response) {
                if (baseHTTPCallBack != null)
                    baseHTTPCallBack.onHTTPSuccess(response);
            }

            @Override
            public void onFailure(ErrorDto error) {
                if (baseHTTPCallBack != null)
                    baseHTTPCallBack.onHTTPFail(error);
            }
        });
    }

    /*
    * Function to get all favorite group and data
    * */

    public void getFavotiteGroupAndData(final BaseHTTPCallbackWithJson baseHTTPCallBack) {

        String url = root_link + Urls.URL_ROOT_2;
        Map<String, String> params = new HashMap<>();

        params.put("command", "" + Urls.URL_GET_FAVORITE_GROUP_AND_DATA);
        params.put("sessionId", "" + CrewChatApplication.getInstance().getmPrefs().getaccesstoken());
        params.put("languageCode", Locale.getDefault().getLanguage().toUpperCase());
        params.put("timeZoneOffset", TimeUtils.getTimezoneOffsetInMinutes());


        params.put("reqJson", "");
        WebServiceManager webServiceManager = new WebServiceManager();
        webServiceManager.doJsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new WebServiceManager.RequestListener<String>() {
            @Override
            public void onSuccess(String response) {
                if (baseHTTPCallBack != null)
                    baseHTTPCallBack.onHTTPSuccess(response);
            }

            @Override
            public void onFailure(ErrorDto error) {
                if (baseHTTPCallBack != null)
                    baseHTTPCallBack.onHTTPFail(error);
            }
        });
    }

    /* Function to get all favorite group and data
    * */

    public void getFavotiteTopGroupAndData(final BaseHTTPCallbackWithJson baseHTTPCallBack) {

        String url = root_link + Urls.URL_ROOT_2;
        Map<String, String> params = new HashMap<>();

        params.put("command", "" + Urls.URL_GET_TOP_FAVORITE_GROUP_AND_DATA);
        params.put("sessionId", "" + CrewChatApplication.getInstance().getmPrefs().getaccesstoken());
        params.put("languageCode", Locale.getDefault().getLanguage().toUpperCase());
        params.put("timeZoneOffset", TimeUtils.getTimezoneOffsetInMinutes());


        params.put("reqJson", "");
        WebServiceManager webServiceManager = new WebServiceManager();
        webServiceManager.doJsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new WebServiceManager.RequestListener<String>() {
            @Override
            public void onSuccess(String response) {
                if (baseHTTPCallBack != null)
                    baseHTTPCallBack.onHTTPSuccess(response);
            }

            @Override
            public void onFailure(ErrorDto error) {
                if (baseHTTPCallBack != null)
                    baseHTTPCallBack.onHTTPFail(error);
            }
        });
    }

    public void getTopFavotiteGroupAndData(final BaseHTTPCallbackWithJson baseHTTPCallBack) {

        String url = root_link + Urls.URL_ROOT_2;

        Utils.printLogs("Url = "+url);

        Map<String, String> params = new HashMap<>();

        params.put("command", "" + Urls.URL_GET_TOP_FAVORITE_GROUP_AND_DATA);
        params.put("sessionId", "" + CrewChatApplication.getInstance().getmPrefs().getaccesstoken());
        params.put("languageCode", Locale.getDefault().getLanguage().toUpperCase());
        params.put("timeZoneOffset", TimeUtils.getTimezoneOffsetInMinutes());
        params.put("reqJson", "");

        WebServiceManager webServiceManager = new WebServiceManager();
        webServiceManager.doJsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new WebServiceManager.RequestListener<String>() {
            @Override
            public void onSuccess(String response) {
                if (baseHTTPCallBack != null)
                    baseHTTPCallBack.onHTTPSuccess(response);
            }

            @Override
            public void onFailure(ErrorDto error) {
                if (baseHTTPCallBack != null)
                    baseHTTPCallBack.onHTTPFail(error);
            }
        });
    }

    public void getGetFavoriteChatRoom(final OnGetFavoriteChatRoom callback) {

        String url = root_link + Urls.URL_ROOT_2;
        Map<String, String> params = new HashMap<>();

        params.put("command", "" + Urls.URL_GET_FAVORITE_CHAT_ROOM);
        params.put("sessionId", "" + CrewChatApplication.getInstance().getmPrefs().getaccesstoken());
        params.put("languageCode", Locale.getDefault().getLanguage().toUpperCase());
        params.put("timeZoneOffset", TimeUtils.getTimezoneOffsetInMinutes());


        params.put("reqJson", "");
        WebServiceManager webServiceManager = new WebServiceManager();
        webServiceManager.doJsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new WebServiceManager.RequestListener<String>() {
            @Override
            public void onSuccess(String response) {

                Type listType = new TypeToken<List<FavoriteChatRoomDto>>() {}.getType();
                List<FavoriteChatRoomDto> list = new Gson().fromJson(response, listType);

                if (callback != null)
                    callback.OnGetChatRoomSuccess(list);
            }

            @Override
            public void onFailure(ErrorDto error) {
                if (callback != null)
                    callback.OnGetChatRoomFail(error);
            }
        });
    }

    // Insert a new favorite group
    public void insertFavoriteGroup(String groupName, final BaseHTTPCallbackWithJson baseHTTPCallBack) {
        String url = root_link + Urls.URL_ROOT_2;
        Map<String, String> params = new HashMap<>();
        Map<String, Object> params2 = new HashMap<>();
        params.put("command", "" + Urls.URL_INSERT_FAVORITE_GROUP);
        params.put("sessionId", "" + CrewChatApplication.getInstance().getmPrefs().getaccesstoken());
        params.put("languageCode", Locale.getDefault().getLanguage().toUpperCase());
        params.put("timeZoneOffset", TimeUtils.getTimezoneOffsetInMinutes());

        params2.put("groupName", groupName);

        Gson gson = new Gson();
        String js = gson.toJson(params2);
        params.put("reqJson", js);
        WebServiceManager webServiceManager = new WebServiceManager();
        webServiceManager.doJsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new WebServiceManager.RequestListener<String>() {
            @Override
            public void onSuccess(String response) {
                if (baseHTTPCallBack != null)
                    baseHTTPCallBack.onHTTPSuccess(response);
            }

            @Override
            public void onFailure(ErrorDto error) {
                if (baseHTTPCallBack != null)
                    baseHTTPCallBack.onHTTPFail(error);
            }
        });

    }


    // Update name of a favorite group
    public void updateFavoriteGroup(long groupNo, String groupName, int sortNo, final BaseHTTPCallbackWithJson baseHTTPCallBack) {
        String url = root_link + Urls.URL_ROOT_2;
        Map<String, String> params = new HashMap<>();
        Map<String, Object> params2 = new HashMap<>();
        params.put("command", "" + Urls.URL_UPDATE_FAVORITE_GROUP);
        params.put("sessionId", "" + CrewChatApplication.getInstance().getmPrefs().getaccesstoken());
        params.put("languageCode", Locale.getDefault().getLanguage().toUpperCase());
        params.put("timeZoneOffset", TimeUtils.getTimezoneOffsetInMinutes());

        params2.put("groupNo", groupNo);
        params2.put("groupName", groupName);
        params2.put("sortNo", sortNo);

        Gson gson = new Gson();
        String js = gson.toJson(params2);
        params.put("reqJson", js);
        WebServiceManager webServiceManager = new WebServiceManager();
        webServiceManager.doJsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new WebServiceManager.RequestListener<String>() {
            @Override
            public void onSuccess(String response) {
                if (baseHTTPCallBack != null)
                    baseHTTPCallBack.onHTTPSuccess(response);
            }

            @Override
            public void onFailure(ErrorDto error) {
                if (baseHTTPCallBack != null)
                    baseHTTPCallBack.onHTTPFail(error);
            }
        });

    }

    // Delete a favorite group
    public void deleteFavoriteGroup(long groupNo, final BaseHTTPCallBack baseHTTPCallBack) {
        String url = root_link + Urls.URL_ROOT_2;
        Map<String, String> params = new HashMap<>();
        Map<String, Object> params2 = new HashMap<>();
        params.put("command", "" + Urls.URL_DELETE_FAVORITE_GROUP);
        params.put("sessionId", "" + CrewChatApplication.getInstance().getmPrefs().getaccesstoken());
        params.put("languageCode", Locale.getDefault().getLanguage().toUpperCase());
        params.put("timeZoneOffset", TimeUtils.getTimezoneOffsetInMinutes());

        params2.put("groupNo", groupNo);

        Gson gson = new Gson();
        String js = gson.toJson(params2);
        params.put("reqJson", js);
        WebServiceManager webServiceManager = new WebServiceManager();
        webServiceManager.doJsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new WebServiceManager.RequestListener<String>() {
            @Override
            public void onSuccess(String response) {
                if (baseHTTPCallBack != null)
                    baseHTTPCallBack.onHTTPSuccess();
            }

            @Override
            public void onFailure(ErrorDto error) {
                if (baseHTTPCallBack != null)
                    baseHTTPCallBack.onHTTPFail(error);
            }
        });

    }

    // Insert a new favorite group
    public void updateChatRoomNotification(long roomNo,boolean notification, final BaseHTTPCallBack baseHTTPCallBack) {
        String url = root_link + Urls.URL_ROOT_2;
        Map<String, String> params = new HashMap<>();
        Map<String, Object> params2 = new HashMap<>();
        params.put("command", "" + Urls.URL_UPDATE_CHAT_ROOM_NOTIFICATION);
        params.put("sessionId", "" + CrewChatApplication.getInstance().getmPrefs().getaccesstoken());
        params.put("languageCode", Locale.getDefault().getLanguage().toUpperCase());
        params.put("timeZoneOffset", TimeUtils.getTimezoneOffsetInMinutes());

        params2.put("roomNo", roomNo);
        params2.put("notification", notification);

        Gson gson = new Gson();
        String js = gson.toJson(params2);
        params.put("reqJson", js);
        WebServiceManager webServiceManager = new WebServiceManager();
        webServiceManager.doJsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new WebServiceManager.RequestListener<String>() {
            @Override
            public void onSuccess(String response) {
                if (baseHTTPCallBack != null)
                    baseHTTPCallBack.onHTTPSuccess();
            }

            @Override
            public void onFailure(ErrorDto error) {
                if (baseHTTPCallBack != null)
                    baseHTTPCallBack.onHTTPFail(error);
            }
        });

    }

    public void signUp(final BaseHTTPCallBackWithString baseHTTPCallBack, final String email) {
        final String url = "http://www.crewcloud.net" + Urls.URL_SIGN_UP;
        Map<String, String> params = new HashMap<>();
        params.put("languageCode", Locale.getDefault().getLanguage().toUpperCase());
        params.put("mailAddress", "" + email);
        WebServiceManager webServiceManager = new WebServiceManager();
        webServiceManager.doJsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new WebServiceManager.RequestListener<String>() {
            @Override
            public void onSuccess(String response) {
                Gson gson = new Gson();
                MessageDto messageDto = gson.fromJson(response, MessageDto.class);

                if (baseHTTPCallBack != null && messageDto != null) {
                    String message = messageDto.getMessage();
                    baseHTTPCallBack.onHTTPSuccess(message);
                }
            }

            @Override
            public void onFailure(ErrorDto error) {
                if (baseHTTPCallBack != null) {
                    baseHTTPCallBack.onHTTPFail(error);
                }
            }
        });
    }

    public void InsertDevice(String deviceId, final BaseHTTPCallBack baseHTTPCallBack) {
        String url = root_link + Urls.URL_ROOT_2;
        Map<String, String> params = new HashMap<>();
        Map<String, Object> params2 = new HashMap<>();
        params.put("command", "" + Urls.URL_INSERT_DEVICE);
        params.put("sessionId", "" + CrewChatApplication.getInstance().getmPrefs().getaccesstoken());
        params.put("languageCode", Locale.getDefault().getLanguage().toUpperCase());
        params.put("timeZoneOffset", TimeUtils.getTimezoneOffsetInMinutes());

        params2.put("DeviceType", Statics.DEVICE_TYPE);
        params2.put("DeviceID", deviceId);

        boolean isEnableN = prefs.getBooleanValue(Statics.ENABLE_NOTIFICATION, false);
        boolean isEnableSound = prefs.getBooleanValue(Statics.ENABLE_SOUND, false);
        boolean isEnableVibrate = prefs.getBooleanValue(Statics.ENABLE_VIBRATE, false);
        boolean isEnableTime = prefs.getBooleanValue(Statics.ENABLE_TIME, false);
        boolean isEnableNotificationWhenUsingPcVersion = prefs.getBooleanValue(Statics.ENABLE_NOTIFICATION_WHEN_USING_PC_VERSION, false);


        int start_hour = prefs.getIntValue(Statics.START_NOTIFICATION_HOUR, Statics.DEFAULT_START_NOTIFICATION_TIME);
        int start_minutes = prefs.getIntValue(Statics.START_NOTIFICATION_MINUTES, 0);
        int end_hour = prefs.getIntValue(Statics.END_NOTIFICATION_HOUR, Statics.DEFAULT_END_NOTIFICATION_TIME);
        int end_minutes = prefs.getIntValue(Statics.END_NOTIFICATION_MINUTES, 0);


        Map<String, Object> notificationParams = new HashMap<>();
        notificationParams.put("enabled", isEnableN);
        notificationParams.put("sound", isEnableSound);
        notificationParams.put("vibrate", isEnableVibrate);
        notificationParams.put("notitime", isEnableTime);
        notificationParams.put("starttime", TimeUtils.timeToStringNotAMPM(start_hour, start_minutes));
        notificationParams.put("endtime", TimeUtils.timeToStringNotAMPM(end_hour, end_minutes));
        notificationParams.put("confirmonline", isEnableNotificationWhenUsingPcVersion);

        Gson gson = new Gson();
        params2.put("NotifcationOptions", notificationParams);
        String js = gson.toJson(params2);
        params.put("reqJson", js);

        WebServiceManager webServiceManager = new WebServiceManager();
        webServiceManager.doJsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new WebServiceManager.RequestListener<String>() {
            @Override
            public void onSuccess(String response) {
                if (baseHTTPCallBack != null)
                    baseHTTPCallBack.onHTTPSuccess();
            }

            @Override
            public void onFailure(ErrorDto error) {
                if (baseHTTPCallBack != null)
                    baseHTTPCallBack.onHTTPFail(error);
            }
        });

    }
    public void DeleteDevice(String deviceId, final BaseHTTPCallBack baseHTTPCallBack) {
        String url = root_link + Urls.URL_ROOT_2;
        Map<String, String> params = new HashMap<>();
        Map<String, Object> params2 = new HashMap<>();
        params.put("command", "" + Urls.URL_DELETE_DEVICE);
        params.put("sessionId", "" + CrewChatApplication.getInstance().getmPrefs().getaccesstoken());
        params.put("languageCode", Locale.getDefault().getLanguage().toUpperCase());
        params.put("timeZoneOffset", TimeUtils.getTimezoneOffsetInMinutes());
        params2.put("DeviceType", "Android");
        params2.put("DeviceID", deviceId);
        Gson gson = new Gson();
        String js = gson.toJson(params2);
        params.put("reqJson", js);
        WebServiceManager webServiceManager = new WebServiceManager();
        webServiceManager.doJsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new WebServiceManager.RequestListener<String>() {
            @Override
            public void onSuccess(String response) {
                if (baseHTTPCallBack != null)
                    baseHTTPCallBack.onHTTPSuccess();
            }

            @Override
            public void onFailure(ErrorDto error) {
                if (baseHTTPCallBack != null)
                    baseHTTPCallBack.onHTTPFail(error);
            }
        });
    }

    public void updateChatRoomInfo(int roomNo, String roomTitle, final BaseHTTPCallBack baseHTTPCallBack) {
        String url = root_link + Urls.URL_ROOT_2;
        Map<String, Object> params = new HashMap<>();
        Map<String, Object> params2 = new HashMap<>();
        params.put("command", "" + Urls.URL_UPDATE_ROOM_NO);
        params.put("sessionId", "" + CrewChatApplication.getInstance().getmPrefs().getaccesstoken());
        params.put("languageCode", Locale.getDefault().getLanguage().toUpperCase());
        params.put("timeZoneOffset", TimeUtils.getTimezoneOffsetInMinutes());

        params2.put("roomNo", roomNo);
        params2.put("roomTitle", roomTitle);

        Gson gson = new Gson();
        String js = gson.toJson(params2);

        params.put("reqJson", js);

        WebServiceManager webServiceManager = new WebServiceManager();
        webServiceManager.doJsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new WebServiceManager.RequestListener<String>() {
            @Override
            public void onSuccess(String response) {
                if (baseHTTPCallBack != null)
                    baseHTTPCallBack.onHTTPSuccess();
            }

            @Override
            public void onFailure(ErrorDto error) {
                if (baseHTTPCallBack != null)
                    baseHTTPCallBack.onHTTPFail(error);
            }
        });
    }

    public void addRoomToFavorite(long roomNo, final BaseHTTPCallBack baseHTTPCallBack) {
        String url = root_link + Urls.URL_ROOT_2;
        Map<String, Object> params = new HashMap<>();
        Map<String, Object> params2 = new HashMap<>();
        params.put("command", "" + Urls.URL_INSERT_FAVORITE_CHAT_ROOM);
        params.put("sessionId", "" + CrewChatApplication.getInstance().getmPrefs().getaccesstoken());
        params.put("languageCode", Locale.getDefault().getLanguage().toUpperCase());
        params.put("timeZoneOffset", TimeUtils.getTimezoneOffsetInMinutes());

        params2.put("roomNo", roomNo);

        Gson gson = new Gson();
        String js = gson.toJson(params2);

        params.put("reqJson", js);

        WebServiceManager webServiceManager = new WebServiceManager();
        webServiceManager.doJsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new WebServiceManager.RequestListener<String>() {
            @Override
            public void onSuccess(String response) {
                if (baseHTTPCallBack != null)
                    baseHTTPCallBack.onHTTPSuccess();
            }

            @Override
            public void onFailure(ErrorDto error) {
                if (baseHTTPCallBack != null)
                    baseHTTPCallBack.onHTTPFail(error);
            }
        });
    }

    public void removeFromFavorite(long roomNo, final BaseHTTPCallBack baseHTTPCallBack) {
        String url = root_link + Urls.URL_ROOT_2;
        Map<String, Object> params = new HashMap<>();
        Map<String, Object> params2 = new HashMap<>();
        params.put("command", "" + Urls.URL_DELETE_FAVORITE_CHAT_ROOM);
        params.put("sessionId", "" + CrewChatApplication.getInstance().getmPrefs().getaccesstoken());
        params.put("languageCode", Locale.getDefault().getLanguage().toUpperCase());
        params.put("timeZoneOffset", TimeUtils.getTimezoneOffsetInMinutes());

        params2.put("roomNo", roomNo);

        Gson gson = new Gson();
        String js = gson.toJson(params2);

        params.put("reqJson", js);

        WebServiceManager webServiceManager = new WebServiceManager();
        webServiceManager.doJsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new WebServiceManager.RequestListener<String>() {
            @Override
            public void onSuccess(String response) {
                if (baseHTTPCallBack != null)
                    baseHTTPCallBack.onHTTPSuccess();
            }

            @Override
            public void onFailure(ErrorDto error) {
                if (baseHTTPCallBack != null)
                    baseHTTPCallBack.onHTTPFail(error);
            }
        });
    }


    public void AddChatRoomUser(ArrayList<TreeUserDTO> list, long roomNo, final BaseHTTPCallBack baseHTTPCallBack) {
        String url = root_link + Urls.URL_ROOT_2;
        Map<String, String> params = new HashMap<>();
        Map<String, Object> params2 = new HashMap<>();
        params.put("command", "" + Urls.URL_ADD_USER_CHAT);
        params.put("sessionId", "" + CrewChatApplication.getInstance().getmPrefs().getaccesstoken());
        params.put("languageCode", Locale.getDefault().getLanguage().toUpperCase());
        params.put("timeZoneOffset", TimeUtils.getTimezoneOffsetInMinutes());

        List<Integer> temp = new ArrayList<>();
        for (TreeUserDTO treeUserDTO : list) {
            temp.add(treeUserDTO.getId());
        }
        params2.put("userNos", temp);


        /*String user = "";
        for (TreeUserDTO treeUserDTO : list) {
            if(treeUserDTO.getType()==2)
                user+=treeUserDTO.getId()+",";
        }
        params2.put("userNos",user.substring(0,user.length()-1));*/
        params2.put("roomNo", roomNo);
        Gson gson = new Gson();
        String js = gson.toJson(params2);
        params.put("reqJson", js);
        WebServiceManager webServiceManager = new WebServiceManager();
        webServiceManager.doJsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new WebServiceManager.RequestListener<String>() {
            @Override
            public void onSuccess(String response) {
                if (baseHTTPCallBack != null)
                    baseHTTPCallBack.onHTTPSuccess();
            }

            @Override
            public void onFailure(ErrorDto error) {
                if (baseHTTPCallBack != null)
                    baseHTTPCallBack.onHTTPFail(error);
            }
        });
    }
}
