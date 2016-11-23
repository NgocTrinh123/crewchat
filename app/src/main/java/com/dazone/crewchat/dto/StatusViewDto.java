package com.dazone.crewchat.dto;

import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Admin on 6/10/2016.
 */
public class StatusViewDto {
    public ImageView status_icon;
    public TextView status_text;

    public StatusViewDto(ImageView status_icon, TextView status_text) {
        this.status_icon = status_icon;
        this.status_text = status_text;
    }
}
