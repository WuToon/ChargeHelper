package com.zhangwenl1993163.chargehelper.view.activity;

import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.zhangwenl1993163.chargehelper.R;
import com.zhangwenl1993163.chargehelper.dao.AttendanceDao;
import com.zhangwenl1993163.chargehelper.model.Constants;
import com.zhangwenl1993163.chargehelper.util.CommonUtil;
import com.zhangwenl1993163.chargehelper.util.DateUtil;
import com.zhangwenl1993163.chargehelper.util.JsonUtil;
import com.zhangwenl1993163.chargehelper.view.fragment.CheckSearchDialogFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/3/14.
 */

public class AttendanceActivity extends AppCompatActivity {
    private AttendanceDao dao;
    private SwipeMenuListView swipeMenuListView;
    private TextView tipTV;
    private List<Map<String,Object>> attendances;
    private List<Map<String,Object>> formatAttendances;
    private SimpleAdapter adapter;
    private int attendanceType;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);
        Intent intent = getIntent();
        attendanceType = intent.getIntExtra("attendanceType",-1);
        initUI();
        loadDate(new Date(), Constants.DESC);
    }

    private void initUI(){
        dao = new AttendanceDao(AttendanceActivity.this);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.show();

        tipTV = findViewById(R.id.attendance_no_data);

        swipeMenuListView = findViewById(R.id.attendance_container);
        swipeMenuListView.setMenuCreator(creator);
        swipeMenuListView.setOnItemClickListener(onItemClickListener);
        swipeMenuListView.setOnMenuItemClickListener(onMenuItemClickListener);
    }

    private void loadDate(Date date,String sortType){
        //修改actionbar标题
        ActionBar actionBar = getSupportActionBar();
        String title = (attendanceType == 1 ? "请假" : "加班") + "记录（"+ new SimpleDateFormat("yyyy-MM").format(date) +"）";
        actionBar.setTitle(title);
        actionBar.show();

        List<Long> dates = DateUtil.getMonthRange(date.getTime());

        attendances = dao.getAttendanceByTypeInRange(attendanceType,dates,sortType);
        showTip();
        //格式化数据
        String json = JSON.toJSONString(attendances);
        formatAttendances = JsonUtil.json2MapList(json);
        for (Map<String,Object> map : formatAttendances){
            long times = (long)map.get("addTime");
            String comment = (String)map.get("comment");
            map.put("addTime",new SimpleDateFormat("MM-dd").format(new Date(times)));
            map.put("comment",comment.length() > 5 ? comment.substring(0,5)+"..." : comment);
        }

        adapter = new SimpleAdapter(AttendanceActivity.this,formatAttendances,R.layout.record_list_item,
                new String[]{"attendancePeople","attendanceHours","addTime","comment"},
                new int[]{R.id.item_process_card_number,R.id.item_module_name,
                        R.id.item_qulified_number,R.id.item_total_money});
        swipeMenuListView.setAdapter(adapter);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.check_actionbar_menu,menu);
        MenuItem backMenuItem = menu.findItem(R.id.action_bar_back);
        backMenuItem.setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_bar_search:
                CheckSearchDialogFragment fragment = new CheckSearchDialogFragment(new CheckSearchDialogFragment.OnSelectedListener() {
                    @Override
                    public void onSelected(Date date, String sortItem,String sortType) {
                        loadDate(date,sortType);
                    }
                });
                fragment.setHideSortItemLinearLayout(true);
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                fragment.show(transaction,"attendance");

                break;
            default:break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 滑动菜单
     * */
    SwipeMenuCreator creator = new SwipeMenuCreator() {
        @Override
        public void create(SwipeMenu menu) {
            SwipeMenuItem del = new SwipeMenuItem(AttendanceActivity.this);
            del.setWidth(120);
            del.setBackground(new ColorDrawable(getResources().getColor(R.color.slideMenuDel)));
            del.setTitle("删除");
            del.setTitleColor(Color.BLACK);
            del.setTitleSize(16);
            menu.addMenuItem(del);
        }
    };

    /**
     * 滑动菜单点击事件
     * */
    SwipeMenuListView.OnMenuItemClickListener onMenuItemClickListener = new SwipeMenuListView.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
            delItem(position);
            return false;
        }
    };

    /**
     * 删除条目
     * */
    private void delItem(final int position){
        final int id = (Integer)(attendances.get(position).get("id"));
        final String peopleName = (String)(attendances.get(position).get("attendancePeople"));
        final String date = new SimpleDateFormat("yyyy年MM月dd日").
                format(new Date((Long) attendances.get(position).get("addTime")));
        AlertDialog.Builder builder = new AlertDialog.Builder(AttendanceActivity.this);
        builder.setTitle("是否删除");
        builder.setMessage("即将删除 "+ peopleName + " " + date + " 的" +
                (attendanceType == 1 ? "请假" : "加班") + "记录，删除之后不可恢复，请谨慎操作！");
        builder.setCancelable(true);
        builder.setNegativeButton("取消",null);
        builder.setPositiveButton("删除", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dao.deleteById(id);
                hideItem(position);
                CommonUtil.showMsg("删除成功");
            }
        });
        builder.show();
    }

    /**
     * 隐藏条目
     * */
    private void hideItem(int position){
        attendances.remove(position);
        formatAttendances.remove(position);
        adapter.notifyDataSetChanged();
        swipeMenuListView.setAdapter(adapter);
        showTip();
    }

    /**
     * 条目点击事件
     * */
    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Map<String,Object> record = attendances.get(position);
            List<String> strs = new ArrayList<>();
            strs.add("      姓名："+record.get("attendancePeople"));
            strs.add("      时长："+record.get("attendanceHours")+" 小时");
            strs.add("      日期："+new SimpleDateFormat("yyyy-MM-dd  HH:mm").
                    format(new Date((Long) record.get("addTime"))));
            strs.add("      备注："+record.get("comment"));
            String[] sa = strs.toArray(new String[0]);

            AlertDialog.Builder builder = new AlertDialog.Builder(AttendanceActivity.this);
            builder.setTitle("详情");
            builder.setItems(sa,null);
            builder.setPositiveButton("确定",null);
            builder.show();
        }
    };

    /**
     * 显示tip
     */
    private void showTip(){
        if (attendances == null || attendances.size() == 0){
            tipTV.setVisibility(View.VISIBLE);
        }else {
            tipTV.setVisibility(View.GONE);
        }
    }
}
