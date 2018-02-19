package com.zhangwenl1993163.chargehelper;

import android.content.Context;

import com.zhangwenl1993163.chargehelper.util.DBUtil;

/**
 * Created by zhang on 2018/2/4.
 */

public class Application extends android.app.Application {
    private static Context context;
    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        DBUtil.init(this);
    }

    public static Context getContext(){
        return context;
    }
}
