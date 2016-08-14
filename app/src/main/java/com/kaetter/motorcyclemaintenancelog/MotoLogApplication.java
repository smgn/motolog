package com.kaetter.motorcyclemaintenancelog;

import android.app.Application;

import com.facebook.stetho.Stetho;

public class MotoLogApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
    }
}
