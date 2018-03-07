package com.zhangwenl1993163.chargehelper.view.activity;

import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
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
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.zhangwenl1993163.chargehelper.R;
import com.zhangwenl1993163.chargehelper.dao.ChargeDao;
import com.zhangwenl1993163.chargehelper.util.DateUtil;
import com.zhangwenl1993163.chargehelper.view.fragment.CheckSearchDialogFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by zhangwenliang on 2018/3/4.
 */

public class CheckActivity extends AppCompatActivity {
    private TextView tips;
    private SwipeMenuListView swipeMenuListView;
    private ChargeDao chargeDao;
    private List<Map<String,Object>> records;
    private SimpleAdapter adapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check);
        initUI();
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveData();
    }

    private void initUI(){
        chargeDao = new ChargeDao(this);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.show();
        tips = findViewById(R.id.check_tips);
        swipeMenuListView = findViewById(R.id.check_container);
        swipeMenuListView.setMenuCreator(creator);
        swipeMenuListView.setOnItemClickListener(onItemClickListener);
        swipeMenuListView.setOnMenuItemClickListener(onMenuItemClickListener);
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
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_bar_search:
                CheckSearchDialogFragment fragment = new CheckSearchDialogFragment(new CheckSearchDialogFragment.OnSelectedListener() {
                    @Override
                    public void onSelected(Date date, String sortItem) {
                        records = query(date,sortItem);
                        adapter = new SimpleAdapter(CheckActivity.this,records,R.layout.record_list_item,
                                new String[]{"processCardNumber","modelName","qulifiedNumber","totalMoney"},
                                new int[]{R.id.item_process_card_number,R.id.item_module_name,
                                        R.id.item_qulified_number,R.id.item_total_money});
                        swipeMenuListView.setAdapter(adapter);
                        setTips(records);
                    }
                });
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                fragment.show(transaction,"check");

                break;
            default:break;
        }
        return super.onOptionsItemSelected(item);
    }



    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    /**
     * 加载records，先从sharedpreferences加载上次数据，若不存在查询数据库加载当月数据
     */
    private void loadData(){
        SharedPreferences sharedPreferences = getSharedPreferences("checkData",MODE_PRIVATE);
        //查询记录的日期，修改actionbar的标题
        Date date = new Date(sharedPreferences.getLong("date", System.currentTimeMillis()));
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("对账 ("+new SimpleDateFormat("yyyy-MM").format(date) + ")");
        actionBar.show();

        //查询records
        String recordJson = sharedPreferences.getString("records",null);
        if (recordJson != null && !"".equals(recordJson)){
            //反序列化查询结果
            records = new ArrayList<>();
            JSONArray array = JSON.parseArray(recordJson);
            for (int i = 0 ; i < array.size() ; i++){
                JSONObject jsonObject = array.getJSONObject(i);
                Map<String,Object> map = jsonObject.getInnerMap();
                records.add(map);
            }
        }else {
            records = query(new Date(),CheckSearchDialogFragment.PROCESS_CARD_NUMBER);
        }

        //设置提示
        setTips(records);
        //更新adapter
        adapter = new SimpleAdapter(CheckActivity.this,records,R.layout.record_list_item,
                new String[]{"processCardNumber","modelName","qulifiedNumber","totalMoney"},
                new int[]{R.id.item_process_card_number,R.id.item_module_name,
                        R.id.item_qulified_number,R.id.item_total_money});
        swipeMenuListView.setAdapter(adapter);
    }

    /**
     * 保存当前剩余records记录
     */
    private void saveData(){
        //转成json
        String recordsJson = JSON.toJSONString(records);
        //保存
        SharedPreferences sharedPreferences = getSharedPreferences("checkData",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("records",recordsJson);
        editor.commit();
    }

    /**
     * 查询数据库
     * */
    private List<Map<String,Object>> query(Date date,String sortColoumName){
        //将日期保存到sharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("checkData",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong("date",date.getTime());
        editor.commit();
        //修改actionbar标题
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("对账 ("+new SimpleDateFormat("yyyy-MM").format(date) + ")");
        actionBar.show();
        //查询结果
        List<Long> l = DateUtil.getMonthRange(date.getTime());
        List<Map<String,Object>> records = chargeDao.getRecordMapInRange(l,sortColoumName);
        return records;
    }

    /**
     * 滑动菜单
     * */
    SwipeMenuCreator creator = new SwipeMenuCreator() {
        @Override
        public void create(SwipeMenu menu) {
            SwipeMenuItem del = new SwipeMenuItem(CheckActivity.this);
            del.setWidth(120);
            del.setBackground(new ColorDrawable(getResources().getColor(R.color.slideMenuHide)));
            del.setTitle("隐藏");
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
            if (index == 0){
                //隐藏
                records.remove(position);
                adapter.notifyDataSetChanged();
                swipeMenuListView.setAdapter(adapter);
                setTips(records);
            }
            return false;
        }
    };

    /**
     * 设置tips
     * */
    private void setTips(List<Map<String,Object>> l){
        if (l != null && l.size() != 0){
            String s = "<-- 剩余"+ l.size() +"条数据，点击查看详情，左滑弹出菜单 -->";
            tips.setText(s);
        }else{
            String s = "暂无数据，请更换查询条件";
            tips.setText("<-- "+s+" -->");
        }
    }

    /**
     * 条目点击事件
     * */
    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Map<String,Object> record = records.get(position);
            List<String> strs = new ArrayList<>();
            strs.add("      流程卡号："+record.get("processCardNumber"));
            strs.add("      产品型号："+record.get("modelName"));
            strs.add("      型号单价："+record.get("modelPrice"));
            strs.add("      合格个数："+record.get("qulifiedNumber"));
            strs.add("      总计金额："+record.get("totalMoney"));
            strs.add("      添加日期："+new SimpleDateFormat("yyyy-MM-dd  HH:mm").
                    format(new Date((Long) record.get("addTime"))));
            strs.add("      备         注："+record.get("comment"));
            String[] sa = strs.toArray(new String[0]);

            AlertDialog.Builder builder = new AlertDialog.Builder(CheckActivity.this);
            builder.setTitle("详情");
            builder.setItems(sa,null);
            builder.setPositiveButton("确定",null);
            builder.show();
        }
    };
}
