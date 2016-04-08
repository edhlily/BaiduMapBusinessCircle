package com.dragonsoftbravo.businesscircle;

import android.app.Application;

public class MApplication extends Application {
    private static MApplication app;

    public static MApplication get() {
        return app;
    }

    ;

    @Override
    public void onCreate() {
        super.onCreate();
        this.app = this;
    }
}
