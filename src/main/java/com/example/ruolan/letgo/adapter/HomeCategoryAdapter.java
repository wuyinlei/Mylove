package com.example.ruolan.letgo.adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ruolan.letgo.R;
import com.example.ruolan.letgo.bean.Campaign;
import com.example.ruolan.letgo.bean.HomeCampaign;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by ruolan on 2015/11/30.
 */
public class HomeCategoryAdapter extends RecyclerView.Adapter<HomeCategoryAdapter.ViewHolder> {

    private static final int VIEW_TYPE_R = 0;
    private static final int VIEW_TYPE_L = 1;

    private List<HomeCampaign> mDatas;

    private OnCampaignClickListener mListener;

    public void setListener(OnCampaignClickListener listener) {
        mListener = listener;
    }

    private Context mContext;

    public HomeCategoryAdapter(List<HomeCampaign> datas,Context context) {
        mDatas = datas;
        mContext = context;
    }

    private LayoutInflater mInflater;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        mInflater = LayoutInflater.from(parent.getContext());

        if (viewType == VIEW_TYPE_R) {
            return new ViewHolder(mInflater.inflate(R.layout.template_home_cardview2, parent, false));
        } else if (viewType == VIEW_TYPE_L) {
            return new ViewHolder(mInflater.inflate(R.layout.templete_home_cardview, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        //绑定数据
        HomeCampaign campaign = mDatas.get(position);
        holder.mTextTitle.setText(campaign.getTitle());
        /*holder.mImageViewBig.setImageResource(campaign.getCpOne().getImgUrl());
        holder.mImageViewSmallTop.setImageResource(category.getImgSmallTop());
        holder.mImageViewSmallBottom.setImageResource(category.getImgSmallBottom());*/

        /**
         * 在调用Picasso图片加载的时候要这样加载图片
         */
        Picasso.with(mContext).load(campaign.getCpOne().getImgUrl()).into(holder.mImageViewBig);
        Picasso.with(mContext).load(campaign.getCpTwo().getImgUrl()).into(holder.mImageViewSmallTop);
        Picasso.with(mContext).load(campaign.getCpThree().getImgUrl()).into(holder.mImageViewSmallBottom);

    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position % 2 == 0) {
            return VIEW_TYPE_R;
        } else
            return VIEW_TYPE_L;
    }

     class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView mTextTitle;
        ImageView mImageViewBig;
        ImageView mImageViewSmallTop;
        ImageView mImageViewSmallBottom;

        public ViewHolder(View itemView) {
            super(itemView);
            mTextTitle = (TextView) itemView.findViewById(R.id.title_text);
            mImageViewBig = (ImageView) itemView.findViewById(R.id.imgview_big);
            mImageViewSmallTop = (ImageView) itemView.findViewById(R.id.imgview_small_top);
            mImageViewSmallBottom = (ImageView) itemView.findViewById(R.id.imgview_small_bottom);

            mImageViewBig.setOnClickListener(this);
            mImageViewSmallTop.setOnClickListener(this);
            mImageViewSmallBottom.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mListener != null) {
                aniv(v);
            }
        }


         /**
          * 一个动画的效果
          * @param v
          */
         private void aniv(final View v) {
             ObjectAnimator animator = ObjectAnimator.ofFloat(v, "rotationX", 0.0F, 360.0F)
                     .setDuration(200);
             animator.addListener(new AnimatorListenerAdapter() {

                 @Override
                 public void onAnimationEnd(Animator animation) {

                     HomeCampaign campaign = mDatas.get(getLayoutPosition());

                     switch (v.getId()) {

                         case R.id.imgview_big:
                             mListener.onClick(v, campaign.getCpOne());
                             break;

                         case R.id.imgview_small_top:
                             mListener.onClick(v, campaign.getCpTwo());
                             break;

                         case R.id.imgview_small_bottom:
                             mListener.onClick(v, campaign.getCpThree());
                             break;

                     }

                 }
             });
             animator.start();
         }
    }

   public interface OnCampaignClickListener{
        void onClick(View view,Campaign campaign);
    }
}
