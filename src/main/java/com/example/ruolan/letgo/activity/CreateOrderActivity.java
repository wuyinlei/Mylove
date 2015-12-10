package com.example.ruolan.letgo.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.ruolan.letgo.R;
import com.example.ruolan.letgo.adapter.WareOrderAdapter;
import com.example.ruolan.letgo.adapter.layoutmanager.FullyLinearLayoutManager;
import com.example.ruolan.letgo.application.CniaoApplication;
import com.example.ruolan.letgo.bean.Charge;
import com.example.ruolan.letgo.bean.ShoppingCart;
import com.example.ruolan.letgo.http.OkHttpHelper;
import com.example.ruolan.letgo.http.SpotsCallback;
import com.example.ruolan.letgo.msg.BaseRespMsg;
import com.example.ruolan.letgo.msg.CreateOrderRespMsg;
import com.example.ruolan.letgo.uri.Contants;
import com.example.ruolan.letgo.utils.CartProvider;
import com.example.ruolan.letgo.utils.JSONUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.pingplusplus.android.PaymentActivity;
import com.squareup.okhttp.Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateOrderActivity extends AppCompatActivity implements View.OnClickListener {


    /**
     * 银联支付渠道
     */
    private static final String CHANNEL_UPACP = "upacp";
    /**
     * 微信支付渠道
     */
    private static final String CHANNEL_WECHAT = "wx";
    /**
     * 支付支付渠道
     */
    private static final String CHANNEL_ALIPAY = "alipay";
    /**
     * 百度支付渠道
     */
    private static final String CHANNEL_BFB = "bfb";
    /**
     * 京东支付渠道
     */
    private static final String CHANNEL_JDPAY_WAP = "jdpay_wap";


    private HashMap<String, RadioButton> chanels = new HashMap<>(3);

    @ViewInject(R.id.txt_order)
    private TextView txtOrder;

    @ViewInject(R.id.recycler_view)
    private RecyclerView mRecyclerView;

    @ViewInject(R.id.rl_alipay)
    private RelativeLayout mLayoutAlipay;

    @ViewInject(R.id.rl_wechat)
    private RelativeLayout mLayoutWechat;

    @ViewInject(R.id.rl_bd)
    private RelativeLayout mLayoutBd;


    @ViewInject(R.id.rb_alipay)
    private RadioButton mRbAlipay;

    @ViewInject(R.id.rb_webchat)
    private RadioButton mRbWechat;

    @ViewInject(R.id.rb_bd)
    private RadioButton mRbBd;

    @ViewInject(R.id.btn_createOrder)
    private Button mBtnCreateOrder;

    @ViewInject(R.id.txt_total)
    private TextView mTxtTotal;

    private float amount;

    private OkHttpHelper okHttpHelper = OkHttpHelper.getInstance();

    private String orderNum;
    private String payChannel = CHANNEL_ALIPAY;


    private CartProvider mCartProvider;

    private WareOrderAdapter mWareOrderAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_order);
        ViewUtils.inject(this);
        showData();
        init();
    }

    private void init() {

        //把三种支付方式的Tag加入到数组中
        chanels.put(CHANNEL_ALIPAY, mRbAlipay);
        chanels.put(CHANNEL_WECHAT, mRbWechat);
        chanels.put(CHANNEL_BFB, mRbBd);


        /**
         * 对三种支付方式的RadioButton实现点击监听
         */
        mLayoutAlipay.setOnClickListener(this);
        mLayoutWechat.setOnClickListener(this);
        mLayoutBd.setOnClickListener(this);

        amount = mWareOrderAdapter.getTotalPrice();
        mTxtTotal.setText("应付款：  ¥" + amount);
    }

    /**
     * 显示购买的商品的图片
     */
    public void showData() {
        mCartProvider = new CartProvider(this);
        mWareOrderAdapter = new WareOrderAdapter(this, mCartProvider.getAll());

        //在这里用到了FullyLinearLayoutManager，这个是为了让在ScrollView里面的图片显示有问题的转化
        //在这里从新测量了图片的长宽
        FullyLinearLayoutManager layoutManager = new FullyLinearLayoutManager(this);
        layoutManager.setOrientation(GridLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mWareOrderAdapter);
    }

    @Override
    public void onClick(View v) {
        selectPayChannel(v.getTag().toString());
    }


    @OnClick(R.id.btn_createOrder)
    public void createNewOrder(View view) {
        postNewOrder();
    }

    /**
     * 选择支付方式，在这里对RadioButton进行了只能选择一个，其余的均为不选择情况
     *
     * @param paychannel
     */
    public void selectPayChannel(String paychannel) {
        payChannel = paychannel;
        for (Map.Entry<String, RadioButton> entry : chanels.entrySet()) {
            RadioButton rb = entry.getValue();
            if (entry.getKey().equals(payChannel)) {
                boolean isCheck = rb.isChecked();
                rb.setChecked(!isCheck);
            } else
                rb.setChecked(false);
        }
    }

    /**
     * 提交新的订单
     * 以下是官方给的文档
     * Map<String, Object> chargeParams = new HashMap<String, Object>();
     * chargeParams.put("order_no",  "123456789");
     * chargeParams.put("amount", 100);
     * Map<String, String> app = new HashMap<String, String>();
     * app.put("id", "app_1Gqj58ynP0mHeX1q");
     * chargeParams.put("app",app);
     * chargeParams.put("channel","alipay");
     * chargeParams.put("currency","cny");
     * chargeParams.put("client_ip","127.0.0.1");
     * chargeParams.put("subject","Your Subject");
     * chargeParams.put("body","Your Body");
     * Charge.create(chargeParams);
     */
    private void postNewOrder() {

        final List<ShoppingCart> carts = mWareOrderAdapter.getDatas();
        List<WareItem> items = new ArrayList<>(carts.size());

        for (ShoppingCart c : carts) {
            WareItem item = new WareItem(c.getId(), c.getPrice().intValue());
            items.add(item);
        }

        String item_json = JSONUtil.toJSON(items);

        Map<String, String> params = new HashMap<>(5);
        params.put("user_id", CniaoApplication.getInstance().getUser().getId().toString());
        params.put("item_json", item_json);
        params.put("pay_channel", payChannel);
        params.put("amount", (int) amount + "");
        params.put("addr_id", 1 + "");

        mBtnCreateOrder.setEnabled(false);
        okHttpHelper.post(Contants.API.ORDER_CREATE, params, new SpotsCallback<CreateOrderRespMsg>(this) {

            @Override
            public void onSuccess(Response response, CreateOrderRespMsg respMsg) {
                mCartProvider.clear();   //当订单提交成功后把购物车清空
                mBtnCreateOrder.setEnabled(true);
                orderNum = respMsg.getData().getOrderNum();
                Charge charge = respMsg.getData().getCharge();
                openPaymentActivity(JSONUtil.toJSON(charge));
            }

            @Override
            public void onError(Response response, int code, Exception e) {
                mBtnCreateOrder.setEnabled(true);
            }
        });

    }

    /**
     * 上述发起方式是 Ping++ client-sdk 唯一公开调用方式，
     * 因为 Ping++ 已经封装好了相应的调用方法，所以只需要照写下面的几行代码即可调起支付控件：
     * “.wxapi.WXPayEntryActivity“ 是所有渠道支付的入口，并非只是微信支付入口。
     *
     * @param charge
     */
    private void openPaymentActivity(String charge) {

        Intent intent = new Intent();
        String packageName = getPackageName();
        ComponentName componentName = new ComponentName(packageName, packageName + ".wxapi.WXPayEntryActivity");
        intent.setComponent(componentName);
        intent.putExtra(PaymentActivity.EXTRA_CHARGE, charge);
        startActivityForResult(intent, Contants.REQUEST_CODE_PAYMENT);

    }

    /**
     * 重载 onActivityResult 方法可以获得支付结果
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //支付页面返回处理
        if (requestCode == Contants.REQUEST_CODE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                String result = data.getExtras().getString("pay_result");
                 /* 处理返回值
             * "success" - payment succeed
             * "fail"    - payment failed
             * "cancel"  - user canceld
             * "invalid" - payment plugin not installed
             */
                if (result.equals("success"))
                    changeOrderStatus(1);
                else if (result.equals("fail"))
                    changeOrderStatus(-1);
                else if (result.equals("cancel"))
                    changeOrderStatus(-2);
                else changeOrderStatus(0);

                String errorMsg = data.getExtras().getString("error_msg"); // 错误信息
                String extraMsg = data.getExtras().getString("extra_msg"); // 错误信息
            }
        }
    }

    private void changeOrderStatus(int status) {
        Map<String, String> params = new HashMap<>(5);
        params.put("order_num", orderNum);
        params.put("status", status + "");
        okHttpHelper.post(Contants.API.ORDER_COMPLEPE, params, new SpotsCallback<BaseRespMsg>(this) {

            @Override
            public void onSuccess(Response response, BaseRespMsg baseRespMsg) {
                toPayResultActiity(baseRespMsg.getStatus());
            }

            @Override
            public void onError(Response response, int code, Exception e) {
                toPayResultActiity(-1);
            }
        });
    }

    /**
     * 如果支付成功，进入支付成功的页面
     * @param status
     */
    private void toPayResultActiity(int status) {
        Intent intent = new Intent(this, PayResultActivity.class);
        intent.putExtra("status", status);
        startActivity(intent);
        this.finish();
    }

    class WareItem {
        private Long ware_id;
        private int amount;

        public WareItem(Long ware_id, int amount) {
            this.ware_id = ware_id;
            this.amount = amount;
        }

        public Long getWare_id() {
            return ware_id;
        }

        public void setWare_id(Long ware_id) {
            this.ware_id = ware_id;
        }

        public int getAmount() {
            return amount;
        }

        public void setAmount(int amount) {
            this.amount = amount;
        }
    }

}
