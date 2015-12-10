package com.example.ruolan.letgo.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;


import com.example.ruolan.letgo.application.CniaoApplication;
import com.example.ruolan.letgo.bean.User;

/**
 * Created by ruolan on 2015/12/6.
 */
public class BaseActivity extends AppCompatActivity {

    public void startActivity(Intent intent,boolean isNeedLogin){
        if (isNeedLogin){
            User user = CniaoApplication.getInstance().getUser();
            if (user != null){
                super.startActivity(intent);
            }
            else {
                CniaoApplication.getInstance().putIntent(intent);
                Intent loginIntent = new Intent(this,LoginActivity.class);
                super.startActivity(intent);
            }
        }
        else {
            super.startActivity(intent);
        }
    }
}
