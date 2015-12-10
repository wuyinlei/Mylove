package com.example.ruolan.letgo.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.example.ruolan.letgo.R;
import com.example.ruolan.letgo.widget.LetToolBar;

/**
 * Created by ruolan on 2015/11/29.
 */
public class TestActivity extends AppCompatActivity {
    private LetToolBar mToolBar;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.text_ceshi);
        mToolBar = (LetToolBar) findViewById(R.id.toolbar);
        mToolBar.setRightButtonOnClickLinster(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(TestActivity.this, "点击我了哈", Toast.LENGTH_SHORT).show();
            }
        });
        mToolBar.setLeftButtonOnClickLinster(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(TestActivity.this, "点击我了哈", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
