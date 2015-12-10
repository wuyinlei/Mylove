package com.example.ruolan.letgo.adapter;

import android.content.Context;
import android.net.Uri;

import com.example.ruolan.letgo.R;
import com.example.ruolan.letgo.bean.Wares;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

/**
 * Created by ruolan on 2015/12/2.
 */
public class WaresAdapter extends SimpleAdapter<Wares> {
    public WaresAdapter(Context context, List<Wares> datas) {
        super(context, datas, R.layout.template_grid_wares);
    }

    @Override
    protected void convert(BaseViewHolder viewHoder, Wares item) {
        SimpleDraweeView draweeView = (SimpleDraweeView) viewHoder.getView(R.id.drawee_view);
        draweeView.setImageURI(Uri.parse(item.getImgUrl()));
        viewHoder.getTextView(R.id.text_title).setText(item.getName());
        viewHoder.getTextView(R.id.text_price).setText("￥" + item.getPrice());
    }
}
