package com.midounoo.midounoo.Db;

import android.app.Application;
import android.os.AsyncTask;

import com.midounoo.midounoo.Model.Order;

import java.util.List;

import androidx.lifecycle.LiveData;

public class OrderRepository {

    private OrderDao orderDao;
    private LiveData<List<Order>> allOrders;

    OrderRepository(Application mApplication) {
        OrderDatabase db = OrderDatabase.getDatabase(mApplication);
        orderDao = db.orderDao();
        allOrders = orderDao.getAllOrders();
    }

    public LiveData<List<Order>> getAllOrders(){
        return allOrders;
    }

    //public LiveData<Integer> getNumberOfOrders(){return allOrders}

    public void delete(Order order) {
        new deleteAsyncTask(orderDao).execute(order);
    }

    public void insert(Order order){
        new insertAsyncTask(orderDao).execute(order);
    }

    public void deleteAll()  {
        new deleteAllOrdersAsyncTask(orderDao).execute();
    }

    private static class deleteAsyncTask extends AsyncTask<Order, Void, Void> {

        private OrderDao mAsyncTaskDao;

        deleteAsyncTask(OrderDao dao){ mAsyncTaskDao = dao; }


        @Override
        protected Void doInBackground(final Order... orders) {
            mAsyncTaskDao.delete(orders[0]);
            return null;
        }
    }

    private static class insertAsyncTask extends AsyncTask<Order, Void, Void> {

        private OrderDao mAsyncTaskDao;

        insertAsyncTask(OrderDao dao) {
            mAsyncTaskDao = dao;
        }


        @Override
        protected Void doInBackground(final Order... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }

    private static class deleteAllOrdersAsyncTask extends AsyncTask<Void, Void, Void> {
        private OrderDao mAsyncTaskDao;

        deleteAllOrdersAsyncTask(OrderDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            mAsyncTaskDao.deleteAll();
            return null;
        }
    }


}
