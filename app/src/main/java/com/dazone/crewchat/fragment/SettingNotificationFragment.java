package com.dazone.crewchat.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import com.dazone.crewchat.HTTPs.HttpRequest;
import com.dazone.crewchat.R;
import com.dazone.crewchat.constant.Statics;
import com.dazone.crewchat.dto.ErrorDto;
import com.dazone.crewchat.interfaces.OnSetNotification;
import com.dazone.crewchat.interfaces.Urls;
import com.dazone.crewchat.utils.CrewChatApplication;
import com.dazone.crewchat.utils.Prefs;
import com.dazone.crewchat.utils.TimeUtils;
import com.dazone.crewchat.utils.Utils;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by DAZONE on 16/05/16.
 */
public class SettingNotificationFragment extends Fragment implements CompoundButton.OnCheckedChangeListener, View.OnClickListener{
    private View mView;
    private SwitchCompat swEnableNotification, swEnableSound, swEnableVibrate, swEnableNotificationTime, swEnableNotificationWhenUsingPCVersion;
    private Prefs prefs;
    private TextView tvStartTime, tvEndTime;
    private Context mContext;
    /*
    * All date time var
    * */
    private int hour;
    private int min;
    private int START_TIME = 0;
    private int END_TIME = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.activity_notification_setting, container, false);


        prefs = CrewChatApplication.getInstance().getmPrefs();
        initView();

        return mView;
    }

    private void initView(){
        swEnableNotification = (SwitchCompat) mView.findViewById(R.id.sw_enable_notification);
        swEnableSound = (SwitchCompat) mView.findViewById(R.id.sw_enable_sound);
        swEnableVibrate = (SwitchCompat) mView.findViewById(R.id.sw_enable_vibrate);
        swEnableNotificationTime = (SwitchCompat) mView.findViewById(R.id.sw_enable_notification_time);
        swEnableNotificationWhenUsingPCVersion = (SwitchCompat) mView.findViewById(R.id.sw_enable_chat_by_pc_version);

        tvStartTime = (TextView) mView.findViewById(R.id.tv_start_time);
        tvEndTime = (TextView) mView.findViewById(R.id.tv_end_time);

        boolean isEnableN = prefs.getBooleanValue(Statics.ENABLE_NOTIFICATION, false);
        boolean isEnableSound = prefs.getBooleanValue(Statics.ENABLE_SOUND, false);
        boolean isEnableVibrate = prefs.getBooleanValue(Statics.ENABLE_VIBRATE, false);
        boolean isEnableTime = prefs.getBooleanValue(Statics.ENABLE_TIME, false);
        boolean isEnableNotificationWhenUsingPcVersion = prefs.getBooleanValue(Statics.ENABLE_NOTIFICATION_WHEN_USING_PC_VERSION, false);


        int start_hour = prefs.getIntValue(Statics.START_NOTIFICATION_HOUR, Statics.DEFAULT_START_NOTIFICATION_TIME);
        int start_minutes = prefs.getIntValue(Statics.START_NOTIFICATION_MINUTES, 0);
        int end_hour = prefs.getIntValue(Statics.END_NOTIFICATION_HOUR, Statics.DEFAULT_END_NOTIFICATION_TIME);
        int end_minutes = prefs.getIntValue(Statics.END_NOTIFICATION_MINUTES, 0);

        tvStartTime.setText(TimeUtils.timeToString(start_hour, start_minutes));
        tvEndTime.setText(TimeUtils.timeToString(end_hour, end_minutes));



        if (isEnableN){
            swEnableNotification.setChecked(true);

            swEnableSound.setEnabled(true);
            swEnableVibrate.setEnabled(true);
            swEnableNotificationTime.setEnabled(true);
            swEnableNotificationWhenUsingPCVersion.setEnabled(true);
        } else {

            swEnableNotification.setChecked(false);

            swEnableSound.setEnabled(false);
            swEnableVibrate.setEnabled(false);
            swEnableNotificationTime.setEnabled(false);
            swEnableNotificationWhenUsingPCVersion.setEnabled(false);
        }

        swEnableSound.setChecked(isEnableSound);
        swEnableVibrate.setChecked(isEnableVibrate);
        swEnableNotificationTime.setChecked(isEnableTime);
        swEnableNotificationWhenUsingPCVersion.setChecked(isEnableNotificationWhenUsingPcVersion);

        // Checked time to enable button time select
        if (isEnableTime){
            tvStartTime.setEnabled(true);
            tvEndTime.setEnabled(true);
        }else{
            tvStartTime.setEnabled(false);
            tvEndTime.setEnabled(false);
        }


        // Set event when toggle button change state
        swEnableNotification.setOnCheckedChangeListener(this);
        swEnableSound.setOnCheckedChangeListener(this);
        swEnableVibrate.setOnCheckedChangeListener(this);
        swEnableNotificationTime.setOnCheckedChangeListener(this);
        swEnableNotificationWhenUsingPCVersion.setOnCheckedChangeListener(this);
        // set event listener for set time text view
        tvStartTime.setOnClickListener(this);
        tvEndTime.setOnClickListener(this);
    }

    private void setNotification(){

        boolean isEnableN = prefs.getBooleanValue(Statics.ENABLE_NOTIFICATION, false);
        boolean isEnableSound = prefs.getBooleanValue(Statics.ENABLE_SOUND, false);
        boolean isEnableVibrate = prefs.getBooleanValue(Statics.ENABLE_VIBRATE, false);
        boolean isEnableTime = prefs.getBooleanValue(Statics.ENABLE_TIME, false);
        boolean isEnableNotificationWhenUsingPcVersion = prefs.getBooleanValue(Statics.ENABLE_NOTIFICATION_WHEN_USING_PC_VERSION, false);


        int start_hour = prefs.getIntValue(Statics.START_NOTIFICATION_HOUR, Statics.DEFAULT_START_NOTIFICATION_TIME);
        int start_minutes = prefs.getIntValue(Statics.START_NOTIFICATION_MINUTES, 0);
        int end_hour = prefs.getIntValue(Statics.END_NOTIFICATION_HOUR, Statics.DEFAULT_END_NOTIFICATION_TIME);
        int end_minutes = prefs.getIntValue(Statics.END_NOTIFICATION_MINUTES, 0);


        Map<String, Object> params = new HashMap<>();
        params.put("enabled",isEnableN);
        params.put("sound",isEnableSound);
        params.put("vibrate",isEnableVibrate);
        params.put("notitime",isEnableTime);
        params.put("starttime",TimeUtils.timeToStringNotAMPM(start_hour, start_minutes));
        params.put("endtime", TimeUtils.timeToStringNotAMPM(end_hour, end_minutes));
        params.put("confirmonline",isEnableNotificationWhenUsingPcVersion);


        HttpRequest.getInstance().setNotification(Urls.URL_INSERT_DEVICE,
                prefs.getGCMregistrationid(),
                params,
                new OnSetNotification() {
                    @Override
                    public void OnSuccess() {
                        Utils.printLogs("Update notification to server is success");
                    }

                    @Override
                    public void OnFail(ErrorDto errorDto) {
                        Utils.printLogs("Update notification to server is failed");
                    }
                }
        );
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()){
            case R.id.sw_enable_notification:
                if (isChecked){
                    prefs.putBooleanValue(Statics.ENABLE_NOTIFICATION, true);

                    swEnableSound.setEnabled(true);
                    swEnableVibrate.setEnabled(true);
                    swEnableNotificationTime.setEnabled(true);
                    swEnableNotificationWhenUsingPCVersion.setEnabled(true);

                }else {
                    prefs.putBooleanValue(Statics.ENABLE_NOTIFICATION, false);

                    swEnableSound.setEnabled(false);
                    swEnableVibrate.setEnabled(false);
                    swEnableNotificationTime.setEnabled(false);
                    swEnableNotificationWhenUsingPCVersion.setEnabled(false);
                }
                break;
            case R.id.sw_enable_sound:
                if (isChecked){
                    prefs.putBooleanValue(Statics.ENABLE_SOUND, true);
                }else {
                    prefs.putBooleanValue(Statics.ENABLE_SOUND, false);
                }
                break;
            case R.id.sw_enable_vibrate:
                if (isChecked){
                    prefs.putBooleanValue(Statics.ENABLE_VIBRATE, true);
                }else {
                    prefs.putBooleanValue(Statics.ENABLE_VIBRATE, false);
                }
                break;
            case R.id.sw_enable_notification_time:
                if (isChecked){
                    prefs.putBooleanValue(Statics.ENABLE_TIME, true);

                    // Enable time select
                    tvStartTime.setEnabled(true);
                    tvEndTime.setEnabled(true);

                }else {
                    prefs.putBooleanValue(Statics.ENABLE_TIME, false);

                    // Disable time select
                    tvStartTime.setEnabled(false);
                    tvEndTime.setEnabled(false);
                }
                break;
            case R.id.sw_enable_chat_by_pc_version:
                if (isChecked){
                    prefs.putBooleanValue(Statics.ENABLE_NOTIFICATION_WHEN_USING_PC_VERSION, true);
                }else {
                    prefs.putBooleanValue(Statics.ENABLE_NOTIFICATION_WHEN_USING_PC_VERSION, false);
                }
                break;
        }
        // Update to server
        setNotification();
    }



    private TimePickerDialog.OnTimeSetListener startTimeListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
            // set value and tag for start time textview
            int end_hour = prefs.getIntValue(Statics.END_NOTIFICATION_HOUR, 0);
            int end_minutes = prefs.getIntValue(Statics.END_NOTIFICATION_MINUTES, 0);
            if((hourOfDay > end_hour) || (hourOfDay == end_hour && minute > end_minutes)){
                showTimePicker(START_TIME);
                return;
            }
            tvStartTime.setText(TimeUtils.timeToString(hourOfDay, minute));
            // Save value time
            prefs.putIntValue(Statics.START_NOTIFICATION_HOUR, hourOfDay);
            prefs.putIntValue(Statics.START_NOTIFICATION_MINUTES, minute);

            // Update to server
            setNotification();
        }
    };

    private TimePickerDialog.OnTimeSetListener endTimeListener = new TimePickerDialog.OnTimeSetListener() {

        @Override
        public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
            // check end time is bigger than start time
            int start_hour = prefs.getIntValue(Statics.START_NOTIFICATION_HOUR, 0);
            int start_minutes = prefs.getIntValue(Statics.START_NOTIFICATION_MINUTES, 0);
            if((hourOfDay < start_hour) || (hourOfDay == start_hour && minute < start_minutes)){
                showTimePicker(END_TIME);
                return;
            }
            // set value and tag for start time textview
            tvEndTime.setText(TimeUtils.timeToString(hourOfDay, minute));
            // Save value time
            prefs.putIntValue(Statics.END_NOTIFICATION_HOUR, hourOfDay);
            prefs.putIntValue(Statics.END_NOTIFICATION_MINUTES, minute);

            // Update to server
            setNotification();
        }
    };

    private void showTimePicker(int type){
        getTime(type);

        TimePickerDialog tpd = null;
        if (type == START_TIME){
            tpd = TimePickerDialog.newInstance(startTimeListener, hour, min, true);
        }else if(type == END_TIME){
            tpd = TimePickerDialog.newInstance(endTimeListener, hour,min, true);
        }

        if (tpd != null) {
            int accentColor = getResources().getColor(R.color.actionbar_background);
            tpd.setAccentColor(accentColor);
            tpd.show(getFragmentManager(), "Timepickerdialog");
        }

    }

    private void getTime(int type){
        if (type == START_TIME){
            hour = prefs.getIntValue(Statics.START_NOTIFICATION_HOUR, Statics.DEFAULT_START_NOTIFICATION_TIME);
            min = prefs.getIntValue(Statics.START_NOTIFICATION_MINUTES, 0);
        }else if (type == END_TIME){
            hour = prefs.getIntValue(Statics.END_NOTIFICATION_HOUR, Statics.DEFAULT_END_NOTIFICATION_TIME);
            min = prefs.getIntValue(Statics.END_NOTIFICATION_MINUTES, 0);
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.tv_start_time:
                showTimePicker(START_TIME);
                break;

            case R.id.tv_end_time:
                showTimePicker(END_TIME);
                break;
        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }
}
