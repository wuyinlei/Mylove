package com.example.ruolan.letgo.http;

import android.content.Context;
import android.content.Intent;

import com.example.ruolan.letgo.R;
import com.example.ruolan.letgo.activity.LoginActivity;
import com.example.ruolan.letgo.application.CniaoApplication;
import com.example.ruolan.letgo.utils.ToastUtils;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;




/**
 * Created by <a href="http://www.cniao5.com">菜鸟窝</a>
 * 一个专业的Android开发在线教育平台
 */
public abstract class SimpleCallback<T> extends BaseCallback<T> {

    protected Context mContext;

    public SimpleCallback(Context context){

        mContext = context;

    }

    @Override
    public void onBeforeRequest(Request request) {

    }

    @Override
    public void onFailure(Request request, Exception e) {

    }

    @Override
    public void onResponse(Response response) {

    }

    @Override
    public void onTokenError(Response response, int code) {
        ToastUtils.show(mContext, mContext.getString(R.string.token_error));


        Intent intent = new Intent();
        intent.setClass(mContext, LoginActivity.class);
        mContext.startActivity(intent);

        CniaoApplication.getInstance().clearUser();

    }


}
