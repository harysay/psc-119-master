package com.kominfo.harysay.psc.handler;

/**
 * Created by harysay on 16/05/2017.
 */

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.kominfo.harysay.psc.LoginActivity;

import java.util.HashMap;

public class SessionManager {
    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "UserLoginPref";

    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";

    public static final String KEY_NIK = "nik";
    public static final String KEY_NAME = "nama";
    public static final String KEY_JK = "jk";
    public static final String KEY_email = "email";
    public static final String KEY_TOKEN = "token";
    public static final String KEY_URLIMAGE = "urlimage";
    public static final String KEY_alamat = "alamat";
    public static final String KEY_telepon = "telepon";

    // Constructor
    public SessionManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    /**
     * Create login session
     * */
    public void createLoginSession(String idnik, String name, String jeniskelamin, String email, String token, String urlimage, String alamat, String telepon){
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_NIK, idnik);
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_JK, jeniskelamin);
        editor.putString(KEY_email, email);
        editor.putString(KEY_TOKEN, token);
        editor.putString(KEY_URLIMAGE, urlimage);
        editor.putString(KEY_alamat, alamat);
        editor.putString(KEY_telepon, telepon);
        // commit changes
        editor.commit();
    }

    /**
     * Check login method wil check user login status
     * If false it will redirect user to login page
     * Else won't do anything
     * */
    public void checkLogin(){
        // Check login status
        if(!this.isLoggedIn()){
            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(_context, LoginActivity.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            _context.startActivity(i);
        }

    }



    /**
     * Get stored session data
     * */
    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();
        //user id
        user.put(KEY_NIK, pref.getString(KEY_NIK, null));
        // user name
        user.put(KEY_NAME, pref.getString(KEY_NAME, null));
        user.put(KEY_JK, pref.getString(KEY_JK, null));
        user.put(KEY_email, pref.getString(KEY_email, null));
        user.put(KEY_TOKEN, pref.getString(KEY_TOKEN, null));
        user.put(KEY_URLIMAGE, pref.getString(KEY_URLIMAGE, null));
        user.put(KEY_alamat, pref.getString(KEY_alamat, null));
        user.put(KEY_telepon, pref.getString(KEY_telepon, null));
        // return user
        return user;
    }

    /**
     * Clear session details
     * */
    public void logoutUser(){
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();

        // After logout redirect user to Loing Activity
        Intent i = new Intent(_context, LoginActivity.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        _context.startActivity(i);
    }

    /**
     * Quick check for login
     * **/
    // Get Login NamaPekerjaan
    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }
}
