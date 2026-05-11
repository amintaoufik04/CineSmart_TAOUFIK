package com.example.cinesmart_taoufik.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "CineSmartPrefs";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_TOKEN = "token";
    
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void saveSession(String userId, String token) {
        editor.putString(KEY_USER_ID, userId);
        editor.putString(KEY_TOKEN, token);
        editor.apply();
    }

    public String getUserId() {
        return pref.getString(KEY_USER_ID, null);
    }

    public String getToken() {
        return pref.getString(KEY_TOKEN, null);
    }

    public void logout() {
        editor.clear();
        editor.apply();
    }
}
