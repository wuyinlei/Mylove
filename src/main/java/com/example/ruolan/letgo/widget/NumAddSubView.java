package com.example.ruolan.letgo.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.internal.widget.TintTypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.ruolan.letgo.R;

/**
 * Created by ruolan on 2015/11/14.
 */
public class NumAddSubView extends LinearLayout implements View.OnClickListener {

    private LayoutInflater mInflater;

    private Button btnAdd, btnSub;
    private TextView mTextView;

    private int value;

    private int minValue;

    private int maxValue;

    private OnButtonClickListener mOnButtonClickListener;

    public NumAddSubView(Context context) {
        this(context, null);
    }

    public NumAddSubView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mInflater = LayoutInflater.from(context);
        initView();
        if (attrs != null) {

            //如果自定义属性不为空，我们就通过以下的方式来获取在XML布局文件中自定义的那些属性
            TintTypedArray a = TintTypedArray.obtainStyledAttributes(context, attrs, R.styleable.NumAddSubView, defStyleAttr, 0);

            int val = a.getInt(R.styleable.NumAddSubView_value, 0);
            setValue(val);

            int maxVal = a.getInt(R.styleable.NumAddSubView_maxValue, 5);
            setMaxValue(maxVal);

            int minVal = a.getInt(R.styleable.NumAddSubView_minValue, 1);
            setMinValue(minVal);


            Drawable drawableBtnAdd = a.getDrawable(R.styleable.NumAddSubView_btnAddBackground);
            Drawable drawableBtnSub = a.getDrawable(R.styleable.NumAddSubView_btnSubBackground);
            Drawable drawableTextview = a.getDrawable(R.styleable.NumAddSubView_TextViewBackground);

            setBtnAddBackground(drawableBtnAdd);
            setBtnSubBackground(drawableBtnSub);
            setTextViewBackground(drawableTextview);
            //自定义属性的回收
            a.recycle();
        }
    }

    public void setTextViewBackground(Drawable drawable) {
        mTextView.setBackgroundDrawable(drawable);
    }

    public void setTextViewBackground(int drawbleId) {
        setBackground(getResources().getDrawable(drawbleId));
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void setBtnAddBackground(Drawable drawable) {
        btnAdd.setBackgroundDrawable(drawable);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void setBtnSubBackground(Drawable drawable) {
        btnSub.setBackgroundDrawable(drawable);
    }

    public NumAddSubView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public void initView() {
        View view = mInflater.inflate(R.layout.weight_number_add_sub, this, true);

        btnAdd = (Button) view.findViewById(R.id.numberAdd);
        btnAdd.setOnClickListener(this);
        btnSub = (Button) view.findViewById(R.id.numberSub);
        btnSub.setOnClickListener(this);
        mTextView = (TextView) view.findViewById(R.id.numberTextView);
    }

    public int getValue() {
        String val = mTextView.getText().toString();
        if (val != null || !"".equals(val))
            this.value = Integer.parseInt(val);
        return value;
    }

    public void setValue(int value) {
        mTextView.setText(value + "");
        this.value = value;
    }

    public int getMinValue() {
        return minValue;
    }

    public void setMinValue(int minValue) {
        this.minValue = minValue;
    }

    public int getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
    }

    public void setOnButtonClickListener(OnButtonClickListener onButtonClickListener) {
        mOnButtonClickListener = onButtonClickListener;
    }

    @Override
    public void onClick(View v) {


        if (v.getId() == R.id.numberAdd) {
            numAdd();
            if (mOnButtonClickListener != null) {
                mOnButtonClickListener.onButtonAddClick(v, value);
            }
        } else if (v.getId() == R.id.numberSub) {
            numSub();
            if (mOnButtonClickListener != null) {
                mOnButtonClickListener.onButtonSumClick(v, value);
            }
        }

    }

    private void numSub() {
        if (value > minValue) {
            value = value - 1;
            mTextView.setText(value + "");
        }
    }

    private void numAdd() {
        //取出textview的值
        if (value < maxValue) {
            value = value + 1;
            mTextView.setText(value + "");
        }
    }

    public interface OnButtonClickListener {
        void onButtonAddClick(View view, int value);

        void onButtonSumClick(View view, int value);
    }
}
