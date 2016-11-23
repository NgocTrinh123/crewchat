package com.dazone.crewchat.utils;

/**
 * Created by david on 12/23/15.
 */
public class ChatTextUtils {
    public static String getFirstLetter(String string){
        if(!android.text.TextUtils.isEmpty(string)){
            String[] temp = string.split(" ");
            if(temp.length>1)
            {
                return temp[0].substring(0, 1).toUpperCase()+temp[temp.length-1].substring(0, 1).toUpperCase();
            }
            else {
                return temp[0].substring(0, 1).toUpperCase();
            }
        }else{
            return "";
        }
    }
}
