package com.example.ruolan.letgo.fragment;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.example.ruolan.letgo.R;
import com.example.ruolan.letgo.adapter.BaseAdapter;
import com.example.ruolan.letgo.adapter.CategoryAdapter;
import com.example.ruolan.letgo.adapter.WaresAdapter;
import com.example.ruolan.letgo.adapter.divider.DividerGridItemDecoration;
import com.example.ruolan.letgo.adapter.divider.DividerItemDecortion;
import com.example.ruolan.letgo.bean.Banner;
import com.example.ruolan.letgo.bean.Category;
import com.example.ruolan.letgo.bean.Page;
import com.example.ruolan.letgo.bean.Wares;
import com.example.ruolan.letgo.http.OkHttpHelper;
import com.example.ruolan.letgo.http.SpotsCallback;
import com.example.ruolan.letgo.uri.Contants;
import com.example.ruolan.letgo.utils.ToastUtils;
import com.lidroid.xutils.ViewUtils;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.util.List;

/**
 * Created by ruolan on 2015/11/29.
 */
public class CategoryFragment extends BaseFragment {

    private int currPage = 1;
    private int totalPage = 1;
    private int pageSize = 10;
    private long category_id = 0;

    private MaterialRefreshLayout mRefreshLayout;

    //定义三种状态，分别为正常状态，加载更多，刷新
    private static final int STATE_NORMAL = 0;
    private static final int STATE_REFRESH = 1;
    private static final int STATE_MORE = 2;

    //默认状态是normal
    private int STATE = STATE_NORMAL;

    /**
     * 轮番广告slider
     */
    private SliderLayout slider;

    private OkHttpHelper mHttpHelper = OkHttpHelper.getInstance();

    /**
     * 一级商品的recycleview
     */
    private RecyclerView mRecyclerView;

    private WaresAdapter mWaresAdapter;

    private CategoryAdapter mAdapter;

    /**
     * 二级商品的recycleview
     */
    private RecyclerView mRecyclerviewWares;


    @Override
    public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerview_category);
        mRecyclerviewWares = (RecyclerView) view.findViewById(R.id.recycleView);
        slider = (SliderLayout) view.findViewById(R.id.slider);
        mRefreshLayout = (MaterialRefreshLayout) view.findViewById(R.id.refresh);
        ViewUtils.inject(this, view);
        requestData();
        requestImages();
        initRefreshLayout();
        return view;
    }

    @Override
    public void init() {

    }

    /**
     * 请求一级商品数据
     */
    private void requestData() {
        String url = Contants.API.CATEGORY_LIST;
        mHttpHelper.get(url, new SpotsCallback<List<Category>>(getContext()) {

            @Override
            public void onFailure(Request request, Exception e) {

            }

            @Override
            public void onSuccess(Response response, List<Category> categories) {

                showData(categories);
                if (categories.size() > 0 && categories != null) {
                    category_id = categories.get(0).getId();
                    requestWaresData(category_id);
                }
            }

            @Override
            public void onError(Response response, int code, Exception e) {

            }

            @Override
            public void onTokenError(Response response, int code) {

            }
        });
    }

    /**
     * 显示一级商品数据
     *
     * @param categories
     */
    private void showData(final List<Category> categories) {
        mAdapter = new CategoryAdapter(getContext(), categories);

        mAdapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Category category = categories.get(position);
                category_id = category.getId();
                currPage = 1;
                STATE = STATE_NORMAL;
                requestWaresData(category_id);
            }
        });
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mRecyclerView.addItemDecoration(new DividerItemDecortion());
    }

    /**
     * 请求轮番广告数据
     */

    private void requestImages() {
        String url = Contants.API.BANNER + "?type=1";
        mHttpHelper.get(url, new SpotsCallback<List<Banner>>(getContext()) {

            @Override
            public void onFailure(Request request, Exception e) {

            }

            @Override
            public void onSuccess(Response response, List<Banner> banners) {
                showSliderView(banners);
            }

            @Override
            public void onError(Response response, int code, Exception e) {

            }

            @Override
            public void onTokenError(Response response, int code) {

            }
        });
    }

    /**
     * 显示轮番广告
     *
     * @param banners
     */
    private void showSliderView(List<Banner> banners) {
        if (banners != null) {
            for (Banner banner : banners) {
                DefaultSliderView sliderView = new DefaultSliderView(this.getActivity());
                sliderView.image(banner.getImgUrl());
                sliderView.setScaleType(BaseSliderView.ScaleType.Fit);
                slider.addSlider(sliderView);
            }
        }
        slider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        slider.setCustomAnimation(new DescriptionAnimation());
        slider.setPresetTransformer(SliderLayout.Transformer.Default);
        slider.setDuration(3000);
    }


    /**
     * 请求二级商品数据
     */
    private void requestWaresData(long categoryId) {
        String url = Contants.API.WARES_LIST + "?categoryId=" + categoryId + "&curPage=" + currPage + "&pageSize=" + pageSize;
        mHttpHelper.get(url, new SpotsCallback<Page<Wares>>(getContext()) {


            @Override
            public void onFailure(Request request, Exception e) {

            }

            @Override
            public void onSuccess(Response response, Page<Wares> waresPage) {
                currPage = waresPage.getCurrentPage();
                totalPage = waresPage.getTotalPage();
                showWaresData(waresPage.getList());
            }

            @Override
            public void onError(Response response, int code, Exception e) {

            }

            @Override
            public void onTokenError(Response response, int code) {

            }
        });
    }


    /**
     * 加载更多，在这里状态置为加载更多
     * 当前页++
     */
    private void loadMoreData() {
        currPage = ++currPage;
        STATE = STATE_MORE;
        requestWaresData(category_id);
    }

    /**
     * 刷新数据
     */
    private void refreshData() {
        currPage = 1;
        STATE = STATE_REFRESH;
        requestWaresData(category_id);
    }


    /**
     * 显示二级商品的数据
     *
     * @param list
     */
    private void showWaresData(List<Wares> list) {
        switch (STATE) {
            //正常状态
            case STATE_NORMAL:
                mWaresAdapter = new WaresAdapter(getContext(), list);
                mRecyclerviewWares.setAdapter(mWaresAdapter);
                //设置的排版方式是2列
                mRecyclerviewWares.setLayoutManager(new GridLayoutManager(getContext(), 2));
                mRecyclerviewWares.setItemAnimator(new DefaultItemAnimator());
                mRecyclerviewWares.addItemDecoration(new DividerGridItemDecoration(getContext()));
                break;

            //刷新状态
            case STATE_REFRESH:
                //刷新之前先清空数据
                mWaresAdapter.clear();
                mWaresAdapter.addData(list);
                mRecyclerviewWares.scrollToPosition(0);
                mRefreshLayout.finishRefresh();
                break;

            //加载状态
            case STATE_MORE:
                mWaresAdapter.addData(mWaresAdapter.getDatas().size(), list);
                mRecyclerviewWares.scrollToPosition(mWaresAdapter.getDatas().size());
                mRefreshLayout.finishRefreshLoadMore();
                break;
        }
    }

    /**
     * 在刷新的时候初始化
     */
    private void initRefreshLayout() {
        mRefreshLayout.setLoadMore(true);  //支持加载更多
        mRefreshLayout.setMaterialRefreshListener(new MaterialRefreshListener() {
            /**
             * 刷新的方法
             * @param materialRefreshLayout
             */
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                refreshData();
                mRefreshLayout.finishRefresh();
            }

            /**
             * 加载更多数据的方法
             * @param materialRefreshLayout
             */
            @Override
            public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout) {
                super.onRefreshLoadMore(materialRefreshLayout);
                if (currPage <= totalPage) {
                    loadMoreData();
                } else {
                    mRefreshLayout.finishRefreshLoadMore();
                    ToastUtils.show(getContext(), "已经没有下一页数据了");
                }
            }
        });
    }

}
