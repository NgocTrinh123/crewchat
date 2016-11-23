package com.dazone.crewchat.dto;

/**
 * Created by david on 12/25/15.
 */
public interface MenuDrawItem {
    String getStringTitle();
    int getIconResID();
    String getMenuIconUrl();
    int getItemID();
    boolean isHide();
}
