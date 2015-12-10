package com.example.ruolan.letgo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.example.ruolan.letgo.R;
import com.example.ruolan.letgo.application.CniaoApplication;
import com.example.ruolan.letgo.bean.User;
import com.example.ruolan.letgo.http.OkHttpHelper;
import com.example.ruolan.letgo.http.SpotsCallback;
import com.example.ruolan.letgo.msg.LoginRespMsg;
import com.example.ruolan.letgo.uri.Contants;
import com.example.ruolan.letgo.utils.DESUtil;
import com.example.ruolan.letgo.utils.ToastUtils;
import com.example.ruolan.letgo.widget.ClearEditText;
import com.example.ruolan.letgo.widget.LetToolBar;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.squareup.okhttp.Response;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ruolan on 2015/12/5.
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {


    @ViewInject(R.id.toolbar)
    private LetToolBar mToolBar;
    @ViewInject(R.id.edit_phone)
    private ClearEditText mEtxtPhone;
    @ViewInject(R.id.edit_pwd)
    private ClearEditText mEtxtPwd;

    private TextView txt_toReg;


    private OkHttpHelper okHttpHelper = OkHttpHelper.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ViewUtils.inject(this);
        txt_toReg = (TextView) findViewById(R.id.txt_toReg);
        txt_toReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegActivity.class);
                startActivity(intent);
            }
        });
        initToolBar();
    }


    private void initToolBar() {


        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LoginActivity.this.finish();
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();
    }

    @OnClick(R.id.btn_login)
    public void login(View view) {


        //获取到输入的手机号
        String phone = mEtxtPhone.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            ToastUtils.show(this, "请输入手机号码");
            return;
        }

        //获取到输入的密码
        String pwd = mEtxtPwd.getText().toString().trim();
        if (TextUtils.isEmpty(pwd)) {
            ToastUtils.show(this, "请输入密码");
            return;
        }

        //把电话号码和密码添加到map里面
        Map<String, String> params = new HashMap<>(2);
        params.put("phone", phone);
        params.put("password", DESUtil.encode(Contants.DES_KEY, pwd));

        /**
         * 调用post请求，提交手机号和验证码
         */
        okHttpHelper.post(Contants.API.LOGIN, params, new SpotsCallback<LoginRespMsg<User>>(this) {


            @Override
            public void onSuccess(Response response, LoginRespMsg<User> userLoginRespMsg) {

                CniaoApplication application = CniaoApplication.getInstance();
                application.putUser(userLoginRespMsg.getData(), userLoginRespMsg.getToken());

                /**
                 * 在这里判断登录的意图是否有，如果为空，那么就登录
                 * 如果不是为空，就跳转到目标activity
                 */
                if (application.getIntent() == null) {
                    setResult(RESULT_OK);
                    finish();
                } else {
                    application.jumpToTargetActivity(LoginActivity.this);
                    finish();
                }

            }

            @Override
            public void onError(Response response, int code, Exception e) {

            }

        });

    }

    @Override
    public void onClick(View v) {

    }
}