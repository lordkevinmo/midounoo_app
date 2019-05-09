package com.midounoo.midounoo.Model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "Order")
public class Order {

    @NonNull
    @PrimaryKey
    private String productId;

    @ColumnInfo(name = "productName")
    private String productName;

    @ColumnInfo(name = "productQuantity")
    private String productQuantity;

    @ColumnInfo(name = "productPrice")
    private String productPrice;

    @ColumnInfo(name = "productDiscount")
    private String productDiscount;

    @ColumnInfo(name = "restaurantName")
    private String restaurantName;

    public Order() {
    }

    public Order(@NonNull String productId, String productName,
                 String productQuantity, String productPrice,
                 String productDiscount, String restau) {
        this.productId = productId;
        this.productName = productName;
        this.productQuantity = productQuantity;
        this.productPrice = productPrice;
        this.productDiscount = productDiscount;
        this.restaurantName = restau;
    }

    @NonNull
    public String getProductId() {
        return productId;
    }

    public void setProductId(@NonNull String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(String productQuantity) {
        this.productQuantity = productQuantity;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public String getProductDiscount() {
        return productDiscount;
    }

    public void setProductDiscount(String productDiscount) {
        this.productDiscount = productDiscount;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }
}
