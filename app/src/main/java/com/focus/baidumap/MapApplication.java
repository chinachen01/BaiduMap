package com.focus.baidumap;

import android.app.Application;

import com.baidu.mapapi.SDKInitializer;

/**
 *
 */
public class MapApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //初始化百度地图
        SDKInitializer.initialize(getApplicationContext());
    }
}
