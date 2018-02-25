package com.zhangwenl1993163.chargehelper;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.zhangwenl1993163.chargehelper.util.DateUtil;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by zhang on 2018/2/5.
 */
@RunWith(AndroidJUnit4.class)
public class DBUtilTest {
    private final String TAG = DBUtilTest.class.getName();
    private Context context = InstrumentationRegistry.getContext();

    @Test
    public void getDayRangeTest(){
        List<Long> longs = DateUtil.getDayRange(System.currentTimeMillis());
        Log.d(TAG,"=================start===============");
        Log.d(TAG,new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(longs.get(0))));
        Log.d(TAG,"=================end===============");
        Log.d(TAG,new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(longs.get(1))));
    }
    @Test
    public void getMonthRangeTest(){
        List<Long> longs = DateUtil.getMonthRange(System.currentTimeMillis());
        Log.d(TAG,"=================start===============");
        Log.d(TAG,new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(longs.get(0))));
        Log.d(TAG,"=================end===============");
        Log.d(TAG,new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(longs.get(1))));
    }
    @Test
    public void getYearRangeTest(){
        List<Long> longs = DateUtil.getYearRange(System.currentTimeMillis());
        Log.d(TAG,"=================start===============");
        Log.d(TAG,new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(longs.get(0))));
        Log.d(TAG,"=================end===============");
        Log.d(TAG,new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(longs.get(1))));
    }
}
