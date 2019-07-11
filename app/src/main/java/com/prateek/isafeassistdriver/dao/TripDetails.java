package com.prateek.isafeassistdriver.dao;

public class TripDetails {
    String price;
    String username;
    String userphone;
    String timeservice;
    String locateservice;

    public TripDetails(){}

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserphone() {
        return userphone;
    }

    public void setUserphone(String userphone) {
        this.userphone = userphone;
    }

    public String getTimeservice() {
        return timeservice;
    }

    public void setTimeservice(String timeservice) {
        this.timeservice = timeservice;
    }

    public String getLocateservice() {
        return locateservice;
    }

    public void setLocateservice(String locateservice) {
        this.locateservice = locateservice;
    }
}
