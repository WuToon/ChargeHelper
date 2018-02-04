package com.zhangwenl1993163.chargehelper.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by zhang on 2018/2/4.
 */

public class DBUtil {
    private static final String DBNAME = "chargehelper.db";
    private static final String DBPATH = "data/data/com.zhangwenl1993163.chargehelper/databases/";

    public static SQLiteDatabase getDBReadOnly(Context context){
        if (!checkDB()){
            copyDB(context);
            return SQLiteDatabase.openDatabase(DBPATH + DBNAME,null,SQLiteDatabase.OPEN_READONLY);
        }else
            return SQLiteDatabase.openDatabase(DBPATH + DBNAME,null,SQLiteDatabase.OPEN_READONLY);
    }

    public static SQLiteDatabase getDBWriteable(Context context){
        if (!checkDB()){
            copyDB(context);
            return SQLiteDatabase.openDatabase(DBPATH + DBNAME,null,SQLiteDatabase.OPEN_READWRITE);
        }else
            return SQLiteDatabase.openDatabase(DBPATH + DBNAME,null,SQLiteDatabase.OPEN_READWRITE);
    }

    private static void copyDB(Context context){
        InputStream is = null;
        OutputStream os = null;
        try {
            File file = new File(DBPATH);
            if (!file.exists())
                file.mkdir();
            is = context.getAssets().open(DBNAME);
            os = new FileOutputStream(DBPATH + DBNAME);
            byte[] buf = new byte[1024];
            int len = 0;
            while ((len = is.read(buf)) > 0){
                os.write(buf,0,len);
            }
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (is != null){
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (os != null){
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private static boolean checkDB(){
        String path = DBPATH + DBNAME;
        SQLiteDatabase database = null;
        try {
            database = SQLiteDatabase.openDatabase(path,null,SQLiteDatabase.OPEN_READONLY);
        }catch (Exception e){

        }
        if (database == null){
            return false;
        }else {
            database.close();
            return true;
        }
    }

    public static void init(Context context){
        if (!checkDB())
            copyDB(context);
    }
}
