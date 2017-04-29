package com.blacknebula.ghostsms;

import android.app.Application;
import android.content.Context;

public class GhostSmsApplication extends Application {

    private static Application context;

    public static Context getAppContext() {
        return GhostSmsApplication.context;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.context = this;
    }
}