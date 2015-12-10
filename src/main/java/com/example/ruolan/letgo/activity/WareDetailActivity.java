package com.example.ruolan.letgo.activity;

import android.os.Bundle;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.example.ruolan.letgo.R;
import com.example.ruolan.letgo.bean.Wares;
import com.example.ruolan.letgo.uri.Contants;
import com.example.ruolan.letgo.utils.CartProvider;
import com.example.ruolan.letgo.widget.LetToolBar;

import java.io.Serializable;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import dmax.dialog.SpotsDialog;

public class WareDetailActivity extends BaseActivity implements View.OnClickListener {

    private WebView mWebView;
    private LetToolBar mToolBar;
    private Wares mWares;
    private WebAppInterface mAppInterface;
    private CartProvider mCartProvider;
    private SpotsDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ware_detail);

        //初始化dialog
        mDialog = new SpotsDialog(this, "loading...");
        mDialog.isShowing();
        mCartProvider = new CartProvider(this);

        getData();
        initWebView();
        initToolBar();

    }

    /**
     * 获取到热卖商品传递过来的ware
     */
    private void getData() {
        Serializable serializable = getIntent().getSerializableExtra(Contants.WARE);
        if (serializable == null) {
            this.finish();
        }
        mWares = (Wares) serializable;
    }

    /**
     * 初始化ToolBar
     */
    private void initToolBar() {
        mToolBar = (LetToolBar) findViewById(R.id.toolbar);
        mToolBar.getLeftButton().setOnClickListener(this);
        mToolBar.setRightButtonTitle(getString(R.string.share));
        mToolBar.getRightButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showShare();
            }
        });
    }

    /**
     * 初始化webview
     */
    private void initWebView() {
        mWebView = (WebView) findViewById(R.id.webView);
        WebSettings settings = mWebView.getSettings();
        //允许脚本执行
        settings.setJavaScriptEnabled(true);
        settings.setBlockNetworkImage(false);  //设置是否web视图不应该从网络加载图
        // 像资源（通过HTTP和HTTPS URI方案访问的资源


        settings.setAppCacheEnabled(true); //设置缓存

        //调用js中的网址
        mWebView.loadUrl(Contants.API.WARES_DETAIL);
        mAppInterface = new WebAppInterface();
        //添加通信接口
        mWebView.addJavascriptInterface(mAppInterface, "appInterface");
        mWebView.setWebViewClient(new WareShow());

    }

    @Override
    public void onClick(View v) {
        this.finish();
    }

    /**
     * 创建一个类，用来在这里重写方法，重写的一个onPageFinished方法来判断页面程序加载完成
     */
    class WareShow extends WebViewClient {

        //下面的这个方法是查看页面是否加载完成，如果加载完成
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);

            //加载对话框消失
            if (mDialog != null && mDialog.isShowing()) {
                mDialog.dismiss();
            }
            //调用加载详情
            mAppInterface.showDetail();
        }
    }


    /**
     * 这里面是调用JS方法
     */
    class WebAppInterface {

        /**
         * 这个是后台服务器的一个显示商品详情的方法
         */
        @JavascriptInterface
        public void showDetail() {
            //必须在UI线程中
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mWebView.loadUrl("javascript:showDetail(" + mWares.getId() + ")");
                    Toast.makeText(WareDetailActivity.this, "已经添", Toast.LENGTH_SHORT).show();
                }
            });
        }

        /**
         * JS后台的购买商品的方法
         *
         * @param id
         */
        @JavascriptInterface
        public void buy(long id) {
            mCartProvider.put(mWares);
            Toast.makeText(WareDetailActivity.this, "已经添加到购物车", Toast.LENGTH_SHORT).show();
        }

        /**
         * JS后台的添加到我的收藏的方法
         *
         * @param id
         */
        @JavascriptInterface
        public void addFavorites(long id) {

        }
    }



    private void showShare() {
        ShareSDK.initSDK(this);
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

// 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle(getString(R.string.share));
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl("http://sharesdk.cn");
        // text是分享文本，所有平台都需要这个字段
        oks.setText("我是分享文本");
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl("http://sharesdk.cn");
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("我是测试评论文本");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl("http://sharesdk.cn");

// 启动分享GUI
        oks.show(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        ShareSDK.stopSDK();
    }

}
