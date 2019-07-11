package com.prateek.isafeassistdriver.dao;

public class NotifyDao {
    String title;
    String body;

    public NotifyDao(){

    }

    public NotifyDao(String title, String body) {
        this.title = title;
        this.body = body;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
