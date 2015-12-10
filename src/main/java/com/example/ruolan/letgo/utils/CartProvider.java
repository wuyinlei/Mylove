package com.example.ruolan.letgo.utils;

import android.content.Context;
import android.util.SparseArray;

import com.example.ruolan.letgo.bean.ShoppingCart;
import com.example.ruolan.letgo.bean.Wares;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ruolan on 2015/12/3.
 */
public class CartProvider {

    private static final String CART_JSON = "wuyinlei";

    /**
     * 这个是android自身提供的一个比HashMap高效的
     */
    private SparseArray datas = null;

    private Context mContext;

    public CartProvider(Context context) {
        mContext = context;
        datas = new SparseArray<>(10);
        listToSparse();
    }

    /**
     * 往购物车中放入数据
     * @param cart
     */
    public void put(ShoppingCart cart) {
        ShoppingCart temp = (ShoppingCart) datas.get(cart.getId().intValue());
        if (temp != null) {   //如果这个数据已经在购物车中了，那么就要加一
            temp.setCount(temp.getCount() + 1);
        } else {   //如果之前购物车中没有这个数据，那么就直接把这个数据放入到购物车中
            temp = cart;
            temp.setCount(1);
        }
        datas.put(cart.getId().intValue(), temp);
        commit();
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
    public void put(Wares wares) {
      ShoppingCart cart = convertDatas(wares);
        put(cart);
    }

    //更新数据
    public void update(ShoppingCart cart) {
        datas.put(cart.getId().intValue(), cart);
        commit();
    }

    //删除数据
    public void delete(ShoppingCart cart) {
        datas.delete(cart.getId().intValue());
        commit();
    }

    public List<ShoppingCart> getAll() {
        return getDataFromLocal();
    }

    /**
     * 从本地中读取数据
     * @return
     */
    public List<ShoppingCart> getDataFromLocal() {
        String json = PreferencesUtils.getString(mContext, CART_JSON);
        List<ShoppingCart> carts = null;
        if (json != null) {
            //读取的是json数据类型
            carts = JSONUtil.fromJson(json, new TypeToken<List<ShoppingCart>>() {
            }.getType());
        }
        return carts;
    }

    /**
     * 提交数据，就是把数据转换为json格式，保存到本地
     */
    public void commit() {
        List<ShoppingCart> carts = sparseToList();
        PreferencesUtils.putString(mContext, CART_JSON, JSONUtil.toJSON(carts));
    }

    public void clear(){
        int size = datas.size();
        List<ShoppingCart> list = new ArrayList<>(size);
        //就是一个for循环
        for (int i = 0; i < size; i++) {
            list.add((ShoppingCart) datas.valueAt(i));
        }
        for (ShoppingCart shop:list) {
            delete(shop);
        }
        commit();
    }

    /**
     * 把sparse格式的数据转换为list格式的数据
     * @return
     */
    public List<ShoppingCart> sparseToList() {
        int size = datas.size();
        List<ShoppingCart> list = new ArrayList<>(size);
        //就是一个for循环
        for (int i = 0; i < size; i++) {
            list.add((ShoppingCart) datas.valueAt(i));
        }
        return list;
    }

    /**
     * 把list格式的数据转换为sparse格式的数据
     */
    public void listToSparse() {
        List<ShoppingCart> carts = getDataFromLocal();
        if (carts != null && carts.size() > 0) {
            for (ShoppingCart cart : carts) {
                datas.put(cart.getId().intValue(), cart);

            }
        }
    }

}
