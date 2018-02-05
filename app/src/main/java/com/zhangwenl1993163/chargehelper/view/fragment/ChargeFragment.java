package com.zhangwenl1993163.chargehelper.view.fragment;

import android.app.DatePickerDialog;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.zhangwenl1993163.chargehelper.R;
import com.zhangwenl1993163.chargehelper.dao.ChargeDao;
import com.zhangwenl1993163.chargehelper.dao.ProductDao;
import com.zhangwenl1993163.chargehelper.model.Product;
import com.zhangwenl1993163.chargehelper.model.Record;
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
    private TextView todayTotalCount,todayTotalMoney,modelPrice,addDateTv;
    private EditText processNumber,qulifiedNumber,comment;
    private Spinner modelName;
    private Button addButton;
    private Calendar calendar = Calendar.getInstance();
    private ProductDao productDao;
    private ChargeDao chargeDao;
    private Map<String,Double> namePriceMap = new HashMap<>();
    private List<String> names = new ArrayList<>();

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
        calendar.setTime(new Date());
        processNumber = getView().findViewById(R.id.add_record_process_num);
        modelName = getView().findViewById(R.id.add_record_model_name);
        modelName.setOnItemSelectedListener(listener);
        modelPrice = getView().findViewById(R.id.add_record_model_price);
        qulifiedNumber = getView().findViewById(R.id.add_record_qulified_num);
        addButton = getView().findViewById(R.id.add_record_add_button);
        addButton.setOnClickListener(this);
        addDateTv = getView().findViewById(R.id.add_record_add_date);
        addDateTv.setText(new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime()));
        addDateTv.setOnClickListener(this);
        comment = getView().findViewById(R.id.add_record_comment);
        todayTotalCount = getView().findViewById(R.id.today_charge_count);
        todayTotalMoney = getView().findViewById(R.id.today_total_money);
        loadStatistics();
        loadModels();
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

    private void loadStatistics(){
        int count = chargeDao.getWorkedCount(new Date());
        double money = chargeDao.getTotalMoney(new Date());
        todayTotalCount.setText(count+"");
        todayTotalMoney.setText(money+"");
    }

    /**
     * 列表点击事件
     * */
    private AdapterView.OnItemSelectedListener listener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String name = names.get(position);
            double price = namePriceMap.get(name);
            modelPrice.setText(price+" 元");
            record.setModelName(name);
            record.setModelPrice(price);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

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
     * 加载型号
     * */
    private void loadModels(){
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
        s = record.getModelName();
        if (s != null && !"".equals(s)) {
        }else {
            showToast("型号名称加载失败，请稍后再试");
            return;
        }
        Double p = record.getModelPrice();
        if (p != null && p != 0) {
        }else {
            showToast("型号单价加载失败，请稍后再试");
            return;
        }
        s = comment.getText().toString();
        record.setComment(s);
        record.setAddTimeStamp(calendar.getTimeInMillis());
        record.setModifyTimeStamp(calendar.getTimeInMillis());

        //调用数据库
        showToast(record.toString());
    }

    private void showToast(String msg){
        Toast.makeText(getView().getContext(),msg,Toast.LENGTH_SHORT).show();
    }
}
