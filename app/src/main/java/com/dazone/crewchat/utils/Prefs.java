package com.dazone.crewchat.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.dazone.crewchat.constant.Statics;

import java.io.Serializable;

public class Prefs implements Serializable {

    private SharedPreferences prefs;

    private final String SHAREDPREFERENCES_NAME = "oathsharedpreferences";
    private final String ACCESSTOKEN = "accesstoken";
    private final String SERVERSITE = "serversite";
    private final String USER_NAME = "username";
    private final String FULL_NAME = "full_name";
    private final String EMAIL = "email";
    private final String COMPANY_NAME = "company_name";
    private final String COMPANY_NO = "company_no";
    private final String USERNO = "user_no";
    private final String INTRO_COUNT = "introcount";
    private final String AVATAR_URL = "avatar_url";

    private static final String PREF_FLAG_GMC_ID = "flag_gmc_id_new";


    public Prefs() {
        prefs = CrewChatApplication.getInstance().getApplicationContext().
                getSharedPreferences(SHAREDPREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    public boolean isContainKey(String key) {
        return prefs.contains(key);
    }

    public void putServerSite(String serversite) {
        putStringValue(SERVERSITE, serversite);
    }

    public String getServerSite() {
        return getStringValue(SERVERSITE, "");
    }

    public void putUserName(String username) {
        putStringValue(USER_NAME, username);
    }

    public void setAvatarUrl(String url) {
        putStringValue(AVATAR_URL, url);
    }

    public String getAvatarUrl() {
        return getStringValue(AVATAR_URL, "");
    }

    public void putCompanyName(String companyName) {
        putStringValue(COMPANY_NAME, companyName);
    }

    public String getCompanyName() {
        return getStringValue(COMPANY_NAME, "");
    }

    public void putCompanyNo(int companyNo) {
        putIntValue(COMPANY_NO, companyNo);
    }

    public int getCompanyNo() {
        return getIntValue(COMPANY_NO, 1);
    }

    public void setFullName(String fullName) {
        putStringValue(FULL_NAME, fullName);
    }

    public String getFullName() {
        return getStringValue(FULL_NAME, "");
    }

    public String getUserName() {
        return getStringValue(USER_NAME, "");
    }

    public void putaccesstoken(String accesstoken) {
        putStringValue(ACCESSTOKEN, accesstoken);
    }

    public void putUserNo(int userNo) {
        putIntValue(USERNO, userNo);
    }

    public int getUserNo() {
        return getIntValue(USERNO, -1);
    }

    public void putScaleImageMode(int mode) {
        putIntValue(Statics.IMAGE_SIZE_MODE, mode);
    }

    public int getScaleImageMode() {
        return getIntValue(Statics.IMAGE_SIZE_MODE, Statics.MODE_ORIGINAL);
    }

    public String getaccesstoken() {
        return getStringValue(ACCESSTOKEN, "");
    }

    public void putBooleanValue(String KEY, boolean value) {
        prefs.edit().putBoolean(KEY, value).apply();
    }

    public boolean getBooleanValue(String KEY, boolean defvalue) {
        return prefs.getBoolean(KEY, defvalue);
    }

    public void putStringValue(String KEY, String value) {
        prefs.edit().putString(KEY, value).apply();
    }

    public String getStringValue(String KEY, String defvalue) {
        return prefs.getString(KEY, defvalue);
    }

    public void putIntValue(String KEY, int value) {
        prefs.edit().putInt(KEY, value).apply();
    }

    public int getIntValue(String KEY, int defvalue) {
        return prefs.getInt(KEY, defvalue);
    }

    public void putLongValue(String KEY, long value) {
        prefs.edit().putLong(KEY, value).apply();
    }

    public long getLongValue(String KEY, long defvalue) {
        return prefs.getLong(KEY, defvalue);
    }

    public void putFloatValue(String KEY, float value) {
        prefs.edit().putFloat(KEY, value).apply();
    }

    public void putintrocount(int introcount) {
        putIntValue(INTRO_COUNT, introcount);
    }

    public int getintrocount() {
        return getIntValue(INTRO_COUNT, 0);
    }

    public float getFloatValue(String KEY, float defvalue) {
        return prefs.getFloat(KEY, defvalue);
    }

    public void removeValue(String KEY) {
        prefs.edit().remove(KEY).apply();
    }

    public void clear() {
        prefs.edit().clear().apply();
    }

    public void clearLogin() {
        prefs.edit().remove(ACCESSTOKEN).apply();
    }

    //gmc
    public String getGCMregistrationid() {
        return getStringValue(PREF_FLAG_GMC_ID, "");
    }

    public void setGCMregistrationid(String value) {
        putStringValue(PREF_FLAG_GMC_ID, value);
    }

    public void putEmail(String mailAddress) {
        putStringValue(EMAIL, mailAddress);
    }

    public String getEmail() {
        return getStringValue(EMAIL, "");
    }
}
