package com.dragonsoftbravo.businesscircle;

import android.app.Application;

import com.baidu.mapapi.SDKInitializer;
import com.dragonsoftbravo.businesscircle.utils.Screen;

public class MApplication extends Application {
    private static MApplication app;

    public static MApplication get() {
        return app;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.app = this;
        SDKInitializer.initialize(getApplicationContext());
        Screen.init(getApplicationContext());
    }
}
