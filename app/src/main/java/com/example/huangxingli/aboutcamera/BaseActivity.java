package com.example.huangxingli.aboutcamera;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by huangxingli on 2015/5/14.
 */
public abstract class BaseActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentRes());
        initView();
    }

    public abstract int getContentRes();

    public abstract void initView();
}
