package com.dazone.crewchat.interfaces;

public interface OAUTHUrls {
    String URL_ROOT = "/UI/WebService/WebServiceCenter.asmx/";
    //String URL_DEFAULT_API = "http://www.crewcloud.net";
    String URL_DEFAULT_API = "";
    String URL_ROOT_DOMAIN = "crewcloud.net";
    String URL_GET_LOGIN = URL_ROOT + "Login";
    String URL_GET_LOGIN_V3 = URL_ROOT + "Login_v3";
    String URL_INSERT_PHONE_TOKEN = URL_DEFAULT_API+ "/UI/WebService/WebServiceCenter.asmx/AddPhoneTokens";
    String URL_CHECK_SESSION = URL_ROOT + "CheckSessionUser";
    String URL_CHECK_DEVICE_TOKEN =URL_DEFAULT_API+ "/UI/WebService/WebServiceCenter.asmx/CheckPhoneToken";
    String URL_LOG_OUT = URL_ROOT + "LogOutUser";
    String URL_GET_DOMAIN = URL_DEFAULT_API+ "/UI/WebService/WebServiceCenter.asmx/GetListDomain";
    String URL_GET_USER_DETAIL = URL_ROOT + "GetUser";
}