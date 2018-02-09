package com.zhangwenl1993163.chargehelper.view.fragment;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhang on 2018/2/9.
 */

public class UpdateDialogFragment extends DialogFragment {
    private Record record;
    private EditText cardNumber,qulifiedNumber,comment;
    private Spinner modelName;
    private TextView modelPrice,addTime;
    private ChargeDao chargeDao;
    private ProductDao productDao;
    private List<String> names = new ArrayList<>();
    private Map<String,Double> namePriceMap = new HashMap<>();
    private Context mContext;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.update_record_dialog,container,false);
    }

    private void initUI(){
        chargeDao = new ChargeDao(mContext);
        productDao = new ProductDao(mContext);
        Log.i("=============",productDao == null?"yes":"no");
        cardNumber = getView().findViewById(R.id.update_item_card);
        qulifiedNumber = getView().findViewById(R.id.update_item_qulified_num);
        comment = getView().findViewById(R.id.update_item_comment);
        modelName = getView().findViewById(R.id.update_item_model_name);
        modelName.setOnItemSelectedListener(listener);
        modelPrice = getView().findViewById(R.id.update_item_model_price);
        addTime = getView().findViewById(R.id.update_item_add_date);
    }

    /**
     * 初始化数据
     * */
    public void initDate(Map<String,Object> m,Context context){
        this.mContext = context;
        initUI();
        record = new Record();
        record.setId((Integer)m.get("id"));
        record.setProcessCardNumber((Integer)m.get("processCardNumber"));
        record.setModelName((String) m.get("modelName"));
        loadModelNames(record.getModelName());
        record.setModelPrice((double)m.get("modelPrice"));
        record.setQulifiedNumber((Integer)m.get("qulifiedNumber"));
        record.setAddTimeStamp((Long) m.get("addTime"));
        record.setModifyTimeStamp(System.currentTimeMillis());
        record.setComment((String) m.get("comment"));

        cardNumber.setText(record.getProcessCardNumber()+"");
        qulifiedNumber.setText(record.getQulifiedNumber()+"");
        addTime.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date(record.getAddTimeStamp())));
        comment.setText(record.getComment());
    }

    public void update(){
        String s = cardNumber.getText().toString();
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
        record.setModifyTimeStamp(System.currentTimeMillis());

        showToastL(record.toString());
    }

    /**
     * 加载型号
     * @param 默认选中型号
     * */
    private void loadModelNames(String defaultItemNmae){
        //将所有产品保存成map，key为modelName ,value为model_price,便于联动
        List<Product> products = productDao.getAllProduct();
        for (Product product : products){
            names.add(product.getModelName());
            namePriceMap.put(product.getModelName(),product.getModelPrice());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getView().getContext(),android.R.layout.simple_list_item_1,names);
        modelName.setAdapter(adapter);
        //设置默认型号
        modelName.setSelection(names.indexOf(defaultItemNmae));
        //设置默认单价
        double firstPrice = namePriceMap.get(defaultItemNmae);
        modelPrice.setText(firstPrice+" 元");
    }

    /**
     *spinner监听器
     */
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

    private void showToast(String msg){
        Toast.makeText(getView().getContext(),msg,Toast.LENGTH_SHORT).show();
    }
    private void showToastL(String msg){
        Toast.makeText(getView().getContext(),msg,Toast.LENGTH_LONG).show();
    }
}
