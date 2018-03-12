package com.zhangwenl1993163.chargehelper;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.zhangwenl1993163.chargehelper.util.DBUtil;
import com.zhangwenl1993163.chargehelper.dao.ProductDao;
import com.zhangwenl1993163.chargehelper.model.Product;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

/**
 * Created by zhang on 2018/2/4.
 */
@RunWith(AndroidJUnit4.class)
public class ProductDaoTest {
    private final String TAG = ProductDaoTest.class.getName();
    private Context context = InstrumentationRegistry.getContext();
    private ProductDao dao;

    @Before
    public void init(){
        dao = new ProductDao(context);
    }

    @Test
    public void getAllTest(){
        List<Product> products = dao.getAllProduct();
        Log.i(TAG,"======================================");
        Log.i(TAG,products.toString());
    }

    @Test
    public void insertTest(){
        Product p = new Product();
        p.setModelName("hahah");
        p.setModelPrice(2.33);
        p.setAddTimeStamp(System.currentTimeMillis());
        p.setModifyTimeStamp(System.currentTimeMillis());
        dao.insertProduct(p);

    }
    @Test
    public void selectByIDTest(){
        Product product = dao.getProductById(160);
        Log.i(TAG,"=====================================");
        Log.i(TAG,product.toString());
    }

    @Test
    public void deleteByIdTest(){
        dao.deleteProductById(168);
    }

    @Test
    public void updateTest(){
        Product product = dao.getProductById(169);
        product.setModelPrice(999.0);
        dao.updateProduct(product);
    }
}
