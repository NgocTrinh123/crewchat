package com.dazone.crewchat.dto;

import java.io.Serializable;

/**
 * Created by david on 12/23/15.
 */
public abstract class DataDto implements Serializable {
    protected int id  =0;
    protected String title = "";
    protected String content = "";

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "DataDto{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                '}';
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }
}
