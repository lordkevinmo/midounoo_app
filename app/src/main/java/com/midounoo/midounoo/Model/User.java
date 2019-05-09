package com.midounoo.midounoo.Model;

public class User {

    private String name, fName, email, photoUrl, numberPhone, role, domicile, bureau, restaurantId;

    public User(){

    }

    public User(String name, String fName, String email, String numberPhone) {
        this.name = name;
        this.fName = fName;
        this.email = email;
        this.photoUrl = "lienPhoto";
        this.numberPhone = numberPhone;
        this.role = "customer";
        this.domicile = "Ajouter un domicile";
        this.bureau = "Ajouter un bureau";
        this.restaurantId = "0";
    }

    public String getEmail() {
        return email;
    }

    public String getfName() {
        return fName;
    }

    public String getName() {
        return name;
    }

    public String getNumberPhone() {
        return numberPhone;
    }

    public String getRole() {
        return role;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNumberPhone(String numberPhone) {
        this.numberPhone = numberPhone;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getDomicile() {
        return domicile;
    }

    public void setDomicile(String domicile) {
        this.domicile = domicile;
    }

    public String getBureau() {
        return bureau;
    }

    public void setBureau(String bureau) {
        this.bureau = bureau;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }
}
