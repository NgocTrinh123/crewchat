package com.dazone.crewchat.activity.base;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.dazone.crewchat.R;

public abstract class BaseSingleBackActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        addFragment(savedInstanceState);
    }

    protected TextView toolbar_title;
    protected ImageView ivBack;

    protected void init() {
        setContentView(R.layout.activity_base_single_back);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        toolbar_title = (TextView) findViewById(R.id.toolbar_title);
        ivBack = (ImageView) findViewById(R.id.back_imv);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void setUPToolBar(String title) {
        if (TextUtils.isEmpty(title)) {
            toolbar_title.setText(mContext.getResources().getString(R.string.unknown));
            toolbar_title.setTextColor(ContextCompat.getColor(mContext, R.color.gray));
        } else {
            toolbar_title.setText(title);
            toolbar_title.setTextColor(ContextCompat.getColor(mContext, R.color.white));
        }

    }


    protected abstract void addFragment(Bundle bundle);

    public void setTitle(String title) {
        toolbar_title.setText(title);
    }


}
