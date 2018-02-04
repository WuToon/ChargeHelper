package com.zhangwenl1993163.chargehelper;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import com.zhangwenl1993163.chargehelper.dao.DBUtil;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by zhang on 2018/2/4.
 */
@RunWith(AndroidJUnit4.class)
public class ProductDaoTest {
    private Context context = InstrumentationRegistry.getContext();
    @Test
    public void getAllTest(){
        DBUtil.init(context);
        Assert.assertNotNull(DBUtil.getDBReadOnly(context));
    }
}
