package com.midounoo.midounoo.Model;

public class Notificacion {

    public String title;
    public String body;

    public Notificacion(String title, String body) {
        this.title = title;
        this.body = body;
    }

    public Notificacion() {
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
