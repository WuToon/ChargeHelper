package com.zhangwenl1993163.chargehelper.view.fragment;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.zhangwenl1993163.chargehelper.R;
import com.zhangwenl1993163.chargehelper.dao.ChargeDao;
import com.zhangwenl1993163.chargehelper.model.Constants;
import com.zhangwenl1993163.chargehelper.model.Record;
import com.zhangwenl1993163.chargehelper.util.DateUtil;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by zhangwenliang on 2018/2/23.
 */

public class AnalyzeFragment extends Fragment{
    private View containerView;
    private LinearLayout lineChartContainer,pieChartContainer;
    private ChargeDao chargeDao;
    private Spinner salaryItem,modelItem;
    private int salaryItemType = 0,modelItemType = 0;
    private List<String> titleDatas;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        containerView = inflater.inflate(R.layout.analyze_panel,container,false);
        return containerView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        lineChartContainer = getView().findViewById(R.id.analyze_salary);
        pieChartContainer = getView().findViewById(R.id.analyze_model);

        salaryItem = getView().findViewById(R.id.salary_analyze_items);
        salaryItem.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                salaryItemType = position;
                initLineChart(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        modelItem = getView().findViewById(R.id.modelName_analyze_items);
        modelItem.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                modelItemType = position;
                initPieChart(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        chargeDao = new ChargeDao(getActivity());
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden){
            initLineChart(salaryItemType);
            initPieChart(modelItemType);
        }
    }

    /**
     * @param type 0:12月型号分布 1：30天型号分布
     * */
    private void initPieChart(int type){
        PieChart pieChart = new PieChart(getActivity());
        List<Record> records = null;
        if (type == 0){
            records = chargeDao.getRecordInRange(DateUtil.getYearRange(System.currentTimeMillis()));
        }else {
            records = chargeDao.getRecordInRange(DateUtil.getMonthRange(System.currentTimeMillis()));
        }

        //统计各个型号出现次数
        Map<String,Integer> modelMap = new HashMap<>();
        for (Record record : records){
            String modelName = record.getModelName();
            int count = modelMap.get(modelName) != null ? (modelMap.get(modelName)+1) : 1;
            modelMap.put(modelName,count);
        }

        //map按值由大到小排序
        List<Map.Entry<String,Integer>> mapEntries = new ArrayList<>(modelMap.entrySet());
        Collections.sort(mapEntries, new Comparator<Map.Entry<String,Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return o2.getValue() - o1.getValue();
            }
        });

        //选取数量排名前四的型号，其他型号相加为其他,value为所占百分比
        List<PieEntry> entries = new ArrayList<>();
        if (mapEntries.size()>5){
            for (int i = 0 ; i < 4 ; i++){
                PieEntry entry = new PieEntry((float)mapEntries.get(i).getValue(),mapEntries.get(i).getKey());
                entries.add(entry);
            }
            int x = 0;
            for (int i = 4 ; i < mapEntries.size() ; i++){
                x += mapEntries.get(i).getValue();
            }
            PieEntry entry = new PieEntry((float)x,"其它");
            entries.add(entry);
        }else {
            for (int i = 0 ; i < mapEntries.size() ; i++){
                PieEntry entry = new PieEntry((float)mapEntries.get(i).getValue(),mapEntries.get(i).getKey());
                entries.add(entry);
            }
        }

        PieDataSet pieDataSet = new PieDataSet(entries,type == 0 ? "近一年型号分布" : "近一月型号分布");
        //饼图各部分颜色
        pieDataSet.setColors(Color.parseColor("#B0F566"),Color.parseColor("#4AF2A1"),
                Color.parseColor("#5CC9F5"),Color.parseColor("#6638F0"),
                Color.parseColor("#F78AE0"));
        //字体颜色
        pieDataSet.setValueTextColor(Color.WHITE);
        pieDataSet.setValueTextSize(12);

        PieData pieData = new PieData(pieDataSet);
        //格式化value
        IValueFormatter formatter = new IValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                return ((int)(value*100))/100.0+"%";
            }
        };
        pieData.setValueFormatter(formatter);

        pieChart.setData(pieData);
        //不显示description
        pieChart.setDescription(null);
        //显示百分比
        pieChart.setUsePercentValues(true);
        //不显示label
//        pieChart.setDrawEntryLabels(false);
        //设置entry label颜色
        pieChart.setEntryLabelColor(Color.BLACK);

        //中间圆盘半径,值为百分比
//        pieChart.setHoleRadius(20);
        //设置比例图
//        Legend mLegend = pieChart.getLegend();
        //设置比例图显示在饼图的哪个位置
//        mLegend.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
        //设置比例图的形状，默认是方形,可为方形、圆形、线性
//        mLegend.setForm(Legend.LegendForm.CIRCLE);
        //动画
        pieChart.animateXY(500,500);

        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        pieChart.setLayoutParams(params);

        pieChartContainer.removeAllViews();
        pieChartContainer.addView(pieChart);
    }

    /**
     * @param type 0:12月月收入 1：30天日收入
     * */
    private void initLineChart(int type){
        LineChart lineChart = new LineChart(getActivity());
        titleDatas = new ArrayList<>();
        int i = 0;
        List<Entry> entries = new ArrayList<>();
        List<Map<String,Object>> maps = null;
        if (type == 0)
            maps = getSalaryCurrent12Months();
        else
            maps = getSalaryCurrent30Days();
        for (Map<String,Object> map : maps){
            titleDatas.add((String)map.get("date"));
            Entry entry = new Entry((float)i++,Float.parseFloat((String)map.get("salary")));
            entries.add(entry);
        }
        LineDataSet set = new LineDataSet(entries,(type == 0 ? "最近一年工资趋势" : "最近一月工资趋势"));
        //设置线条颜色
        set.setColor(Color.parseColor("#90F0B3"));
        //y轴以左侧label为准
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        //设置模式为曲线
        set.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
        //不显示顶点
//        set.setDrawCircles(false);
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
        //设置最大值
        xAxis.setAxisMaximum((float)titleDatas.size()-1);
        //自定义横坐标内容
        IAxisValueFormatter iAxisValueFormatter = new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return titleDatas.get((int)value);
            }
        };
        xAxis.setValueFormatter(iAxisValueFormatter);

        //不显示右侧y轴
        YAxis yAxis = lineChart.getAxisRight();
        yAxis.setEnabled(false);

        //左侧y轴
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
        Description description = new Description();
        description.setText("双指捏合进行缩放");
        lineChart.setDescription(description);
        //禁止y轴缩放
        lineChart.setScaleYEnabled(false);
        //设置markerview
        SalaryMarkerView markerView = new SalaryMarkerView(getActivity(),R.layout.salary_marker_view);
        lineChart.setMarker(markerView);

        lineChart.invalidate();
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lineChart.setLayoutParams(params);
        lineChartContainer.removeAllViews();
        lineChartContainer.addView(lineChart);
    }

    /**
     * 获取最近一年月工资
     * @return key:date 日期 salary 薪水
     * */
    private List<Map<String,Object>> getSalaryCurrent12Months(){
        List<Map<String,Object>> mapList = new ArrayList<>();
        Calendar now = Calendar.getInstance();
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

    /**
     * 获取最近一月日工资
     * @return key:date 日期 salary 薪水
     * */
    private List<Map<String,Object>> getSalaryCurrent30Days(){
        List<Map<String,Object>> mapList = new ArrayList<>();
        long timestamp = System.currentTimeMillis();
        for (int i = 0 ; i < 30 ; i++){
            Map<String,Object> map = new HashMap<>();
            map.put("date",new SimpleDateFormat("MM/dd").format(new Date(timestamp)));
            BigDecimal bigDecimal = chargeDao.getTotalMoney(DateUtil.getDayRange(timestamp));
            map.put("salary",bigDecimal.setScale(2,BigDecimal.ROUND_HALF_UP).toString());
            mapList.add(map);

            timestamp = timestamp - Constants.MILLISECONDS_OF_DAY;
        }
        Collections.reverse(mapList);
        return mapList;
    }
}

class SalaryMarkerView extends MarkerView {
    private Context context;
    private TextView markview;
    public SalaryMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);
        this.context = context;
        markview = findViewById(R.id.marker_view);
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        super.refreshContent(e, highlight);
        markview.setText(e.getY()+"");
    }

    @Override
    public MPPointF getOffsetForDrawingAtPoint(float posX, float posY) {
        MPPointF mpPointF = new MPPointF(-(getWidth()/2),-getHeight());
        return mpPointF;
    }
}
