package com.zhangwenl1993163.chargehelper.view.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.zhangwenl1993163.chargehelper.R;
import com.zhangwenl1993163.chargehelper.dao.ChargeDao;
import com.zhangwenl1993163.chargehelper.model.Record;
import com.zhangwenl1993163.chargehelper.util.CommonUtil;
import com.zhangwenl1993163.chargehelper.util.DateUtil;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by zhang on 2018/2/7.
 */

public class HistoryFragment extends Fragment implements View.OnClickListener {
    private final String PROCESS_CARD_NUMBER = "process_card_number",
            MODEL_NAME = "model_name",ADD_DATE = "add_time";
    private Spinner queryYear,queryMonth,sortWay;
    private TextView tip;
    private Button queryBtn;
    private SwipeMenuListView container;
    private Calendar calendar = Calendar.getInstance();
    private List<Integer> years;
    private String sortColoumName = PROCESS_CARD_NUMBER;
    private ChargeDao chargeDao;
    private List<Map<String,Object>> records;
    private SimpleAdapter adapter;
    private boolean showToast = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.history_panel,container,false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.query_button:
                showToast = true;
                loadProductList();
                break;
            default:
                break;
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden)
            loadProductList();
    }

    private void init(){
        calendar.setTime(new Date());
        calendar.set(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),15);
        chargeDao = new ChargeDao(getView().getContext());
        tip = getView().findViewById(R.id.history_tips);
        queryYear = getView().findViewById(R.id.query_year);
        loadYears();
        queryYear.setOnItemSelectedListener(yearListener);
        queryMonth = getView().findViewById(R.id.query_months);
        queryMonth.setSelection(calendar.get(Calendar.MONTH));
        queryMonth.setOnItemSelectedListener(monthListemer);
        sortWay = getView().findViewById(R.id.sort_item);
        sortWay.setOnItemSelectedListener(sortListener);
        queryBtn = getView().findViewById(R.id.query_button);
        queryBtn.setBackgroundColor(getColorPrimary());
        queryBtn.setOnClickListener(this);
        container = getView().findViewById(R.id.history_container);
        container.setOnItemClickListener(listener);
        setSlideMenu();
        loadProductList();
    }

    private AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {
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

            AlertDialog.Builder builder = new AlertDialog.Builder(getView().getContext());
            builder.setTitle("详情");
            builder.setItems(sa,null);
            builder.setPositiveButton("确定",null);
            builder.show();
        }
    };

    /**
     * 加载产品列表
     * */
    private void loadProductList(){
        records = query();
        setTips(records);
        adapter = new SimpleAdapter(getView().getContext(),records,R.layout.record_list_item,
                new String[]{"processCardNumber","modelName","qulifiedNumber","totalMoney"},
                new int[]{R.id.item_process_card_number,R.id.item_module_name,
                R.id.item_qulified_number,R.id.item_total_money});
        container.setAdapter(adapter);
    }

    /**
     * 设置tips
     * */
    private void setTips(List<Map<String,Object>> l){
        if (l != null && l.size() != 0){
            String s = "<-- 总计"+ l.size() +"条数据，点击查看详情，左滑弹出菜单 -->";
            if (showToast){
                CommonUtil.showMsgShort("总计"+ l.size() +"条数据");
                showToast = false;
            }
            tip.setText(s);
        }else{
            String s = "暂无数据，请更换查询条件";
            if (showToast){
                CommonUtil.showMsgShort(s);
                showToast = false;
            }
            tip.setText("<-- "+s+" -->");
        }
    }

    /**
     * 查询数据库
     * */
    private List<Map<String,Object>> query(){
        List<Long> l = DateUtil.getMonthRange(calendar.getTimeInMillis());
        List<Map<String,Object>> records = chargeDao.getRecordMapInRange(l,sortColoumName);
        return records;
    }

    /**
     * 加载年份下拉列表
     **/
    private void loadYears(){
        years = DateUtil.getLatest3Years();
        List<String> b = new ArrayList<>();
        for (int i : years){
            b.add(i + " 年");
        }
        ArrayAdapter<String> c = new ArrayAdapter<String>(getView().getContext(),android.R.layout.simple_list_item_1,b);
        queryYear.setAdapter(c);
    }

    /**
     * 设置滑动菜单
     * */
    private void setSlideMenu(){
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem del = new SwipeMenuItem(getView().getContext());
                del.setWidth(120);
                del.setBackground(new ColorDrawable(getResources().getColor(R.color.slideMenuDel)));
                del.setTitle("删除");
                del.setTitleColor(Color.BLACK);
                del.setTitleSize(16);
                menu.addMenuItem(del);

                SwipeMenuItem update = new SwipeMenuItem(getView().getContext());
                update.setWidth(120);
                update.setBackground(new ColorDrawable(getResources().getColor(R.color.slideMenuUpdate)));
                update.setTitle("修改");
                update.setTitleColor(Color.BLACK);
                update.setTitleSize(16);
                menu.addMenuItem(update);

                SwipeMenuItem hide = new SwipeMenuItem(getView().getContext());
                hide.setWidth(120);
                hide.setBackground(new ColorDrawable(getResources().getColor(R.color.slideMenuHide)));
                hide.setTitle("隐藏");
                hide.setTitleColor(Color.BLACK);
                hide.setTitleSize(16);
                menu.addMenuItem(hide);
            }
        };
        container.setMenuCreator(creator);
        container.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index){
                    case 0:
                        delItem(position);
                        break;
                    case 1:
                        updateItem(position);
                        break;
                    case 2://隐藏条目
                        hideItem(position);
                        break;
                    default:break;
                }
                return false;
            }
        });
    }

    /**
     * 隐藏条目
     * */
    private void hideItem(int position){
        records.remove(position);
        adapter.notifyDataSetChanged();
        container.setAdapter(adapter);
        setTips(records);
    }

    /**
     * 删除条目
     * */
    private void delItem(final int position){
        final int id = (Integer)(records.get(position).get("id"));
        final int cardNum = (Integer)(records.get(position).get("processCardNumber"));
        AlertDialog.Builder builder = new AlertDialog.Builder(getView().getContext());
        builder.setTitle("是否删除");
        builder.setMessage("即将删除流程卡号为"+ cardNum +"的记录，删除之后不可恢复，请谨慎操作！");
        builder.setCancelable(true);
        builder.setNegativeButton("取消",null);
        builder.setPositiveButton("删除", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                chargeDao.deleteRecordById(id);
                hideItem(position);
            }
        });
        builder.show();
    }

    /**
     * 修改条目
     * */
    private void updateItem(int position){
        Map<String,Object> m = records.get(position);
        UpdateDialogFragment fragment = new UpdateDialogFragment(records.get(position));
        fragment.setOnRecordChanged(new onRecordChanged() {
            @Override
            public void onChanged(Record record) {
                chargeDao.updateRecord(record);
                loadProductList();
            }
        });
        FragmentTransaction fm = getFragmentManager().beginTransaction();
        fragment.show(fm,"dialog");
    }

    //监听器
    private AdapterView.OnItemSelectedListener yearListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            calendar.set(years.get(position),calendar.get(Calendar.MONTH),15);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    //监听器
    private AdapterView.OnItemSelectedListener monthListemer = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            calendar.set(calendar.get(Calendar.YEAR),position,15);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    //监听器
    private AdapterView.OnItemSelectedListener sortListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            switch (position){
                case 0:
                    sortColoumName = PROCESS_CARD_NUMBER;
                    break;
                case 1:
                    sortColoumName = MODEL_NAME;
                    break;
                case 2:
                    sortColoumName = ADD_DATE;
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    /**
     * 获取主题颜色
     * @return
     */
    public int getColorPrimary(){
        TypedValue typedValue = new  TypedValue();
        getView().getContext().getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        return typedValue.data;
    }
}
