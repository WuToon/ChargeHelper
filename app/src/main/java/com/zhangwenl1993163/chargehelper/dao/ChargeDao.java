package com.zhangwenl1993163.chargehelper.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.zhangwenl1993163.chargehelper.model.Record;
import com.zhangwenl1993163.chargehelper.util.DBUtil;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public List<Record> getAllRecord(){
        db = DBUtil.getDBReadOnly(context);
        List<Record> l = new ArrayList<>();
        String sql = "select * from charge_list";
        Cursor cursor = db.rawQuery(sql,new String[]{});
        while (cursor.moveToNext()){
            Record r = new Record();
            r.setId(cursor.getInt(0));
            r.setProcessCardNumber(cursor.getString(1));
            r.setModelName(cursor.getString(2));
            r.setModelPrice(cursor.getDouble(3));
            r.setQulifiedNumber(cursor.getInt(4));
            r.setAddTimeStamp(cursor.getLong(5));
            r.setModifyTimeStamp(cursor.getLong(6));
            r.setComment(cursor.getString(7));
            l.add(r);
        }
        db.close();
        return l;
    }

    public List<Record> getRecordInRange(List<Long> dates){
        db = DBUtil.getDBReadOnly(context);
        List<Record> l = new ArrayList<>();
        String sql = "select * from charge_list where add_time >= ? and add_time < ?";
        Cursor cursor = db.rawQuery(sql,new String[]{dates.get(0)+"",dates.get(1)+""});
        while (cursor.moveToNext()){
            Record r = new Record();
            r.setId(cursor.getInt(0));
            r.setProcessCardNumber(cursor.getString(1));
            r.setModelName(cursor.getString(2));
            r.setModelPrice(cursor.getDouble(3));
            r.setQulifiedNumber(cursor.getInt(4));
            r.setAddTimeStamp(cursor.getLong(5));
            r.setModifyTimeStamp(cursor.getLong(6));
            r.setComment(cursor.getString(7));
            l.add(r);
        }
        db.close();
        return l;
    }

    public List<Map<String,Object>> getRecordMapInRange(List<Long> range, String sortItem,String sortType){
        db = DBUtil.getDBReadOnly(context);
        List<Map<String,Object>> l = new ArrayList<>();
        String sql = "select *,(qulified_number * model_price) from charge_list where add_time >= ? and add_time < ? order by "+sortItem+" "+sortType;
        Cursor cursor = db.rawQuery(sql,new String[]{range.get(0)+"",range.get(1)+""});
        while (cursor.moveToNext()){
            Map<String,Object> m = new HashMap<>();
            m.put("id",cursor.getInt(0));
            m.put("processCardNumber",cursor.getString(1));
            m.put("modelName",cursor.getString(2));
            m.put("modelPrice",cursor.getDouble(3));
            m.put("qulifiedNumber",cursor.getInt(4));
            m.put("addTime",cursor.getLong(5));
            m.put("modifyTime",cursor.getLong(6));
            m.put("comment",cursor.getString(7));
            m.put("totalMoney",new BigDecimal(cursor.getDouble(8)).setScale(2,BigDecimal.ROUND_HALF_UP));
            l.add(m);
        }
        db.close();
        return l;
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
