package com.zhangwenl1993163.chargehelper.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.zhangwenl1993163.chargehelper.model.Constants;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by zhang on 2018/2/4.
 */

public class DBUtil {
    private final static String TAG = DBUtil.class.getName();
    private static final int DBVERSION = 2;

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

        SQLiteDatabase database = SQLiteDatabase.openDatabase(Constants.DBPATH + Constants.DBNAME,null,SQLiteDatabase.OPEN_READWRITE);
        int version = database.getVersion();
        Log.d(TAG,"数据库当前版本："+version);
        Log.d(TAG,"数据库最新版本："+DBVERSION);
        //若当前数据库版本与DBVERSION不一致，升级数据库
        if (version != DBVERSION){
            upgradeDB(database);
            database.setVersion(DBVERSION);
        }

        database.close();
        return SQLiteDatabase.openDatabase(Constants.DBPATH + Constants.DBNAME,null,writeAble ? SQLiteDatabase.OPEN_READWRITE : SQLiteDatabase.OPEN_READONLY);
    }

    private static void upgradeDB(SQLiteDatabase db){
        //由当前数据库版本的下一个版本开始循环，直至更新到最新版
        for (int i = db.getVersion() + 1 ; i <= DBVERSION ; i++){
            //历次版本数据库更新内容
            switch (i){
                case 2:
                    String rename = "ALTER TABLE charge_list RENAME TO charge_list_old;";
                    String createNew = "CREATE TABLE charge_list" +
                            "(" +
                            "  id                  INTEGER        NOT NULL" +
                            "    PRIMARY KEY," +
                            "  process_card_number TEXT(255)      NOT NULL," +
                            "  model_name          TEXT(255)      NOT NULL," +
                            "  model_price         DECIMAL(10, 2) NOT NULL," +
                            "  qulified_number     INTEGER        NOT NULL," +
                            "  add_time            DECIMAL        NOT NULL," +
                            "  modify_time         DECIMAL        NOT NULL," +
                            "  comment             TEXT(255)      NOT NULL" +
                            ");";
                    String copy = "INSERT INTO charge_list (id, process_card_number, model_name, model_price, qulified_number, add_time, modify_time, comment)\n" +
                            "  SELECT id,process_card_number,model_name,model_price,qulified_number,add_time,modify_time,comment FROM charge_list_old;";
                    String dropOld = "DROP TABLE charge_list_old;";
                    db.execSQL(rename);
                    db.execSQL(createNew);
                    db.execSQL(copy);
                    db.execSQL(dropOld);
                    Log.d(TAG,"数据库升级成功");
                    break;
                case 1:
                    //新增考勤表
                    String sql = "CREATE TABLE 'attendance_list' (" +
                            "'id' INTEGER NOT NULL," +
                            "'attendance_people' TEXT(10) NOT NULL," +
                            "'attendance_hours' REAL NOT NULL," +
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
            File file = new File(Constants.DBPATH);
            if (!file.exists())
                file.mkdir();
            is = context.getAssets().open(Constants.DBNAME);
            os = new FileOutputStream(Constants.DBPATH + Constants.DBNAME);
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
        String path = Constants.DBPATH + Constants.DBNAME;
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

    public static boolean exportDB(String absPath){
        boolean result;
        InputStream is = null;
        OutputStream os = null;
        try {
            is = new FileInputStream(Constants.DBPATH + Constants.DBNAME);
            os = new FileOutputStream(absPath);
            byte[] buf = new byte[1024];
            int len = 0;
            while ((len = is.read(buf)) > 0){
                os.write(buf,0,len);
            }
            os.flush();
            result = true;
        } catch (IOException e) {
            result = false;
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
        return result;
    }
}
