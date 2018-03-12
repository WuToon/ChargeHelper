package com.zhangwenl1993163.chargehelper;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.zhangwenl1993163.chargehelper.dao.AttendanceDao;
import com.zhangwenl1993163.chargehelper.model.Attendance;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

/**
 * Created by zhangwenliang on 2018/3/12.
 */
@RunWith(AndroidJUnit4.class)
public class AttendanceDaoTest {
    private Context context = InstrumentationRegistry.getContext();
    private AttendanceDao dao = new AttendanceDao(context);

    @Test
    public void insertTest(){
        Attendance attendance = new Attendance();
        attendance.setAttendancePeople("zhangsan");
        attendance.setAttendanceHours(3);
        attendance.setAttendanceType(1);
        attendance.setAddTime(System.currentTimeMillis());
        attendance.setComment("没有备注");
        dao.insert(attendance);
    }

    @Test
    public void deleteTest(){
        dao.deleteById(1);
    }

    @Test
    public void selectTest(){
        List<Attendance> attendances = dao.getAttendanceByType(1);
        Log.d("========",attendances.toString());
    }
}
