package com.kominfo.harysay.psc.async;

import android.os.AsyncTask;

import com.kominfo.harysay.psc.model.User;
import com.kominfo.harysay.psc.persistence.UserDao;

public class DeleteAsyncTask extends AsyncTask<User, Void, Void> {

    private UserDao mUserDao;

    public DeleteAsyncTask(UserDao dao) {
        mUserDao = dao;
    }

    @Override
    protected Void doInBackground(User... users) {
        mUserDao.delete(users);
        return null;
    }
}
