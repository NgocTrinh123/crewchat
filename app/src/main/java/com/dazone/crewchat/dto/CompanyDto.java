package com.dazone.crewchat.dto;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

/**
 * Created by david on 7/20/15.
 */
public class CompanyDto {

    public String Latitude;

    public String Longitude;

    public String Description;

    public int LocationNo;

    @Override
    public String toString() {
        return "CompanyDto{" +
                "Latitude='" + Latitude + '\'' +
                ", Longitude='" + Longitude + '\'' +
                ", Description='" + Description + '\'' +
                ", LocationNo=" + LocationNo +
                ", ErrorRange=" + ErrorRange +
                ", IsWorking=" + IsWorking +
                '}';
    }

    public int ErrorRange = 100;

    public int IsWorking;

    public double getLat()
    {
        return Double.parseDouble(Latitude);
    }
    public double getLng()
    {
        return Double.parseDouble(Longitude);
    }

    public LatLng getLatLng()
    {
        return new LatLng(getLat(),getLng());
    }

    public CompanyDto(JSONObject jsonObject) {

        Latitude=jsonObject.optString("Latitude");
        Longitude=jsonObject.optString("Longitude");
        Description=jsonObject.optString("Description");
        LocationNo=jsonObject.optInt("LocationNo");
        IsWorking=jsonObject.optInt("IsWorking");
    }

    public CompanyDto() {
    }
}
