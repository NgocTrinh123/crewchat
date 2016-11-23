package com.dazone.crewchat.database;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.text.TextUtils;
import com.dazone.crewchat.dto.ChattingDto;
import com.dazone.crewchat.utils.CrewChatApplication;
import com.dazone.crewchat.utils.Utils;

import java.util.ArrayList;

import static java.lang.Integer.parseInt;

/**
 * Created by Admin on 5/20/2016.
 */
public class ChatRomDBHelper {

    public static final String TABLE_NAME = "ChatRoomTbl";
    public static final String ID = "Id";

    //private long RoomNo;
    public static final String ROOM_NO = "room_no";
    public static final String MAKE_USER_NO = "make_user_no";
    public static final String MOD_DATE = "mod_date";
    public static final String IS_ONE = "is_one";
    public static final String ROOM_TITLE = "room_title";
    public static final String LASTED_MSG = "lasted_msg";
    public static final String LASTED_MSG_DATE = "lasted_msg_date";
    public static final String LASTED_MSG_USER_NO = "lasted_msg_user_no";

    public static final String LASTED_MSG_TYPE = "lasted_msg_type";
    public static final String LASTED_MSG_ATTACH_TYPE = "lasted_msg_attach_type";

    public static final String USER_NOS = "user_nos";
    public static final String WRITER_USER = "writer_user";
    public static final String WRITER_USER_NO = "writer_user_no";
    public static final String MESSAGE_NO = "message_no";
    public static final String USER_NO = "user_no";
    public static final String MESSAGE = "message";
    public static final String TYPE = "type";
    public static final String ATTACH_NO = "attach_no";
    public static final String REG_DATE = "reg_date";
    public static final String UNREAD_COUNT = "unread_count";
    public static final String IS_CHECK_FROM_SERVER = "is_check_from_server";
    public static final String ATTACH_FILE_NAME = "attach_file_name";
    public static final String ATTACH_FILE_TYPE = "attach_file_type";
    public static final String ATTACH_FILE_PATH = "attach_file_path";
    public static final String ATTACH_FILE_SIZE = "attach_file_size";
    public static final String UNREAD_TOTAL_COUNT = "unread_total_count";

    // Custom field on local
    public static final String IS_FAVORITE = "is_favorite";
    public static final String IS_NOTIFICATION = "is_notification";


    public static final String SQL_EXCUTE = "CREATE TABLE " + TABLE_NAME + "("
            + ID + " integer primary key autoincrement not null,"
            + ROOM_NO +" integer,"
            + MAKE_USER_NO + " integer, "
            + MOD_DATE + " text, "
            + IS_ONE + " integer, "
            + LASTED_MSG_USER_NO + " integer, "
            + ROOM_TITLE + " text, "
            + LASTED_MSG + " text, "
            + LASTED_MSG_TYPE + " integer, "
            + LASTED_MSG_ATTACH_TYPE + " integer, "
            + LASTED_MSG_DATE + " text, "
            + USER_NOS + " text, "
            + WRITER_USER + " integer, "
            + WRITER_USER_NO + " integer, "
            + MESSAGE_NO + " integer, "
            + USER_NO + " integer, "
            + MESSAGE + " text, "
            + TYPE + " integer, "
            + ATTACH_NO + " integer, "
            + REG_DATE + " text, "
            + UNREAD_COUNT + " integer, "
            + IS_CHECK_FROM_SERVER + " integer, "
            + ATTACH_FILE_NAME + " text, "
            + ATTACH_FILE_TYPE + " integer, "
            + ATTACH_FILE_PATH + " text, "
            + ATTACH_FILE_SIZE + " integer, "
            + IS_FAVORITE + " integer, "
            + IS_NOTIFICATION + " integer, "
            + UNREAD_TOTAL_COUNT + " integer );";

    /*
    * This function to add one of message line
    * */
    public static boolean addChatRoom(ChattingDto dto){
        try {
            ContentValues values = new ContentValues();
            values.put(ROOM_NO, dto.getRoomNo());
            values.put(MAKE_USER_NO, dto.getMakeUserNo());
            values.put(MOD_DATE, dto.getModdate());
            int isOne = 0;
            if (dto.isOne()){isOne = 1;}
            values.put(IS_ONE, isOne);
            values.put(ROOM_TITLE, dto.getRoomTitle());
            values.put(LASTED_MSG, dto.getLastedMsg());
            values.put(LASTED_MSG_DATE, dto.getLastedMsgDate());
            values.put(LASTED_MSG_USER_NO, dto.getMsgUserNo());

            values.put(LASTED_MSG_TYPE, dto.getLastedMsgType());
            values.put(LASTED_MSG_ATTACH_TYPE, dto.getLastedMsgAttachType());

            String userNos = TextUtils.join(",", dto.getUserNos());
            Utils.printLogs("User nos = "+userNos);
            values.put(USER_NOS, userNos);

            values.put(WRITER_USER, dto.getWriterUser());
            values.put(WRITER_USER_NO, dto.getWriterUserNo());
            values.put(MESSAGE_NO, dto.getMessageNo());
            values.put(USER_NO, dto.getUserNo());
            values.put(MESSAGE, dto.getMessage());
            values.put(TYPE, dto.getType());
            values.put(ATTACH_NO, dto.getAttachNo());
            values.put(REG_DATE, dto.getRegDate());
            values.put(UNREAD_COUNT, dto.getUnReadCount());
            int isCheck = 0;
            if (dto.isCheckFromServer()){ isCheck = 1;}
            values.put(IS_CHECK_FROM_SERVER, isCheck);

            values.put(ATTACH_FILE_NAME, dto.getAttachFileName());
            values.put(ATTACH_FILE_TYPE, dto.getAttachFileType());
            values.put(ATTACH_FILE_PATH, dto.getAttachFilePath());
            values.put(ATTACH_FILE_SIZE, dto.getAttachFileSize());

            int isFavorite = dto.isFavorite() ? 1 : 0;

            values.put(IS_FAVORITE, isFavorite);

            int isNotification = dto.isNotification() ? 1 : 0;
            values.put(IS_NOTIFICATION, isNotification);

            values.put(UNREAD_TOTAL_COUNT, dto.getUnreadTotalCount());

            ContentResolver resolver = CrewChatApplication.getInstance()
                    .getApplicationContext().getContentResolver();
            resolver.insert(AppContentProvider.GET_CHAT_ROOM_CONTENT_URI, values);
            return true;
        } catch (Exception e) {
            // TODO: handle exception
            Utils.printLogs("Catch = "+e.getMessage());
        }
        return false;
    }

    public static boolean updateChatRoom(long roomNo, String lastedMsg, int lastedMsgType, int lastedMsgAttachType, String lastedMsgDate, int unreadCountTotal, int unreadCount, long lastMsgUserNo){
        try {

            ContentResolver resolver = CrewChatApplication.getInstance()
                    .getApplicationContext().getContentResolver();

            ContentValues conValues = new ContentValues();
            conValues.put(LASTED_MSG, lastedMsg);
            conValues.put(LASTED_MSG_TYPE, lastedMsgType);
            conValues.put(LASTED_MSG_ATTACH_TYPE, lastedMsgAttachType);
            conValues.put(LASTED_MSG_DATE, lastedMsgDate);
            conValues.put(LASTED_MSG_USER_NO, lastMsgUserNo);
            conValues.put(UNREAD_TOTAL_COUNT, unreadCountTotal);
            conValues.put(UNREAD_COUNT, unreadCount);
            resolver.update(AppContentProvider.GET_CHAT_ROOM_CONTENT_URI, conValues, ROOM_NO + "=" + roomNo, null);
            return true;

        } catch (Exception e) {
            // TODO: handle exception
            Utils.printLogs("Update chatroom error ####");
        }

        return false;
    }

    public static boolean updateChatRoom(long roomNo, String roomTitle){
        try {

            ContentResolver resolver = CrewChatApplication.getInstance()
                    .getApplicationContext().getContentResolver();

            ContentValues conValues = new ContentValues();
            conValues.put(ROOM_TITLE, roomTitle);
            resolver.update(AppContentProvider.GET_CHAT_ROOM_CONTENT_URI, conValues, ROOM_NO + "=" + roomNo, null);
            return true;

        } catch (Exception e) {
            // TODO: handle exception
            Utils.printLogs("Update chatroom error ####");
        }

        return false;
    }

    public static int getUnreadCount(long roomNo){
        String[] columns = new String[] { "*"};
        ContentResolver resolver = CrewChatApplication.getInstance()
                .getApplicationContext().getContentResolver();
        Cursor cursor = resolver.query(
                AppContentProvider.GET_CHAT_ROOM_CONTENT_URI, columns, ROOM_NO + "=" + roomNo,
                null, null);
        if(cursor!=null){
            cursor.moveToFirst();
            int unReadCount = Integer.parseInt(cursor.getString(cursor.getColumnIndex(UNREAD_TOTAL_COUNT)));
            cursor.close();
            return  unReadCount;
        }
        return 0;
    }

    public static boolean deleteChatRoom(long roomNo){
        try {
            ContentResolver resolver = CrewChatApplication.getInstance()
                    .getApplicationContext().getContentResolver();
            resolver.delete(AppContentProvider.GET_CHAT_ROOM_CONTENT_URI, ROOM_NO + "=" + roomNo, null);
            return true;

        } catch (Exception e) {
            // TODO: handle exception
            Utils.printLogs("Update chatroom error ####");
        }

        return false;
    }

    public static boolean updateUnreadTotalCountChatRoom(long roomNo, long unreadCountTotal){
        try {

            ContentResolver resolver = CrewChatApplication.getInstance()
                    .getApplicationContext().getContentResolver();

            ContentValues conValues = new ContentValues();
            conValues.put(UNREAD_TOTAL_COUNT, unreadCountTotal);
            conValues.put(UNREAD_COUNT, 0);
            resolver.update(AppContentProvider.GET_CHAT_ROOM_CONTENT_URI, conValues, ROOM_NO + "=" + roomNo, null);
            return true;

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }

        return false;
    }

    public static boolean updateChatRoomNotification(long roomNo, boolean isNotification){
        try {

            ContentResolver resolver = CrewChatApplication.getInstance()
                    .getApplicationContext().getContentResolver();

            ContentValues conValues = new ContentValues();
            int notification = isNotification? 1: 0;
            conValues.put(IS_NOTIFICATION, notification);

            resolver.update(AppContentProvider.GET_CHAT_ROOM_CONTENT_URI, conValues, ROOM_NO + "=" + roomNo, null);
            return true;

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }

        return false;
    }

    public static boolean updateChatRoomFavorite(long roomNo, boolean isFavorite){
        try {

            ContentResolver resolver = CrewChatApplication.getInstance()
                    .getApplicationContext().getContentResolver();

            ContentValues conValues = new ContentValues();
            int isFavoriteValue = isFavorite ? 1: 0;
            conValues.put(IS_FAVORITE, isFavoriteValue);
            resolver.update(AppContentProvider.GET_CHAT_ROOM_CONTENT_URI, conValues, ROOM_NO + "=" + roomNo, null);
            return true;

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }

        return false;
    }





    /*
    * This function to get all chat list, short by time
    * */
    public static ArrayList<ChattingDto> getChatRooms(){

        ArrayList<ChattingDto> mesArray = new ArrayList<>();
        String[] columns = new String[] { "*"};
        ContentResolver resolver = CrewChatApplication.getInstance()
                .getApplicationContext().getContentResolver();
        Cursor cursor = resolver.query(
                AppContentProvider.GET_CHAT_ROOM_CONTENT_URI, columns, null,
                null, null);
        if(cursor!=null){
            if(cursor.getCount()>0){
                try {
                    while (cursor.moveToNext())
                    {
                        ChattingDto chattingDto = new ChattingDto();

                        chattingDto.setId(parseInt(cursor.getString(cursor.getColumnIndex(ID))));
                        chattingDto.setRoomNo(parseInt(cursor.getString(cursor.getColumnIndex(ROOM_NO))));
                        chattingDto.setMakeUserNo(parseInt(cursor.getString(cursor.getColumnIndex(MAKE_USER_NO))));
                        chattingDto.setModdate(cursor.getString(cursor.getColumnIndex(MOD_DATE)));
                        boolean is_one = cursor.getInt(cursor.getColumnIndex(IS_ONE)) == 1;
                        chattingDto.setOne(is_one);
                        chattingDto.setRoomTitle(cursor.getString(cursor.getColumnIndex(ROOM_TITLE)));
                        chattingDto.setLastedMsg(cursor.getString(cursor.getColumnIndex(LASTED_MSG)));
                        chattingDto.setLastedMsgDate(cursor.getString(cursor.getColumnIndex(LASTED_MSG_DATE)));

                        String sUserNos = cursor.getString(cursor.getColumnIndex(USER_NOS));
                        if (sUserNos != null){
                            String [] elements = sUserNos.split(",");
                            ArrayList<Integer> userNosLList = new ArrayList<>();
                            for(String temp : elements){
                                userNosLList.add(parseInt(temp.trim()));
                            }
                            chattingDto.setUserNos(userNosLList);
                        }

                        chattingDto.setLastedMsgType(parseInt(cursor.getString(cursor.getColumnIndex(LASTED_MSG_TYPE))));
                        chattingDto.setLastedMsgAttachType(parseInt(cursor.getString(cursor.getColumnIndex(LASTED_MSG_ATTACH_TYPE))));

                        chattingDto.setWriterUser(parseInt(cursor.getString(cursor.getColumnIndex(WRITER_USER))));
                        chattingDto.setWriterUserNo(parseInt(cursor.getString(cursor.getColumnIndex(WRITER_USER_NO))));
                        chattingDto.setMessageNo(parseInt(cursor.getString(cursor.getColumnIndex(MESSAGE_NO))));
                        chattingDto.setUserNo(parseInt(cursor.getString(cursor.getColumnIndex(USER_NO))));
                        chattingDto.setMessage(cursor.getString(cursor.getColumnIndex(MESSAGE)));
                        chattingDto.setType(parseInt(cursor.getString(cursor.getColumnIndex(TYPE))));
                        chattingDto.setAttachNo(parseInt(cursor.getString(cursor.getColumnIndex(ATTACH_NO))));

                        chattingDto.setMsgUserNo(parseInt(cursor.getString(cursor.getColumnIndex(LASTED_MSG_USER_NO))));
                        chattingDto.setRegDate(cursor.getString(cursor.getColumnIndex(REG_DATE)));
                        chattingDto.setUnReadCount(parseInt(cursor.getString(cursor.getColumnIndex(UNREAD_COUNT))));
                        chattingDto.setCheckFromServer(parseInt(cursor.getString(cursor.getColumnIndex(IS_CHECK_FROM_SERVER))) == 1);
                        chattingDto.setAttachFileName(cursor.getString(cursor.getColumnIndex(ATTACH_FILE_NAME)));
                        chattingDto.setAttachFileType(parseInt(cursor.getString(cursor.getColumnIndex(ATTACH_FILE_TYPE))));
                        chattingDto.setAttachFilePath(cursor.getString(cursor.getColumnIndex(ATTACH_FILE_PATH)));
                        chattingDto.setAttachFileSize(parseInt(cursor.getString(cursor.getColumnIndex(ATTACH_FILE_SIZE))));
                        chattingDto.setUnreadTotalCount(parseInt(cursor.getString(cursor.getColumnIndex(UNREAD_TOTAL_COUNT))));

                        boolean isFavorite = parseInt(cursor.getString(cursor.getColumnIndex(IS_FAVORITE))) == 1;
                        chattingDto.setFavorite(isFavorite);

                        boolean isNotification = parseInt(cursor.getString(cursor.getColumnIndex(IS_NOTIFICATION))) == 1;
                        chattingDto.setNotification(isNotification);

                        mesArray.add(chattingDto);
                    }

                }finally {
                    cursor.close();
                }

            }
            cursor.close();
        }
        return mesArray;
    }

    /*
    * This function to get all chat list, short by time
    * */
    public static ArrayList<ChattingDto> getFavoriteChatRooms(){

        ArrayList<ChattingDto> mesArray = new ArrayList<>();
        String[] columns = new String[] { "*"};
        ContentResolver resolver = CrewChatApplication.getInstance()
                .getApplicationContext().getContentResolver();
        Cursor cursor = resolver.query(
                AppContentProvider.GET_CHAT_ROOM_CONTENT_URI, columns, IS_FAVORITE + " = "+ 1,
                null, null);
        if(cursor!=null){
            if(cursor.getCount()>0){
                try {
                    while (cursor.moveToNext())
                    {
                        ChattingDto chattingDto = new ChattingDto();

                        chattingDto.setId(parseInt(cursor.getString(cursor.getColumnIndex(ID))));
                        chattingDto.setRoomNo(parseInt(cursor.getString(cursor.getColumnIndex(ROOM_NO))));
                        chattingDto.setMakeUserNo(parseInt(cursor.getString(cursor.getColumnIndex(MAKE_USER_NO))));
                        chattingDto.setModdate(cursor.getString(cursor.getColumnIndex(MOD_DATE)));
                        boolean is_one = cursor.getInt(cursor.getColumnIndex(IS_ONE)) == 1;
                        chattingDto.setOne(is_one);
                        chattingDto.setRoomTitle(cursor.getString(cursor.getColumnIndex(ROOM_TITLE)));
                        chattingDto.setLastedMsg(cursor.getString(cursor.getColumnIndex(LASTED_MSG)));
                        chattingDto.setLastedMsgDate(cursor.getString(cursor.getColumnIndex(LASTED_MSG_DATE)));

                        String sUserNos = cursor.getString(cursor.getColumnIndex(USER_NOS));
                        if (sUserNos != null){
                            String [] elements = sUserNos.split(",");
                            ArrayList<Integer> userNosLList = new ArrayList<>();
                            for(String temp : elements){
                                userNosLList.add(parseInt(temp.trim()));
                            }
                            chattingDto.setUserNos(userNosLList);
                        }

                        chattingDto.setLastedMsgType(parseInt(cursor.getString(cursor.getColumnIndex(LASTED_MSG_TYPE))));
                        chattingDto.setLastedMsgAttachType(parseInt(cursor.getString(cursor.getColumnIndex(LASTED_MSG_ATTACH_TYPE))));

                        chattingDto.setWriterUser(parseInt(cursor.getString(cursor.getColumnIndex(WRITER_USER))));
                        chattingDto.setWriterUserNo(parseInt(cursor.getString(cursor.getColumnIndex(WRITER_USER_NO))));
                        chattingDto.setMessageNo(parseInt(cursor.getString(cursor.getColumnIndex(MESSAGE_NO))));
                        chattingDto.setUserNo(parseInt(cursor.getString(cursor.getColumnIndex(USER_NO))));
                        chattingDto.setMessage(cursor.getString(cursor.getColumnIndex(MESSAGE)));
                        chattingDto.setType(parseInt(cursor.getString(cursor.getColumnIndex(TYPE))));
                        chattingDto.setAttachNo(parseInt(cursor.getString(cursor.getColumnIndex(ATTACH_NO))));

                        chattingDto.setMsgUserNo(parseInt(cursor.getString(cursor.getColumnIndex(LASTED_MSG_USER_NO))));
                        chattingDto.setRegDate(cursor.getString(cursor.getColumnIndex(REG_DATE)));
                        chattingDto.setUnReadCount(parseInt(cursor.getString(cursor.getColumnIndex(UNREAD_COUNT))));
                        chattingDto.setCheckFromServer(parseInt(cursor.getString(cursor.getColumnIndex(IS_CHECK_FROM_SERVER))) == 1);
                        chattingDto.setAttachFileName(cursor.getString(cursor.getColumnIndex(ATTACH_FILE_NAME)));
                        chattingDto.setAttachFileType(parseInt(cursor.getString(cursor.getColumnIndex(ATTACH_FILE_TYPE))));
                        chattingDto.setAttachFilePath(cursor.getString(cursor.getColumnIndex(ATTACH_FILE_PATH)));
                        chattingDto.setAttachFileSize(parseInt(cursor.getString(cursor.getColumnIndex(ATTACH_FILE_SIZE))));
                        chattingDto.setUnreadTotalCount(parseInt(cursor.getString(cursor.getColumnIndex(UNREAD_TOTAL_COUNT))));

                        boolean isFavorite = parseInt(cursor.getString(cursor.getColumnIndex(IS_FAVORITE))) == 1;
                        chattingDto.setFavorite(isFavorite);

                        boolean isNotification = parseInt(cursor.getString(cursor.getColumnIndex(IS_NOTIFICATION))) == 1;
                        chattingDto.setNotification(isNotification);

                        mesArray.add(chattingDto);
                    }

                }finally {
                    cursor.close();
                }

            }
            cursor.close();
        }
        return mesArray;
    }

    public static boolean clearChatRooms() {
        try {

            ContentResolver resolver = CrewChatApplication.getInstance()
                    .getApplicationContext().getContentResolver();
            resolver.delete(AppContentProvider.GET_CHAT_ROOM_CONTENT_URI, null,null);
            return true;
        } catch (Exception e) {
            // TODO: handle exception
        }
        return false;
    }
}
