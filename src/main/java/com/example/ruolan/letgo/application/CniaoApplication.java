package com.example.ruolan.letgo.application;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.example.ruolan.letgo.bean.User;
import com.example.ruolan.letgo.utils.UserLocalData;
import com.facebook.drawee.backends.pipeline.Fresco;

/**
 * Created by ruolan on 2015/11/12.
 */
public class CniaoApplication extends Application {
    private User user;

    private static CniaoApplication mInstance;


    public static CniaoApplication getInstance() {

        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;
        initUser();
        Fresco.initialize(this);
    }


    /**
     * 初始化用户对象，从本地得到User
     */
    private void initUser() {

        this.user = UserLocalData.getUser(this);
    }


    public User getUser() {

        return user;
    }


    /**
     * 保存User对象，保存到本地
     * @param user
     * @param token
     */
    public void putUser(User user, String token) {
        this.user = user;
        UserLocalData.putUser(this, user);
        UserLocalData.putToken(this, token);
    }

    /**
     * 清除User对象，从本地清除
     */
    public void clearUser() {
        this.user = null;
        UserLocalData.clearUser(this);
        UserLocalData.clearToken(this);


    }


    /**
     * 得到Token错误码
     * @return
     */
    public String getToken() {

        return UserLocalData.getToken(this);
    }


    private Intent intent;

    //定义成全局变量，好取值
    public void putIntent(Intent intent) {
        this.intent = intent;
    }

    public Intent getIntent() {
        return this.intent;
    }

    //跳转到目标的activity
    public void jumpToTargetActivity(Context context) {
        context.startActivity(intent);
        this.intent = null;
    }

}
