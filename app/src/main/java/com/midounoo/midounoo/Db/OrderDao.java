package com.midounoo.midounoo.Db;

import com.midounoo.midounoo.Model.Order;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface OrderDao {

    @Query("SELECT * FROM `Order`")
    LiveData<List<Order>> getAllOrders();

    @Query("SELECT COUNT(*) FROM `Order`")
    int getNumberOfOrders();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Order order);

    @Delete
    void delete(Order order);

    @Query("DELETE FROM `Order`")
    void deleteAll();

}
