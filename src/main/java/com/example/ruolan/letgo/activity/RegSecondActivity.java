package com.example.ruolan.letgo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ruolan.letgo.R;
import com.example.ruolan.letgo.application.CniaoApplication;
import com.example.ruolan.letgo.bean.User;
import com.example.ruolan.letgo.http.OkHttpHelper;
import com.example.ruolan.letgo.http.SpotsCallback;
import com.example.ruolan.letgo.msg.LoginRespMsg;
import com.example.ruolan.letgo.uri.Contants;
import com.example.ruolan.letgo.utils.CountTimerView;
import com.example.ruolan.letgo.utils.DESUtil;
import com.example.ruolan.letgo.utils.ManifestUtil;
import com.example.ruolan.letgo.utils.ToastUtils;
import com.example.ruolan.letgo.widget.ClearEditText;
import com.example.ruolan.letgo.widget.LetToolBar;
import com.squareup.okhttp.Response;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.UserInterruptException;
import cn.smssdk.utils.SMSLog;
import dmax.dialog.SpotsDialog;

import static com.mob.tools.utils.R.getStringRes;

/**
 * Created by ruolan on 2015/12/5.
 */
public class RegSecondActivity extends BaseActivity {

    private SMSEventHandler mEventHandler;

    private LetToolBar mToolBar;
    private TextView txtTip;
    private ClearEditText edittxt_code;
    private Button btn_reSend;

    private OkHttpHelper mHttpHelper = OkHttpHelper.getInstance();

    private SpotsDialog mDialog;
    private String phone, countryCode, pwd;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg_second);
        initControl();
        initToolbar();
        mDialog= new SpotsDialog(this);
        getIntentData();


        //执行倒计时的功能
        CountTimerView timer = new CountTimerView(btn_reSend);
        timer.start();


        //初始化sdk
        SMSSDK.initSDK(this, ManifestUtil.getMetaDataValue(getApplicationContext(), "mob_sms_appKey"),
                ManifestUtil.getMetaDataValue(getApplicationContext(), "mob_sms_appSecrect"));

        mEventHandler = new SMSEventHandler();

        /**
         * registerEventHandler用来往SMSSDK中注册一个事件接收器
         * SMSSDK允许开发者注册任意数量的接收器，所有接收器都会在事件 被触发时收到消息。
         */
        SMSSDK.registerEventHandler(mEventHandler);

    }

    /**
     * 得到第一个注册页面传来的手机号码、密码、国家代码
     */
    private void getIntentData(){
        phone = getIntent().getStringExtra("phone");
        pwd = getIntent().getStringExtra("pwd");
        countryCode = getIntent().getStringExtra("countryCode");

        String formatePhone = "+" + countryCode + " " + splitePhoneNum(phone);
        String text = getString(R.string.smssdk_send_mobile_detail) + formatePhone;
        txtTip.setText(Html.fromHtml(text));
    }

    /**
     * 分割电话号码
     *
     * @param phone
     * @return
     */
    private String splitePhoneNum(String phone) {
        StringBuilder builder = new StringBuilder(phone);
        builder.reverse();
        for (int i = 4, len = builder.length(); i < len; i += 5) {
            builder.insert(i, ' ');
        }
        builder.reverse();

        return builder.toString();
    }

    /**
     * 初始化所用到的控件
     */
    private void initControl() {
        txtTip = (TextView) findViewById(R.id.txtTip);
        edittxt_code = (ClearEditText) findViewById(R.id.edittxt_code);
        btn_reSend = (Button) findViewById(R.id.btn_reSend);
    }

    /**
     * 初始化ToolBar
     */
    private void initToolbar() {
        mToolBar = (LetToolBar) findViewById(R.id.toolbar);
        mToolBar.setTitle(R.string.register_finish);
        mToolBar.setRightButtonTitle(getString(R.string.finish));
        mToolBar.setRightButtonOnClickLinster(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitRegister();
            }
        });
    }

    /**
     * 提交注册
     */
    private void submitRegister(){
        String vCode = edittxt_code.getText().toString().trim();
        if (TextUtils.isEmpty(vCode)){
            ToastUtils.show(this,R.string.smssdk_write_identify_code);
            return;
        }
        //提交验证码进行判断
        SMSSDK.submitVerificationCode(countryCode, phone, vCode);

    }

    /**
     * 注册，把注册信息提交到服务端，判断是否已经注册了，如果已经注册了就返回
     */
    private void doReg(){
        //把电话号码和密码添加到map里面
        Map<String, String> params = new HashMap<>(2);
        params.put("phone", phone);
        params.put("password", DESUtil.encode(Contants.DES_KEY, pwd));

        /**
         * 调用post请求，提交手机号和验证码
         */
        mHttpHelper.post(Contants.API.REGISTER, params, new SpotsCallback<LoginRespMsg<User>>(this) {

            @Override
            public void onSuccess(Response response, LoginRespMsg<User> userLoginRespMsg) {

                if (mDialog.isShowing() && mDialog != null)
                    mDialog.dismiss();

                CniaoApplication application = CniaoApplication.getInstance();
                application.putUser(userLoginRespMsg.getData(), userLoginRespMsg.getToken());

                startActivity(new Intent(RegSecondActivity.this, LoginActivity.class));
                finish();
            }

            @Override
            public void onError(Response response, int code, Exception e) {

            }

            @Override
            public void onTokenError(Response response, int code) {
                super.onTokenError(response, code);
                ToastUtils.show(RegSecondActivity.this,"该手机号码已经注册过了，请不要再次注册");
            }
        });
    }

    /**
     * 其中的EventHandler即为操作回调。它包括4个方法，分别为
     * public void onRegister();      onRegister在回调对象注册的时候被触发
     * <p/>
     * public void beforeEvent(int event, Object data);     在操作执行前被触发，其参数event表示操作的类
     * 型，data是从外部传入的数据
     * <p/>
     * public void afterEvent(int event, int result, Object data);    在操作结束时被触发，同
     * 样具备event和data参数，但是data是事件操作结果，其具体取值根据参数result而定。result是操作结果，
     * 为SMSSDK.RESULT_COMPLETE表示操作成功，为SMSSDK.RESULT_ERROR表示操作失败。
     * <p/>
     * public void onUnregister();
     * <p/>
     * <p/>
     * 在EventHandler的4个回调方法都可能不在UI线程下，
     * 因此如果要在其中执行UI操作，请务必使用Handler发送一个消息给UI线程处理。
     */
    class SMSEventHandler extends EventHandler {
        @Override
        public void afterEvent(final int event, final int result,
                               final Object data) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    /**
                     *
                     EVENT	                                     DATA类型	                  说明
                     EVENT_GET_SUPPORTED_COUNTRIES	 ArrayList<HashMap<String,Object>>	返回支持发送验证码的国家列表
                     EVENT_GET_VERIFICATION_CODE	           Boolean	                true为智能验证，false为普通下发短信
                     EVENT_SUBMIT_VERIFICATION_CODE	       HashMap<String,Object>      </String,Object>	            校验验证码，返回校验的手机和国家代码
                     EVENT_GET_CONTACTS	           ArrayList<HashMap<String,Object>>	 获取手机内部的通信录列表
                     EVENT_SUBMIT_USER_INFO	                  null	                                    提交应用内的用户资料
                     EVENT_GET_FRIENDS_IN_APP	 ArrayList<HashMap<String,Object>>	   获取手机通信录在当前应用内的用户列表
                     EVENT_GET_VOICE_VERIFICATION_CODE	      null	                 请求发送语音验证码，无返回
                     */
                    if (result == SMSSDK.RESULT_COMPLETE) {
                        //提交验证码信息
                        if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {

                            doReg();
                            mDialog.setMessage("正在提交注册信息");
                            mDialog.show();
                        }
                    } else {
                        if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE
                                && data != null
                                && (data instanceof UserInterruptException)) {
                            // 由于此处是开发者自己决定要中断发送的，因此什么都不用做
                            return;
                        }

                        // 根据服务器返回的网络错误，给toast提示
                        try {
                            ((Throwable) data).printStackTrace();
                            Throwable throwable = (Throwable) data;

                            JSONObject object = new JSONObject(
                                    throwable.getMessage());
                            String des = object.optString("detail");
                            if (!TextUtils.isEmpty(des)) {
                                Toast.makeText(RegSecondActivity.this, des, Toast.LENGTH_SHORT).show();
                                return;
                            }
                        } catch (Exception e) {
                            SMSLog.getInstance().w(e);
                        }
                        // 如果木有找到资源，默认提示
                        int resId = getStringRes(RegSecondActivity.this,
                                "smssdk_network_error");
                        if (resId > 0) {
                            Toast.makeText(RegSecondActivity.this, resId, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

        }
    }

    /**
     * 为了避免EventHandler注册后不再使用而造成内存泄漏，
     * 请务必在确定不使用某个EventHandler时，调用反注册代码将其注销
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //销毁EventHandler
        SMSSDK.unregisterEventHandler(mEventHandler);
    }

}
