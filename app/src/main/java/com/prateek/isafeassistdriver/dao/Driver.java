package com.prateek.isafeassistdriver.dao;

public class Driver {
    String name;
    String contact;
    String mail;
    String pass;
    String did;
    String latitude;
    String longitude;
    String driverid;
    String sublocality;
    String state;
    String country;
    String status;
    String request;




    public Driver() {

    }

    public Driver(String latitude, String longitude, String driverid, String sublocality, String state, String country){
        this.latitude = latitude;

        this.longitude = longitude;
        this.driverid = driverid;
        this.sublocality = sublocality;
        this.state = state;
        this.country = country;
    }

    public Driver(String request,String status,String name, String contact, String mail, String pass, String did,String latitude, String longitude, String driverid, String sublocality, String state, String country) {
        this.name = name;
        this.request= request;
        this.status= status;
        this.contact = contact;
        this.mail = mail;
        this.pass = pass;
        this.did = did;
        this.latitude = latitude;
        this.longitude = longitude;
        this.driverid = driverid;
        this.sublocality = sublocality;
        this.state = state;
        this.country = country;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
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

    public String getDid() {
        return did;
    }

    public void setDid(String did) {
        this.did = did;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
