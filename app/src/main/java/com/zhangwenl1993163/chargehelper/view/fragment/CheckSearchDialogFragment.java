package com.zhangwenl1993163.chargehelper.view.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.zhangwenl1993163.chargehelper.R;
import com.zhangwenl1993163.chargehelper.util.DateUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by zhangwenliang on 2018/3/4.
 */

public class CheckSearchDialogFragment extends DialogFragment{
    public static final String PROCESS_CARD_NUMBER = "process_card_number",
            MODEL_NAME = "model_name",ADD_DATE = "add_time";
    private View view;
    private Spinner yearSpinner,monthSpinner,sortSpinner;
    private OnSelectedListener onSelectedListener;
    private List<Integer> years;
    private Calendar date = Calendar.getInstance();
    private String sortItem;

    @SuppressLint({"NewApi","ValidFragment"})
    public CheckSearchDialogFragment() {
    }
    @SuppressLint({"NewApi","ValidFragment"})
    public CheckSearchDialogFragment(OnSelectedListener onSelectedListener) {
        this.onSelectedListener = onSelectedListener;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        if (view == null)
            view = inflater.inflate(R.layout.check_search_dialog,container,false);
        return view;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (view == null)
            view = getActivity().getLayoutInflater().inflate(R.layout.check_search_dialog,null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("选择查询条件");
        builder.setView(view);
        builder.setNegativeButton("取消",null);
        builder.setPositiveButton("查询", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (onSelectedListener != null)
                    onSelectedListener.onSelected(date.getTime(),sortItem);
            }
        });

        return builder.create();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        yearSpinner = view.findViewById(R.id.check_query_year);
        yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                date.set(Calendar.YEAR,years.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        loadYears();
        monthSpinner = view.findViewById(R.id.check_query_months);
        String[] b = getResources().getStringArray(R.array.months);
        ArrayAdapter<String> c = new ArrayAdapter<String>(getView().getContext(),android.R.layout.simple_list_item_1,b);
        monthSpinner.setAdapter(c);
        monthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                date.set(Calendar.MONTH,position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        monthSpinner.setSelection(date.get(Calendar.MONTH));
        sortSpinner = view.findViewById(R.id.check_sort_item);
        String[] d = getResources().getStringArray(R.array.sort_item);
        ArrayAdapter<String> e = new ArrayAdapter<String>(getView().getContext(),android.R.layout.simple_list_item_1,d);
        sortSpinner.setAdapter(e);
        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0)
                    sortItem = PROCESS_CARD_NUMBER;
                else if (position == 1)
                    sortItem = MODEL_NAME;
                else
                    sortItem = ADD_DATE;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
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
        yearSpinner.setAdapter(c);
    }

    public interface OnSelectedListener{
        void onSelected(Date date,String sortItem);
    }
}
