package com.zhangwenl1993163.chargehelper.view.fragment;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.zhangwenl1993163.chargehelper.R;
import com.zhangwenl1993163.chargehelper.dao.ChargeDao;
import com.zhangwenl1993163.chargehelper.util.DateUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhangwenliang on 2018/2/23.
 */

public class AnalyzeFragment extends Fragment {
    private final String TAG = AnalyzeFragment.class.getName();
    private View containerView;
    private LineChart lineChart;
    private ChargeDao chargeDao;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        containerView = inflater.inflate(R.layout.analyze_panel,container,false);
        return containerView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        lineChart = getView().findViewById(R.id.analyze_salary);
        chargeDao = new ChargeDao(getActivity());
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden)
            initData();
    }

    private void initData(){
        int i = 0;
        List<Entry> entries = new ArrayList<>();
        final List<String> dates = new ArrayList<>();
        for (Map<String,Object> map : getSalaryCurrent6Month()){
            dates.add((String)map.get("date"));
            Entry entry = new Entry((float)i++,Float.parseFloat((String)map.get("salary")));
            entries.add(entry);
        }
        LineDataSet set = new LineDataSet(entries,"最近一年工资趋势");
        //设置线条颜色
        set.setColor(Color.parseColor("#90F0B3"));
        //y轴以左侧label为准
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        //设置模式为曲线
        set.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
        //不显示顶点
        set.setDrawCircles(false);
        //不显示顶点数据
        set.setDrawValues(false);
        //设置字体颜色
//        set.setValueTextColor(Color.BLACK);
        //设置字体大小
//        set.setValueTextSize(10);
        //设置填充色
        set.setFillColor(Color.parseColor("#90F0B3"));
        //设置填充曲线下区域
        set.setDrawFilled(true);

        LineData data = new LineData(set);
        lineChart.setData(data);
        lineChart.setDescription(null);
        XAxis xAxis = lineChart.getXAxis();
        //禁止显示竖直网格线
        xAxis.setDrawGridLines(false);
        //禁止显示标题下的横线
//        xAxis.setDrawAxisLine(false);
        //设置间隔
        xAxis.setGranularity(1f);
        //设置字体大小
        xAxis.setTextSize(11);
        //设置最小值
        xAxis.setAxisMinimum(0);
        //自定义横坐标内容
        IAxisValueFormatter iAxisValueFormatter = new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return dates.get((int)value);
            }
        };
        xAxis.setValueFormatter(iAxisValueFormatter);

        //不显示右侧y轴
        YAxis yAxis = lineChart.getAxisRight();
        yAxis.setEnabled(false);

        yAxis = lineChart.getAxisLeft();
        yAxis.setTextSize(11);
        IAxisValueFormatter iAxisValueFormatter2 = new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return value + "元";
            }
        };
        yAxis.setValueFormatter(iAxisValueFormatter2);

        //动画
        lineChart.animateY(500);

        lineChart.invalidate();
    }

    /**
     * 获取最近一年工资
     * @return key:date 日期 salary 薪水
     * */
    private List<Map<String,Object>> getSalaryCurrent6Month(){
        List<Map<String,Object>> mapList = new ArrayList<>();
        Calendar now = Calendar.getInstance();
        now.setTime(new Date());
        int year = now.get(Calendar.YEAR);
        int month = now.get(Calendar.MONTH);
        for (int i = 0 ; i < 12 ; i++){
            Map<String,Object> map = new HashMap<>();
            map.put("date",year+"/"+(month+1));
            Calendar time = Calendar.getInstance();
            time.set(year,month,15);
            BigDecimal bigDecimal = chargeDao.getTotalMoney(DateUtil.getMonthRange(time.getTimeInMillis()));
            map.put("salary",bigDecimal.setScale(2,BigDecimal.ROUND_HALF_UP).toString());
            mapList.add(map);

            if (month > 0){
                month--;
            }else {
                year--;
                month = 11;
            }
        }
        Collections.reverse(mapList);
        return mapList;
    }
}
