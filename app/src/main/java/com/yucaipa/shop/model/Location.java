package com.yucaipa.shop.model;

/**
 * Created by Vivek_Hexa on 10-Aug-17.
 */

public class Location {

    private int _ID;
    private double latitude;
    private double longitude;
    private float radius;

    public Location (int _ID, double latitude, double longitude, float radius){
        this._ID = _ID;
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
    }

    public int get_ID() {
        return _ID;
    }

    public void set_ID(int _ID) {
        this._ID = _ID;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }
}
