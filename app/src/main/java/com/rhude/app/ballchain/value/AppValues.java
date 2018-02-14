package com.rhude.app.ballchain.value;

import android.content.Context;
import android.content.SharedPreferences;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

/**
 * Created by sean on 2018-02-14.
 */

public class AppValues {
    private static AppValues appValues;
    private SharedPreferences prefs;

    private AppValues(Context context) {
        prefs = getDefaultSharedPreferences(context);
    }

    public static void initialize(Context context) {
        if (appValues == null) {
            appValues = new AppValues(context);
        }
    }

    public static AppValues getInstance() {
        if (appValues == null) {
            throw new IllegalStateException("Must call initialize first");
        }
        return appValues;
    }

    public interface Preference {
        String USERNAME = "username";
        String PASSWORD = "password";
    }

    //<editor-fold desc="Endpoint Authorization">
    public void setAuthorization(String username, String password) {
        if (username == null || password == null) {
            prefs.edit().remove(Preference.USERNAME).apply();
            prefs.edit().remove(Preference.PASSWORD).apply();
            return;
        }
        prefs.edit().putString(Preference.USERNAME, username).apply();
        prefs.edit().putString(Preference.PASSWORD, password).apply();
    }

    public boolean hasAuthorization() {
        return !getUsername().isEmpty() && !getPassword().isEmpty();
    }

    public String getUsername() {
        return prefs.getString(Preference.USERNAME, "");
    }

    public String getPassword() {
        return prefs.getString(Preference.PASSWORD, "");
    }
    //</editor-fold>
}
