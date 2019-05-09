package com.midounoo.midounoo.Model;

public class Sender {
    public String to;
    public Notificacion notification;


    public Sender(String token, Notificacion notificacion) {
        this.to = token;
        this.notification = notificacion;
    }
}
