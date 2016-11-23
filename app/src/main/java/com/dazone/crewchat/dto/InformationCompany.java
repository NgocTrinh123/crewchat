package com.dazone.crewchat.dto;

/**
 * Created by Admin on 8/1/2016.
 */
public class InformationCompany {
    private Double Latitude;
    private Double Longitude;
    private String Description;
    private long LocationNo;
    private long ErrorRange;
    private int IsWorking;

    public Double getLatitude() {
        return Latitude;
    }

    public void setLatitude(Double latitude) {
        Latitude = latitude;
    }

    public Double getLongitude() {
        return Longitude;
    }

    public void setLongitude(Double longitude) {
        Longitude = longitude;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public long getLocationNo() {
        return LocationNo;
    }

    public void setLocationNo(long locationNo) {
        LocationNo = locationNo;
    }

    public long getErrorRange() {
        return ErrorRange;
    }

    public void setErrorRange(long errorRange) {
        ErrorRange = errorRange;
    }

    public int getIsWorking() {
        return IsWorking;
    }

    public void setIsWorking(int isWorking) {
        IsWorking = isWorking;
    }
}
