package com.midounoo.midounoo.Model;

import com.google.firebase.database.Exclude;

public class Restaurants {

    private String nameRestau;
    private String adresse;
    private boolean isOpen;
    private String image;
    @Exclude private String id;

    public Restaurants(String nameRestau, String adresse, String image) {
        this.nameRestau = nameRestau;
        this.adresse = adresse;
        this.isOpen = false;
        this.image = image;
    }

    public Restaurants(){

    }

    public String getNameRestau() {
        return nameRestau;
    }

    public void setNameRestau(String nameRestau) {
        this.nameRestau = nameRestau;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
