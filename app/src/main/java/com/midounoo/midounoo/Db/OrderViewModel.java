package com.midounoo.midounoo.Db;

import android.app.Application;

import com.midounoo.midounoo.Model.Order;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class OrderViewModel extends AndroidViewModel {

    private OrderRepository orderRepository;
    private LiveData<List<Order>> allOrders;

    public OrderViewModel(@NonNull Application application) {
        super(application);
        orderRepository = new OrderRepository(application);
        allOrders = orderRepository.getAllOrders();
    }

    public LiveData<List<Order>> getAllOrders() {return allOrders;}

    public void insert(Order order) {orderRepository.insert(order);}

    public void delete(Order order) {orderRepository.delete(order);}

    public void deleteAll() {orderRepository.deleteAll();}

}
