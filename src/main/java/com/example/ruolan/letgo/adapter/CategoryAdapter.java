package com.example.ruolan.letgo.adapter;

import android.content.Context;

import com.example.ruolan.letgo.R;
import com.example.ruolan.letgo.bean.Category;

import java.util.List;

/**
 * Created by ruolan on 2015/12/2.
 */
public class CategoryAdapter extends SimpleAdapter<Category> {


    public CategoryAdapter(Context context, List<Category> datas) {
        super(context, datas, R.layout.template_single_text);
    }

    @Override
    protected void convert(BaseViewHolder viewHoder, Category item) {
        viewHoder.getTextView(R.id.textView).setText(item.getName());
    }
}
