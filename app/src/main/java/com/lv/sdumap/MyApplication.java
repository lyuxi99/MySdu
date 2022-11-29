package com.lv.sdumap;

import android.app.Application;

import com.tencent.mmkv.MMKV;

public class MyApplication extends Application {
    public void onCreate() {
        super.onCreate();
        try {
            MMKV.initialize(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
