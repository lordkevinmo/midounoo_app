package com.midounoo.midounoo.Model;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

public class Request {

    private String phone, userId, total, date, status, restaurantId;
    private List<Order> foods;

    public Request() {

    }

    public Request(String phone, String userId, String total,
                   List<Order> foods, String restaurantId) {
        this.phone = phone;
        this.userId = userId;
        this.total = total;
        this.foods = foods;
        this.date = getDateNow();
        this.restaurantId = restaurantId;
        this.status = "0"; //0 = ajouter, 1 = en cours de livraison, 2 = livrer
    }

    public String getStatus() {
        return status;
    }

    private String getDateNow(){
        Date date = new Date();
        DateFormat dateFormat = DateFormat.getDateTimeInstance();

        return dateFormat.format(date);
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public List<Order> getFoods() {
        return foods;
    }

    public void setFoods(List<Order> foods) {
        this.foods = foods;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }
}
