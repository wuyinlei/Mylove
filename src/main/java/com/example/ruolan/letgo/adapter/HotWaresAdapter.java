package com.example.ruolan.letgo.adapter;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ruolan.letgo.R;
import com.example.ruolan.letgo.bean.Wares;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

/**
 * Created by ruolan on 2015/12/1.
 */
public class HotWaresAdapter extends RecyclerView.Adapter<HotWaresAdapter.ViewHolder> {

    private List<Wares> mDatas;

    private LayoutInflater mInflater;

    public HotWaresAdapter(List<Wares> wares) {
        mDatas = wares;
    }

    /**
     * 清空数据的方法
     */
    public void clean(){
        mDatas.clear();
        notifyItemRangeRemoved(0,mDatas.size());
    }

    /**
     * 获得第一页数据的长度
     */
    public List<Wares> getDatas(){
        return mDatas;
    }

    /**
     *添加数据的方法
     */
    public void addData(int position,List<Wares> wares){
        if (wares != null && wares.size()>0){
            mDatas.addAll(wares);
            notifyItemRangeChanged(position,mDatas.size());
        }
    }

    /**
     * 添加数据的方法
     * @param wares
     */
    public void addData(List<Wares> wares){
        addData(0,wares);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mInflater = LayoutInflater.from(parent.getContext());
        View view = mInflater.inflate(R.layout.template_hot_wares, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Wares wares = mDatas.get(position);
        holder.draweeView.setImageURI(Uri.parse(wares.getImgUrl()));
        holder.mTextTitle.setText(wares.getName());
        holder.mTextPrice.setText("￥" + wares.getPrice());
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView mTextTitle, mTextPrice;
        SimpleDraweeView draweeView;

        public ViewHolder(View itemView) {
            super(itemView);
            draweeView = (SimpleDraweeView) itemView.findViewById(R.id.drawee_view);
            mTextTitle = (TextView) itemView.findViewById(R.id.text_title);
            mTextPrice = (TextView) itemView.findViewById(R.id.text_price);
        }
    }
}
