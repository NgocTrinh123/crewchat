package com.dazone.crewchat.dto;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by THANHTUNG on 18/02/2016.
 */
public class AttachDTO implements Serializable{
    @SerializedName("AttachNo")
    private int AttachNo;
    @SerializedName("FileName")
    private String FileName;
    @SerializedName("Extension")
    private String Extension;
    @SerializedName("FilePath")
    private String FullPath;
    @SerializedName("Type")
    private int Type;
    @SerializedName("Size")
    private int Size;

    private String FileType;
    private String uriPath;

    public AttachDTO() {
    }

    public int getAttachNo() {
        return AttachNo;
    }

    public void setAttachNo(int attachNo) {
        AttachNo = attachNo;
    }

    public String getFileName() {
        return FileName;
    }

    public void setFileName(String fileName) {
        FileName = fileName;
    }

    public String getExtension() {
        return Extension;
    }

    public void setExtension(String extension) {
        Extension = extension;
    }

    public String getFullPath() {
        return FullPath;
    }

    public void setFullPath(String fullPath) {
        FullPath = fullPath;
    }

    public int getType() {
        return Type;
    }

    public void setType(int type) {
        Type = type;
    }

    public int getSize() {
        return Size;
    }

    public void setSize(int size) {
        Size = size;
    }

    public String getFileType() {
        return FileType;
    }

    public void setFileType(String fileType) {
        FileType = fileType;
    }

    public String getUriPath() {
        return uriPath;
    }

    public void setUriPath(String uriPath) {
        this.uriPath = uriPath;
    }

    @Override
    public String toString() {
        return "AttachDTO{" +
                "AttachNo=" + AttachNo +
                ", FileName='" + FileName + '\'' +
                ", Extension='" + Extension + '\'' +
                ", FullPath='" + FullPath + '\'' +
                ", Type=" + Type +
                ", Size=" + Size +
                ", FileType='" + FileType + '\'' +
                ", uriPath='" + uriPath + '\'' +
                '}';
    }
}
