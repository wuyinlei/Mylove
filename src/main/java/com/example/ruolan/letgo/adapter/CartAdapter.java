package com.example.ruolan.letgo.adapter;

import android.content.Context;
import android.net.Uri;
import android.text.Html;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.ruolan.letgo.R;
import com.example.ruolan.letgo.bean.ShoppingCart;
import com.example.ruolan.letgo.utils.CartProvider;
import com.example.ruolan.letgo.widget.NumAddSubView;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.Iterator;
import java.util.List;

/**
 * Created by ruolan on 2015/12/3.
 */
public class CartAdapter extends SimpleAdapter<ShoppingCart> implements BaseAdapter.OnItemClickListener {

    private CheckBox mCheckBox;
    private TextView mTextView;

    private CartProvider mCartProvider ;

    public CartAdapter(Context context, List<ShoppingCart> datas, CheckBox checkBox, TextView textView) {
        super(context, datas, R.layout.template_cart);
        mCheckBox = checkBox;
        setCheckBox(mCheckBox);
        mTextView = textView;
        setOnItemClickListener(this);
        mCartProvider = new CartProvider(context);
        showTotalPrice();
    }

    @Override
    protected void convert(BaseViewHolder viewHoder, final ShoppingCart item) {
        viewHoder.getTextView(R.id.text_title).setText(item.getName());
        viewHoder.getTextView(R.id.text_price).setText("¥" + item.getPrice());
        SimpleDraweeView draweeView = (SimpleDraweeView) viewHoder.getView(R.id.drawee_view);
        draweeView.setImageURI(Uri.parse(item.getImgUrl()));
        CheckBox checkBox = (CheckBox) viewHoder.getView(R.id.checkbox);
        checkBox.setChecked(item.isChecked());
        NumAddSubView numAddSubView = (NumAddSubView) viewHoder.getView(R.id.num_control);
        numAddSubView.setValue(item.getCount());
        numAddSubView.setOnButtonClickListener(new NumAddSubView.OnButtonClickListener() {
            @Override
            public void onButtonAddClick(View view, int value) {
                item.setCount(value);
                mCartProvider.update(item);

                showTotalPrice();
            }

            @Override
            public void onButtonSumClick(View view, int value) {
                item.setCount(value);
                mCartProvider.update(item);

                showTotalPrice();
            }
        });
    }

    /**
     * 得到购物车中的已经选中商品的总价
     *
     * @return
     */
    private float getTotalPrice() {
        float sum = 0;
        if (!isNull())
            return sum;
        for (ShoppingCart cart : datas) {
            if (cart.isChecked())
                sum += cart.getCount() * cart.getPrice();
        }
        return sum;
    }

    /**
     * 显示总价
     */
    public void showTotalPrice() {
        float total = getTotalPrice();
        mTextView.setText(Html.fromHtml("合计 ￥<span style='color:#eb4f38'>" + total + "</span>"), TextView.BufferType.SPANNABLE);
    }

    /**
     * 判断购物车中是否有商品
     *
     * @return
     */
    private boolean isNull() {
        return (datas.size() > 0 && datas != null);
    }

    /**
     * 对Item实现点击监听事件
     *
     * @param view
     * @param position
     */
    @Override
    public void onItemClick(View view, int position) {
        ShoppingCart cart = getItem(position);
        cart.setIsChecked(!cart.isChecked());
        notifyItemChanged(position);
        isCheck_All();
        showTotalPrice();
    }

    /**
     * 判断购物车中的商品是否全部选中，如果不是的就把全选设置为不选中
     */
    private void isCheck_All() {
        int checkNum = 0;
        int count = 0;
        if (datas != null) {
            count = datas.size();
            for (ShoppingCart cart : datas) {
                if (!cart.isChecked()) {
                    mCheckBox.setChecked(false);
                    break;
                } else {
                    checkNum = checkNum + 1;
                }
            }
            if (count == checkNum)
                mCheckBox.setChecked(true);
        }
    }

    /**
     * 实现点击全选的时候使其购物车中的选项全部选中或者全部选不中
     *
     * @param isCheck
     */
    public void check_All(boolean isCheck) {

        if (!isNull())
            return;
        int i = 0;
        for (ShoppingCart cart : datas) {
            cart.setIsChecked(isCheck);
            notifyItemChanged(i);
            i++;
        }

    }

    /**
     * 删除逻辑，这个删除的时候要在本地和内存这两个位置删除
     */
    public void delCart() {
        if (!isNull())
            return;

        /**
         *必须这样用迭代的方式，去遍历
         */
        for (Iterator iterator = datas.iterator();iterator.hasNext(); ) {
            ShoppingCart cart = (ShoppingCart) iterator.next();
            if (cart.isChecked()) {
                int position = datas.indexOf(cart);
                mCartProvider.delete(cart);
                iterator.remove();
                notifyItemRemoved(position);
            }
            showTotalPrice();

        }

        /**
         * 在对Vector等容器并发地进行迭代修改时，会报ConcurrentModificationException异常
         *  java.util.ConcurrentModificationException
         */
       /* for (ShoppingCart cart : datas) {
            if (cart.isChecked()) {
                int position = datas.indexOf(cart);
                mCartProvider.delete(cart);
                datas.remove(cart);
                notifyItemRemoved(position);
            }
        }*/
    }

    public void setCheckBox(CheckBox check) {
        this.mCheckBox = check;
        mCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check_All(mCheckBox.isChecked());
                showTotalPrice();
            }
        });


    }
}
