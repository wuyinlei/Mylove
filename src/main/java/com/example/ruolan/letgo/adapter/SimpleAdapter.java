package com.example.ruolan.letgo.adapter;

import android.content.Context;

import java.util.List;

/**
 * Created by ruolan on 2015/12/1.
 */
public abstract class SimpleAdapter<T> extends BaseAdapter<T, BaseViewHolder> {
    public SimpleAdapter(Context context, List<T> datas, int layoutResId) {
        super(context, layoutResId,datas);
    }

    public SimpleAdapter(Context context, int layoutResId) {
        super(context, layoutResId);
    }
}
