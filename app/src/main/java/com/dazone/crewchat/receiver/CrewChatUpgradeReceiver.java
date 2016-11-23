package com.dazone.crewchat.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

/**
 * Created by Admin on 5/19/2016.
 * This receiver to listen app upgrade then set sharedreference
 */
public class CrewChatUpgradeReceiver extends BroadcastReceiver{
    private String tag = "CrewChatUpgradeReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        Uri packageName = intent.getData();
        if(packageName.toString().equals("package:" + context.getPackageName())){
            //Application was upgraded
            Log.e(tag, "This package "+context.getPackageName() + " is upgrade");
        }
    }
}
