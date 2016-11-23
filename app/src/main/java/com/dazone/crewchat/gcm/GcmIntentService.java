package com.dazone.crewchat.gcm;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import com.dazone.crewchat.R;
import com.dazone.crewchat.activity.ChattingActivity;
import com.dazone.crewchat.constant.Statics;
import com.dazone.crewchat.database.AllUserDBHelper;
import com.dazone.crewchat.database.ChatMessageDBHelper;
import com.dazone.crewchat.database.ChatRomDBHelper;
import com.dazone.crewchat.database.UserDBHelper;
import com.dazone.crewchat.dto.AttachDTO;
import com.dazone.crewchat.dto.ChattingDto;
import com.dazone.crewchat.dto.NotificationBundleDto;
import com.dazone.crewchat.dto.TreeUserDTOTemp;
import com.dazone.crewchat.fragment.ChattingFragment;
import com.dazone.crewchat.fragment.CurrentChatListFragment;
import com.dazone.crewchat.utils.*;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import me.leolin.shortcutbadger.ShortcutBadger;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Calendar;

public class GcmIntentService extends IntentService {

    public GcmIntentService() {
        super("GcmIntentService");
    }

    private int Code = 0;
    private static int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    ArrayList<TreeUserDTOTemp> listTemp = AllUserDBHelper.getUser();
    int current_user_status = UserDBHelper.getUser().getStatus();
    ChattingDto chattingDto;
    private Prefs prefs;
    //public static NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this.getApplicationContext());
    private NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
    boolean isEnableN, isEnableSound, isEnableVibrate, isEnableTime, isPCVersion;

    private boolean timeAvaiable(){

        boolean isTimeEnable = prefs.getBooleanValue(Statics.ENABLE_TIME, false);
        if(!isTimeEnable){ // Check is enable notification time
            return true; // if check time is disable. the condition always true
        }

        Calendar calendar = Calendar.getInstance();
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        int currentMinute= calendar.get(Calendar.MINUTE);

        int start_hour = prefs.getIntValue(Statics.START_NOTIFICATION_HOUR, Statics.DEFAULT_START_NOTIFICATION_TIME);
        int start_minutes = prefs.getIntValue(Statics.START_NOTIFICATION_MINUTES, 0);
        int end_hour = prefs.getIntValue(Statics.END_NOTIFICATION_HOUR, Statics.DEFAULT_END_NOTIFICATION_TIME);
        int end_minutes = prefs.getIntValue(Statics.END_NOTIFICATION_MINUTES, 0);

        boolean isBetween = (currentHour > start_hour) && (currentHour < end_hour);
        boolean isLeft = (currentHour == start_hour) && (currentMinute > start_minutes);
        boolean isRight = (currentHour == end_hour) && (currentMinute < end_minutes);
        if (isBetween || isLeft || isRight){
            return true;
        }

        return false;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);
        prefs = CrewChatApplication.getInstance().getmPrefs();

        isEnableN = prefs.getBooleanValue(Statics.ENABLE_NOTIFICATION, false);
        isEnableSound = prefs.getBooleanValue(Statics.ENABLE_SOUND, false);
        isEnableVibrate = prefs.getBooleanValue(Statics.ENABLE_VIBRATE, false);
        isEnableTime = prefs.getBooleanValue(Statics.ENABLE_TIME, false);
        isPCVersion = prefs.getBooleanValue(Statics.ENABLE_NOTIFICATION_WHEN_USING_PC_VERSION, false);

        if (!extras.isEmpty()) { // Check enable notification and current time avaiable [on time table]
            if (GoogleCloudMessaging.
                    MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
//                sendNotification("Send error",extras.toString());
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_DELETED.equals(messageType)) {
//                sendNotification("Deleted messages on server ",
//                        extras.toString());
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                Utils.printLogs(extras.toString());

                if (extras.containsKey("Code")) {

                    Utils.printLogs("Bundle received ##############");

                    Code = Integer.parseInt(extras.getString("Code", "0"));
                    Utils.printLogs("Notification code = "+Code);
                    Log.e("TAG", "After notification code");
                    switch (Code) {
                        case 1:
                            Log.e("TAG", "Case 1 ###");
                            receiveCode1(extras);
                            break;
                        case 2:
                            Log.e("TAG", "Case 2 ###");
                            receiveCode2(extras);
                            break;
                        case 3:
                            Log.e("TAG", "Case 3 ###");
                            chatDeleteMember(extras);
                            break;
                        case 4:
                            Log.e("TAG", "Case 4 ###");
                            break;
                        case 5:
                            Log.e("TAG", "Case 5 ###");
                            receiveCode5(extras);
                            break;
                        default:
                            Log.e("TAG", "Case 0 ###");
                            break;
                    }
                }
            }
        }
        GcmBroadcastReceiver.completeWakefulIntent(intent);
        NOTIFICATION_ID = NOTIFICATION_ID + 1;
    }

    /**
     * RECEIVE CODE 1
     */

    private void receiveCode1(Bundle extras) {
        if (extras.containsKey("Data")) {

            long userNo = Long.parseLong(extras.getString("UserNo", "0"));
            if (userNo == Utils.getCurrentId()) {
                try {

                    NotificationBundleDto bundleDto = new Gson().fromJson(extras.getString("Data"), NotificationBundleDto.class);

                    chattingDto = new ChattingDto();
                    chattingDto.setRoomNo(bundleDto.getRoomNo());
                    chattingDto.setUnreadTotalCount(bundleDto.getUnreadTotalCount());
                    chattingDto.setMessage(bundleDto.getMessage());
                    chattingDto.setMessageNo(bundleDto.getMessageNo());
                    chattingDto.setWriterUserNo(bundleDto.getWriteUserNo());

                    chattingDto.setAttachNo(bundleDto.getAttachNo());
                    chattingDto.setAttachFileName(bundleDto.getAttachFileName());
                    chattingDto.setAttachFileType(bundleDto.getAttachFileType());
                    chattingDto.setAttachFilePath(bundleDto.getAttachFilePath());
                    chattingDto.setAttachFileSize(bundleDto.getAttachFileSize());



                    AttachDTO attachInfo = new AttachDTO();
                    attachInfo.setType(bundleDto.getAttachFileType());
                    attachInfo.setAttachNo(bundleDto.getAttachNo());
                    attachInfo.setSize(bundleDto.getAttachFileSize());
                    attachInfo.setFullPath(bundleDto.getAttachFilePath());

                    chattingDto.setAttachInfo(attachInfo);
                    chattingDto.setLastedMsg(bundleDto.getMessage());
                    chattingDto.setMsgUserNo(bundleDto.getWriteUserNo());
                    chattingDto.setWriterUser(bundleDto.getWriteUserNo());



                    if(TextUtils.isEmpty(bundleDto.getAttachFilePath())){
                         chattingDto.setLastedMsgType(Statics.MESSAGE_TYPE_NORMAL);
                        chattingDto.setType(0);
                    } else {
                         chattingDto.setLastedMsgType(Statics.MESSAGE_TYPE_ATTACH);
                        chattingDto.setType(2);
                    }


                    chattingDto.setLastedMsgAttachType(bundleDto.getAttachFileType());



                    final long roomNo = chattingDto.getRoomNo();

                    final long unreadCount = bundleDto.getUnreadTotalCount();
                    // Update unreadTotalCount to database in new thread, hihi

                    ShortcutBadger.applyCount(this, (int) unreadCount); //for 1.1.4

                    String currentTime = System.currentTimeMillis() + "";
                    chattingDto.setRegDate(currentTime);
                    chattingDto.setLastedMsgDate(currentTime);


                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            ChatRomDBHelper.updateUnreadTotalCountChatRoom(roomNo, unreadCount);
                            ChatRomDBHelper.updateChatRoom(chattingDto.getRoomNo(), chattingDto.getLastedMsg(), chattingDto.getLastedMsgType(), chattingDto.getLastedMsgAttachType(), chattingDto.getLastedMsgDate(), chattingDto.getUnreadTotalCount(), chattingDto.getUnReadCount(), chattingDto.getWriterUserNo());
                        }
                    }).start();


                    // When user receive a notification we will store in to database
                    // If chatting Fragment is visible then store this message to database, else get from ChattingFragment
                    if (ChattingFragment.instance != null) {
                        if (ChattingFragment.instance.roomNo == roomNo && ChattingFragment.instance.isVisible) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    ChatMessageDBHelper.addMessage(chattingDto);
                                }
                            }).start();
                        }
                    }


                    if (chattingDto.getWriterUserNo() != Utils.getCurrentId()) {
                        Intent myIntent = new Intent(this, ChattingActivity.class);
                        myIntent.putExtra(Statics.CHATTING_DTO, chattingDto);
                        myIntent.putExtra(Constant.KEY_INTENT_ROOM_NO,roomNo);
                        TreeUserDTOTemp treeUserDTOTemp = Utils.GetUserFromDatabase(listTemp, chattingDto.getWriterUserNo());
                        //if (!CrewChatApplication.isActivityVisible()) {
                        Utils.printLogs("Bundle is show notification = "+bundleDto.isShowNotification());
                        boolean isShowNotification = bundleDto.isShowNotification();
                        if (roomNo != CrewChatApplication.currentRoomNo) {
                            if (treeUserDTOTemp != null) {
                                if (isShowNotification){

                                    String url = "";
                                    for (TreeUserDTOTemp u : listTemp){
                                        if (u.getUserNo() == chattingDto.getWriterUserNo()){
                                            url = new Prefs().getServerSite() + u.getAvatarUrl();
                                            break;
                                        }
                                    }
                                    sendNotification(chattingDto.getMessage(), treeUserDTOTemp.getName(), url , myIntent, chattingDto.getUnreadTotalCount(), roomNo);
                                }
                            } else {
                                if (isShowNotification){
                                    sendNotification(chattingDto.getMessage(), "Crew Chat", null , myIntent, chattingDto.getUnreadTotalCount(), roomNo);
                                }
                            }
                        }else{
                            Utils.printLogs("Rom number == current ");
                        }

                    }else{
                        Utils.printLogs("Writer user id == Current user id");
                    }

                    if (CurrentChatListFragment.fragment != null) {
                        if (ChattingFragment.instance != null && ChattingFragment.instance.isVisible && ChattingFragment.instance.roomNo == roomNo) {
                            Utils.printLogs("Chatting is visible");

                            chattingDto.setLastedMsgDate(TimeUtils.convertTimeDeviceToTimeServer(chattingDto.getRegDate()));

                            CurrentChatListFragment.fragment.isUpdate = true;
                            CurrentChatListFragment.fragment.updateDataSet(chattingDto);
                        } else {
                            chattingDto.setLastedMsgDate(TimeUtils.convertTimeDeviceToTimeServer(chattingDto.getRegDate()));
                            CurrentChatListFragment.fragment.isUpdate = true;
                            CurrentChatListFragment.fragment.updateDataSet(chattingDto);
                        }

                    }
                    if (ChattingFragment.instance != null) {
                        if (ChattingFragment.instance.roomNo == roomNo && !ChattingFragment.instance.isVisible) {
                            ChattingFragment.instance.isUpdate = true;
                            // Check store data TH nay
                            Utils.printLogs("On receve update from GcmIntentService #####");
                            ChattingFragment.instance.updateData(chattingDto);
                        }
                    }

                    // Just send notification
                    sendBroadcastToActivity(chattingDto, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * RECEIVE CODE 2
     */
    private void receiveCode2(Bundle bundle) {
        try {
            if (bundle.containsKey("UserNo")) {
                /** GET UserNo*/
                int userNo = Integer.parseInt(bundle.getString("UserNo", "0"));
                if (userNo == Utils.getCurrentId()) {
                    if (bundle.containsKey("Data")) {
                        /** GET RoomNo */
                        long roomNo = Long.parseLong(bundle.getString("Data", "0"));

                        /** Set Intent */
                        Intent intent = new Intent(this, ChattingActivity.class);
                        intent.putExtra(Constant.KEY_INTENT_ROOM_NO, roomNo);

                        /** Notification */
                        sendNotification(Utils.getString(R.string.notification_add_user),
                                Utils.getString(R.string.app_name),
                                null,
                                intent,
                                0,
                                roomNo);



                        /** Send Broadcast */
                        if ((CurrentChatListFragment.fragment != null && CurrentChatListFragment.fragment.isActive)||(ChattingFragment.instance !=null&& ChattingFragment.instance.isActive)) {
                            Intent intentBroadcast = new Intent(Constant.INTENT_FILTER_ADD_USER);
                            intentBroadcast.putExtra(Constant.KEY_INTENT_ROOM_NO, roomNo);
                            sendBroadcast(intentBroadcast);
                        }
                        else if(CurrentChatListFragment.fragment!=null)
                        {
                            CurrentChatListFragment.fragment.isUpdate = true;
                            CurrentChatListFragment.fragment.reloadDataSet();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Utils.printLogs("ERROR " + e.toString());
        }
    }

    /*
    * CHAT DELETE MEMBER CODE = 3
    * */

    private void chatDeleteMember(Bundle extras){
        if (extras.containsKey("Data")) {
            try {
                /** Get RoomNo */
                String objExtra = extras.getString("Data", "");
                JSONObject object = new JSONObject(objExtra);

                long roomNo = Long.parseLong(object.getString("RoomNo"));
                /** Send Broadcast */
                Intent intent = new Intent(Constant.INTENT_FILTER_CHAT_DELETE_USER);
                intent.putExtra(Constant.KEY_INTENT_ROOM_NO, roomNo);
                sendBroadcast(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * RECEIVE CODE 5
     */
    private void receiveCode5(Bundle extras) {
        if (extras.containsKey("Data")) {
            try {
                /** Get RoomNo */
                String objExtra = extras.getString("Data", "");
                JSONObject object = new JSONObject(objExtra);

                long userNo = 0;
                if (extras.containsKey("UserNo")){
                    userNo = Long.parseLong(extras.getString("UserNo", "0"));
                }


                final long roomNo = object.getLong("RoomNo");
                final int unReadTotalCount = object.getInt("UnreadTotalCount");


                Utils.printLogs("## roomNo ="+roomNo+" unread count ="+unReadTotalCount);

                // Update unreadTotalCount to database in new thread, hihi
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ChatRomDBHelper.updateUnreadTotalCountChatRoom(roomNo, unReadTotalCount);
                    }
                }).start();

                ShortcutBadger.applyCount(this, (int) unReadTotalCount); //for 1.1.4

                Utils.printLogs("Unread count from receiver code 5");
                // Send Broadcast


                Intent intent = new Intent(Constant.INTENT_FILTER_GET_MESSAGE_UNREAD_COUNT);
                intent.putExtra(Constant.KEY_INTENT_ROOM_NO, roomNo);
                intent.putExtra(Constant.KEY_INTENT_UNREAD_TOTAL_COUNT, unReadTotalCount);
                intent.putExtra(Constant.KEY_INTENT_USER_NO, userNo);
                sendBroadcast(intent);

            } catch (Exception e) {
                e.printStackTrace();
                Utils.printLogs("Catch on receive code 5");
            }


        }
    }

    /*private void sendNotification(String txt, String title) {
        int icon_id;

        PendingIntent contentIntent;

        Intent intent = new Intent(this, MainActivity.class);
        icon_id = R.drawable.ic_group_white_24dp;
        contentIntent = PendingIntent.getActivity(this, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManager mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(icon_id)
                        .setContentTitle(title)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(txt))
                        .setContentText(txt);
        mBuilder.setAutoCancel(true);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(0, mBuilder.build());
    }*/

    private void sendNotification(String msg, String title, String avatarUrl , Intent myIntent, int unReadCount, long roomNo) {

        // define sound URI, the sound to be played when there's a notification
        //Uri soundUri = Uri.parse("android.resource://"+this.getPackageName()+"/" +R.raw.notification);
        long[] vibrate = new long[]{1000, 1000, 0, 0, 0};
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);
/*        myintent.putExtra("MESSAGE", link);
        myintent.putExtra("FLAG","1");*/
        myIntent.putExtra(Statics.CHATTING_DTO, chattingDto);
        myIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        if (chattingDto != null && chattingDto.getAttachNo() != 0) {
            msg = Utils.getString(R.string.notification_file) + chattingDto.getAttachFileName();
            chattingDto.setType(2);
            chattingDto.setAttachInfo(new AttachDTO());
            chattingDto.getAttachInfo().setAttachNo(chattingDto.getAttachNo());
            chattingDto.getAttachInfo().setType(chattingDto.getAttachFileType());
            chattingDto.getAttachInfo().setFullPath(chattingDto.getAttachFilePath());
            chattingDto.getAttachInfo().setFileName(chattingDto.getAttachFileName());
            chattingDto.getAttachInfo().setSize(chattingDto.getAttachFileSize());
        } else {
            if (TextUtils.isEmpty(msg)) {
                msg = Utils.getString(R.string.notification_add_user);
            }
        }

        Bitmap bitmap = null;
        if (avatarUrl != null){
            File file = ImageLoader.getInstance().getDiskCache().get(avatarUrl);
            if (file.exists()){
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                try {
                    bitmap = BitmapFactory.decodeStream(new FileInputStream(file), null, options);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }else{
                bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.chatting);
            }

        }

        mBuilder.setNumber(unReadCount)
                .setSmallIcon(R.drawable.small_icon_chat)
                .setLargeIcon(bitmap)
                .setContentTitle(title)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(msg))
                .setContentText(msg)
                .setAutoCancel(true);
        // Check notification setting and config notification
        if (isEnableSound) mBuilder.setSound(soundUri);
        if (isEnableVibrate) mBuilder.setVibrate(vibrate);

        mBuilder.setContentIntent(contentIntent);

        if (msg.contains("\r\n")) {
            NotificationCompat.BigTextStyle bigTextStyle
                    = new NotificationCompat.BigTextStyle();
            /** STYLE BIG TEXT */
            String bigText = msg.replaceAll("\r\n", "<br/>");
            bigTextStyle.bigText(Html.fromHtml(bigText));
            mBuilder.setStyle(bigTextStyle);
            mBuilder.setContentText(msg.split("\r\n")[0]);
        }

        if ((int) roomNo != (int) CrewChatApplication.currentNotification) {
            CrewChatApplication.currentNotification = roomNo;
            mNotificationManager.cancelAll();
        }
        Notification notification = mBuilder.build();

        //consider using setTicker of Notification.Builder
        notification.number = 100;
        notification.tickerText = getTickerText(unReadCount);
        //mNotificationManager.getCurrentInterruptionFilter();
        //System.out.println("aaaaaaaaaaaaaaa ID " + (int) roomNo + " " + CrewChatApplication.currentNotification);
        mNotificationManager.notify((int) roomNo, notification);
        mNotificationManager.notify((int) roomNo, mBuilder.build());

        /*PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG");
        wl.acquire(15000);
        wl.acquire();*/
    }

    private String getTickerText(int total) {
        String result;
        switch (total) {
            case 1:
                result = total + " New Message";
                break;
            default:
                result = total + " New Messages";
                break;
        }
        return result;
    }

    // called to send data to Activity
    private void sendBroadcastToActivity(ChattingDto dto, boolean isNotify) {
        Intent intent = new Intent(Statics.ACTION_RECEIVER_NOTIFICATION);
        intent.putExtra(Statics.GCM_DATA_NOTIFICATOON, new Gson().toJson(dto));
        intent.putExtra(Statics.GCM_NOTIFY, isNotify);
        sendBroadcast(intent);
    }

    /*private void executeGCM(GCMDto dto,Intent intent)
    {
        switch (BuildConfig.App_name)
        {
            case 0:
                if(dto.type ==1||dto.type==2)
                {
                    HttpRequest.getInstance().checkLogin(null);
                }
                break;
            case 1:
                CrewGCMDatabaseHelper.updateGCMItem(dto);
                if(Global.isAppForeground)
                {
                    sendBroadcastToActivity(dto);
                }
                else
                {
                    new CrewPrefs().putBooleanValue(CrewStatics.RELOAD_MAIN_KEY,true);
                }
                break;
            case 2:
                if(dto.type ==1||dto.type==2)
                {
                    HttpRequest.getInstance().checkLogin(null);
                }
                else {
                    Util.setBadge(DazoneApplication.getInstance(), dto.count);
                }
                break;
            default:
                break;
        }

        if(dto.type!=500&&BuildConfig.App_name!=0) {
            sendNotification(dto);
        }
            GcmBroadcastReceiver.completeWakefulIntent(intent);
    }
    private void sendNotification(GCMDto dto) {
        int icon_id;

        PendingIntent contentIntent;
        switch (BuildConfig.App_name)
        {
            case 0:
                icon_id = R.drawable.notifi_ic_timecard;
                contentIntent = PendingIntent.getActivity(this, 0,
                    new Intent(this, LoginActivity.class), 0);
                break;
            case 1:
                icon_id = R.drawable.notifi_ic_crewcloud;
                contentIntent = PendingIntent.getActivity(this, 0,
                        new Intent(this, LoginActivity.class), 0);
                break;
            default:

                Intent intent = new Intent(this, CrewNoteActivity.class);
                intent.putExtra(Statics.KEY_NOTE_NO, dto.NoteNo);
                intent.putExtra(Statics.KEY_NOTE_TITLE, dto.title);
                icon_id = R.drawable.notifi_ic_crewnote;
                contentIntent = PendingIntent.getActivity(this, 0,
                        intent, PendingIntent.FLAG_UPDATE_CURRENT);
                break;
        }
        NotificationManager mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
        .setSmallIcon(icon_id)
        .setContentTitle(dto.title)
        .setStyle(new NotificationCompat.BigTextStyle()
        .bigText(dto.content))
        .setContentText(dto.content);
        mBuilder.setAutoCancel(true);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(0, mBuilder.build());
    }
    // called to send data to Activity
    private void sendBroadcastToActivity(GCMDto dto) {
        Intent intent = new Intent(Statics.ACTION_RECEIVER_NOTIFICATION);
        intent.putExtra(Statics.GCM_DATA_NOTIFICATOON, new Gson().toJson(dto));
        sendBroadcast(intent);
    }*/
}