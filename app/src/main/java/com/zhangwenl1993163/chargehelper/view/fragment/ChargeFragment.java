package com.zhangwenl1993163.chargehelper.view.fragment;

import android.app.DatePickerDialog;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import com.zhangwenl1993163.chargehelper.R;
import com.zhangwenl1993163.chargehelper.dao.AttendanceDao;
import com.zhangwenl1993163.chargehelper.dao.ChargeDao;
import com.zhangwenl1993163.chargehelper.dao.ProductDao;
import com.zhangwenl1993163.chargehelper.model.Attendance;
import com.zhangwenl1993163.chargehelper.model.Product;
import com.zhangwenl1993163.chargehelper.model.Record;
import com.zhangwenl1993163.chargehelper.util.CommonUtil;
import com.zhangwenl1993163.chargehelper.util.DateUtil;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhang on 2018/2/3.
 */

public class ChargeFragment extends Fragment implements View.OnClickListener {
    private Record record = new Record();
    private TextView todayTotalCount,todayTotalMoney,monthTotalMoney,yearTotalMoney,addDateTv,attendanceAddDateTV;
    private EditText processNumber,qulifiedNumber,comment,modelPrice,attendanceNameET,attendanceHoursEt,attendanceCommentET;
    private RadioGroup attendanceTypeRG;
    private Spinner modelName,addTypeSP;
    private Button addButton;
    private Calendar calendar = Calendar.getInstance(),attendanceCalendar = Calendar.getInstance();
    private ProductDao productDao;
    private ChargeDao chargeDao;
    private AttendanceDao attendanceDao;
    private Map<String,Double> namePriceMap = new HashMap<>();
    private List<String> names = new ArrayList<>();
    private int attendanceType = 2;
    private int addTypeFlag = 0;
    private View addRecordArea,addAttendanceArea;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.charge_panel,container,false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    private void init(){
        productDao = new ProductDao(getView().getContext());
        chargeDao = new ChargeDao(getView().getContext());
        attendanceDao = new AttendanceDao(getView().getContext());
        calendar.setTime(new Date());
        processNumber = getView().findViewById(R.id.add_record_process_num);
        modelName = getView().findViewById(R.id.add_record_model_name);
        modelName.setOnItemSelectedListener(listener);
        modelPrice = getView().findViewById(R.id.add_record_model_price);
        qulifiedNumber = getView().findViewById(R.id.add_record_qulified_num);
        addButton = getView().findViewById(R.id.add_record_add_button);
        //设置按钮背景色为主题颜色
        addButton.setBackgroundColor(getColorPrimary());
        addButton.setOnClickListener(this);
        addDateTv = getView().findViewById(R.id.add_record_add_date);
        addDateTv.setText(new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime()));
        addDateTv.setOnClickListener(this);
        comment = getView().findViewById(R.id.add_record_comment);
        todayTotalCount = getView().findViewById(R.id.today_charge_count);
        todayTotalMoney = getView().findViewById(R.id.today_total_money);
        monthTotalMoney = getView().findViewById(R.id.month_total_money);
        yearTotalMoney = getView().findViewById(R.id.year_total_money);
        //==============考勤ui开始==============
        attendanceNameET = getView().findViewById(R.id.add_attendance_name);
        attendanceHoursEt = getView().findViewById(R.id.add_attendance_hours);
        attendanceCommentET = getView().findViewById(R.id.add_attendance_comment);
        attendanceAddDateTV = getView().findViewById(R.id.add_attendance_add_date);
        attendanceAddDateTV.setText(new SimpleDateFormat("yyyy-MM-dd").format(attendanceCalendar.getTime()));
        attendanceAddDateTV.setOnClickListener(this);
        attendanceTypeRG = getView().findViewById(R.id.add_attendance_type);
        attendanceTypeRG.setOnCheckedChangeListener(onCheckedChangeListener);
        //==============考勤ui结束==============
        addTypeSP = getView().findViewById(R.id.charge_add_type);
        addTypeSP.setOnItemSelectedListener(addTypeListen);
        addRecordArea = getView().findViewById(R.id.charge_add_record_area);
        addAttendanceArea = getView().findViewById(R.id.charge_add_attendance_area);
        loadStatistics();
        loadModels();
    }

    //radioGroup监听器
    private RadioGroup.OnCheckedChangeListener onCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if (checkedId == R.id.add_attendance_1){
                //加班
                attendanceType = 2;
            }else {
                attendanceType = 1;
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.add_record_add_button:
                if (addTypeFlag == 0) {
                    insertRecord();
                } else {
                    insertAttendance();
                }
                break;
            case R.id.add_record_add_date:
                loadDatePicker(1);
                break;
            case R.id.add_attendance_add_date:
                loadDatePicker(2);
                break;
            default:
                break;
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) {
            loadStatistics();
            loadModels();
        }
    }

    /**
     * 加载统计信息
     * */
    private void loadStatistics(){
        List<Long> day = DateUtil.getDayRange(new Date().getTime());
        List<Long> month = DateUtil.getMonthRange(new Date().getTime());
        List<Long> year = DateUtil.getYearRange(new Date().getTime());
        int count = chargeDao.getWorkedCount(day);
        BigDecimal money = chargeDao.getTotalMoney(day);
        BigDecimal monthMoney = chargeDao.getTotalMoney(month);
        BigDecimal yearMoney = chargeDao.getTotalMoney(year);
        todayTotalCount.setText(count+"");
        todayTotalMoney.setText(money.setScale(2,BigDecimal.ROUND_HALF_UP)+"");
        monthTotalMoney.setText(monthMoney.setScale(2,BigDecimal.ROUND_HALF_UP)+"");
        yearTotalMoney.setText(yearMoney.setScale(2,BigDecimal.ROUND_HALF_UP)+"");
    }

    /**
     * 添加类型点击事件
     */
    private AdapterView.OnItemSelectedListener addTypeListen = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            addTypeFlag = position;
            if (position == 0){
                //添加record
                addRecordArea.setVisibility(View.VISIBLE);
                addAttendanceArea.setVisibility(View.GONE);
            }else{
                //添加考勤
                addRecordArea.setVisibility(View.GONE);
                addAttendanceArea.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    /**
     * 型号列表点击事件
     * */
    private AdapterView.OnItemSelectedListener listener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String name = names.get(position);
            double price = namePriceMap.get(name);
            modelPrice.setText(price+"");
            record.setModelName(name);
            record.setModelPrice(price);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    /**
     * 设置日期弹窗
     * @param type 1 添加产品记录，2 考勤记录
     * */
    private void loadDatePicker(int type){
        DatePickerDialog dialog;
        if (type == 1) {
            dialog = new DatePickerDialog(getView().getContext(), new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    calendar.set(year,month,dayOfMonth);
                    addDateTv.setText(new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime()));
                }
            },calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
        } else {
            dialog = new DatePickerDialog(getView().getContext(), new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    attendanceCalendar.set(year,month,dayOfMonth);
                    attendanceAddDateTV.setText(new SimpleDateFormat("yyyy-MM-dd").format(attendanceCalendar.getTime()));
                }
            },attendanceCalendar.get(Calendar.YEAR),attendanceCalendar.get(Calendar.MONTH),attendanceCalendar.get(Calendar.DAY_OF_MONTH));
        }
        dialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        dialog.show();
    }

    /**
     * 加载型号
     * */
    private void loadModels(){
        //清空
        names.clear();
        namePriceMap.clear();
        //将所有产品保存成map，key为modelName ,value为model_price,便于联动
        List<Product> products = productDao.getAllProduct();
        for (Product product : products){
            names.add(product.getModelName());
            namePriceMap.put(product.getModelName(),product.getModelPrice());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getView().getContext(),android.R.layout.simple_list_item_1,names);
        modelName.setAdapter(adapter);
        double firstPrice = namePriceMap.get(names.get(0));
        modelPrice.setText(firstPrice+" 元");
        record.setModelName(names.get(0));
        record.setModelPrice(firstPrice);
    }

    /**
     * 添加到数据库
     * */
    private void insertRecord(){
        String s = processNumber.getText().toString();
        if (s != null && !"".equals(s)){
            record.setProcessCardNumber(s);
        }else {
            CommonUtil.showMsg("请输入流程卡号");
            return;
        }
        s = qulifiedNumber.getText().toString();
        if (s != null && !"".equals(s)){
            record.setQulifiedNumber(Integer.parseInt(s));
        }else {
            CommonUtil.showMsg("请输入合格产品个数");
            return;
        }
        s = record.getModelName();
        if (s != null && !"".equals(s)) {
        }else {
            CommonUtil.showMsg("型号名称加载失败，请稍后再试");
            return;
        }
        Double p = record.getModelPrice();
        if (p != null && p != 0) {
        }else {
            CommonUtil.showMsg("型号单价加载失败，请稍后再试");
            return;
        }
        s = comment.getText().toString();
        record.setComment(s);
        record.setAddTimeStamp(calendar.getTimeInMillis());
        record.setModifyTimeStamp(calendar.getTimeInMillis());
        //添加数据库
        chargeDao.insertRecord(record);
        loadStatistics();
        //==========将输入框清空==========
        processNumber.setText("");
        qulifiedNumber.setText("");
        calendar.setTime(new Date());
        addDateTv.setText(new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime()));
        comment.setText("");
        //===============================
        CommonUtil.showMsg("添加成功");
    }

    private void insertAttendance(){
        Attendance attendance = new Attendance();
        String s = attendanceNameET.getText().toString();
        if (s == null || "".equals(s.trim())){
            CommonUtil.showMsg("请输入姓名");
            return;
        }else {
            attendance.setAttendancePeople(s);
        }

        s = attendanceHoursEt.getText().toString();
        if (s == null || "".equals(s.trim())){
            CommonUtil.showMsg("请输入时长");
            return;
        }else{
            attendance.setAttendanceHours(Double.parseDouble(s));
        }

        attendance.setAttendanceType(attendanceType);

        attendance.setAddTime(attendanceCalendar.getTimeInMillis());

        attendance.setComment(attendanceCommentET.getText().toString().trim());

        //添加到数据库
        attendanceDao.insert(attendance);
        //清空输入框
        attendanceNameET.setText("");
        attendanceHoursEt.setText("");
        attendanceCommentET.setText("");
        CommonUtil.showMsg("添加成功");
    }

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
