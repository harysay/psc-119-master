package com.kominfo.harysay.psc.persistence;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.kominfo.harysay.psc.model.User;

@Database(entities = {User.class}, version = 1, exportSchema = false)
public abstract class UserDb extends RoomDatabase {
    public static final String DB_NAME = "user_db";

    private static UserDb instance;

    static UserDb getInstance(final Context context){
        if (instance == null){
            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    UserDb.class,
                    DB_NAME
            ).build();
        }
        return instance;
    }

    public abstract UserDao getUserDao();
}
