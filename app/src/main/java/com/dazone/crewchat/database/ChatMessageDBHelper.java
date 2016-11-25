package com.dazone.crewchat.database;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;

import com.dazone.crewchat.dto.AttachDTO;
import com.dazone.crewchat.dto.ChattingDto;
import com.dazone.crewchat.utils.CrewChatApplication;
import com.dazone.crewchat.utils.Utils;

import java.util.ArrayList;

/**
 * Created by Admin on 5/20/2016.
 */
public class ChatMessageDBHelper {

    public static final String TABLE_NAME = "ChatMessageTbl";
    public static final String ID = "Id";

    //private long RoomNo;
    public static final String ROOM_NO = "room_no";
    public static final String MAKE_USER_NO = "make_user_no";
    public static final String MOD_DATE = "mod_date";
    public static final String IS_ONE = "is_one";
    public static final String ROOM_TITLE = "room_title";
    public static final String LASTED_MSG = "lasted_msg";
    public static final String LASTED_MSG_DATE = "lasted_msg_date";
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
    public static final String HAS_SENT = "message_has_sent";

    public static final String SQL_EXCUTE = "CREATE TABLE " + TABLE_NAME + "("
            + ID + " integer primary key autoincrement not null,"
            + ROOM_NO + " integer DEFAULT 0,"
            + MAKE_USER_NO + " integer DEFAULT 0, "
            + MOD_DATE + " text, "
            + IS_ONE + " integer DEFAULT 0, "
            + ROOM_TITLE + " text, "
            + LASTED_MSG + " text, "
            + LASTED_MSG_DATE + " text, "
            + USER_NOS + " text, "
            + WRITER_USER + " integer DEFAULT 0, "
            + WRITER_USER_NO + " integer DEFAULT 0, "
            + MESSAGE_NO + " integer DEFAULT 0, "
            + USER_NO + " integer DEFAULT 0, "
            + MESSAGE + " text, "
            + TYPE + " integer DEFAULT 0, "
            + ATTACH_NO + " integer DEFAULT 0, "
            + REG_DATE + " text, "
            + UNREAD_COUNT + " integer DEFAULT 0, "
            + IS_CHECK_FROM_SERVER + " integer DEFAULT 0, "
            + ATTACH_FILE_NAME + " text, "
            + ATTACH_FILE_TYPE + " integer DEFAULT 0, "
            + ATTACH_FILE_PATH + " text, "
            + ATTACH_FILE_SIZE + " integer DEFAULT 0, "
            + UNREAD_TOTAL_COUNT + " integer DEFAULT 0, "
            + HAS_SENT + " integer DEFAULT 1);";

    public static boolean isExist(ChattingDto dto) {

        String[] columns = new String[]{"*"};
        ContentResolver resolver = CrewChatApplication.getInstance()
                .getApplicationContext().getContentResolver();
        Cursor cursor = resolver.query(
                AppContentProvider.GET_MESSAGE_CONTENT_URI, columns, MESSAGE_NO + "=" + dto.getMessageNo(), null, null);

        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.close();
                return true;
            }
            cursor.close();
        }

        return false;
    }

    /*
    * Function to delete an message on local database
    * */
    public static boolean deleteMessage(long messageNo) {
        try {
            ContentResolver resolver = CrewChatApplication.getInstance()
                    .getApplicationContext().getContentResolver();
            resolver.delete(AppContentProvider.GET_MESSAGE_CONTENT_URI, MESSAGE_NO + "=" + messageNo, null);
            return true;

        } catch (Exception e) {
            // TODO: handle exception
            Utils.printLogs("Delete message error ####");
        }
        return false;
    }

    /*
  * Function to delete an message on local database by local ID
  * */
    public static boolean deleteMessageByLocalID(long messageID) {
        try {
            ContentResolver resolver = CrewChatApplication.getInstance()
                    .getApplicationContext().getContentResolver();
            resolver.delete(AppContentProvider.GET_MESSAGE_CONTENT_URI, ID + "=" + messageID, null);
            return true;

        } catch (Exception e) {
            // TODO: handle exception
            Utils.printLogs("Delete message error ####");
        }
        return false;
    }

    /*
    * Function to update message maybe happen when user is edit message
    * When message has been sent to server success and received success notification
    * Then we will update to local database
    * */
    public static boolean updateMessage(ChattingDto dto, long id) {
        try {

            ContentValues values = new ContentValues();
            values.put(UNREAD_COUNT, dto.getUnReadCount());
            values.put(ATTACH_NO, dto.getAttachNo());
            values.put(MESSAGE, dto.getMessage());
            values.put(MESSAGE_NO, dto.getMessageNo());
            values.put(REG_DATE, dto.getRegDate());
            int hasSentValue = dto.isHasSent() ? 1 : 0;
            values.put(HAS_SENT, hasSentValue);

            ContentResolver resolver = CrewChatApplication.getInstance()
                    .getApplicationContext().getContentResolver();
            resolver.update(AppContentProvider.GET_MESSAGE_CONTENT_URI, values, ROOM_NO + " = " + dto.getRoomNo() + " AND " + ID + " = " + id, null);
            return true;

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return false;
    }

        /*
    * Update message unread count
    * */

    public static boolean updateMessage(long id, int unreadCount) {
        try {
            ContentValues values = new ContentValues();
            values.put(UNREAD_COUNT, unreadCount);
            ContentResolver resolver = CrewChatApplication.getInstance()
                    .getApplicationContext().getContentResolver();
            resolver.update(AppContentProvider.GET_MESSAGE_CONTENT_URI, values, MESSAGE_NO + " = " + id, null);
            return true;

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return false;
    }

    /*
    * Add a new simple message from Edit text and save to local database
    * */
    public static long addSimpleMessage(ChattingDto dto) {
        try {
            ContentValues values = new ContentValues();
            values.put(MESSAGE, dto.getMessage());
            values.put(MESSAGE_NO, dto.getMessageNo());
            values.put(USER_NO, dto.getUserNo());
            values.put(TYPE, dto.getType());
            values.put(ATTACH_FILE_PATH, dto.getAttachFilePath());
            values.put(ROOM_NO, dto.getRoomNo());
            values.put(WRITER_USER, dto.getWriterUser());
            values.put(REG_DATE, dto.getRegDate());

            int hasSent = dto.isHasSent() ? 1 : 0;
            values.put(HAS_SENT, hasSent);

            ContentResolver resolver = CrewChatApplication.getInstance()
                    .getApplicationContext().getContentResolver();
            Uri uri = resolver.insert(AppContentProvider.GET_MESSAGE_CONTENT_URI, values);
            return ContentUris.parseId(uri);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    /*
    * This function to add one of message line
    * */
    public static boolean addMessage(ChattingDto dto) {
        try {
            if (!isExist(dto)) {
                ContentValues values = new ContentValues();
                values.put(ROOM_NO, dto.getRoomNo());
                values.put(MAKE_USER_NO, dto.getMakeUserNo());
                values.put(MOD_DATE, dto.getModdate());
                int isOne = 0;
                if (dto.isOne()) {
                    isOne = 1;
                }
                values.put(IS_ONE, isOne);
                values.put(ROOM_TITLE, dto.getRoomTitle());
                values.put(LASTED_MSG, dto.getLastedMsg());
                values.put(LASTED_MSG_DATE, dto.getLastedMsgDate());

                if (dto.getUserNos() != null) {
                    String userNos = TextUtils.join(",", dto.getUserNos());
                    values.put(USER_NOS, userNos);
                }


                values.put(WRITER_USER, dto.getWriterUser());
                values.put(WRITER_USER_NO, dto.getWriterUserNo());
                values.put(MESSAGE_NO, dto.getMessageNo());
                values.put(USER_NO, dto.getUserNo());
                values.put(MESSAGE, dto.getMessage());
                values.put(TYPE, dto.getType());
                values.put(REG_DATE, dto.getRegDate());
                values.put(UNREAD_COUNT, dto.getUnReadCount());
                int isCheck = 0;
                if (dto.isCheckFromServer()) {
                    isCheck = 1;
                }
                values.put(IS_CHECK_FROM_SERVER, isCheck);

                // Get attach file info and store it to database
                AttachDTO attachInfo = dto.getAttachInfo();
                if (attachInfo != null) {
                    values.put(ATTACH_NO, attachInfo.getAttachNo());
                    values.put(ATTACH_FILE_NAME, attachInfo.getFileName());
                    values.put(ATTACH_FILE_TYPE, attachInfo.getType());
                    values.put(ATTACH_FILE_PATH, attachInfo.getFullPath());
                    values.put(ATTACH_FILE_SIZE, attachInfo.getSize());

                } else {
                    values.put(ATTACH_NO, dto.getAttachNo());
                    values.put(ATTACH_FILE_NAME, dto.getAttachFileName());
                    values.put(ATTACH_FILE_TYPE, dto.getAttachFileType());
                    values.put(ATTACH_FILE_PATH, dto.getAttachFilePath());
                    values.put(ATTACH_FILE_SIZE, dto.getAttachFileSize());
                }
                values.put(UNREAD_TOTAL_COUNT, dto.getUnreadTotalCount());

                ContentResolver resolver = CrewChatApplication.getInstance()
                        .getApplicationContext().getContentResolver();
                resolver.insert(AppContentProvider.GET_MESSAGE_CONTENT_URI, values);
            }

            // else maybe update data

            return true;

        } catch (Exception e) {
            // TODO: handle exception
            Utils.printLogs("Catch = " + e.getMessage());
        }
        return false;
    }



    // Constant type to get message session
    public static int NONE = 0, FIRST = 1, BEFORE = 2, AFTER = 3;

    public static ArrayList<ChattingDto> getMsgSession(long roomNo, long baseMsgNo, int type) {
        if (type == NONE) { // currently no use
            return null;
        }

        ArrayList<ChattingDto> mesArray = new ArrayList<>();
        String[] columns = new String[]{"*"};
        ContentResolver resolver = CrewChatApplication.getInstance()
                .getApplicationContext().getContentResolver();

        String conditions = ROOM_NO + " = " + roomNo;
        if (type == FIRST) {
            conditions += " AND " + MESSAGE_NO + " > " + baseMsgNo;
        } else if (type == BEFORE) {
            conditions += " AND " + MESSAGE_NO + " < " + baseMsgNo;
        }

        Utils.printLogs("Condition string = " + conditions);
        // If type = NONE or AFTER nothing
        Cursor cursor = resolver.query(
                AppContentProvider.GET_MESSAGE_CONTENT_URI, columns, conditions,
                null
                , MESSAGE_NO + " DESC LIMIT 20");
        // Get and return data
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                try {
                    cursor.moveToLast();
                    while (cursor.moveToPrevious()) {
                        ChattingDto chattingDto = new ChattingDto();

                        chattingDto.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(ID))));
                        chattingDto.setRoomNo(Integer.parseInt(cursor.getString(cursor.getColumnIndex(ROOM_NO))));

                        // convert string to makeUserNo (long)
                        String makeUserNo = cursor.getString(cursor.getColumnIndex(MAKE_USER_NO));
                        int makeUserNoLong = makeUserNo == null ? 0 : Integer.parseInt(makeUserNo);
                        chattingDto.setMakeUserNo(makeUserNoLong);

                        chattingDto.setModdate(cursor.getString(cursor.getColumnIndex(MOD_DATE)));
                        boolean is_one = cursor.getInt(cursor.getColumnIndex(IS_ONE)) == 1;
                        chattingDto.setOne(is_one);
                        chattingDto.setRoomTitle(cursor.getString(cursor.getColumnIndex(ROOM_TITLE)));
                        chattingDto.setLastedMsg(cursor.getString(cursor.getColumnIndex(LASTED_MSG)));
                        chattingDto.setLastedMsgDate(cursor.getString(cursor.getColumnIndex(LASTED_MSG_DATE)));

                        String sUserNos = cursor.getString(cursor.getColumnIndex(USER_NOS));
                        if (sUserNos != null) {
                            String[] elements = sUserNos.split(",");
                            ArrayList<Integer> userNosLList = new ArrayList<>();
                            for (String temp : elements) {
                                userNosLList.add(Integer.parseInt(temp.trim()));
                            }
                            chattingDto.setUserNos(userNosLList);
                        }

                        chattingDto.setWriterUser(Integer.parseInt(cursor.getString(cursor.getColumnIndex(WRITER_USER))));

                        String writeUserNo = cursor.getString(cursor.getColumnIndex(WRITER_USER_NO));
                        int writeUserNoLong = writeUserNo == null ? 0 : Integer.parseInt(writeUserNo);
                        chattingDto.setWriterUserNo(writeUserNoLong);

                        chattingDto.setMessageNo(Long.parseLong(cursor.getString(cursor.getColumnIndex(MESSAGE_NO))));
                        chattingDto.setUserNo(Integer.parseInt(cursor.getString(cursor.getColumnIndex(USER_NO))));
                        chattingDto.setMessage(cursor.getString(cursor.getColumnIndex(MESSAGE)));
                        chattingDto.setType(Integer.parseInt(cursor.getString(cursor.getColumnIndex(TYPE))));
                        chattingDto.setAttachNo(Integer.parseInt(cursor.getString(cursor.getColumnIndex(ATTACH_NO))));

                        chattingDto.setRegDate(cursor.getString(cursor.getColumnIndex(REG_DATE)));
                        chattingDto.setUnReadCount(Integer.parseInt(cursor.getString(cursor.getColumnIndex(UNREAD_COUNT))));

                        String isCheckFromServer = cursor.getString(cursor.getColumnIndex(IS_CHECK_FROM_SERVER));
                        int isCheckFromServerLong = isCheckFromServer == null ? 0 : Integer.parseInt(isCheckFromServer);
                        chattingDto.setCheckFromServer(isCheckFromServerLong == 1);

                        chattingDto.setAttachFileName(cursor.getString(cursor.getColumnIndex(ATTACH_FILE_NAME)));
                        chattingDto.setAttachFileType(Integer.parseInt(cursor.getString(cursor.getColumnIndex(ATTACH_FILE_TYPE))));
                        chattingDto.setAttachFilePath(cursor.getString(cursor.getColumnIndex(ATTACH_FILE_PATH)));
                        chattingDto.setAttachFileSize(Integer.parseInt(cursor.getString(cursor.getColumnIndex(ATTACH_FILE_SIZE))));
                        chattingDto.setUnreadTotalCount(Integer.parseInt(cursor.getString(cursor.getColumnIndex(UNREAD_TOTAL_COUNT))));

                        int hasSendValue = Integer.parseInt(cursor.getString(cursor.getColumnIndex(HAS_SENT)));
                        boolean isHasSent = hasSendValue == 1;
                        chattingDto.setHasSent(isHasSent);

                        AttachDTO attachInfo = new AttachDTO();
                        attachInfo.setType(Integer.parseInt(cursor.getString(cursor.getColumnIndex(ATTACH_FILE_TYPE))));
                        attachInfo.setAttachNo(Integer.parseInt(cursor.getString(cursor.getColumnIndex(ATTACH_NO))));
                        attachInfo.setSize(Integer.parseInt(cursor.getString(cursor.getColumnIndex(ATTACH_FILE_SIZE))));
                        attachInfo.setFullPath(cursor.getString(cursor.getColumnIndex(ATTACH_FILE_PATH)));
                        attachInfo.setFileName(cursor.getString(cursor.getColumnIndex(ATTACH_FILE_NAME)));

                        chattingDto.setAttachInfo(attachInfo);

                        mesArray.add(chattingDto);
                    }

                } finally {
                    cursor.close();
                }

            }
            cursor.close();
        }

        return mesArray;
    }

    /*
    * This function to get all chat list, short by time
    * Get chat message by session like server session
    * */
    public static ArrayList<ChattingDto> getMessages(long roomNo) {

        ArrayList<ChattingDto> mesArray = new ArrayList<>();
        String[] columns = new String[]{"*"};
        ContentResolver resolver = CrewChatApplication.getInstance()
                .getApplicationContext().getContentResolver();
        Cursor cursor = resolver.query(
                AppContentProvider.GET_MESSAGE_CONTENT_URI, columns, ROOM_NO + " = " + roomNo,
                null
                , MESSAGE_NO + " ASC");
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                try {
                    while (cursor.moveToNext()) {
                        ChattingDto chattingDto = new ChattingDto();

                        chattingDto.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(ID))));
                        chattingDto.setRoomNo(Integer.parseInt(cursor.getString(cursor.getColumnIndex(ROOM_NO))));
                        chattingDto.setMakeUserNo(Integer.parseInt(cursor.getString(cursor.getColumnIndex(MAKE_USER_NO))));
                        chattingDto.setModdate(cursor.getString(cursor.getColumnIndex(MOD_DATE)));
                        boolean is_one = cursor.getInt(cursor.getColumnIndex(IS_ONE)) == 1;
                        chattingDto.setOne(is_one);
                        chattingDto.setRoomTitle(cursor.getString(cursor.getColumnIndex(ROOM_TITLE)));
                        chattingDto.setLastedMsg(cursor.getString(cursor.getColumnIndex(LASTED_MSG)));
                        chattingDto.setLastedMsgDate(cursor.getString(cursor.getColumnIndex(LASTED_MSG_DATE)));

                        String sUserNos = cursor.getString(cursor.getColumnIndex(USER_NOS));
                        if (sUserNos != null) {
                            String[] elements = sUserNos.split(",");
                            ArrayList<Integer> userNosLList = new ArrayList<>();
                            for (String temp : elements) {
                                userNosLList.add(Integer.parseInt(temp.trim()));
                            }
                            chattingDto.setUserNos(userNosLList);
                        }

                        chattingDto.setWriterUser(Integer.parseInt(cursor.getString(cursor.getColumnIndex(WRITER_USER))));
                        chattingDto.setWriterUserNo(Integer.parseInt(cursor.getString(cursor.getColumnIndex(WRITER_USER_NO))));
                        chattingDto.setMessageNo(Long.parseLong(cursor.getString(cursor.getColumnIndex(MESSAGE_NO))));
                        chattingDto.setUserNo(Integer.parseInt(cursor.getString(cursor.getColumnIndex(USER_NO))));
                        chattingDto.setMessage(cursor.getString(cursor.getColumnIndex(MESSAGE)));


                        chattingDto.setRegDate(cursor.getString(cursor.getColumnIndex(REG_DATE)));
                        chattingDto.setUnReadCount(Integer.parseInt(cursor.getString(cursor.getColumnIndex(UNREAD_COUNT))));
                        chattingDto.setCheckFromServer(Integer.parseInt(cursor.getString(cursor.getColumnIndex(IS_CHECK_FROM_SERVER))) == 1);

                        chattingDto.setType(Integer.parseInt(cursor.getString(cursor.getColumnIndex(TYPE))));
                        chattingDto.setAttachNo(Integer.parseInt(cursor.getString(cursor.getColumnIndex(ATTACH_NO))));
                        chattingDto.setAttachFileName(cursor.getString(cursor.getColumnIndex(ATTACH_FILE_NAME)));
                        chattingDto.setAttachFileType(Integer.parseInt(cursor.getString(cursor.getColumnIndex(ATTACH_FILE_TYPE))));
                        chattingDto.setAttachFilePath(cursor.getString(cursor.getColumnIndex(ATTACH_FILE_PATH)));
                        chattingDto.setAttachFileSize(Integer.parseInt(cursor.getString(cursor.getColumnIndex(ATTACH_FILE_SIZE))));

                        int hasSendValue = Integer.parseInt(cursor.getString(cursor.getColumnIndex(HAS_SENT)));
                        boolean isHasSent = hasSendValue == 1;
                        chattingDto.setHasSent(isHasSent);

                        AttachDTO attachInfo = new AttachDTO();
                        attachInfo.setType(Integer.parseInt(cursor.getString(cursor.getColumnIndex(ATTACH_FILE_TYPE))));
                        attachInfo.setAttachNo(Integer.parseInt(cursor.getString(cursor.getColumnIndex(ATTACH_NO))));
                        attachInfo.setSize(Integer.parseInt(cursor.getString(cursor.getColumnIndex(ATTACH_FILE_SIZE))));
                        attachInfo.setFullPath(cursor.getString(cursor.getColumnIndex(ATTACH_FILE_PATH)));
                        attachInfo.setFileName(cursor.getString(cursor.getColumnIndex(ATTACH_FILE_NAME)));

                        chattingDto.setAttachInfo(attachInfo);

                        chattingDto.setUnreadTotalCount(Integer.parseInt(cursor.getString(cursor.getColumnIndex(UNREAD_TOTAL_COUNT))));

                        mesArray.add(chattingDto);
                    }

                } finally {
                    cursor.close();
                }

            }
            cursor.close();
        }
        return mesArray;
    }

    public static boolean clearMessages() {
        try {

            ContentResolver resolver = CrewChatApplication.getInstance()
                    .getApplicationContext().getContentResolver();
            resolver.delete(AppContentProvider.GET_MESSAGE_CONTENT_URI, null, null);
            return true;
        } catch (Exception e) {
            // TODO: handle exception
        }
        return false;
    }
}
