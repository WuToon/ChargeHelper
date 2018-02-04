package com.zhangwenl1993163.chargehelper.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.util.Date;

/**
 * Created by zhang on 2018/2/4.
 */

public class ChargeDao {
    private Context context;
    private SQLiteDatabase db;

    public ChargeDao(Context context){
        this.context = context;
    }

    /**
     * 获取今日加工产品个数
     * */
    public int getWorkedCount(Date date){
        return 0;
    }

    public double getTotalMoney(Date date){
        return 3.2;
    }
}
