package com.zhangwenl1993163.chargehelper.util;

import android.content.Context;
import android.widget.Toast;

import com.zhangwenl1993163.chargehelper.Application;

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

    public static void showMsgShort(String msg){
        Toast.makeText(Application.getContext(),msg,Toast.LENGTH_SHORT).show();
    }

    public static void showMsgLong(String msg){
        Toast.makeText(Application.getContext(),msg,Toast.LENGTH_LONG).show();
    }
}
