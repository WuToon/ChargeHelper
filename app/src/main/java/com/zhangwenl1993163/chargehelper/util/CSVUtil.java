package com.zhangwenl1993163.chargehelper.util;

import android.util.Log;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Created by zhangwenliang on 2018/2/19.
 */

public class CSVUtil {
    private static final String TAG = CSVUtil.class.getName();

    public static boolean exportCSV(List<String[]> data,String absPath){
        boolean flag = true;
        File file = new File(absPath);
        CSVWriter csvWriter = null;
        try {
            csvWriter = new CSVWriter(new FileWriter(file));
            csvWriter.writeAll(data);
            csvWriter.flush();
        } catch (IOException e) {
            CommonUtil.showMsgShort("导出CSV失败");
            flag = false;
            Log.e(TAG,"导出CSV失败:" + e.toString());
        }finally {
            if (csvWriter != null){
                try {
                    csvWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return flag;
    }
}
