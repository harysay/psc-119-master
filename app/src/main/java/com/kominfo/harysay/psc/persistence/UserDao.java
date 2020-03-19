package com.kominfo.harysay.psc.persistence;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.kominfo.harysay.psc.model.User;

import java.util.List;

@Dao
public interface UserDao {
    @Insert
    long[] insertUsers(User... users);

    @Query("SELECT * FROM users")
    LiveData<List<User>> getUsers();

    @Delete
    int delete(User... users);

    @Update
    int updateUsers(User... users);
}
