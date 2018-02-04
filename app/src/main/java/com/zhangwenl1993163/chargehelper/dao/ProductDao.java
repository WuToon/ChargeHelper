package com.zhangwenl1993163.chargehelper.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.zhangwenl1993163.chargehelper.model.Product;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhang on 2018/2/4.
 */

public class ProductDao {
    private final String TAG = ProductDao.class.getName();
    private Context context;
    private SQLiteDatabase db;

    public ProductDao(Context context){
        this.context = context;
    }

    public List<Product> getAllProduct(){
        db = DBUtil.getDBWriteable(context);
        List<Product> products = new ArrayList<>();
        String sql = "select * from product_list";
        Cursor cursor = db.rawQuery(sql,null);
        while(cursor.moveToNext()){
            Product product = new Product();
            product.setId(cursor.getInt(0));
            product.setModelName(cursor.getString(1));
            product.setModelPrice(cursor.getDouble(2));
            String addTimeStr = cursor.getString(3);
            String modifyTimeStr = cursor.getString(4);
            SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            try {
                product.setAddTime(format.parse(addTimeStr));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            try {
                product.setModifyTime(format.parse(modifyTimeStr));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            products.add(product);
        }
        cursor.close();
        db.close();
        return products;
    }
}
