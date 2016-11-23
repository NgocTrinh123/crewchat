package com.dazone.crewchat.constant;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.dazone.crewchat.BuildConfig;
import com.dazone.crewchat.R;
import com.dazone.crewchat.utils.Constant;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.dazone.crewchat.customs.RoundedOneCorner;
import com.nostra13.universalimageloader.core.process.BitmapProcessor;

/**
 * Created by david on 12/18/15.
 */
public interface Statics {
    // 자리비움 시간(초)
    int USER_STATUS_AWAY_TIME = 60 * 3;
    // version code
    String VERSION_CODE = "crewchat_version_code";
    // setting notification default time
    int DEFAULT_START_NOTIFICATION_TIME = 8;
    int DEFAULT_END_NOTIFICATION_TIME = 18;
    // setting notification key
    String ENABLE_NOTIFICATION = "enable_notification";
    String ENABLE_SOUND = "enable_notification_sound";
    String ENABLE_VIBRATE = "enable_notification_vibrate";
    String ENABLE_TIME = "enable_notification_time";
    String ENABLE_NOTIFICATION_WHEN_USING_PC_VERSION = "enable_notification_when_using_pc_version";

    String START_NOTIFICATION_HOUR = "start_notification_time_hour";
    String START_NOTIFICATION_MINUTES = "start_notification_time_minutes";

    String END_NOTIFICATION_HOUR = "end_notification_time_hour";
    String END_NOTIFICATION_MINUTES = "end_notification_time_minutes";

//    preft key

    String ORGANIZATION_TREE = "organization_tree";
    String KEY_DATA_CURRENT_CHAT_LIST = "key_data_current_chat_list";

    String IMAGE_SIZE_MODE = "image_size_mode";
    int MODE_ORIGINAL = 0;
    int MODE_DEFAULT = 1;

//    end preft key

    int TYPE_DEPART = 0;
    int TYPE_USER = 2;

    // Room action type
    int ROOM_RENAME = 1;
    int ROOM_OPEN = 2;
    int ROOM_ADD_TO_FAVORITE = 3;
    int ROOM_REMOVE_FROM_FAVORITE = 6;
    int ROOM_ALARM_ON = 4;
    int ROOM_ALARM_OFF = 7;
    int ROOM_LEFT = 5;

    // Image menu conext
    int MENU_OPEN = 0;
    int MENU_COPY = 1;
    int MENU_DOWNLOAD = 2;
    int MENU_DELETE = 3;
    int MENU_SHARE = 4;

    int MENU_REGISTERED_USERS = 5;
    int MENU_MODIFYING_GROUP = 6;
    int MENU_DELETE_GROUP = 7;
    int MENU_REMOVE_FROM_FAVORITE = 8;
    int MENU_OPEN_CHAT_ROOM = 9;

    // Chat room Key
    String ROOM_TITLE = "room_title";
    String ROOM_NO = "room_no";

    //    bundle key
    String TREE_USER_PC = "tree_user_pc";
    //    end bundle key
    String CHATTING_DTO = "chatting_dto";
    String CHATTING_DTO_FOR_GROUP_LIST = "chatting_dto_for_group_list";
    String CHATTING_DTO_GALLERY = "chatting_dto_gallery";

    /*
    * LASTED MESSAGE TYPE
    * */
    int MESSAGE_TYPE_NORMAL = 0;
    int MESSAGE_TYPE_SYSTEM = 1;
    int MESSAGE_TYPE_ATTACH = 2;

    /*
    * LASTED MESSAGE ATTACH TYPE
    * */
    int ATTACH_FILE = 2;
    int ATTACH_IMAGE = 1;
    int ATTACH_NONE = 0;

    /**
     * CHAT VIEW IMAGE
     */
    String CHATTING_DTO_GALLERY_LIST = "chatting_dto_gallery_list";
    String CHATTING_DTO_GALLERY_POSITION = "chatting_dto_gallery_position";
    String CHATTING_DTO_GALLERY_SHOW_FULL = "chatting_dto_gallery_show_full";
    String CHATTING_DTO_GALLERY_SINGLE = "chatting_dto_gallery_single";
    String CHATTING_DTO_REG_DATE = "chatting_dto_reg_date";

    String CHATTING_DTO_ADD_USER = "add_user";
    String CHATTING_DTO_ADD_USER_NEW = "add_user_new";
    String GCM_NOTIFY = "gcm_notify";

    int REQUEST_TIMEOUT_MS = 15000;

    String TAG = "CrewChatLogs";
    String TAG_DAVID = "David_tags";
    String PREFS_KEY_SESSION_ERROR = "session_error";
    int DATABASE_VERSION = 31;
    String DATABASE_NAME = "crewchat.db";


    //main version
    int MAIN_ACTIVITY_TAB_COUNT = 4;

    //format date
    String DATE_FORMAT_YY_MM_DD_DD = "yy-MM-dd-EEEEEEE";
    public static String DATE_FORMAT_YY_MM_DD_DD_H_M = "yy-MM-dd-EEEEEEE hh:mm aa";
    String DATE_FORMAT_YY_MM_DD = "yy-MM-dd";
    String DATE_FORMAT_YY_MM_DD_H_M = "yy-MM-dd hh:mm aa";
    String DATE_FORMAT_HH_MM_AA = "hh:mm aa";
    String DATE_FORMAT_YYYY_MM_DD = "yyyy-MM-dd";

    int CHATTING_VIEW_TYPE_PERSON = 0;
    int CHATTING_VIEW_TYPE_PERSON_NOT_SHOW = 9;
    int CHATTING_VIEW_TYPE_SELF = 1;
    int CHATTING_VIEW_TYPE_SELF_NOT_SHOW = 10;
    int CHATTING_VIEW_TYPE_DATE = 2;
    int CHATTING_VIEW_TYPE_GROUP = 3;
    int CHATTING_VIEW_TYPE_SELF_IMAGE = 4;
    int CHATTING_VIEW_TYPE_PERSON_IMAGE = 5;
    int CHATTING_VIEW_TYPE_PERSON_IMAGE_NOT_SHOW = 11;
    int CHATTING_VIEW_TYPE_SELF_FILE = 6;
    int CHATTING_VIEW_TYPE_PERSON_FILE = 7;
    int CHATTING_VIEW_TYPE_PERSON_FILE_NOT_SHOW = 12;
    int CHATTING_VIEW_TYPE_GROUP_NEW = 8;
    int CHATTING_VIEW_TYPE_SELECT_IMAGE = 13;
    int CHATTING_VIEW_TYPE_SELECT_FILE = 14;
    int CHATTING_VIEW_TYPE_CONTACT = 15;
    int CHATTING_VIEW_TYPE_EMPTY = 16;
    int CHATTING_VIEW_TYPE_SELF_VIDEO = 17;
    int CHATTING_VIEW_TYPE_SELECT_VIDEO = 18;
    int CHATTING_VIEW_TYPE_PERSON_VIDEO_NOT_SHOW = 19;

    // New user status
    int USER_LOGIN = 1;
    int USER_AWAY = 3;
    int USER_LOGOUT = 0;

    // Device Type String
    String DEVICE_TYPE = "Android";

    // End new user status

    int USER_STATUS_WORKING = 0;
    int USER_STATUS_AWAY = 1;
    int USER_STATUS_RESTING = 2;
    int USER_STATUS_WORKING_OUTSIDE = 3;
    int USER_STATUS_IN_A_CAL = 4;
    int USER_STATUS_METTING = 5;

    public static final String NOTE_SUPPORT_URI_IMAGE = "content://media/external/images/media";
    public static final String NOTE_SUPPORT_URI_VIDEO = "content://media/external/video/media";

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    //CusTomGallery
    public static final String TEMP_PATH_IMAGE = "/.temp_tmp";
    public static final String FILE_NOTE_CUSTOM_GALLERY_ADAPTER = "file://";
    public static final String ACTION_MULTIPLE_PICK = BuildConfig.APPLICATION_ID + ".ACTION_MULTIPLE_PICK";
    public static final String KEY_DATA_PATH_INTENT = "key_path_intent";
    public static final String KEY_DATA_NAME_INTENT = "key_name_intent";

    public static final String IMAGE_DIRECTORY_NAME = "CrewChat";
    public static final String DATE_FORMAT_PICTURE = "yyyyMMdd_HHmmss";

    public static final int VIDEO_PICKER_SELECT = 300;
    public static final int FILE_PICKER_SELECT = 400;
    public static final int ADD_USER_SELECT = 500;
    public static final int CONTACT_PICKER_SELECT = 600;
    public static final int ADD_USER_TO_FAVORITE = 700;

    public static final boolean WRITE_HTTP_REQUEST = true;


    int REQUESTCODE_UPDATE_CURRENT_CHAT_LIST = 999;

    //for intent
    int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    int CAMERA_VIDEO_REQUEST_CODE = 102;
    int IMAGE_PICKER_SELECT = 101;
    int IMAGE_ROTATE_CODE = 103;

    public static final int ORGANIZATION_DISPLAY_SELECTED_ACTIVITY = 303;

    public static final String ACTION_SHOW_SEARCH_INPUT = "receiver_show_search_input";
    public static final String ACTION_HIDE_SEARCH_INPUT = "receiver_hide_search_input";

    public static final String ACTION_SHOW_SEARCH_FAVORITE_INPUT = "receiver_show_search_favorite_input";
    public static final String ACTION_HIDE_SEARCH_FAVORITE_INPUT = "receiver_hide_search_favorite_input";

    public static final String ACTION_SHOW_SEARCH_INPUT_IN_CURRENT_CHAT = "receiver_show_search_input_in_current_chat";
    public static final String ACTION_HIDE_SEARCH_INPUT_IN_CURRENT_CHAT = "receiver_hide_search_input_in_current_chat";

    //Google api
    final public static String GOOGLE_SENDER_ID = "360611512660";//AIzaSyDSTPgQtGRDc1tvuWhY8z7h1PlH8jPdRsw
    //final public static String GOOGLE_SENDER_ID = "972801588344";//AIzaSyDoiBazJP1kKBVBTbYRjHyHLm8_2aC0MYs
    public static final String ACTION_RECEIVER_NOTIFICATION = "receiver_notification";
    public static final String GCM_DATA_NOTIFICATOON = "gcm_data_notificaiton";

    public static final String ACTION_UPDATE_CURRENT_CHAT_LIST = "action_update_current_chat_list";
    public static final String DATA_UPDATE_CURRENT_CHAT_LIST = "data_update_current_chat_list";


    public static final String APPLICATION_ID = "com.dazone.crewchat";
    public BitmapFactory.Options decodingOptions = new BitmapFactory.Options();

    public static final DisplayImageOptions options3 = new DisplayImageOptions.Builder()
            .cacheOnDisk(true).cacheInMemory(true)
            .imageScaleType(ImageScaleType.NONE_SAFE)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .considerExifParams(false)
            .displayer(new RoundedBitmapDisplayer(90))
            .build();


    /**
     * DISPLAY OPTIONS AVATAR GROUP
     */
    // 그룹 프로필 사진 이미지(하단 왼쪽) 4인이상의 경우
    DisplayImageOptions avatarGroupBL = new DisplayImageOptions.Builder()
            .showImageForEmptyUri(R.drawable.loading)
            .showImageOnFail(R.drawable.loading)
            .cacheOnDisk(true).cacheInMemory(true)
            .imageScaleType(ImageScaleType.NONE_SAFE)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .displayer(new RoundedOneCorner(90, Constant.TYPE_ROUNDED_BOTTOM_LEFT))
            .considerExifParams(false)
            .build();

    // 그룹 프로필 사진 이미지(하단 오른쪽) 4인이상의 경우
    DisplayImageOptions avatarGroupBR = new DisplayImageOptions.Builder()
            .cacheOnDisk(true).cacheInMemory(true)
            .imageScaleType(ImageScaleType.NONE_SAFE)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .displayer(new RoundedOneCorner(90, Constant.TYPE_ROUNDED_BOTTOM_RIGHT))
            .considerExifParams(false)
            .build();

    // 그룹 프로필 사진 이미지(상단 왼쪽) 4인이상의 경우
    DisplayImageOptions avatarGroupTL = new DisplayImageOptions.Builder()
            .showImageForEmptyUri(R.drawable.loading)
            .showImageOnFail(R.drawable.loading)
            .cacheOnDisk(true)
            .cacheInMemory(true)
            .imageScaleType(ImageScaleType.NONE_SAFE)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .displayer(new RoundedOneCorner(90, Constant.TYPE_ROUNDED_TOP_LEFT))
            .considerExifParams(false)
            .build();

    // 그룹 프로필 사진 이미지(상단 오른쪽) 4인이상의 경우
    DisplayImageOptions avatarGroupTR = new DisplayImageOptions.Builder()
            .showImageForEmptyUri(R.drawable.loading)
            .showImageOnFail(R.drawable.loading)
            .cacheOnDisk(true).cacheInMemory(true)
            .imageScaleType(ImageScaleType.NONE_SAFE)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .displayer(new RoundedOneCorner(90, Constant.TYPE_ROUNDED_TOP_RIGHT))
            .considerExifParams(false)
            .build();

    DisplayImageOptions avatarGroupLeftSide = new DisplayImageOptions.Builder()
            .showImageForEmptyUri(R.drawable.loading)
            .showImageOnFail(R.drawable.loading)
            .cacheOnDisk(true).cacheInMemory(true)
            .imageScaleType(ImageScaleType.NONE_SAFE)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .displayer(new RoundedOneCorner(90, Constant.TYPE_ROUNDED_LEFT_SIDE))
            .considerExifParams(false)
            .build();

    DisplayImageOptions avatarGroupTOP = new DisplayImageOptions.Builder()
            .showImageForEmptyUri(R.drawable.loading)
            .showImageOnFail(R.drawable.loading)
            .cacheOnDisk(true).cacheInMemory(true)
            .imageScaleType(ImageScaleType.NONE_SAFE)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .displayer(new RoundedOneCorner(90, Constant.TYPE_ROUNDED_TOP))
            .considerExifParams(false)
            .build();

    /***/
    DisplayImageOptions options2 = new DisplayImageOptions.Builder()
            .cacheOnDisk(true)
            .cacheInMemory(true)
            .showImageForEmptyUri(R.drawable.avatar_l)
            .showImageOnLoading(R.drawable.avatar_l)
            .showImageOnFail(R.drawable.avatar_l)
            .imageScaleType(ImageScaleType.NONE_SAFE)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .considerExifParams(false)
            .displayer(new RoundedBitmapDisplayer(90))
            .build();

    DisplayImageOptions options = new DisplayImageOptions.Builder()
            .cacheOnDisk(true).cacheInMemory(true)
            .imageScaleType(ImageScaleType.NONE_SAFE)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .considerExifParams(false)
            .displayer(new FadeInBitmapDisplayer(0))
            .considerExifParams(true)
            .decodingOptions(decodingOptions)
            .postProcessor(new BitmapProcessor() {
                @Override
                public Bitmap process(Bitmap bmp) {
                    // return Bitmap.createScaledBitmap(bmp, bmp.getWidth(), bmp.getWidth(), false);
                    int width = bmp.getWidth();
                    int height = bmp.getHeight();
                    if (width > 1000 || height > 1000) {
                        return Bitmap.createScaledBitmap(bmp, width / 3, height / 3, false);
                    }
                    return bmp;
                }
            })
            .build();

    DisplayImageOptions optionsNoCache = new DisplayImageOptions.Builder()
            .cacheOnDisk(false)
            .cacheInMemory(true)
            .imageScaleType(ImageScaleType.NONE_SAFE)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .considerExifParams(false)
            .displayer(new FadeInBitmapDisplayer(0))
            .considerExifParams(true)
            .decodingOptions(decodingOptions)
            .build();

    public static final DisplayImageOptions optionsViewAttach = new DisplayImageOptions.Builder()
            .showImageForEmptyUri(R.drawable.loading)
            .showImageOnFail(R.drawable.loading)
            .cacheOnDisk(true).cacheInMemory(true)
            .imageScaleType(ImageScaleType.NONE_SAFE)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .considerExifParams(true)
            .displayer(new FadeInBitmapDisplayer(0))
            .considerExifParams(true)
            .showImageOnLoading(R.drawable.loading)
            .decodingOptions(decodingOptions)
            .build();

    //file type
    public static final String IMAGE_JPG = ".jpg";
    public static final String IMAGE_JPEG = ".jpeg";
    public static final String IMAGE_PNG = ".png";
    public static final String IMAGE_GIF = ".gif";
    public static final String AUDIO_MP3 = ".mp3";
    public static final String AUDIO_WMA = ".wma";
    public static final String AUDIO_AMR = ".amr";
    public static final String VIDEO_MP4 = ".mp4";
    public static final String VIDEO_MOV = ".mov";
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
    public static final String MIME_TYPE_AUDIO = "audio/*";
    /*    public static final String MIME_TYPE_TEXT = "application/vnd.google-apps.file";*/
    public static final String MIME_TYPE_IMAGE = "image/*";
    public static final String MIME_TYPE_VIDEO = "video/*";
    public static final String MIME_TYPE_APP = "file/*";
    public static final String MIME_TYPE_TEXT = "text/*";
    public static final String MIME_TYPE_ALL = "*/*";
    public static final String MIME_TYPE_PDF = "application/pdf";
    public static final String MIME_TYPE_ZIP = "application/zip";
    public static final String MIME_TYPE_RAR = "application/x-rar-compressed";
    public static final String MIME_TYPE_DOC = "application/doc";
    public static final String MIME_TYPE_XLSX = "application/xls";
    public static final String MIME_TYPE_PPTX = "application/ppt";
    public static final String MIME_TYPE_APK = "application/vnd.android.package-archive";

    public static final String ORANGE = "orange";
}
