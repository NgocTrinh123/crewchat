package com.dazone.crewchat.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.dazone.crewchat.R;
import com.dazone.crewchat.constant.Statics;
import com.dazone.crewchat.customs.AlertDialogView;
import com.dazone.crewchat.database.AllUserDBHelper;
import com.dazone.crewchat.database.UserDBHelper;
import com.dazone.crewchat.dto.ChatRoomDTO;
import com.dazone.crewchat.dto.ChattingDto;
import com.dazone.crewchat.dto.TreeUserDTOTemp;
import com.dazone.crewchat.dto.UserDto;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by david on 12/18/15.
 */
public class Utils {
    /**
     * Check if network available or not
     *
     * @return true-network available; false-network not available
     */
    public static boolean isNetworkAvailable() {
        NetworkInfo networkInfo = getNetworkInfo();
        return (networkInfo != null && networkInfo.isConnectedOrConnecting());
    }

    private static NetworkInfo getNetworkInfo() {
        ConnectivityManager connectivityManager = (ConnectivityManager) CrewChatApplication.getInstance().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo();

    }

    /**
     * Function check wifi enable
     *
     * @return
     */
    public static boolean isWifiEnable() {
        NetworkInfo networkInfo = getNetworkInfo();
        return (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI);
    }

    /**
     * Function print logs as Debug
     *
     * @param logs
     */
    public static void printLogs(String logs) {
        if (logs == null)
            return;
        int maxLogSize = 4000;
        if (logs.length() > maxLogSize) {
            for (int i = 0; i <= logs.length() / maxLogSize; i++) {
                int start = i * maxLogSize;
                int end = (i + 1) * maxLogSize;
                end = end > logs.length() ? logs.length() : end;
                Log.d(Statics.TAG, logs.substring(start, end));
            }
        } else {
            Log.d(Statics.TAG, logs);
        }
    }


    public static String getString(int stringID) {
        return CrewChatApplication.getInstance().getApplicationContext().getResources().getString(stringID);
    }

    public static String getUniqueDeviceId(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
       /*
        * getDeviceId() function Returns the unique device ID.
        * for example,the IMEI for GSM and the MEID or ESN for CDMA phones.
        */
        String deviceId = telephonyManager.getDeviceId();


       /*
        * getSubscriberId() function Returns the unique subscriber ID,
        * for example, the IMSI for a GSM phone.
        */
        if (TextUtils.isEmpty(deviceId)) {
            deviceId = telephonyManager.getSubscriberId();
        }

        /*
        * Settings.Secure.ANDROID_ID returns the unique DeviceID
        * Works for Android 2.2 and above
        */
        if (TextUtils.isEmpty(deviceId)) {
            deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        }

        /*
        * returns the MacAddress
        */
        if (TextUtils.isEmpty(deviceId)) {
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo wInfo = wifiManager.getConnectionInfo();
            deviceId = wInfo.getMacAddress();
        }

        return deviceId;

    }

    public static boolean checkStringValue(String... params) {
        for (String param : params) {
            if (TextUtils.isEmpty(param.trim())) {
                return false;
            }
            if (param.contains("\n") && TextUtils.isEmpty(param.replace("\n", ""))) {
                return false;
            }
        }
        return true;
    }

    public static int getDimenInPx(int id) {
        return (int) CrewChatApplication.getInstance().getApplicationContext().getResources().getDimension(id);
    }

    public static void addFragmentToActivity(FragmentManager fragmentManager, Fragment fragment, int frameLayout, boolean isSaveStack) {
        addFragmentToActivity(fragmentManager, fragment, frameLayout, isSaveStack, null);
    }

    public static void addFragmentToActivity(FragmentManager fragmentManager, Fragment fragment, int frameLayout, boolean isSaveStack, String tag) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (TextUtils.isEmpty(tag)) {
            transaction.add(frameLayout, fragment);
        } else {
            transaction.add(frameLayout, fragment, tag);
        }

        if (isSaveStack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }

    public static void addFragmentNotSuportV4ToActivity(android.app.FragmentManager fragmentManager, android.app.Fragment fragment, int frameLayout, boolean isSaveStack, String tag){
        android.app.FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (TextUtils.isEmpty(tag)) {
            transaction.add(frameLayout, fragment);
        } else {
            transaction.add(frameLayout, fragment, tag);
        }

        if (isSaveStack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }

    public static void replaceFragment(FragmentManager fragmentManager, Fragment fragment, int frameLayout, boolean isSaveStack) {
        replaceFragment(fragmentManager, fragment, frameLayout, isSaveStack, null);
    }

    public static void replaceFragment(FragmentManager fragmentManager, Fragment fragment, int frameLayout, boolean isSaveStack, String tag) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (TextUtils.isEmpty(tag)) {
            transaction.replace(frameLayout, fragment);
        } else {
            transaction.replace(frameLayout, fragment, tag);
        }

        if (isSaveStack) {
            transaction.addToBackStack(null);
        }

        transaction.commit();
    }


    public static void hideKeyboard(Activity activity) {
        try {
            InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    public static String getPathFromURI(Uri contentURI, Context context) {
        String result;
        try {
            Cursor cursor = context.getContentResolver().query(contentURI, null, null, null, null);
            if (cursor == null) { // Source is Dropbox or other similar local file path
                result = contentURI.getPath();
            } else {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                result = cursor.getString(idx);
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            result = "";
        }
        return result;
    }

    public static void showMessage(String message) {
        Toast.makeText(CrewChatApplication.getInstance().getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    public static void showMessageShort(String message) {
        Toast.makeText(CrewChatApplication.getInstance().getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    public static TreeUserDTOTemp GetUserFromDatabase(List<TreeUserDTOTemp> list, int id) {
        for (TreeUserDTOTemp treeUserDTOTemp : list) {
            if (treeUserDTOTemp.getUserNo() == id) {
                return treeUserDTOTemp;
            }
        }
        return null;
    }

    public static TreeUserDTOTemp GetUserFromDatabase(ArrayList<TreeUserDTOTemp> list, int id) {
        for (TreeUserDTOTemp treeUserDTOTemp : list) {
            if (treeUserDTOTemp.getUserNo() == id) {
                return treeUserDTOTemp;
            }
        }
        return null;
    }

    public static void displaySingleChoiceList(final Context context, List<String> itemList, DialogInterface.OnClickListener listener, String title) {
        final AlertDialog.Builder adb = new AlertDialog.Builder(context).setTitle(title);
        ArrayAdapter<String> adapterspiner = new ArrayAdapter<>(context, android.R.layout.select_dialog_item, itemList);
        adb.setAdapter(adapterspiner, listener);
        adb.show();
    }

    public static long getFileSize(String realPath) {
        long sizeOfInputStram = 0;
        try {
            InputStream is = new FileInputStream(realPath);
            sizeOfInputStram = is.available();
        } catch (Exception e) {
            sizeOfInputStram = 0;
        }
        return sizeOfInputStram;
    }

    public static String readableFileSize(long size) {
        if (size <= 0) return "0";
        final String[] units = new String[]{"B", "kB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    public static void displayoDownloadFileDialog(final Context context, final String url, final String name) {
        try {
            AlertDialogView.normalAlertDialogWithCancel(context, Utils.getString(R.string.app_name), Utils.getString(R.string.notice_download),
                    Utils.getString(R.string.no), Utils.getString(R.string.yes), new AlertDialogView.OnAlertDialogViewClickEvent() {
                        @Override
                        public void onOkClick(DialogInterface alertDialog) {
                            String mimeType;
                            String serviceString = Context.DOWNLOAD_SERVICE;
                            String fileType = name.substring(name.lastIndexOf(".")).toLowerCase();
                            final DownloadManager downloadmanager;
                            downloadmanager = (DownloadManager) context.getSystemService(serviceString);
                            Uri uri = Uri
                                    .parse(url);

                            DownloadManager.Request request = new DownloadManager.Request(uri);
                            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                            request.setDestinationInExternalPublicDir(Constant.pathDownload, name);
                            //request.setTitle(name);
                            int type = getTypeFile(fileType);
                            switch (type) {
                                case 1:
                                    request.setMimeType(Statics.MIME_TYPE_IMAGE);
                                    break;
                                case 2:
                                    request.setMimeType(Statics.MIME_TYPE_VIDEO);
                                    break;
                                case 3:
                                    request.setMimeType(Statics.MIME_TYPE_AUDIO);
                                    break;
                                default:
                                    try {
                                        mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(url));
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        mimeType = Statics.MIME_TYPE_ALL;
                                    }
                                    if (TextUtils.isEmpty(mimeType)) {
                                        request.setMimeType(Statics.MIME_TYPE_ALL);
                                    } else {
                                        request.setMimeType(mimeType);
                                    }
                                    break;
                            }
                            final Long reference = downloadmanager.enqueue(request);

                            BroadcastReceiver receiver = new BroadcastReceiver() {
                                @Override
                                public void onReceive(Context context, Intent intent) {
                                    String action = intent.getAction();
                                    if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                                        long downloadId = intent.getLongExtra(
                                                DownloadManager.EXTRA_DOWNLOAD_ID, 0);
                                        DownloadManager.Query query = new DownloadManager.Query();
                                        query.setFilterById(reference);
                                        Cursor c = downloadmanager.query(query);
                                        if (c.moveToFirst()) {
                                            int columnIndex = c
                                                    .getColumnIndex(DownloadManager.COLUMN_STATUS);
                                            if (DownloadManager.STATUS_SUCCESSFUL == c
                                                    .getInt(columnIndex)) {
                                            }
                                        }
                                    }
                                }
                            };
                            context.registerReceiver(receiver, new IntentFilter(
                                    DownloadManager.ACTION_DOWNLOAD_COMPLETE));
                        }

                        @Override
                        public void onCancelClick() {

                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
            showMessage(context.getString(R.string.download_file_error));
        }
    }

    /*public static boolean exists(String URLName) {
        try {
            HttpURLConnection.setFollowRedirects(false);
            // note : you may also need
            //        HttpURLConnection.setInstanceFollowRedirects(false)
            HttpURLConnection con =
                    (HttpURLConnection) new URL(URLName).openConnection();
            con.setRequestMethod("HEAD");
            return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }*/

    //1: Image
    //2: Video
    //3: Audio
    //4: DOC
    //5: XLS
    //6: PDF
    //7: PPT
    //8: zip
    //9: rar
    //10: apk
    //11: default
    public static int getTypeFile(String typeFile) {
        int type = 0;
        switch (typeFile) {
            case Statics.IMAGE_GIF:
            case Statics.IMAGE_JPEG:
            case Statics.IMAGE_JPG:
            case Statics.IMAGE_PNG:
                type = 1;
                break;
            case Statics.VIDEO_MP4:
            case Statics.VIDEO_MOV:
                type = 2;
                break;
            case Statics.AUDIO_MP3:
            case Statics.AUDIO_AMR:
            case Statics.AUDIO_WMA:
                type = 3;
                break;
            case Statics.FILE_DOC:
            case Statics.FILE_DOCX:
                type = 4;
                break;
            case Statics.FILE_XLS:
            case Statics.FILE_XLSX:
                type = 5;
                break;
            case Statics.FILE_PDF:
                type = 6;
                break;
            case Statics.FILE_PPT:
            case Statics.FILE_PPTX:
                type = 7;
                break;
            case Statics.FILE_ZIP:
                type = 8;
                break;
            case Statics.FILE_RAR:
                type = 9;
                break;
            case Statics.FILE_APK:
                type = 10;
                break;
            default:
                type = 11;
                break;
        }
        return type;
    }

    public static Uri getImageContentUri(Context context, File imageFile, int flag) {
        String filePath = imageFile.getAbsolutePath();
        if (flag == Statics.MEDIA_TYPE_IMAGE) {
            Cursor cursor = context.getContentResolver().query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    new String[]{MediaStore.Images.Media._ID},
                    MediaStore.Images.Media.DATA + "=? ",
                    new String[]{filePath}, null);
            if (cursor != null && cursor.moveToFirst()) {
                int id = cursor.getInt(cursor
                        .getColumnIndex(MediaStore.MediaColumns._ID));
                Uri baseUri = Uri.parse(Statics.NOTE_SUPPORT_URI_IMAGE);
                return Uri.withAppendedPath(baseUri, "" + id);
            } else {
                if (imageFile.exists()) {
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.DATA, filePath);
                    return context.getContentResolver().insert(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                } else {
                    return null;
                }
            }
        } else {
            Cursor cursor = context.getContentResolver().query(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    new String[]{MediaStore.Video.Media._ID},
                    MediaStore.Video.Media.DATA + "=? ",
                    new String[]{filePath}, null);
            if (cursor != null && cursor.moveToFirst()) {
                int id = cursor.getInt(cursor
                        .getColumnIndex(MediaStore.MediaColumns._ID));
                Uri baseUri = Uri.parse(Statics.NOTE_SUPPORT_URI_VIDEO);
                return Uri.withAppendedPath(baseUri, "" + id);
            } else {
                if (imageFile.exists()) {
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Video.Media.DATA, filePath);
                    return context.getContentResolver().insert(
                            MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
                } else {
                    return null;
                }
            }
        }

    }

    public static boolean isVideo(String fileName){
        Utils.printLogs("Check "+"FileName = "+fileName);
        if (fileName == null || TextUtils.isEmpty(fileName)){
            return false;
        }
        String fileType = fileName.substring(fileName.lastIndexOf(".")).toLowerCase();

        Utils.printLogs("Check "+"File Type = "+fileType);

        int type = getTypeFile(fileType);

        Utils.printLogs("Check "+"Type = "+type);

        return type == 2;
    }


    public static int getTypeFileAttach(String fileType) {
        int type = 2;
        if (TextUtils.isEmpty(fileType))
            return type;
        else
            switch (fileType) {
                case Statics.IMAGE_JPEG:
                case Statics.IMAGE_JPG:
                case Statics.IMAGE_PNG:
                case Statics.IMAGE_GIF:
                    type = 1;
                    break;
                default:
                    type = 2;
                    break;
            }
        return type;
    }

    public static String getFileName(String path) {
        if (!TextUtils.isEmpty(path)) {
            return path.substring(path.lastIndexOf("/") + 1);
        } else {
            return "";
        }
    }

    public static String getFileType(String name) {
        if (!TextUtils.isEmpty(name)) {
            return name.substring(name.lastIndexOf(".")).toLowerCase();
        } else {
            return "";
        }
    }

    /**
     * chattingDto 의 글쓴이와 chattingDto2 의 글쓴이가 서로 다르고 chattingDto2의 채팅 타입이 1이 아닐 경우인지 체크한다.
     * @param chattingDto
     * @param chattingDto2
     * @return
     */
    public static boolean getChattingType(ChattingDto chattingDto, ChattingDto chattingDto2) {
        try {
            return (chattingDto.getWriterUser() == chattingDto2.getWriterUser()) && (chattingDto2.getType() != 1);
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean checkChat(ChattingDto chattingDto, int myId) {
        boolean check = true;
        if (chattingDto != null) {
            if (chattingDto.getUserNos().size() > 1) {
                for (int i : chattingDto.getUserNos())
                    if (i != myId) {
                        check = false;
                        break;
                    }

            } else {
                check = true;
            }
        } else {
            return true;
        }
        return check;
    }

    public static boolean checkChatV2(ChatRoomDTO chattingDto) {
        boolean check = true;
        if (chattingDto != null) {
            if (chattingDto.getUserNos().size() > 1) {
                for (int i : chattingDto.getUserNos())
                    if (i != UserDBHelper.getUser().Id) {
                        check = false;
                        break;
                    }

            } else {
                check = true;
            }
        } else {
            return true;
        }
        return check;
    }

    public static boolean checkChatId198(ChattingDto chattingDto) {
        boolean check = false;
        if (chattingDto != null) {
            if (chattingDto.getUserNos().size() > 0) {
                for (int i : chattingDto.getUserNos())
                    if (i == 198) {
                        check = true;
                        break;
                    }

            } else {
                check = false;
            }
        } else {
            return false;
        }
        return check;
    }

    public static boolean checkChatId198V2(ChatRoomDTO chattingDto) {
        boolean check = false;
        if (chattingDto != null) {
            if (chattingDto.getUserNos().size() > 0) {
                for (int i : chattingDto.getUserNos())
                    if (i == 198) {
                        check = true;
                        break;
                    }

            } else {
                check = false;
            }
        } else {
            return false;
        }
        return check;
    }

    /**
     * CONVERT DP TO PX
     */
    public static int convertDipToPixels(Context context, float dips) {
        return (int) (dips * context.getResources().getDisplayMetrics().density + 0.5f);
    }


    /**
     * SAVE CURRENT CHAT LIST
     */
    /*public static void saveStringCurrentChatList(ChattingDto dto, boolean isAddUnread) {
        String data = new Prefs().getStringValue(Statics.KEY_DATA_CURRENT_CHAT_LIST, "");

        if (!TextUtils.isEmpty(data)) {
            boolean isContains = false;
            List<ChattingDto> list = Utils.convertStringToListChatting(data);
            for (ChattingDto chattingDto1 : list) {
                if (chattingDto1.getRoomNo() == dto.getRoomNo()) {
                    chattingDto1.setLastedMsg(dto.getMessage());
                    chattingDto1.setLastedMsgDate(dto.getRegDate());
                    if (isAddUnread) {
                        chattingDto1.setUnReadCount(chattingDto1.getUnReadCount() + 1);
                    }
                    isContains = true;
                    break;
                }
            }
            if (!isContains) {
                HttpRequest.getInstance().GetChatList(new OnGetChatList() {
                    @Override
                    public void OnGetChatListSuccess(List<ChattingDto> list) {
                        new Prefs().putStringValue(Statics.KEY_DATA_CURRENT_CHAT_LIST, Utils.convertListChattingToString(list) + "");
                        CurrentChatListFragment.fragment.updateCurrentChatList();
                    }

                    @Override
                    public void OnGetChatListFail(ErrorDto errorDto) {

                    }
                });
            } else {
                new Prefs().putStringValue(Statics.KEY_DATA_CURRENT_CHAT_LIST, Utils.convertListChattingToString(list) + "");
            }
        }
    }*/

    /**
     * CHECK CALL VISIBLE
     */
    public static boolean isCallVisible(ArrayList<Integer> userNos) {
        ArrayList<TreeUserDTOTemp> treeUserDTOTemps = AllUserDBHelper.getUser();
        for (int i : userNos) {
            if (i != UserDBHelper.getUser().Id) {
                for (TreeUserDTOTemp treeUserDTOTemp : treeUserDTOTemps) {
                    if (i == treeUserDTOTemp.getUserNo()) {
                        String phone = !TextUtils.isEmpty(treeUserDTOTemp.getCellPhone().trim()) ?
                                treeUserDTOTemp.getCellPhone() :
                                !TextUtils.isEmpty(treeUserDTOTemp.getCompanyPhone().trim()) ?
                                        treeUserDTOTemp.getCompanyPhone() :
                                        "";
                        if (!TextUtils.isEmpty(phone)) {
                            return true;
                        }
                        break;
                    }
                }
            }
        }
        return false;
    }

    /**
     * CHECK CALL VISIBLE
     */
    public static void addCallArray(ArrayList<Integer> userNos, ArrayAdapter<String> arrayAdapter) {
        for (int i : userNos) {
            if (i != UserDBHelper.getUser().Id) {
                TreeUserDTOTemp treeUserDTOTemp = Utils.GetUserFromDatabase(AllUserDBHelper.getUser(), i);
                if (treeUserDTOTemp != null) {
                    String userName = treeUserDTOTemp.getName();
                    String phone = !TextUtils.isEmpty(treeUserDTOTemp.getCellPhone().trim()) ?
                            treeUserDTOTemp.getCellPhone() :
                            !TextUtils.isEmpty(treeUserDTOTemp.getCompanyPhone().trim()) ?
                                    treeUserDTOTemp.getCompanyPhone() :
                                    "";
                    if (!TextUtils.isEmpty(phone)) {
                        arrayAdapter.add(userName + " (" + phone + ")");
                    }
                }
            }
        }
    }

    /**
     * CALL PHONE CALL
     */
    public static void CallPhone(Context context, String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        context.startActivity(intent);
    }

    public static void sendSMS(Context context, String phoneNumber) {

        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setType("vnd.android-dir/mms-sms");
        intent.setData(Uri.parse("sms:" + phoneNumber));
        context.startActivity(intent);
    }

    public static void sendMail(Context context, String emailAddress) {

        Intent intent = new Intent(Intent.ACTION_SEND);
        String[] recipients = new String[]{emailAddress, "",};
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.putExtra(android.content.Intent.EXTRA_EMAIL, recipients);
        intent.setType("message/rfc822");
        context.startActivity(Intent.createChooser(intent, "Choose an Email client"));
    }

    public static int getCurrentId(){
        int myId = CrewChatApplication.currentId;
        if (myId == 0){
            myId = new Prefs().getUserNo();
        }
        if (myId == 0){
            myId = UserDBHelper.getUser().Id;
        }
        return myId;
    }

    public static UserDto getCurrentUser(){
        if (CrewChatApplication.currentUser != null){
            return CrewChatApplication.currentUser;
        }

        return UserDBHelper.getUser();
    }

    public static ArrayList<TreeUserDTOTemp> getUsers(){
        if (CrewChatApplication.listUsers != null && CrewChatApplication.listUsers.size() > 0){
            return CrewChatApplication.listUsers;
        } else {
            return AllUserDBHelper.getUser();
        }

    }

    /**
     * SAVE FILE
     */
    public static String saveFile(Bitmap bitmapImage) {


        String filename = ".CrewChat/share.png";
        File file = new File(Environment.getExternalStorageDirectory(), filename);
        createParentDirectories(file);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file, false);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 80, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file.getAbsolutePath();
    }

    /**
     * CREATE PARENT DIRECTORIES
     */
    public static void createParentDirectories(final File inFile) {
        if (inFile != null) {
            final File parentDir = inFile.getParentFile();

            if ((parentDir != null) && !parentDir.exists()) {
                parentDir.mkdirs();
            }
        }
    }

    /*
    * Remove duplicate item
    * */
    public static void removeArrayDuplicate(ArrayList<Integer> ids){
        Utils.printLogs("-->Ids size = "+ids.size());
        for (int i = 0 ; i < ids.size(); i++) {
            for (int j = i + 1; j < ids.size(); j++) {
                Utils.printLogs("-->So sanh Id1="+ids.get(i)+" Id2="+ids.get(j));
                if (ids.get(i).intValue() == ids.get(j).intValue()) {
                    Utils.printLogs("-->Remove "+ids.get(j));
                    ids.remove(j);
                    removeArrayDuplicate(ids);
                    break;
                }
            }
        }
    }

    public static void DownloadImage(final Context context, final String url, final String name) {
        String mimeType;
        String serviceString = Context.DOWNLOAD_SERVICE;
        String fileType = name.substring(name.lastIndexOf(".")).toLowerCase();
        final DownloadManager downloadmanager;
        downloadmanager = (DownloadManager) context.getSystemService(serviceString);
        Uri uri = Uri
                .parse(url);

        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Constant.pathDownload, name);
        //request.setTitle(name);
        int type = getTypeFile(fileType);
        switch (type) {
            case 1:
                request.setMimeType(Statics.MIME_TYPE_IMAGE);
                break;
            case 2:
                request.setMimeType(Statics.MIME_TYPE_VIDEO);
                break;
            case 3:
                request.setMimeType(Statics.MIME_TYPE_AUDIO);
                break;
            default:
                try {
                    mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(url));
                } catch (Exception e) {
                    e.printStackTrace();
                    mimeType = Statics.MIME_TYPE_ALL;
                }
                if (TextUtils.isEmpty(mimeType)) {
                    request.setMimeType(Statics.MIME_TYPE_ALL);
                } else {
                    request.setMimeType(mimeType);
                }
                break;
        }
        final Long reference = downloadmanager.enqueue(request);

        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                    long downloadId = intent.getLongExtra(
                            DownloadManager.EXTRA_DOWNLOAD_ID, 0);
                    DownloadManager.Query query = new DownloadManager.Query();
                    query.setFilterById(reference);
                    Cursor c = downloadmanager.query(query);
                    if (c.moveToFirst()) {
                        int columnIndex = c
                                .getColumnIndex(DownloadManager.COLUMN_STATUS);
                        if (DownloadManager.STATUS_SUCCESSFUL == c
                                .getInt(columnIndex)) {
                        }
                    }
                }
            }
        };
        context.registerReceiver(receiver, new IntentFilter(
                DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }
}
