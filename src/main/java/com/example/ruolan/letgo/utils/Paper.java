package com.example.ruolan.letgo.utils;

import android.content.Context;
import android.widget.Toast;

import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.example.ruolan.letgo.bean.Page;
import com.example.ruolan.letgo.http.OkHttpHelper;
import com.example.ruolan.letgo.http.SpotsCallback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ruolan on 2015/12/4.
 */
public class Paper {


    private static Builder builder;

    private OkHttpHelper httpHelper;

    //三种状态来判断是否刷新、正常、加载
    private  static final int STATE_NORMAL=0;
    private  static final int STATE_REFREH=1;
    private  static final int STATE_MORE=2;
    private int STATE =STATE_NORMAL;


    private  Paper(){
        //在这里初始化OkHttpHelper实例
        httpHelper = OkHttpHelper.getInstance();
        initRefreshLayout();
    }


    public  static Builder newBuilder(){
        builder = new Builder();
        return builder;
    }

    //对外提供一个公开的访问数据的方法以被其他类滴调用
    public void request(){
        requestData();
    }

    public void  putParam(String key,Object value){
        builder.params.put(key, value);

    }


    /**
     * 初始化刷新的方法
     */
    private void initRefreshLayout(){


        builder.mRefreshLayout.setLoadMore(builder.canLoadMore);

        builder.mRefreshLayout.setMaterialRefreshListener(new MaterialRefreshListener() {

            //刷新数据
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                builder.mRefreshLayout.setLoadMore(builder.canLoadMore);
                refresh();
            }


            //加载更多
            @Override
            public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout) {

                if (builder.pageIndex < builder.totalPage)
                    loadMore();
                else {
                    Toast.makeText(builder.mContext, "无更多数据", Toast.LENGTH_LONG).show();
                    materialRefreshLayout.finishRefreshLoadMore();
                    materialRefreshLayout.setLoadMore(false);
                }
            }
        });
    }

    /**
     * 请求数据
     */
    private void requestData(){

        String url = buildUrl();

        httpHelper.get(url, new RequestCallBack<>(builder.mContext));

    }

    /**
     * 显示数据
     */
    private <T> void showData(List<T> datas,int totalPage,int totalCount){

        //判断数据是否是空，如果是空的，就会提示加载不到数据
        if(datas ==null|| datas.size()<=0){
            Toast.makeText(builder.mContext,"加载不到数据",Toast.LENGTH_LONG).show();
            return;
        }

        //如果是正常状态，就加载数据
        if(STATE_NORMAL== STATE){

            if(builder.onPageListener !=null){
                builder.onPageListener.load(datas,totalPage,totalCount);
            }
        }

        //是刷新状态就调用刷新接口
        else  if(STATE_REFREH== STATE)   {
            builder.mRefreshLayout.finishRefresh();
            if(builder.onPageListener !=null){
                builder.onPageListener.refresh(datas,totalPage,totalCount);
            }

        }

        //是加载更多状态就调用加载更多的接口
        else  if(STATE_MORE == STATE){

            builder.mRefreshLayout.finishRefreshLoadMore();
            if(builder.onPageListener !=null){
                builder.onPageListener.loadMore(datas,totalPage,totalCount);
            }
        }
    }

    /**
     * 刷新数据
     */
    private void refresh(){

        STATE =STATE_REFREH;
        builder.pageIndex =1;
        requestData();
    }

    /**
     * 加载更多数据
     */
    private void loadMore(){

        STATE =STATE_MORE;
        builder.pageIndex =++builder.pageIndex;
        requestData();
    }


    /**
     * 构建URL
     * @return
     */
    private String buildUrl(){

        return builder.mUrl +"?"+buildUrlParams();
    }


    /**
     * 构造地址
     * @return
     */
    private String buildUrlParams() {

        HashMap<String, Object> map = builder.params;

        map.put("curPage",builder.pageIndex);
        map.put("pageSize",builder.pageSize);

        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            sb.append(entry.getKey() + "=" + entry.getValue());
            sb.append("&");
        }
        String s = sb.toString();
        if (s.endsWith("&")) {
            s = s.substring(0,s.length()-1);
        }
        return s;
    }


    /**
     *定义个一个内部类
     */
    public static class Builder{


        private Context mContext;
        private Type mType;
        private String mUrl;

        private MaterialRefreshLayout mRefreshLayout;

        private boolean canLoadMore;


        private int totalPage = 1;
        private int pageIndex = 1;
        private int pageSize = 10;

        private HashMap<String,Object> params = new HashMap<>(5);

        private OnPageListener onPageListener;

        //传入url
        public Builder setUrl(String url){
            builder.mUrl = url;
            return builder;
        }

        //传入pageSize
        public Builder setPageSize(int pageSize){
            this.pageSize = pageSize;
            return builder;
        }

        public Builder putParam(String key,Object value){
            params.put(key,value);
            return builder;
        }

        //是否加载更多的判断
        public Builder setLoadMore(boolean loadMore){
            this.canLoadMore = loadMore;
            return builder;
        }

        public Builder setRefreshLayout(MaterialRefreshLayout refreshLayout){

            this.mRefreshLayout = refreshLayout;
            return builder;
        }


        public Builder setOnPageListener(OnPageListener onPageListener) {
            this.onPageListener = onPageListener;
            return builder;
        }


        //在这我们直接传入type，也就是为了防止在数据为空的时候，报错（找不到类型）
        public Paper build(Context context, Type type){

            this.mType = type;
            this.mContext =context;

            valid();
            return new Paper();

        }


        //判断是否为空
        private void valid(){

            if(this.mContext==null)
                throw  new RuntimeException("content can't be null");

            if(this.mUrl==null || "".equals(this.mUrl))
                throw  new RuntimeException("url can't be  null");

            if(this.mRefreshLayout==null)
                throw  new RuntimeException("MaterialRefreshLayout can't be  null");
        }

    }


    class  RequestCallBack<T> extends SpotsCallback<Page<T>> {

        public RequestCallBack(Context context) {
            super(context);

            super.mType = builder.mType;
        }

        @Override
        public void onFailure(Request request, Exception e) {

            //如果没有这一句就会在加载失败的时候，对话框是不能消失的
            dismissDialog();
            Toast.makeText(builder.mContext,"请求出错："+e.getMessage(),Toast.LENGTH_LONG).show();

            if(STATE_REFREH== STATE)   {
                //完成刷新
                builder.mRefreshLayout.finishRefresh();
            }
            else  if(STATE_MORE == STATE){

                //完成加载更多
                builder.mRefreshLayout.finishRefreshLoadMore();
            }
        }



        @Override
        public void onSuccess(Response response, Page<T> page) {

            //在请求成功过的时候获取到当前页数，pageSize，页数的总数
            builder.pageIndex = page.getCurrentPage();
            builder.pageSize = page.getPageSize();
            builder.totalPage = page.getTotalPage();

            //然后调用显示数据，传入必要的参数
            showData(page.getList(),page.getTotalPage(),page.getTotalCount());
        }

        @Override
        public void onError(Response response, int code, Exception e) {

            Toast.makeText(builder.mContext, "加载数据失败", Toast.LENGTH_LONG).show();

            if(STATE_REFREH== STATE)   {
                builder.mRefreshLayout.finishRefresh();
            }
            else  if(STATE_MORE == STATE){

                builder.mRefreshLayout.finishRefreshLoadMore();
            }
        }

        @Override
        public void onTokenError(Response response, int code) {

        }
    }




    public interface  OnPageListener<T>{

        void load(List<T> datas,int totalPage,int totalCount);

        void refresh(List<T> datas,int totalPage,int totalCount);

        void loadMore(List<T> datas,int totalPage,int totalCount);


    }
}
