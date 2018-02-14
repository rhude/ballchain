package com.rhude.app.ballchain;

import android.app.Application;

import com.rhude.app.ballchain.network.WordpressApi;
import com.rhude.app.ballchain.value.AppValues;

/**
 * Created by sean on 2018-02-14.
 */

public class RhudeApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        AppValues.initialize(this);
        WordpressApi.initialize();
    }
}
