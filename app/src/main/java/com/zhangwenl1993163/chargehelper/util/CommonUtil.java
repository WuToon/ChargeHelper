package com.zhangwenl1993163.chargehelper.util;

import android.content.Context;

/**
 * Created by zhang on 2018/2/9.
 */

public class CommonUtil {
    public static int px2dp(Context context,int px){
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (px / scale + 0.5f);
    }

    public static int dp2px(Context context, int dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
}
