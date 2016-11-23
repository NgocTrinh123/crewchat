package com.dazone.crewchat.utils;

import android.content.Context;
import com.dazone.crewchat.R;
import com.dazone.crewchat.constant.Statics;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by david on 12/23/15.
 */
public class TimeUtils {

    public static long GTMServer = 32400000;
    public static int KEY_FROM_SERVER = 200;
    public static int KEY_TO_SERVER = 201;

    public static String showTime(long date, String defaultPattern) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(defaultPattern, Locale.getDefault());
        return simpleDateFormat.format(new Date(date));
    }

    public static String showTimeWithoutTimeZone(long date, String defaultPattern) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(defaultPattern, Locale.getDefault());
        //simpleDateFormat.setTimeZone(TimeZone.getDefault());
        return simpleDateFormat.format(new Date(date));
    }

    public static String getTimezoneOffsetInMinutes() {
        TimeZone tz = TimeZone.getDefault();
        int offsetMinutes = tz.getRawOffset() / 60000;
        String sign = "";
        if (offsetMinutes < 0) {
            sign = "-";
            offsetMinutes = -offsetMinutes;
        }
        return sign + "" + offsetMinutes;
    }

    /**
     * Convert Time Device To Time Server
     */
    public static String convertTimeDeviceToTimeServer(String regDate) {
        String result;
        /** Time Zone */
        TimeZone timeZoneServer = TimeZone.getTimeZone("Asia/Seoul");
        TimeZone timeZoneDevice = TimeZone.getDefault();
        long timeCompared = timeZoneServer.getRawOffset() - timeZoneDevice.getRawOffset();


        /** Calculator */
        long time;

        if (regDate.contains("(")) {
            regDate = regDate.replace("/Date(", "");
            int plusIndex = regDate.indexOf("+");

            if (plusIndex != -1) {
                time = Long.valueOf(regDate.substring(0, plusIndex));
            } else {
                time = Long.valueOf(regDate.substring(0, regDate.indexOf(")")));
            }
        } else {
            time = Long.valueOf(regDate);
        }

        time = time - timeCompared;
        result = "/Date(" + time + ")/";

        return result;
    }

    /**
     * Convert Time Device To Time Server
     */
    public static String convertTimeDeviceToTimeServerDefault(String regDate) {
        String result;
        /** Calculator */
        long time;

        if (regDate.contains("(")) {
            regDate = regDate.replace("/Date(", "");
            int plusIndex = regDate.indexOf("+");

            if (plusIndex != -1) {
                time = Long.valueOf(regDate.substring(0, plusIndex));
            } else {
                time = Long.valueOf(regDate.substring(0, regDate.indexOf(")")));
            }
        } else {
            time = Long.valueOf(regDate);
        }

        result = "/Date(" + time + ")/";
        return result;
    }

    /**
     * @param context    application context
     * @param timeString with format "/Date(1450746095000)/"
     * @return Today hh:mm aa || yesterday hh:mm aa || yyyy-MM-dd hh:mm aa
     */
    public static String displayTimeWithoutOffset(Context context, String timeString, int task, int key) {
        try {
            timeString = timeString.replace("/Date(", "");
            int plusIndex = timeString.indexOf("+");

            long time;

            if (plusIndex != -1) {
                time = Long.valueOf(timeString.substring(0, plusIndex));
            } else {
                time = Long.valueOf(timeString.substring(0, timeString.indexOf(")")));
            }

            /*if (key == KEY_FROM_SERVER) {
                time += TimeZone.getDefault().getRawOffset() - GTMServer;
            }*/

            return displayTimeWithoutOffset(context, time, task);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * @param context    application context
     * @param timeString with format "/Date(1450746095000)/"
     * @return Today hh:mm aa || yesterday hh:mm aa || yyyy-MM-dd hh:mm aa
     */
    public static String displayTimeWithoutOffsetV2(Context context, String timeString, int task, int key) {
        try {
            long time;

            if (timeString.contains("(")) {
                timeString = timeString.replace("/Date(", "");
                int plusIndex = timeString.indexOf("+");

                if (plusIndex != -1) {
                    time = Long.valueOf(timeString.substring(0, plusIndex));
                } else {
                    time = Long.valueOf(timeString.substring(0, timeString.indexOf(")")));
                }
            } else {
                time = Long.valueOf(timeString);
            }

            /*if (key == KEY_FROM_SERVER) {
                time += TimeZone.getDefault().getRawOffset() - GTMServer;
            }*/

            return displayTimeWithoutOffsetFix(context, time, task);
        } catch (Exception e) {
            return "";
        }
    }

    public static String displayTimeWithoutOffset(String timeString) {
        try {
            long time;

            if (timeString.contains("(")) {
                timeString = timeString.replace("/Date(", "");
                int plusIndex = timeString.indexOf("+");

                if (plusIndex != -1) {
                    time = Long.valueOf(timeString.substring(0, plusIndex));
                } else {
                    time = Long.valueOf(timeString.substring(0, timeString.indexOf(")")));
                }
            } else {
                time = Long.valueOf(timeString);
            }

            SimpleDateFormat formatter = new SimpleDateFormat(Statics.DATE_FORMAT_YYYY_MM_DD, Locale.getDefault());
            return formatter.format(new Date(time));
        } catch (Exception e) {
            return "";
        }
    }

    public static long getTime(String timeString) {
        try {
            long time;

            if (timeString.contains("(")) {
                timeString = timeString.replace("/Date(", "");
                int plusIndex = timeString.indexOf("+");

                if (plusIndex != -1) {
                    time = Long.valueOf(timeString.substring(0, plusIndex));
                } else {
                    time = Long.valueOf(timeString.substring(0, timeString.indexOf(")")));
                }
            } else {
                time = Long.valueOf(timeString);
            }

            return time;
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * format time
     *
     * @param context application context
     * @param time    long in milliseconds
     * @return Today hh:mm aa || yesterday hh:mm aa || yyyy-MM-dd hh:mm aa
     */
    //task - 0: EN
    //task - 1: KO
    public static String displayTimeWithoutOffset(Context context, long time, int task) {
        SimpleDateFormat formatter;

        if (task == 0) {
            formatter = new SimpleDateFormat("hh:mm aa", Locale.getDefault());
            formatter.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        } else {
            formatter = new SimpleDateFormat("aa hh:mm", Locale.getDefault());
            formatter.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        }
        int type = (int) getTimeForMail(time);

        String dateString;
        switch (type) {
            case -2:
                dateString = formatter.format(new Date(time)).toLowerCase();
                break;
            case -3:
                dateString = context.getString(R.string.yesterday) + " " + formatter.format(new Date(time)).toLowerCase();
                break;
            default:
                Calendar calendar = Calendar.getInstance();
                int currentYear = calendar.get(Calendar.YEAR);
                calendar.setTime(new Date(time));
                int year = calendar.get(Calendar.YEAR);
                if (task == 0) {
                    if (currentYear == year) {
                        formatter.applyLocalizedPattern("MM-dd hh:mm aa");
                    } else {
                        formatter.applyLocalizedPattern("yyyy-MM-dd hh:mm aa");
                    }
                } else {
                    if (currentYear == year) {
                        formatter.applyLocalizedPattern("MM-dd aa hh:mm");
                    } else {
                        formatter.applyLocalizedPattern("yyyy-MM-dd aa hh:mm");
                    }
                }
                dateString = formatter.format(new Date(time)).toLowerCase();
                break;
        }
        return dateString;
    }


    public static String displayTimeWithoutOffsetFix(Context context, long time, int task) {
        SimpleDateFormat formatter;

        if (task == 0) {
            formatter = new SimpleDateFormat("hh:mm aa", Locale.getDefault());
            //formatter.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        } else {
            formatter = new SimpleDateFormat("aa hh:mm", Locale.getDefault());
            //formatter.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        }
        int type = (int) getTimeForMail(time);

        String dateString;
        switch (type) {
            case -2:
                dateString = formatter.format(new Date(time)).toLowerCase();
                break;
            case -3:
                dateString = context.getString(R.string.yesterday) + " " + formatter.format(new Date(time)).toLowerCase();
                break;
            default:
                Calendar calendar = Calendar.getInstance();
                int currentYear = calendar.get(Calendar.YEAR);
                calendar.setTime(new Date(time));
                int year = calendar.get(Calendar.YEAR);
                if (task == 0) {
                    if (currentYear == year) {
                        formatter.applyLocalizedPattern("MM-dd hh:mm aa");
                    } else {
                        formatter.applyLocalizedPattern("yyyy-MM-dd hh:mm aa");
                    }
                } else {
                    if (currentYear == year) {
                        formatter.applyLocalizedPattern("MM-dd aa hh:mm");
                    } else {
                        formatter.applyLocalizedPattern("yyyy-MM-dd aa hh:mm");
                    }
                }
                dateString = formatter.format(new Date(time)).toLowerCase();
                break;
        }
        return dateString;
    }

    //-2: today
    //-3: Yesterday
    //-4: this month
    //-5: last Month
    //-1: default
    public static long getTimeForMail(long time) {
        int date = -1;
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        if (cal.get(Calendar.YEAR) == getYearNote(time)) {
            if (cal.get(Calendar.MONTH) == getMonthNote(time)) {
                int temp = cal.get(Calendar.DAY_OF_MONTH) - getDateNote(time);
                if (cal.get(Calendar.DAY_OF_MONTH) == getDateNote(time)) {
                    date = -2;
                } else if (temp == 1) {
                    date = -3;
                } else {
                    date = -4;
                }
            } else if (cal.get(Calendar.MONTH) - 1 == getMonthNote(time)) {
                date = -5;
            }
        } else if (cal.get(Calendar.YEAR) == getYearNote(time) + 1) {
            if (cal.get(Calendar.MONTH) == 0 && getMonthNote(time) == 11) {
                date = -5;
            }
        }
        return date;
    }

    //1: today
    //2: Yesterday
    //0: default

    public static int getYearNote(long date) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(date);
        return cal.get(Calendar.YEAR);
    }

    public static int getMonthNote(long date) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(date);
        return cal.get(Calendar.MONTH);
    }

    public static int getDateNote(long date) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(date);
        return cal.get(Calendar.DAY_OF_MONTH);
    }

    public static boolean compareTime(long date1, long date2) {
        SimpleDateFormat formatter = new SimpleDateFormat(Statics.DATE_FORMAT_YY_MM_DD);
        String date = formatter.format(new Date(date1));
        String dateTemp = formatter.format(new Date(date2));
        if (date.equalsIgnoreCase(dateTemp)) {
            return true;
        } else {
            return false;
        }
    }

    // Notification time convert to string
    public static String timeToStringNotAMPM(int hourOfDay, int minute){
        String text = "";
        String minutes = "";
        if (minute < 10){
            minutes = "0" + minute;
        }else{
            minutes = String.valueOf(minute);
        }
        if ((hourOfDay == 12 && minute > 0) || hourOfDay > 12){// PM
            text += hourOfDay + ":" + minutes;
        }else{ // AM
            if (hourOfDay < 10){
                text += "0";
            }
            text += hourOfDay + ":" + minutes;
        }
        return text;
    }

    public static String timeToString(int hourOfDay, int minute){
        String text = "";
        String minutes = "";
        if (minute < 10){
            minutes = "0" + minute;
        }else{
            minutes = String.valueOf(minute);
        }
        if ((hourOfDay == 12 && minute > 0) || hourOfDay > 12){// PM
            text = "PM ";
            text += hourOfDay + ":" + minutes;
        }else{ // AM
            text = "AM ";
            if (hourOfDay < 10){
                text += "0";
            }
            text += hourOfDay + ":" + minutes;
        }
        return text;
    }

}
