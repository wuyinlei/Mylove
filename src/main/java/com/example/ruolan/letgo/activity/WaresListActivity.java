package com.example.ruolan.letgo.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.cjj.MaterialRefreshLayout;
import com.example.ruolan.letgo.R;
import com.example.ruolan.letgo.adapter.HWAdapter;
import com.example.ruolan.letgo.adapter.divider.DividerItemDecoration;
import com.example.ruolan.letgo.bean.Page;
import com.example.ruolan.letgo.bean.Wares;
import com.example.ruolan.letgo.uri.Contants;
import com.example.ruolan.letgo.utils.Paper;
import com.example.ruolan.letgo.widget.LetToolBar;
import com.google.gson.reflect.TypeToken;

import java.util.List;

public class WaresListActivity extends BaseActivity implements Paper.OnPageListener<Wares>, TabLayout.OnTabSelectedListener, View.OnClickListener {

    /**
     * 布局中用到的控件
     */
    private TabLayout mTabLayout;
    private TextView mTextSummary;
    private RecyclerView mRecyclerView;
    private MaterialRefreshLayout mRefreshLayout;
    private LetToolBar mToolBar;

    /**
     * 下面的两个标志是用来给生成list布局还是grid布局的Tag
     */
    private static final int ACTION_GRID = 0;
    private static final int ACTION_LIST = 1;


    /**
     * 下面的三个标志是用来给Tablayout设置标志位的，分别是默认、销量、价格
     */
    private static final int TAG_DEFAULT = 0;
    private static final int TAG_SALE = 1;
    private static final int TAG_PRICE = 2;

    /**
     * 两个谁用来判断排序的标志位
     */
    private int orderBy = 0;
    private long campaignId = 0;

    private HWAdapter mHWAdapter;
    Paper paper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wares_list);

        initControl();
        initToolBar();
        getData();
        initTab();
    }

    /**
     * 初始化各种控件
     */
    private void initControl() {
        mTabLayout = (TabLayout) findViewById(R.id.tab_layout);
        mTextSummary = (TextView) findViewById(R.id.text_summary);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycle_view);
        mRefreshLayout = (MaterialRefreshLayout) findViewById(R.id.refresh_layout);
        campaignId = getIntent().getLongExtra(Contants.COMPAINGAIN_ID, 0);

    }

    /**
     * ToolBar的初始化，在这里实现toolbar的所有操作，包括两侧按钮的点击监听事件
     */
    private void initToolBar() {
        mToolBar = (LetToolBar) findViewById(R.id.toolbar);
        mToolBar.getLeftButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mToolBar.setRightButtonIcon(R.drawable.icon_grid_32);
        mToolBar.getRightButton().setTag(ACTION_LIST);
        mToolBar.getRightButton().setOnClickListener(this);
    }

    /**
     * Tab的初始化
     */
    private void initTab() {
        //第一步newTab（）
        TabLayout.Tab tab = mTabLayout.newTab();
        //第二步设置
        tab.setText("默认");
        //第三步添加
        mTabLayout.addTab(tab);
        //第四步设置标志位
        tab.setTag(TAG_DEFAULT);
        tab = mTabLayout.newTab();
        tab.setText("价格");
        tab.setTag(TAG_PRICE);
        mTabLayout.addTab(tab);
        tab = mTabLayout.newTab();
        tab.setText("销量");
        tab.setTag(TAG_SALE);
        mTabLayout.addTab(tab);
        //第五步，对tab设置点击监听事件
        mTabLayout.setOnTabSelectedListener(this);
    }

    /**
     * 当点击商品的时候，获取到商品列表
     */
    private void getData() {
        paper = Paper.newBuilder().setUrl(Contants.API.WARES_CAMPAIN_LIST)
                .putParam("campaignId", campaignId)
                .putParam("orderBy", orderBy)
                .setRefreshLayout(mRefreshLayout)
                .setLoadMore(true)
                .setOnPageListener(this)
                .build(this, new TypeToken<Page<Wares>>() {
                }.getType());
        //显示数据的
        paper.request();
    }

    /**
     * 默认
     *
     * @param datas
     * @param totalPage
     * @param totalCount
     */

    @Override
    public void load(List<Wares> datas, int totalPage, int totalCount) {
        mTextSummary.setText("共有商品" + totalCount + "商品");

        if (mHWAdapter == null) {
            mHWAdapter = new HWAdapter(this, datas);
            mRecyclerView.setAdapter(mHWAdapter);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
            mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        } else {
            mHWAdapter.refreshData(datas);
        }
    }

    /**
     * 刷新
     *
     * @param datas
     * @param totalPage
     * @param totalCount
     */
    @Override
    public void refresh(List<Wares> datas, int totalPage, int totalCount) {
        mHWAdapter.refreshData(datas);
        mRecyclerView.scrollToPosition(0);
    }

    /**
     * 加载更多
     *
     * @param datas
     * @param totalPage
     * @param totalCount
     */
    @Override
    public void loadMore(List<Wares> datas, int totalPage, int totalCount) {
        mHWAdapter.loadMoreData(datas);
    }

    /**
     * tab事件的监听
     *
     * @param tab
     */
    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        orderBy = (int) tab.getTag();
        paper.putParam("orderBy", orderBy);
        paper.request();
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }


    /**
     * 对Button，也就是toolbat右侧按钮的点击监听事件的逻辑处理
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        int action = (int) v.getTag();
        if (ACTION_LIST == action) {
            mToolBar.setRightButtonIcon(R.drawable.icon_grid_32);
            mHWAdapter.resetLayout(R.layout.template_grid_wares);
            mToolBar.getRightButton().setTag(ACTION_GRID);
            mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        } else if (ACTION_GRID == action) {
            mToolBar.setRightButtonIcon(R.drawable.icon_list_32);
            mHWAdapter.resetLayout(R.layout.template_hot_wares);
            mToolBar.getRightButton().setTag(ACTION_LIST);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        }
    }
}
