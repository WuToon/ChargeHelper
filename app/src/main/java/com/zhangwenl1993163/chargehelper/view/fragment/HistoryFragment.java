package com.zhangwenl1993163.chargehelper.view.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.zhangwenl1993163.chargehelper.R;
import com.zhangwenl1993163.chargehelper.dao.ChargeDao;
import com.zhangwenl1993163.chargehelper.model.Record;
import com.zhangwenl1993163.chargehelper.util.DateUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by zhang on 2018/2/7.
 */

public class HistoryFragment extends Fragment implements View.OnClickListener {
    private final String PROCESS_CARD_NUMBER = "process_card_number",
            MODEL_NAME = "model_name",ADD_DATE = "add_time";
    private Spinner queryYear,queryMonth,sortWay;
    private Button queryBtn;
    private View container;
    private Calendar calendar = Calendar.getInstance();
    private List<Integer> years;
    private String sortColoumName = PROCESS_CARD_NUMBER;
    private ChargeDao chargeDao;

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
                query();
                break;
            default:
                break;
        }
    }

    private void init(){
        calendar.setTime(new Date());
        calendar.set(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),15);
        chargeDao = new ChargeDao(getView().getContext());
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
    }

    /**
     * 查询数据库
     * */
    private void query(){
        List<Long> l = DateUtil.getMonthRange(calendar.getTimeInMillis());
        List<Record> records = chargeDao.getRecordInRange(l,sortColoumName);
        showToast(records.toString());
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

    private void showToast(String msg){
        Toast.makeText(getView().getContext(),msg,Toast.LENGTH_SHORT).show();
    }
}
