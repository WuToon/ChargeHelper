package com.zhangwenl1993163.chargehelper.util;

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
    private final static String TAG = DBUtil.class.getName();
    private static final String DBNAME = "chargehelper.db";
    private static final int DBVERSION = 1;
    private static final String DBPATH = "data/data/com.zhangwenl1993163.chargehelper/databases/";

    public static SQLiteDatabase getDBReadOnly(Context context){
        return getDB(context,false);
    }

    public static SQLiteDatabase getDBWriteable(Context context){
        return getDB(context,true);
    }

    private static SQLiteDatabase getDB(Context context,boolean writeAble){
        //检查数据库是否存在，不存在则为第一次安装，复制数据库
        if (!checkDB()){
            copyDB(context);
        }

        SQLiteDatabase database = SQLiteDatabase.openDatabase(DBPATH + DBNAME,null,SQLiteDatabase.OPEN_READWRITE);
        int version = database.getVersion();
        Log.d(TAG,"数据库当前版本："+version);
        Log.d(TAG,"数据库最新版本："+DBVERSION);
        //若当前数据库版本与DBVERSION不一致，升级数据库
        if (version != DBVERSION){
            upgradeDB(database);
            database.setVersion(DBVERSION);
        }

        database.close();
        return SQLiteDatabase.openDatabase(DBPATH + DBNAME,null,writeAble ? SQLiteDatabase.OPEN_READWRITE : SQLiteDatabase.OPEN_READONLY);
    }

    private static void upgradeDB(SQLiteDatabase db){
        //由当前数据库版本的下一个版本开始循环，直至更新到最新版
        for (int i = db.getVersion() + 1 ; i <= DBVERSION ; i++){
            //历次版本数据库更新内容
            switch (i){
                case 1:
                    //新增考勤表
                    String sql = "CREATE TABLE 'attendance_list' (" +
                            "'id' INTEGER NOT NULL," +
                            "'attendance_people' TEXT(10) NOT NULL," +
                            "'attendance_hours' INTEGER NOT NULL," +
                            "'attendance_type' INTEGER NOT NULL," +
                            "'add_time' DECIMAL NOT NULL," +
                            "'comment' TEXT(255)," +
                            "PRIMARY KEY('ID'))";
                    db.execSQL(sql);
                    Log.d(TAG,"考勤表升级成功");
                    break;
                default:
                    break;
            }
        }
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
}
