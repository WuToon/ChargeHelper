package com.zhangwenl1993163.chargehelper.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.zhangwenl1993163.chargehelper.model.Record;
import com.zhangwenl1993163.chargehelper.util.DBUtil;

import java.math.BigDecimal;
import java.util.List;

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
     * 获取加工产品个数
     * @param dates 时间戳范围
     * */
    public int getWorkedCount(List<Long> dates){
        db = DBUtil.getDBReadOnly(context);
        String sql = "select sum(qulified_number) from charge_list where add_time >= ? and add_time < ?";
        Cursor cursor = db.rawQuery(sql,new String[]{dates.get(0)+"",dates.get(1)+""});
        cursor.moveToNext();
        int i = cursor.getInt(0);
        db.close();
        return i;
    }


    public BigDecimal getTotalMoney(List<Long> dates){
        db = DBUtil.getDBReadOnly(context);
        String sql = "select sum(qulified_number * model_price) from charge_list where add_time >= ? and add_time < ?";
        Cursor cursor = db.rawQuery(sql,new String[]{dates.get(0)+"",dates.get(1)+""});
        cursor.moveToNext();
        BigDecimal i = new BigDecimal(cursor.getDouble(0));
        db.close();
        return i;
    }

    public void insertRecord(Record record){
        db = DBUtil.getDBWriteable(context);
        String sql = "insert into charge_list (process_card_number,model_name," +
                "model_price,qulified_number,add_time,modify_time,comment) values" +
                "(?,?,?,?,?,?,?)";
        db.execSQL(sql,new Object[]{record.getProcessCardNumber(),record.getModelName(),
                record.getModelPrice(),record.getQulifiedNumber(),record.getAddTimeStamp(),
                record.getModifyTimeStamp(),record.getComment()});
        db.close();
    }

    public void deleteRecordById(int id){
        db = DBUtil.getDBWriteable(context);
        String sql = "delete from charge_list where id = ?";
        db.execSQL(sql,new Object[]{id});
        db.close();
    }

    public void updateRecord(Record record){
        db = DBUtil.getDBWriteable(context);
        String sql = "update charge_list set process_card_number = ? , model_name = ? , " +
                "model_price = ? , qulified_number = ? , add_time = ? , modify_time = ? ," +
                "comment = ? where id = ?";
        db.execSQL(sql,new Object[]{record.getProcessCardNumber(),record.getModelName(),
                record.getModelPrice(),record.getQulifiedNumber(),record.getAddTimeStamp(),
                record.getModifyTimeStamp(),record.getComment(),record.getId()});
        db.close();
    }
}
