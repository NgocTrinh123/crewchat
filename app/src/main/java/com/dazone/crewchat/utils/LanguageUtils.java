package com.dazone.crewchat.utils;

import java.util.Locale;

/**
 * Created by david on 12/30/15.
 */
public class LanguageUtils {

    public static String getPhoneLanguage()
    {
        return Locale.getDefault().getLanguage();
    }

    public static boolean isPhoneLanguageEN()
    {
        return Locale.getDefault().getLanguage().equalsIgnoreCase("EN");
    }
    public static boolean isPhoneLanguageVN()
    {
        return Locale.getDefault().getLanguage().equalsIgnoreCase("VN");
    }
    public static boolean isPhoneLanguageKO()
    {
        return Locale.getDefault().getLanguage().equalsIgnoreCase("KO");
    }
}
