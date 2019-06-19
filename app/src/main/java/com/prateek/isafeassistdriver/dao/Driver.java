package com.prateek.isafeassistdriver.dao;

public class Driver {
    String name;
    String contact;
    String mail;
    String pass;
    String did;

    public Driver() {

    }

    public Driver(String name, String contact, String mail, String pass, String did) {
        this.name = name;
        this.contact = contact;
        this.mail = mail;
        this.pass = pass;
        this.did = did;
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
}
