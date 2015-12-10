package com.example.ruolan.letgo.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by ruolan on 2015/12/1.
 */
public class BaseViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    /**
     * 稀疏数组，android内部提供的，是一个比HashMap更加高效的一个数组
     */
    private SparseArray<View> views;

    private BaseAdapter.OnItemClickListener mOnItemClickListener ;

    public BaseViewHolder(View itemView,BaseAdapter.OnItemClickListener onItemClickListener){
        super(itemView);
        itemView.setOnClickListener(this);

        this.mOnItemClickListener =onItemClickListener;
        this.views = new SparseArray<View>();
    }

    /**
     * 获取TextView组件的方法
     * @param viewId
     * @return
     */
    public TextView getTextView(int viewId) {
        return retrieveView(viewId);
    }

    /**
     * 获取到Button组件的方法
     * @param viewId
     * @return
     */
    public Button getButton(int viewId) {
        return retrieveView(viewId);
    }

    /**
     * 获取到ImageView组件的方法
     * @param viewId
     * @return
     */
    public ImageView getImageView(int viewId) {
        return retrieveView(viewId);
    }

    public View getView(int viewId) {
        return retrieveView(viewId);
    }


    /**
     *
     * @param viewId
     * @param <T>
     * @return
     */
    protected <T extends View> T retrieveView(int viewId) {
        View view = views.get(viewId);
        if (view == null) {
            view = itemView.findViewById(viewId);
            views.put(viewId, view);
        }
        return (T) view;
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(v,getLayoutPosition());
        }
    }
}
