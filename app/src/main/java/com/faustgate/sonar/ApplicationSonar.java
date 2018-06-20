package com.faustgate.sonar;

import android.app.Application;
import android.content.Context;

public class ApplicationSonar extends Application {
    private static ApplicationSonar instance;

    public static ApplicationSonar getInstance() {
        return instance;
    }

    public static Context getContext(){
        return instance;
        // or return instance.getApplicationContext();
    }

    @Override
    public void onCreate() {
        instance = this;
        super.onCreate();
    }
}
