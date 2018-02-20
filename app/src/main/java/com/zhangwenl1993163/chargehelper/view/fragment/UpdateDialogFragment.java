package com.zhangwenl1993163.chargehelper.view.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.zhangwenl1993163.chargehelper.R;
import com.zhangwenl1993163.chargehelper.dao.ProductDao;
import com.zhangwenl1993163.chargehelper.model.Product;
import com.zhangwenl1993163.chargehelper.model.Record;
import com.zhangwenl1993163.chargehelper.util.CommonUtil;

import java.lang.reflect.Field;
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
    private TextView modelPrice,addTime,modelName;
    private ProductDao productDao;
    private List<String> names = new ArrayList<>();
    private Map<String,Double> namePriceMap = new HashMap<>();
    private View view;
    private AlertDialog dialog;
    private Map<String,Object> records;
    private onRecordChanged onRecordChanged;

    public void setOnRecordChanged(com.zhangwenl1993163.chargehelper.view.fragment.onRecordChanged onRecordChanged) {
        this.onRecordChanged = onRecordChanged;
    }

    @SuppressLint({"NewApi", "ValidFragment"})
    public UpdateDialogFragment(Map<String,Object> records) {
        this.records = records;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        if (view == null)
            view = inflater.inflate(R.layout.update_record_dialog,container,false);
        initUI();
        return view;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (view == null)
            view = getActivity().getLayoutInflater().inflate(R.layout.update_record_dialog,null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("修改记录");
        builder.setView(view);
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
                    field.setAccessible(true);
                    field.set(dialog,true);
                    dialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        builder.setPositiveButton("提交", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
                    field.setAccessible(true);
                    if (checkParams()){
                        field.set(dialog,true);
                        if (onRecordChanged != null)
                            onRecordChanged.onChanged(record);
                        dialog.dismiss();
                        CommonUtil.showMsgShort("修改成功");
                    }else
                        field.set(dialog,false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        dialog = builder.create();
        return dialog;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initDate(records);
    }

    private void initUI(){
        cardNumber = view.findViewById(R.id.update_item_card);
        qulifiedNumber = view.findViewById(R.id.update_item_qulified_num);
        comment = view.findViewById(R.id.update_item_comment);
        modelName = view.findViewById(R.id.update_item_model_name);
        modelPrice = view.findViewById(R.id.update_item_model_price);
        addTime = view.findViewById(R.id.update_item_add_date);
    }

    /**
     * 初始化数据
     * */
    public void initDate(Map<String,Object> m){
        productDao = new ProductDao(getActivity());
        record = new Record();
        record.setId((Integer)m.get("id"));
        record.setProcessCardNumber((Integer)m.get("processCardNumber"));
        record.setModelName((String) m.get("modelName"));
        record.setModelPrice((double)m.get("modelPrice"));
        record.setQulifiedNumber((Integer)m.get("qulifiedNumber"));
        record.setAddTimeStamp((Long) m.get("addTime"));
        record.setModifyTimeStamp(System.currentTimeMillis());
        record.setComment((String) m.get("comment"));

        cardNumber.setText(record.getProcessCardNumber()+"");
        qulifiedNumber.setText(record.getQulifiedNumber()+"");
        modelName.setText(record.getModelName());
        modelPrice.setText(record.getModelPrice()+"");
        addTime.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date(record.getAddTimeStamp())));
        comment.setText(record.getComment());
    }

    public boolean checkParams(){
        String s = cardNumber.getText().toString();
        if (s != null && !"".equals(s)){
            record.setProcessCardNumber(Integer.parseInt(s));
        }else {
            CommonUtil.showMsgLong("请输入流程卡号");
            return false;
        }
        s = qulifiedNumber.getText().toString();
        if (s != null && !"".equals(s)){
            record.setQulifiedNumber(Integer.parseInt(s));
        }else {
            CommonUtil.showMsgLong("请输入合格产品个数");
            return false;
        }
        s = record.getModelName();
        if (s != null && !"".equals(s)) {
        }else {
            CommonUtil.showMsgLong("型号名称加载失败，请稍后再试");
            return false;
        }
        Double p = record.getModelPrice();
        if (p != null && p != 0) {
        }else {
            CommonUtil.showMsgLong("型号单价加载失败，请稍后再试");
            return false;
        }
        s = comment.getText().toString();
        record.setComment(s);
        record.setModifyTimeStamp(System.currentTimeMillis());

        return true;
    }
}
interface onRecordChanged{
    void onChanged(Record record);
}