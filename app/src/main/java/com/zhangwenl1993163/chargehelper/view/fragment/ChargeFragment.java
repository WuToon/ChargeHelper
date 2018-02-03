package com.zhangwenl1993163.chargehelper.view.fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.zhangwenl1993163.chargehelper.R;
import com.zhangwenl1993163.chargehelper.model.Record;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by zhang on 2018/2/3.
 */

public class ChargeFragment extends Fragment implements View.OnClickListener {
    private Record record = new Record();
    private TextView todayTotalCount,todayTotalMoney,modelPrice,addDateTv;
    private EditText processNumber,qulifiedNumber;
    private Spinner modelName;
    private Button addButton;
    private Calendar calendar = Calendar.getInstance();

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
        calendar.setTime(new Date());
        processNumber = getView().findViewById(R.id.add_record_process_num);
        modelName = getView().findViewById(R.id.add_record_model_name);
        modelPrice = getView().findViewById(R.id.add_record_model_price);
        qulifiedNumber = getView().findViewById(R.id.add_record_qulified_num);
        addButton = getView().findViewById(R.id.add_record_add_button);
        addButton.setOnClickListener(this);
        addDateTv = getView().findViewById(R.id.add_record_add_date);
        addDateTv.setText(new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime()));
        addDateTv.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.add_record_add_button:
                insertRecord();
                return;

            case R.id.add_record_add_date:
                loadDatePicker();
                return;
            default:
                return;
        }
    }

    /**
     * 设置日期弹窗
     * */
    private void loadDatePicker(){
        DatePickerDialog dialog = new DatePickerDialog(getView().getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(year,month,dayOfMonth);
                addDateTv.setText(new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime()));
            }
        },calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }
    /**
     * 添加到数据库
     * */
    private void insertRecord(){
        showToast(calendar.getTime().toString());
        String s = processNumber.getText().toString();
        if (s != null && !"".equals(s)){
            record.setProcessCardNumber(Integer.parseInt(s));
        }else {
            showToast("请输入流程卡号");
            return;
        }
        s = qulifiedNumber.getText().toString();
        if (s != null && !"".equals(s)){
            record.setQulifiedNumber(Integer.parseInt(s));
        }else {
            showToast("请输入合格产品个数");
            return;
        }
        s = modelName.getSelectedItem().toString();
        if (s != null && !"".equals(s)) {
            record.setModelName(modelName.getSelectedItem().toString());
        }else {
            showToast("型号名称加载失败，请稍后再试");
            return;
        }
        s = modelPrice.getText().toString();
        if (s != null && !"".equals(s)) {
            record.setModelPrice(Double.parseDouble(modelPrice.getText().toString()));
        }else {
            showToast("型号单价加载失败，请稍后再试");
            return;
        }
        record.setAddTime(calendar.getTime());
        record.setModifyTime(calendar.getTime());

        //调用数据库
        showToast(record.toString());
    }

    private void showToast(String msg){
        Toast.makeText(getView().getContext(),msg,Toast.LENGTH_SHORT).show();
    }
}
