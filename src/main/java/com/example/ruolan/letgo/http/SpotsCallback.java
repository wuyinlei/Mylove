package com.example.ruolan.letgo.http;

import android.content.Context;
import android.content.Intent;

import com.example.ruolan.letgo.R;
import com.example.ruolan.letgo.activity.LoginActivity;
import com.example.ruolan.letgo.application.CniaoApplication;
import com.example.ruolan.letgo.utils.ToastUtils;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import dmax.dialog.SpotsDialog;

/**
 * Created by ruolan on 2015/11/12.
 */

/**
 * github上面的一个开源的项目
 * 项目地址：https://github.com/d-max/spots-dialog
 *
 * @param <T>
 */
public abstract class SpotsCallback<T> extends  BaseCallback<T> {

    public Context mContext;

    //显示dialog
    public void showDialog(){
        dialog.show();
    }

    //消失dialog
    public void dismissDialog(){
        if (dialog != null)
            dialog.dismiss();
    }

    SpotsDialog dialog ;
    //constructor
    public SpotsCallback(Context context){
        mContext = context;
        dialog = new SpotsDialog(context);
    }

    public void setMessage(String message){
        dialog.setMessage(message);
    }

    @Override
    public void onBeforeRequest(Request request) {
        //在请求之前显示dialog，以提示用户正在加载
        showDialog();
    }

    @Override
    public void onFailure(Request request, Exception e) {
        //在请求不成功的时候把dialog给消除
        dismissDialog();
    }

    @Override
    public void onResponse(Response response) {
        //在请求成功的时候也要把dialog给消除
        dismissDialog();
    }


    @Override
    public void onTokenError(Response response, int code) {
        ToastUtils.show(mContext, R.string.token_error);

        //如果验证失败，重新回到登录界面
        Intent intent = new Intent(mContext, LoginActivity.class);
        mContext.startActivity(intent);

        CniaoApplication.getInstance().clearUser();
    }
}
