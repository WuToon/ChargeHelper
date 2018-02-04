package com.zhangwenl1993163.chargehelper;

import com.zhangwenl1993163.chargehelper.dao.DBUtil;

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
