package com.example.majkel.gpsapplication;

import java.io.Serializable;

/**
 * Created by majkel on 16.01.15.
 */
public class Place  implements Serializable{
    private double mLat;
    private double mLong;
    private String p_name;
    private String p_desc;

    public Place(double lat, double longitude, String name, String desc) {
        p_name = name;
        p_desc = desc;
        mLat = lat;
        mLong = longitude;
    }

    public double getmLat() {
        return mLat;
    }

    public void setmLat(double mLat) {
        this.mLat = mLat;
    }

    public double getmLong() {
        return mLong;
    }

    public void setmLong(double mLong) {
        this.mLong = mLong;
    }

    public String getP_name() {
        return p_name;
    }

    public void setP_name(String p_name) {
        this.p_name = p_name;
    }

    public String getP_desc() {
        return p_desc;
    }

    public void setP_desc(String p_desc) {
        this.p_desc = p_desc;
    }
}
