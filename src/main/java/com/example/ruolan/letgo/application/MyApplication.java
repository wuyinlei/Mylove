package com.example.ruolan.letgo.application;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;

/**
 * Created by ruolan on 2015/12/1.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //必须调用一下的这一步，要不然就会在加载图片的时候出错
        Fresco.initialize(this);
    }
}
