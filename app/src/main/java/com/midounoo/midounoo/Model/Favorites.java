package com.midounoo.midounoo.Model;

public class Favorites {

    private String foodId, userId;
    private boolean isCheck;

    public Favorites() {
    }

    public Favorites(String foodId, String userId) {
        this.foodId = foodId;
        this.userId = userId;
        this.isCheck = false;
    }

    public String getFoodId() {
        return foodId;
    }

    public void setFoodId(String foodId) {
        this.foodId = foodId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }
}
