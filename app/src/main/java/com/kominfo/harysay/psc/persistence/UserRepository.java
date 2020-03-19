package com.kominfo.harysay.psc.persistence;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.kominfo.harysay.psc.async.DeleteAsyncTask;
import com.kominfo.harysay.psc.async.InsertAsyncTask;
import com.kominfo.harysay.psc.async.UpdateAsyncTask;
import com.kominfo.harysay.psc.model.User;

import java.util.List;

public class UserRepository {
    private UserDb mUserDb;

    public UserRepository(Context context) {
        mUserDb = UserDb.getInstance(context);
    }

    public void insertUserTask(User user){
        new InsertAsyncTask(mUserDb.getUserDao()).execute(user);
    }

    public void updateUserTask(User user){
        new UpdateAsyncTask(mUserDb.getUserDao()).execute(user);
    }

    public LiveData<List<User>> retrieveUsersTask() {
        return mUserDb.getUserDao().getUsers();
    }

    public void deleteUserTask(User user){
        new DeleteAsyncTask(mUserDb.getUserDao()).execute(user);
    }
}
