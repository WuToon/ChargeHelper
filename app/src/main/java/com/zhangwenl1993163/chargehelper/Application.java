package com.zhangwenl1993163.chargehelper;

import android.util.TypedValue;

import com.zhangwenl1993163.chargehelper.util.DBUtil;

/**
 * Created by zhang on 2018/2/4.
 */

public class Application extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        DBUtil.init(this);
    }
}
