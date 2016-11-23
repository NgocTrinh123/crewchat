package com.dazone.crewchat.utils;

import android.graphics.Bitmap;

import com.dazone.crewchat.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

/**
 * Created by Dat on 4/14/2016.
 */
public class Constant {
    /**
     * URLS
     */
    public static final String ROOT_URL_UPDATE = "http://www.crewcloud.net";


    /**
     * PATH SAVE DOWNLOAD
     */
    public static final String pathDownload = "/CrewChat/";

    /**
     * URI IMAGE DEFAULT
     */
    public static final String UriDefaultAvatar = "drawable://" + R.drawable.avatar_l;


    /**
     * INTENT FILTER
     */
    public static final String INTENT_FILTER = "INTENT_FILTER";
    public static final String INTENT_FILTER_SEARCH = "INTENT_FILTER_SEARCH";
    public static final String INTENT_FILTER_GET_MESSAGE_UNREAD_COUNT = "INTENT_FILTER_GET_MESSAGE_UNREAD_COUNT";
    public static final String INTENT_FILTER_ADD_USER = "INTENT_FILTER_ADD_USER";
    public static final String INTENT_FILTER_CHAT_DELETE_USER = "INTENT_FILTER_CHAT_DELETE_USER";

    public static final String INTENT_FILTER_NOTIFY_ADAPTER = "INTENT_FILTER_NOTIFY_ADAPTER";
    /**
     * INTENT RESULT
     */
    public static final int INTENT_RESULT_CREATE_NEW_ROOM = 800;


    /**
     * KEY INTENT
     */
    public static final String KEY_INTENT_TEXT_SEARCH = "KEY_INTENT_TEXT_SEARCH";
    public static final String KEY_INTENT_FROM_NOTIFICATION = "KEY_INTENT_FROM_NOTIFICATION";
    public static final String KEY_INTENT_ROOM_NO = "KEY_INTENT_ROOM_NO";
    public static final String KEY_INTENT_ROOM_DTO = "KEY_INTENT_ROOM_DTO";
    public static final String KEY_INTENT_GROUP_NO = "KEY_INTENT_GROUP_NO";
    public static final String KEY_INTENT_USER_NO = "KEY_INTENT_USER_NO";
    public static final String KEY_INTENT_USER_NO_ARRAY = "KEY_INTENT_USER_NO_ARRAY";
    public static final String KEY_INTENT_COUNT_MEMBER = "KEY_INTENT_COUNT_MEMBER";
    public static final String KEY_INTENT_CHATTING_DTO = "KEY_INTENT_CHATTING_DTO";
    public static final String KEY_INTENT_UNREAD_TOTAL_COUNT = "KEY_INTENT_UNREAD_TOTAL_COUNT";
    public static final String KEY_INTENT_USER_STATUS_DTO = "KEY_INTENT_USER_STATUS_DTO";
    public static final String KEY_INTENT_ROOM_TITLE = "KEY_INTENT_ROOM_TITLE";
    public static final String KEY_INTENT_SELECT_USER_RESULT = "KEY_INTENT_SELECT_USER_RESULT";

    /**
     * DISPLAY IMAGE OPTION
     */
    public static final DisplayImageOptions optionsProfileAvatar = new DisplayImageOptions.Builder()
            .showImageForEmptyUri(R.drawable.loading)
            .showImageOnFail(R.drawable.loading)
            .cacheOnDisk(true).cacheInMemory(true)
            .imageScaleType(ImageScaleType.NONE_SAFE)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .considerExifParams(false)
            .displayer(new RoundedBitmapDisplayer(180))
            .showImageOnLoading(R.drawable.loading)
            .build();


    /**
     * DISPLAY IMAGE OPTION SETTING PROFILE
     */
    public static final DisplayImageOptions optionsProfileAvatarSetting = new DisplayImageOptions.Builder()
            .showImageForEmptyUri(R.drawable.loading)
            .showImageOnFail(R.drawable.loading)
            .cacheOnDisk(true)
            .cacheInMemory(true)
            .imageScaleType(ImageScaleType.NONE_SAFE)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .considerExifParams(false)
            .displayer(new RoundedBitmapDisplayer(10))
            .showImageOnLoading(R.drawable.loading)
            .build();

    /*
    * TYPE ACTION
    * */
    public static final int TYPE_ACTION_FAVORITE = 1009;
    public static final int TYPE_ACTION_ALARM_ON = 1010;
    public static final int TYPE_ACTION_ALARM_OFF = 1011;

    /**
     * TYPE ROUNDED
     */
    public static final int TYPE_ROUNDED_TOP_RIGHT = 1001;
    public static final int TYPE_ROUNDED_TOP_LEFT = 1002;
    public static final int TYPE_ROUNDED_BOTTOM_RIGHT = 1003;
    public static final int TYPE_ROUNDED_BOTTOM_LEFT = 1004;
    public static final int TYPE_ROUNDED_LEFT_SIDE = 1005;
    public static final int TYPE_ROUNDED_TOP = 1007;
    public static final int TYPE_ROUNDED_RIGHT_SIDE = 1006;


    /**
     * FILE TYPES
     */
    public static final String IMAGE_JPG = ".jpg";
    public static final String IMAGE_JPEG = ".jpeg";
    public static final String IMAGE_PNG = ".png";
    public static final String IMAGE_GIF = ".gif";
    public static final String AUDIO_MP3 = ".mp3";
    public static final String AUDIO_WMA = ".wma";
    public static final String AUDIO_AMR = ".amr";
    public static final String VIDEO_MP4 = ".mp4";
    public static final String FILE_PDF = ".pdf";
    public static final String FILE_DOCX = ".docx";
    public static final String FILE_DOC = ".doc";
    public static final String FILE_XLS = ".xls";
    public static final String FILE_XLSX = ".xlsx";
    public static final String FILE_PPTX = ".pptx";
    public static final String FILE_PPT = ".ppt";
    public static final String FILE_ZIP = ".zip";
    public static final String FILE_RAR = ".rar";
    public static final String FILE_APK = ".apk";
}
