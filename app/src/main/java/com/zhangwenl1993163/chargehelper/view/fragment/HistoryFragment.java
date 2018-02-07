package com.zhangwenl1993163.chargehelper.view.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.zhangwenl1993163.chargehelper.R;
import com.zhangwenl1993163.chargehelper.util.DateUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhang on 2018/2/7.
 */

public class HistoryFragment extends Fragment implements View.OnClickListener {
    private Spinner queryYear,queryMonth,sortWay;
    private Button queryBtn;
    private View container;

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
        queryYear = getView().findViewById(R.id.query_year);
        loadYears();
        queryMonth = getView().findViewById(R.id.query_months);
        sortWay = getView().findViewById(R.id.sort_item);
        queryBtn = getView().findViewById(R.id.query_button);
        queryBtn.setBackgroundColor(getColorPrimary());
        queryBtn.setOnClickListener(this);
        container = getView().findViewById(R.id.history_container);
    }

    private void query(){

    }

    private void loadYears(){
        List<Integer> a = DateUtil.getLatest3Years();
        List<String> b = new ArrayList<>();
        for (int i : a){
            b.add(i + " 年");
        }
        ArrayAdapter<String> c = new ArrayAdapter<String>(getView().getContext(),android.R.layout.simple_list_item_1,b);
        queryYear.setAdapter(c);
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
