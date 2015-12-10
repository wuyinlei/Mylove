package com.example.ruolan.letgo.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ruolan.letgo.R;
import com.example.ruolan.letgo.utils.ManifestUtil;
import com.example.ruolan.letgo.utils.ToastUtils;
import com.example.ruolan.letgo.widget.ClearEditText;
import com.example.ruolan.letgo.widget.LetToolBar;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.UserInterruptException;
import cn.smssdk.utils.SMSLog;

import static com.mob.tools.utils.R.getStringRes;

/**
 * Created by ruolan on 2015/12/5.
 */
public class RegActivity extends BaseActivity {

    private LetToolBar mToolBar;
    private TextView txtCountry, txtCountryCode;
    private ClearEditText edittxt_phone, edittxt_pwd;

    private SMSEventHandler mEventHandler;

    // 默认使用中国区号
    private static final String DEFAULT_COUNTRY_ID = "42";


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);
        //注册eventhandler

        //必须先初始化各种控件，要不然在调用的时候会出错
        initToolbar();
        initControl();
        /**
         * 下面的是有GUI的短信验证码的请求
         */
        /**
         * initSDK方法是短信SDK的入口，需要传递您从ShareSDK应用管理后台中
         * 注册的应用AppKey和AppSecrete，如果填写错误，后续的操作都将不能进行。
         */
        SMSSDK.initSDK(this, ManifestUtil.getMetaDataValue(getApplicationContext(), "mob_sms_appKey"),
                ManifestUtil.getMetaDataValue(getApplicationContext(), "mob_sms_appSecrect"));

        mEventHandler = new SMSEventHandler();

        /**
         * registerEventHandler用来往SMSSDK中注册一个事件接收器
         * SMSSDK允许开发者注册任意数量的接收器，所有接收器都会在事件 被触发时收到消息。
         */
        SMSSDK.registerEventHandler(mEventHandler);
        //String[] country = getCurrentCountry();
        //获取国家代码和国家
        String[] country = SMSSDK.getCountry(DEFAULT_COUNTRY_ID);
        if (country != null) {
            txtCountryCode.setText("+" + country[1]);
            txtCountry.setText(country[0]);
        }

        //获取短信目前支持的国家列表，在监听中返回
        SMSSDK.getSupportedCountries();
        /*//打开注册页面
         RegisterPage registerPage = new RegisterPage();
        registerPage.setRegisterCallback(new EventHandler() {
            public void afterEvent(int event, int result, Object data) {
// 解析注册结果
                if (result == SMSSDK.RESULT_COMPLETE) {
                    @SuppressWarnings("unchecked")
                    HashMap<String, Object> phoneMap = (HashMap<String, Object>) data;
                    String country = (String) phoneMap.get("country");
                    String phone = (String) phoneMap.get("phone");

// 提交用户信息
                 //   registerUser(country, phone);
                }
            }
        });
        registerPage.show(this);*/

    }

    /**
     * 初始化各种控件
     */
    private void initControl() {
        txtCountry = (TextView) findViewById(R.id.txtCountry);
        txtCountryCode = (TextView) findViewById(R.id.txtCountryCode);
        edittxt_phone = (ClearEditText) findViewById(R.id.edittxt_phone);
        edittxt_pwd = (ClearEditText) findViewById(R.id.edittxt_pwd);
    }

    /**
     * 初始化ToolBar
     */
    private void initToolbar() {
        mToolBar = (LetToolBar) findViewById(R.id.toolbar);
        mToolBar.setTitle(R.string.register_one);
        mToolBar.setRightButtonTitle(getString(R.string.next));
        mToolBar.setRightButtonOnClickLinster(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // SMSSDK.getVerificationCode();
                //获取手机号码和code，验证码
                getCode();
               /* Intent intent = new Intent(RegActivity.this, RegSecondActivity.class);
                //在这往第二个注册界面传入值

                startActivity(intent);*/
            }
        });
    }

    /**
     * 获取手机号码和密码还有国家代码
     */
    private void getCode() {
        String phone = edittxt_phone.getText().toString().trim().replaceAll("\\s*", "");
        String code = txtCountryCode.getText().toString().trim();
        String pwd = edittxt_pwd.getText().toString().trim();

        //在验证的时候要检查手机号码是否是正确的
        checkPhone(phone, code);

        /**
         * 调用下面的方法进行回调，那么就要写一个eventHandler
         * 请求获取短信验证码，在监听中返回
         */
        SMSSDK.getVerificationCode(code, phone);
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
                        if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {
                            // 请求支持国家列表
                            onCountryListGot((ArrayList<HashMap<String, Object>>) data);

                        } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                            // 请求验证码后，跳转到验证码填写页面
                            afterVerificationCodeRequested((Boolean) data);

                        } else if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {

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
                                Toast.makeText(RegActivity.this, des, Toast.LENGTH_SHORT).show();
                                return;
                            }
                        } catch (Exception e) {
                            SMSLog.getInstance().w(e);
                        }
                        // 如果木有找到资源，默认提示
                        int resId = getStringRes(RegActivity.this,
                                "smssdk_network_error");
                        if (resId > 0) {
                            Toast.makeText(RegActivity.this, resId, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

        }
    }

    /**
     * 检查手机号码是否输入的正确
     *
     * @param phone
     * @param code
     */
    private void checkPhone(String phone, String code) {
        if (code.startsWith("+")) {
            code = code.substring(1);
        }
        if (TextUtils.isEmpty(phone)) {
            ToastUtils.show(this, "请输入手机号");
            return;
        }
        if (code == "86") {
            if (phone.length() != 11) {
                ToastUtils.show(this, "手机号码长度不对");
                return;
            }
        }
        /**
         * 正则表达式，用来判断手机号码是否符合
         */
        String rule = "^1(3|5|7|8|4)\\d{9}";
        Pattern p = Pattern.compile(rule);
        Matcher m = p.matcher(phone);
        if (!m.matches()) {
            ToastUtils.show(this, "您输入的手机号码格式不正确");
            return;
        }
    }


    /**
     * 下面的两个代码是通过sim卡获取国家代码的
     *
     * @return
     */

    private String[] getCurrentCountry() {
        String mcc = getMCC();
        String[] country = null;
        if (!TextUtils.isEmpty(mcc)) {
            country = SMSSDK.getCountryByMCC(mcc);
        }

        if (country == null) {
            Log.w("SMSSDK", "no country found by MCC: " + mcc);
            country = SMSSDK.getCountry(DEFAULT_COUNTRY_ID);
        }
        return country;
    }

    private String getMCC() {
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        // 返回当前手机注册的网络运营商所在国家的MCC+MNC. 如果没注册到网络就为空.
        String networkOperator = tm.getNetworkOperator();
        if (!TextUtils.isEmpty(networkOperator)) {
            return networkOperator;
        }

        // 返回SIM卡运营商所在国家的MCC+MNC. 5位或6位. 如果没有SIM卡返回空
        return tm.getSimOperator();
    }

    /**
     * 请求验证码后，跳转到验证码填写页面
     */
    private void afterVerificationCodeRequested(boolean smart) {
        String phone = edittxt_phone.getText().toString().trim().replaceAll("\\s*", "");
        String code = txtCountryCode.getText().toString().trim();
        String pwd = edittxt_pwd.getText().toString().trim();

        if (code.startsWith("+")){
            code = code.substring(1);
        }

        Intent intent = new Intent(this,RegSecondActivity.class);
        intent.putExtra("phone",phone);
        intent.putExtra("pwd",pwd);
        intent.putExtra("countryCode",code);

        startActivity(intent);


        ToastUtils.show(this, "获取验证码成功");
    }


    /**
     * 获取可用国家list
     */
    private void onCountryListGot(ArrayList<HashMap<String, Object>> countries) {
        // 解析国家列表
        for (HashMap<String, Object> country : countries) {
            String code = (String) country.get("zone");
            //正则表达式
            String rule = (String) country.get("rule");
            if (TextUtils.isEmpty(code) || TextUtils.isEmpty(rule)) {
                continue;
            }
            Log.d("RegActivity", "code" + code + " " + "rule" + rule);
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
