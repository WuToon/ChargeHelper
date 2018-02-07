package com.zhangwenl1993163.chargehelper.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by zhang on 2018/2/5.
 */

public class DateUtil {
    private static final Long MILLISECONDS_OF_DAY = (long) 24*60*60*1000;

    /**
     * 获取某天零点到第二天零点的时间戳
     *
     * @param timestamp 当前时间戳
     * @return 两个时间点的时间戳
     * */
    public static List<Long> getDayRange(long timestamp){
        List<Long> longs = new ArrayList<>();
        Calendar now = Calendar.getInstance();
        now.setTime(new Date(timestamp));
        //当天开始的时间戳
        Calendar start = Calendar.getInstance();
        start.set(now.get(Calendar.YEAR),now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH),0,0,0);
        Long startTimeStamp = start.getTimeInMillis();
        //当天结束的时间戳
        Long endTimeStamp = startTimeStamp + MILLISECONDS_OF_DAY;
        longs.add(startTimeStamp);
        longs.add(endTimeStamp);
        return longs;
    }

    /**
     * 获取某月时间戳
     *
     * @param timestamp 当前时间戳
     * @return 两个时间点的时间戳
     * */
    public static List<Long> getMonthRange(long timestamp){
        List<Long> longs = new ArrayList<>();
        Calendar now = Calendar.getInstance();
        now.setTime(new Date(timestamp));
        Calendar start = Calendar.getInstance();
        start.set(now.get(Calendar.YEAR),now.get(Calendar.MONTH),
                1,0,0,0);
        Long startTimeStamp = start.getTimeInMillis();
        Calendar end = Calendar.getInstance();
        end.set(now.get(Calendar.MONTH) == 12 ? now.get(Calendar.YEAR) + 1 : now.get(Calendar.YEAR),
                now.get(Calendar.MONTH) == 12 ? 1 : (now.get(Calendar.MONTH) + 1),
                1,0,0,0);
        Long endTimeStamp = end.getTimeInMillis();
        longs.add(startTimeStamp);
        longs.add(endTimeStamp);
        return longs;
    }

    /**
     * 获取最近三年的年份
     * */
    public static List<Integer> getLatest3Years(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        int now = calendar.get(Calendar.YEAR);
        List<Integer> l = new ArrayList<>();
        l.add(now - 2);
        l.add(now - 1);
        l.add(now);
        return l;
    }
}
