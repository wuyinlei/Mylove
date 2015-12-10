package com.example.ruolan.letgo.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.example.ruolan.letgo.R;
import com.example.ruolan.letgo.activity.WaresListActivity;
import com.example.ruolan.letgo.adapter.HomeCategoryAdapter;
import com.example.ruolan.letgo.adapter.divider.DividerItemDecortion;
import com.example.ruolan.letgo.bean.Banner;
import com.example.ruolan.letgo.bean.Campaign;
import com.example.ruolan.letgo.bean.HomeCampaign;
import com.example.ruolan.letgo.http.OkHttpHelper;
import com.example.ruolan.letgo.http.SpotsCallback;
import com.example.ruolan.letgo.uri.Contants;
import com.google.gson.Gson;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.util.List;

/**
 * Created by ruolan on 2015/11/29.
 */
public class HomeFragment extends BaseFragment {

    private SliderLayout slider;
    private RecyclerView mRecyclerView;
    private PagerIndicator custom_indicator;

    private OkHttpHelper mHttpHelper = OkHttpHelper.getInstance();

    //用来解析json数据的
    private Gson mGson = new Gson();

    private HomeCategoryAdapter mHomeCategoryAdapter;

    List<Banner> mBanners ;

    @Override
    public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        slider = (SliderLayout) view.findViewById(R.id.slider);
        custom_indicator = (PagerIndicator) view.findViewById(R.id.custom_indicator);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycleView);
        //initSlider();
        initRecycleView();

        requestImages();
        return view;
    }

    @Override
    public void init() {

    }

    private void initRecycleView(){
//        List<HomeCategory> datas = new ArrayList<>(15);
//        HomeCategory category = new HomeCategory("热门活动",R.drawable.img_big_1,R.drawable.img_1_small1,R.drawable.img_1_small2);
//        datas.add(category);
//
//        category = new HomeCategory("有利可图",R.drawable.img_big_4,R.drawable.img_4_small1,R.drawable.img_4_small2);
//        datas.add(category);
//        category = new HomeCategory("品牌街",R.drawable.img_big_2,R.drawable.img_2_small1,R.drawable.img_2_small2);
//        datas.add(category);
//
//        category = new HomeCategory("金融街 包赚翻",R.drawable.img_big_1,R.drawable.img_3_small1,R.drawable.imag_3_small2);
//        datas.add(category);
//
//        category = new HomeCategory("超值购",R.drawable.img_big_0,R.drawable.img_0_small1,R.drawable.img_0_small2);
//        datas.add(category);
//
//        mHomeCategoryAdapter = new HomeCategoryAdapter(datas);
//        mRecyclerView.setAdapter(mHomeCategoryAdapter);
//
//        //设置属性
//        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//       // mRecyclerView.addItemDecoration(new DividerGridItemDecoration(getContext()));
//        mRecyclerView.addItemDecoration(new DividerItemDecortion());

        mHttpHelper.get(Contants.API.CAMPAIGN_HOME, new SpotsCallback<List<HomeCampaign>>(getContext()) {


            @Override
            public void onFailure(Request request, Exception e) {

            }

            @Override
            public void onSuccess(Response response, List<HomeCampaign> homeCampaigns) {
                initData(homeCampaigns);
            }

            @Override
            public void onError(Response response, int code, Exception e) {

            }


        });

    }

    /**
     *
     */
    public void initData(List<HomeCampaign> homeCampaigns){
        mHomeCategoryAdapter = new HomeCategoryAdapter(homeCampaigns,getContext());

        mHomeCategoryAdapter.setListener(new HomeCategoryAdapter.OnCampaignClickListener() {
            @Override
            public void onClick(View view, Campaign campaign) {

                Intent intent = new Intent(getContext(), WaresListActivity.class);
                intent.putExtra(Contants.COMPAINGAIN_ID,campaign.getId());
                startActivity(intent);
                Toast.makeText(getContext(), campaign.getTitle(), Toast.LENGTH_SHORT).show();
            }
        });
        mRecyclerView.setAdapter(mHomeCategoryAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        mRecyclerView.addItemDecoration(new DividerItemDecortion());

    }

    /**
     * 请求网络数据
     */
    private void requestImages(){

        mHttpHelper.get(Contants.API.BANNER + "?type=1", new SpotsCallback<List<Banner>>(getContext()) {


            @Override
            public void onFailure(Request request, Exception e) {

            }

            @Override
            public void onSuccess(Response response, List<Banner> banners) {
                mBanners = banners;
                initSlider();
            }

            @Override
            public void onError(Response response, int code, Exception e) {

            }

        });

        }

    private void initSlider() {

        for (Banner banner:mBanners) {
            TextSliderView textSliderView = new TextSliderView(this.getContext());
            textSliderView.image(banner.getImgUrl());
            textSliderView.description(banner.getName());
            textSliderView.setScaleType(BaseSliderView.ScaleType.Fit);
            slider.addSlider(textSliderView);
        }

        //第一个事默认的转场效果
        //sliderShow.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);

        //自定义的指示器
        slider.setCustomIndicator(custom_indicator);
        //默认的动画
        slider.setCustomAnimation(new DescriptionAnimation());
        //设置专场效果  16种转场效果
        slider.setPresetTransformer(SliderLayout.Transformer.RotateUp);
        //设置持续事件
        slider.setDuration(3000);

       /* //对图片的点击监听事件
        sliderShow.addOnPageChangeListener(new ViewPagerEx.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Log.d("HomeFragment", "onPageScrolled");
            }

            @Override
            public void onPageSelected(int position) {
                Log.d("HomeFragment", "onPageSelected");
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                Log.d("HomeFragment", "onPageScrollStateChanged");
            }
        });*/

    }

    /**
     * Note! To prevent a memory leak on rotation,
     * make sure to call stopAutoCycle() on the slider before activity or fragment is destroyed:
     */
    @Override
    public void onStop() {
        slider.stopAutoCycle();
        super.onStop();
    }
}
