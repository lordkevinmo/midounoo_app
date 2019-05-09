package com.midounoo.midounoo.Db;

import android.content.Context;

import com.midounoo.midounoo.Model.Order;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {Order.class}, version = 2)
public abstract class OrderDatabase extends RoomDatabase {
    public abstract OrderDao orderDao();

    private static volatile OrderDatabase INSTANCE;

    public static OrderDatabase getDatabase(final Context context){
        if (INSTANCE == null) {
            synchronized (OrderDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            OrderDatabase.class, "order_database")
                            .addMigrations(MIGRATION_1_2)
                            .build();
                }
            }
        }

        return INSTANCE;
    }

    private static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE `Order` ADD COLUMN restaurantName TEXT" );
        }
    };

}
