package com.example.ruolan.letgo.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.ruolan.letgo.R;
import com.example.ruolan.letgo.activity.LoginActivity;
import com.example.ruolan.letgo.application.CniaoApplication;
import com.example.ruolan.letgo.bean.User;
import com.example.ruolan.letgo.uri.Contants;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by ruolan on 2015/11/29.
 */
public class MineFragment extends BaseFragment{

    // private CnToolbar mToolbar;
    @ViewInject(R.id.img_head)
    private CircleImageView mImageHead;


    @ViewInject(R.id.txt_username)
    private TextView mTxtUserName;

    @ViewInject(R.id.btn_logout)
    private Button mbtnLogout;

    @Override
    public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //mToolbar.hideSearchView();
        View view = inflater.inflate(R.layout.fragment_mine,container,false);
        //  mToolbar = (CnToolbar) view.findViewById(R.id.toolbar);

        return view;
    }

    @Override
    public void init() {
        // mToolbar.hideSearchView();
        User user = CniaoApplication.getInstance().getUser();
        showUser(user);
    }

    @OnClick(value = {R.id.img_head, R.id.txt_username})
    public void toLogin(View view) {
       /* Intent intent = new Intent(getContext(), LoginActivity.class);
        startActivityForResult(intent, Contants.REQUEST_CODE);*/
        if(null== CniaoApplication.getInstance().getUser() ) {
            Intent intent = new Intent(getContext(), LoginActivity.class);
            startActivityForResult(intent, Contants.REQUEST_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        User user = CniaoApplication.getInstance().getUser();
        showUser(user);
    }

    private void showUser(User user){
        if(user!=null){

            if(!TextUtils.isEmpty(user.getLogo_url()))
                showHeadImage(user.getLogo_url());

            mTxtUserName.setText(user.getUsername());

            mbtnLogout.setVisibility(View.VISIBLE);
        }
        else {
            // mToolbar.hideSearchView();
            mTxtUserName.setText(R.string.to_login);
            mbtnLogout.setVisibility(View.GONE);
        }
    }
    private void showHeadImage(String url){

        Picasso.with(getActivity()).load(url).into(mImageHead);
    }

    @OnClick(R.id.btn_logout)
    public void logout(View view){

        CniaoApplication.getInstance().clearUser();
        showUser(null);
    }

}
