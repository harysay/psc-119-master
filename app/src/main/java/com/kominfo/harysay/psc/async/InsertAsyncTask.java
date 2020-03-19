package com.kominfo.harysay.psc.async;

import android.os.AsyncTask;

import com.kominfo.harysay.psc.model.User;
import com.kominfo.harysay.psc.persistence.UserDao;

public class InsertAsyncTask extends AsyncTask<User, Void, Void> {

    private UserDao mUserDao;

    public InsertAsyncTask(UserDao dao){
        mUserDao = dao;
    }

    @Override
    protected Void doInBackground(User... users) {
        mUserDao.insertUsers(users);
        return null;
    }
}
