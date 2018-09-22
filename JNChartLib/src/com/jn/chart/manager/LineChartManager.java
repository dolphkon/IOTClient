package com.jn.chart.manager;

import android.content.Context;
import android.graphics.Color;

import com.jn.chart.animation.Easing;
import com.jn.chart.charts.LineChart;
import com.jn.chart.components.Legend;
import com.jn.chart.components.XAxis;
import com.jn.chart.components.YAxis;
import com.jn.chart.data.Entry;
import com.jn.chart.data.LineData;
import com.jn.chart.data.LineDataSet;
import com.jn.chart.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;

/**
 * @Author: pyz
 * @Package: com.pyz.myproject.utils
 * @Description: TODO
 * @Project: MyProject
 * @Company: 深圳君南信息系统有限公司
 * @Date: 2016/7/20 16:39
 */
public class LineChartManager {

    private static String lineName = null;
    private static String lineName1 = null;
    private static String lineName2=null;
    private static String lineName3=null;

    /**
     * @Description:创建两条折线
     * @param context 上下文
     * @param mLineChart 折线图控件
     * @param xValues 折线在x轴的值
     * @param yValue 折线在y轴的值
     */
    public static void initSingleLineChart(Context context, LineChart mLineChart, ArrayList<String> xValues,
                                           ArrayList<Entry> yValue) {
        initDataStyle(context,mLineChart);
        //设置折线的样式
        LineDataSet dataSet = new LineDataSet(yValue, lineName);
        dataSet.setColor(Color.RED);
        dataSet.setCircleColor(Color.RED);
        dataSet.setDrawValues(true);
//        dataSet.setValueFormatter(new PercentFormatter(new DecimalFormat("%").format()));

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(dataSet);

        //构建一个LineData  将dataSets放入
        LineData lineData = new LineData(xValues, dataSets);
        //将数据插入
        mLineChart.setData(lineData);

        //设置动画效果
        mLineChart.animateY(2000, Easing.EasingOption.Linear);
        mLineChart.animateX(2000, Easing.EasingOption.Linear);
        mLineChart.invalidate();
        mLineChart.setDragEnabled(true);
    }
    /**
     * @Description:创建两条折线
     * @param context 上下文
     * @param mLineChart 折线图控件
     * @param xValues 折线在x轴的值
     * @param yValue 折线在y轴的值
     * @param yValue1 另一条折线在y轴的值
     */
    public static void initDoubleLineChart(Context context, LineChart mLineChart, ArrayList<String> xValues,
                                           ArrayList<Entry> yValue, ArrayList<Entry> yValue1) {

        initDataStyle(context,mLineChart);
        LineDataSet dataSet = new LineDataSet(yValue, lineName);
        dataSet.setColor(Color.RED);
        dataSet.setCircleColor(Color.RED);
        dataSet.setDrawValues(true);
        LineDataSet dataSet1 = new LineDataSet(yValue1, lineName1);
        dataSet1.enableDashedLine(10f, 5f, 0f);//将折线设置为曲线
        dataSet1.setColor(Color.parseColor("#66CDAA"));
        dataSet1.setCircleColor(Color.parseColor("#66CDAA"));
        dataSet1.setDrawValues(true);

        //构建一个类型为LineDataSet的ArrayList 用来存放所有 y的LineDataSet   他是构建最终加入LineChart数据集所需要的参数
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        //将数据加入dataSets
        dataSets.add(dataSet);
        dataSets.add(dataSet1);

        //构建一个LineData  将dataSets放入
        LineData lineData = new LineData(xValues, dataSets);
        //将数据插入
        mLineChart.setData(lineData);
        //设置动画效果
        mLineChart.animateY(2000, Easing.EasingOption.Linear);
        mLineChart.animateX(2000, Easing.EasingOption.Linear);
        mLineChart.invalidate();
    }

    public static void initDoubleLineChart(Context context, LineChart mLineChart, ArrayList<String> xValues,
                                           ArrayList<Entry> yValue, ArrayList<Entry> yValue1,ArrayList<Entry> ysValue, ArrayList<Entry> ysValue1) {

        initDataStyle(context,mLineChart);
        LineDataSet dataSet = new LineDataSet(yValue, lineName);
        dataSet.setColor(Color.RED);
        dataSet.setCircleColor(Color.RED);
        dataSet.setDrawValues(true);

        LineDataSet dataSet1 = new LineDataSet(yValue1, lineName1);
        dataSet1.enableDashedLine(10f, 5f, 0f);//将折线设置为曲线
        dataSet1.setColor(Color.parseColor("#66CDAA"));
        dataSet1.setCircleColor(Color.parseColor("#66CDAA"));
        dataSet1.setDrawValues(true);

        LineDataSet dataSet2 = new LineDataSet(ysValue, lineName2);
        dataSet2.setColor(Color.YELLOW);
        dataSet2.setCircleColor(Color.YELLOW);
        dataSet2.setDrawValues(true);

        LineDataSet dataSet3 = new LineDataSet(ysValue1, lineName3);
        dataSet3.enableDashedLine(10f, 5f, 0f);//将折线设置为曲线
        dataSet3.setColor(Color.parseColor("#C43CA9"));
        dataSet3.setCircleColor(Color.parseColor("#C43CA9"));
        dataSet3.setDrawValues(true);

        //构建一个类型为LineDataSet的ArrayList 用来存放所有 y的LineDataSet   他是构建最终加入LineChart数据集所需要的参数
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        //将数据加入dataSets
        dataSets.add(dataSet);
        dataSets.add(dataSet1);
        dataSets.add(dataSet2);
        dataSets.add(dataSet3);

        //构建一个LineData  将dataSets放入
        LineData lineData = new LineData(xValues, dataSets);
        //将数据插入
        mLineChart.setData(lineData);
        //设置动画效果
        mLineChart.animateY(2000, Easing.EasingOption.Linear);
        mLineChart.animateX(2000, Easing.EasingOption.Linear);
        mLineChart.invalidate();
    }

    /**
     *  @Description:初始化图表的样式
     * @param context
     * @param mLineChart
     */
    private static void initDataStyle(Context context, LineChart mLineChart) {
        //设置图表是否支持触控操作
        mLineChart.setTouchEnabled(true);
        mLineChart.setScaleEnabled(true);
        //设置点击折线点时，显示其数值
//        MyMakerView mv = new MyMakerView(context, R.layout.item_mark_layout);
//        mLineChart.setMarkerView(mv);
        //设置折线的描述的样式（默认在图表的左下角）
        Legend title = mLineChart.getLegend();
        title.setForm(Legend.LegendForm.LINE);
        //设置x轴的样式
        XAxis xAxis = mLineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisLineColor(Color.parseColor("#BB44BB"));
        xAxis.setAxisLineWidth(2);
        xAxis.setDrawGridLines(false);
        //设置是否显示x轴
        xAxis.setEnabled(true);

        //设置左边y轴的样式
        YAxis yAxisLeft = mLineChart.getAxisLeft();
        yAxisLeft.setAxisLineColor(Color.parseColor("#BB44BB"));
        yAxisLeft.setAxisLineWidth(2);
        yAxisLeft.setDrawGridLines(false);

        //设置右边y轴的样式
        YAxis yAxisRight = mLineChart.getAxisRight();
        yAxisRight.setEnabled(false);

        xAxis.setEnabled(true);
        //设置是否开启绘制轴的标签
        xAxis.setDrawLabels(true);
        //是否绘制轴线
        xAxis.setDrawAxisLine(true);
        //是否绘制网格线
        xAxis.setDrawGridLines(true);



    }

    /**
     * @Description:设置折线的名称
     * @param name
     */
    public static void setLineName(String name){
        lineName = name;
    }

    /**
     * @Description:设置另一条折线的名称
     * @param name
     */
    public static void setLineName1(String name){
        lineName1 = name;
    }
}
