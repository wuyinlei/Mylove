package com.example.ruolan.letgo.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.View;

import com.example.ruolan.letgo.R;
import com.example.ruolan.letgo.bean.ShoppingCart;
import com.example.ruolan.letgo.bean.Wares;
import com.example.ruolan.letgo.utils.CartProvider;
import com.example.ruolan.letgo.utils.ToastUtils;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

/**
 * Created by ruolan on 2015/12/1.
 */
public class HWAdapter extends SimpleAdapter<Wares>{

    CartProvider mCartProvider;

    public HWAdapter(Context context, List<Wares> datas) {
        super(context, datas, R.layout.template_hot_wares);
        mCartProvider = new CartProvider(context);
    }


    /**
     * 在这里进行数据绑定，也就是相当于onBindViewHolder
     * @param viewHoder A fully initialized helper.
     * @param item   The item that needs to be displayed.
     */
    @Override
    protected void convert(BaseViewHolder viewHoder, final Wares item) {
        SimpleDraweeView draweeView = (SimpleDraweeView) viewHoder.getView(R.id.drawee_view);
        draweeView.setImageURI(Uri.parse(item.getImgUrl()));
        viewHoder.getTextView(R.id.text_title).setText(item.getName());
        viewHoder.getTextView(R.id.text_price).setText("￥" + item.getPrice());
        if (viewHoder.getButton(R.id.btn_add) != null) {  //在这要判断一下这个button是不是为空，也就是为了在
            //商品列表中点击的时候，用到了这个布局，防止出错，也可以在商品详情的那个界面，可以重新定义一个布局
            //就可以解决这个办法
            viewHoder.getButton(R.id.btn_add).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCartProvider.put(item);
                    ToastUtils.show(context, "已经添加到购物车");
                }
            });
        }
    }

    /**
     * 把Wares改成ShoppingCart
     * @param item
     * @return
     */
    public ShoppingCart convertDatas(Wares item){
        ShoppingCart cart = new ShoppingCart();
        cart.setId(item.getId());
        cart.setDescription(item.getDescription());
        cart.setImgUrl(item.getImgUrl());
        cart.setPrice(item.getPrice());
        cart.setName(item.getName());
        return cart;
    }

    /**
     * 更改布局的方法，也就是动态的改变了layoutId
     * @param layoutId
     */
    public void resetLayout(int layoutId){
        this.layoutResId  = layoutId;
        notifyItemRangeChanged(0,getDatas().size());
    }
}
