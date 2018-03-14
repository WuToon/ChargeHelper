package com.zhangwenl1993163.chargehelper.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.zhangwenl1993163.chargehelper.model.Attendance;
import com.zhangwenl1993163.chargehelper.util.DBUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhangwenliang on 2018/3/12.
 */

public class AttendanceDao {
    private Context context;
    private SQLiteDatabase db;

    public AttendanceDao(Context context) {
        this.context = context;
    }

    public void insert(Attendance attendance){
        db = DBUtil.getDBWriteable(context);
        String sql = "insert into attendance_list " +
                "(attendance_people,attendance_hours,attendance_type,add_time,comment)" +
                "values (?,?,?,?,?)";
        db.execSQL(sql,new Object[]{attendance.getAttendancePeople(),attendance.getAttendanceHours(),
                attendance.getAttendanceType(),attendance.getAddTime(),attendance.getComment()});
        db.close();
    }

    public void deleteById(int id){
        db = DBUtil.getDBWriteable(context);
        String sql = "delete from attendance_list where id = ?";
        db.execSQL(sql,new Object[]{id});
        db.close();
    }

    public List<Attendance> getAttendanceByType(int attendanceType){
        db = DBUtil.getDBReadOnly(context);

        List<Attendance> result = new ArrayList<>();
        String sql = "select * from attendance_list where attendance_type = ?";
        Cursor cursor = db.rawQuery(sql,new String[]{String.valueOf(attendanceType)});
        while (cursor.moveToNext()){
            Attendance attendance = new Attendance();
            attendance.setId(cursor.getInt(0));
            attendance.setAttendancePeople(cursor.getString(1));
            attendance.setAttendanceHours(cursor.getDouble(2));
            attendance.setAttendanceType(cursor.getInt(3));
            attendance.setAddTime(cursor.getLong(4));
            attendance.setComment(cursor.getString(5));
            result.add(attendance);
        }

        db.close();
        return result;
    }

    public List<Map<String,Object>> getAttendanceByTypeInRange(int attendanceType, List<Long> dates,String sortType){
        db = DBUtil.getDBReadOnly(context);

        List<Map<String,Object>> result = new ArrayList<>();
        String sql = "select * from attendance_list where attendance_type = ? and add_time >= ? and add_time < ? order by add_time "+sortType;
        Cursor cursor = db.rawQuery(sql,new String[]{String.valueOf(attendanceType),
                String.valueOf(dates.get(0)), String.valueOf(dates.get(1))});
        while (cursor.moveToNext()){
            Map<String,Object> map = new HashMap<>();
            map.put("id",cursor.getInt(0));
            map.put("attendancePeople",cursor.getString(1));
            map.put("attendanceHours",cursor.getDouble(2));
            map.put("attendanceType",cursor.getInt(3));
            map.put("addTime",cursor.getLong(4));
            map.put("comment",cursor.getString(5));
            result.add(map);
        }

        db.close();
        return result;
    }
}
