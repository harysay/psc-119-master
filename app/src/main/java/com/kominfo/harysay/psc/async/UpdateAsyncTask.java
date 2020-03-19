package com.kominfo.harysay.psc.async;

import android.os.AsyncTask;

import com.kominfo.harysay.psc.model.User;
import com.kominfo.harysay.psc.persistence.UserDao;

public class UpdateAsyncTask extends AsyncTask<User, Void, Void> {

    private UserDao mUserDao;

    public UpdateAsyncTask(UserDao dao) {
        mUserDao = dao;
    }

    @Override
    protected Void doInBackground(User... users) {
        mUserDao.updateUsers(users);
        return null;
    }
}
