package com.dazone.crewchat.dto;

/**
 * Created by david on 1/5/16.
 */
public class SelectionPlusDto extends DataDto {
    private String title;
    //1: camera
    //2: cameror
    //3: Image
    //4: video
    //5: file
    //6. contact
    private int type;

    @Override
    public String toString() {
        return "SelectionPlusDto{" +
                "title='" + title + '\'' +
                ", type=" + type +
                '}';
    }

    public SelectionPlusDto(String title, int type) {
        this.title = title;
        this.type = type;
    }

    public SelectionPlusDto() {
    }

    public SelectionPlusDto(int type) {
        this.type = type;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
