package com.dazone.crewchat.dto;


import com.dazone.crewchat.utils.Utils;

/**
 * Created by david on 12/25/15.
 */
public class MenuDto extends DataDto implements MenuDrawItem {

    private String title;
    private int iconRes;
    private String iconUrl;
    private boolean isHide = false;

    @Override
    public String toString() {
        return "MenuDto{" +
                "title='" + title + '\'' +
                ", iconRes=" + iconRes +
                ", iconUrl='" + iconUrl + '\'' +
                ", isHide=" + isHide +
                '}';
    }

    public MenuDto(String title, int iconRes) {
        this.title = title;
        this.iconRes = iconRes;
    }
    public MenuDto(int title, int iconRes) {
        this.title = Utils.getString(title);
        this.iconRes = iconRes;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getIconRes() {
        return iconRes;
    }

    public void setIconRes(int iconRes) {
        this.iconRes = iconRes;
    }

    @Override
    public String getStringTitle() {
        return getTitle();
    }

    @Override
    public int getIconResID() {
        return getIconRes();
    }

    @Override
    public String getMenuIconUrl() {
        return getIconUrl();
    }

    @Override
    public int getItemID() {
        return getId();
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public boolean isHide() {
        return isHide;
    }

    public void setIsHide(boolean isHide) {
        this.isHide = isHide;
    }

}
