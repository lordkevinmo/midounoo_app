package com.midounoo.midounoo.Model;

public class Rating {

    private String userId;
    private String foodId;
    private String rateValue;
    private String comment;

    public Rating() {
    }

    public Rating(String userId, String foodId, String rateValue, String comment) {
        this.userId = userId;
        this.foodId = foodId;
        this.rateValue = rateValue;
        this.comment = comment;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFoodId() {
        return foodId;
    }

    public void setFoodId(String foodId) {
        this.foodId = foodId;
    }

    public String getRateValue() {
        return rateValue;
    }

    public void setRateValue(String rateValue) {
        this.rateValue = rateValue;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
