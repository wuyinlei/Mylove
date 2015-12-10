package com.example.ruolan.letgo.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;

import com.example.ruolan.letgo.R;
import com.example.ruolan.letgo.bean.Tab;
import com.example.ruolan.letgo.fragment.CartFragment;
import com.example.ruolan.letgo.fragment.CategoryFragment;
import com.example.ruolan.letgo.fragment.HomeFragment;
import com.example.ruolan.letgo.fragment.HotFragment;
import com.example.ruolan.letgo.fragment.MineFragment;
import com.example.ruolan.letgo.widget.FragmentTabHost;
import com.example.ruolan.letgo.widget.LetToolBar;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends BaseActivity {

    public FragmentTabHost mTabHost;
    private LayoutInflater mInflater;
    private ImageView img;
    private TextView text;
    private LetToolBar mToolBar;
    CartFragment cartFragment;

    private List<Tab> mTabs = new ArrayList<>(5);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolBar = (LetToolBar) findViewById(R.id.toolbar);
        mToolBar.hideTitleView();
        mToolBar.showSearchView();

        initTab();

    }

    /**
     * 初始化tab
     */
    private void initTab() {
        Tab home = new Tab(R.string.home, R.drawable.selector_icon_home, HomeFragment.class);
        Tab hot = new Tab(R.string.hot, R.drawable.selector_icon_hot, HotFragment.class);
        Tab category = new Tab(R.string.category, R.drawable.selector_icon_category, CategoryFragment.class);
        Tab cart = new Tab(R.string.cart, R.drawable.selector_icon_cart, CartFragment.class);
        Tab mine = new Tab(R.string.mine, R.drawable.selector_icon_mine, MineFragment.class);

        mTabs.add(home);
        mTabs.add(hot);
        mTabs.add(category);
        mTabs.add(cart);
        mTabs.add(mine);

        mInflater = LayoutInflater.from(this);
        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);

        for (Tab tab : mTabs) {
            TabHost.TabSpec tabSpec = mTabHost.newTabSpec(getString(tab.getTitle()));
            tabSpec.setIndicator(builderIndiator(tab));
            mTabHost.addTab(tabSpec, tab.getFragment(), null);
        }

        //去掉分割线
        mTabHost.getTabWidget().setShowDividers(LinearLayout.SHOW_DIVIDER_NONE);

        /**
         * 对tabhost实现点击监听事件
         */
        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                if (tabId == getString(R.string.cart)) {
                    mToolBar.setRightButtonTitle("编辑");
                    mToolBar.hideSearchView();
                    mToolBar.showTitleView();
                    //在这要调用刷新的方法，也就是当点击其他的fragment的时候
                    //如果不调用这个方法，那么当再次点击购物车的时候，就不会及时的刷新购物车里面的数据
                    refData();
                }else if (tabId == getString(R.string.mine)){
                    mToolBar.hideTitleView();
                    mToolBar.hideSearchView();
                }
                else {
                    mToolBar.setRightButtonTitle("");
                    mToolBar.showSearchView();
                    mToolBar.hideTitleView();
                }
            }
        });
        mTabHost.setCurrentTab(0);
    }

    /**
     * 刷新数据，如果fragment的id是cart的，那么就刷新一个，否则如果不这么做的话，就会导致
     * 在购物车中增删改的时候出现不时时的问题
     */
    private void refData() {
        if (cartFragment == null) {
            //拿到fragment对象，然后调用里面的刷新方法
            Fragment fragment = getSupportFragmentManager().findFragmentByTag(getString(R.string.cart));
            if (fragment != null) {
                cartFragment = (CartFragment) fragment;
                cartFragment.refData();
            }
        } else {
            cartFragment.refData();
        }
    }

    /**
     * gouzai Indiator
     *
     * @param tab
     * @return
     */
    private View builderIndiator(Tab tab) {
        View view = mInflater.inflate(R.layout.tab_indicator, null);

        img = (ImageView) view.findViewById(R.id.icon_tab);
        text = (TextView) view.findViewById(R.id.text_indicator);
        img.setBackgroundResource(tab.getImage());
        text.setText(tab.getTitle());

        return view;
    }


}
