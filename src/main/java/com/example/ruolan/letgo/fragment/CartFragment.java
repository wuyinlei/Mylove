package com.example.ruolan.letgo.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.ruolan.letgo.R;
import com.example.ruolan.letgo.activity.CreateOrderActivity;
import com.example.ruolan.letgo.activity.MainActivity;
import com.example.ruolan.letgo.adapter.CartAdapter;
import com.example.ruolan.letgo.adapter.divider.DividerItemDecoration;
import com.example.ruolan.letgo.bean.ShoppingCart;
import com.example.ruolan.letgo.utils.CartProvider;
import com.example.ruolan.letgo.widget.LetToolBar;

import java.util.List;

/**
 * Created by ruolan on 2015/11/29.
 */
public class CartFragment extends BaseFragment implements View.OnClickListener {

    /**
     * 下面的两个是设置的标志位
     */
    private static final int ACTION_EDIT=1;
    private static final int ACTION_FINISH =2;

    private CartProvider mCartProvider;
    private CartAdapter mCartAdapter;

    private RecyclerView mRecyclerView;

    private CheckBox mCheckBox;

    private TextView mTextTotal;

    private Button mButtonOrd;

    private Button mButtonDel;

    private LetToolBar mToolBar;


    @Override
    public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mCheckBox = (CheckBox) view.findViewById(R.id.checkbox_all);
        mTextTotal = (TextView) view.findViewById(R.id.txt_total);
        mButtonOrd = (Button) view.findViewById(R.id.btn_order);
        mButtonOrd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), CreateOrderActivity.class);
                startActivity(intent, true);
            }
        });
        mButtonDel = (Button) view.findViewById(R.id.btn_del);
        mButtonDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCartAdapter.delCart();
            }
        });
        mCartProvider = new CartProvider(getActivity());
        showData();
        return view;
    }

    @Override
    public void init() {

    }

    /**
     * 显示数据的
     */
    private void showData() {
        List<ShoppingCart> carts = mCartProvider.getAll();
        mCartAdapter = new CartAdapter(getContext(), carts,mCheckBox,mTextTotal);
        mRecyclerView.setAdapter(mCartAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));
    }

    public void refData(){
        mCartAdapter.clear();
        List<ShoppingCart> carts = mCartProvider.getAll();
        mCartAdapter.addData(carts);
        mCartAdapter.showTotalPrice();
    }

    /**
     * fragment的一个生命周期，这是刚开始的一个方法
     * @param context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity){
            MainActivity activity = (MainActivity) context;
            mToolBar = (LetToolBar) activity.findViewById(R.id.toolbar);
            mToolBar.setTitle(R.string.cart);
            mToolBar.setRightButtonTitle("编辑");
            mToolBar.getRightButton().setTag(ACTION_EDIT);
            mToolBar.setRightButtonOnClickLinster(this);

        }
    }

    /**
     * 隐藏删除按钮，也就是购买的时候的一个逻辑实现
     */
    private void hideDelControl() {
        mCartAdapter.showTotalPrice();
        mToolBar.setRightButtonTitle(getString(R.string.edit));
        mTextTotal.setVisibility(View.VISIBLE);
        mButtonOrd.setVisibility(View.VISIBLE);
        mButtonDel.setVisibility(View.GONE);
        mCheckBox.setChecked(true);
        mCartAdapter.setCheckBox(mCheckBox);
        mCartAdapter.check_All(true);
    }


    /**
     * 显示删除按钮的时候的一些逻辑实现
     */
    private void showDelControl(){
        mToolBar.setRightButtonTitle(getString(R.string.finish));
        mTextTotal.setVisibility(View.GONE);
        mButtonOrd.setVisibility(View.GONE);
        mButtonDel.setVisibility(View.VISIBLE);
        mCheckBox.setChecked(false);
        mCartAdapter.setCheckBox(mCheckBox);
        mCartAdapter.check_All(false);
    }


    /**
     * 对ToolBar右侧按钮的一个监听事件
     * 给他一个标志，分别为完成和编辑
     * 如果是完成显示的时候，也就是点击了编辑，那么给他设置标志位 ACTION_FINISH
     * 那么这个时候，就要是全部不选中，并且显示删除的按钮
     *
     * 如果是编辑显示的时候，也就是点击了完成，那么给他设置标志位ACTION_EDIT
     * 那么这个时候，就要全部选中，显示结算按钮
     * @param v
     */
    @Override
    public void onClick(View v) {
        int action = (int) v.getTag();
        if (ACTION_EDIT == action) {
            showDelControl();
            //mCartAdapter.showTotalPrice();
            mToolBar.getRightButton().setTag(ACTION_FINISH);
        } else if (ACTION_FINISH == action) {
            mToolBar.getRightButton().setTag(ACTION_EDIT);
            hideDelControl();
        }
    }
}
