package com.prateek.isafeassistdriver.dao;

public class DriverLocation {
    String latitude;
    String longitude;
    String driverid;
    String sublocality;
    String state;
    String country;

    public DriverLocation(){

    }

    public DriverLocation(String latitude, String longitude, String driverid, String sublocality, String state, String country) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.driverid = driverid;
        this.sublocality = sublocality;
        this.state = state;
        this.country = country;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getDriverid() {
        return driverid;
    }

    public void setDriverid(String driverid) {
        this.driverid = driverid;
    }

    public String getSublocality() {
        return sublocality;
    }

    public void setSublocality(String sublocality) {
        this.sublocality = sublocality;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
