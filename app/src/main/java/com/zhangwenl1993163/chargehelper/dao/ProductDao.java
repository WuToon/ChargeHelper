package com.zhangwenl1993163.chargehelper.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.zhangwenl1993163.chargehelper.model.Product;
import com.zhangwenl1993163.chargehelper.util.DBUtil;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhang on 2018/2/4.
 */

public class ProductDao {
    private Context context;
    private SQLiteDatabase db;

    public ProductDao(Context context){
        this.context = context;
    }

    public List<Product> getAllProduct(){
        db = DBUtil.getDBReadOnly(context);
        List<Product> products = new ArrayList<>();
        String sql = "select * from product_list";
        Cursor cursor = db.rawQuery(sql,null);
        while(cursor.moveToNext()){
            Product product = new Product();
            product.setId(cursor.getInt(0));
            product.setModelName(cursor.getString(1));
            product.setModelPrice(cursor.getDouble(2));
            product.setAddTimeStamp(cursor.getLong(3));
            product.setModifyTimeStamp(cursor.getLong(4));
            products.add(product);
        }
        cursor.close();
        db.close();
        return products;
    }

    public Product getProductById(int id){
        db = DBUtil.getDBReadOnly(context);
        String sql = "select * from product_list where id = ?";
        Cursor cursor = db.rawQuery(sql,new String[]{id+""});
        cursor.moveToNext();
        Product product = new Product();
        product.setId(cursor.getInt(0));
        product.setModelName(cursor.getString(1));
        product.setModelPrice(cursor.getDouble(2));
        product.setAddTimeStamp(cursor.getLong(3));
        product.setModifyTimeStamp(cursor.getLong(4));
        return product;
    }

    public void deleteProductById(Integer id){
        db = DBUtil.getDBWriteable(context);
        String sql = "delete from product_list where id = ?";
        db.execSQL(sql,new Object[]{id});
        db.close();
    }

    public void updateProduct(Product product){
        db = DBUtil.getDBWriteable(context);
        String sql = "update product_list set model_name = ? , model_price = ? , modify_time = ? where id = ?";
        db.execSQL(sql,new Object[]{product.getModelName(),product.getModelPrice(),product.getModifyTimeStamp(),product.getId()});
        db.close();
    }

    public void insertProduct(Product product){
        db = DBUtil.getDBWriteable(context);
        String sql = "insert into product_list (model_name,model_price,add_time,modify_time) values (?,?,?,?)";
        db.execSQL(sql,new Object[]{product.getModelName(),product.getModelPrice(),product.getAddTimeStamp(),product.getModifyTimeStamp()});
        db.close();
    }
}
