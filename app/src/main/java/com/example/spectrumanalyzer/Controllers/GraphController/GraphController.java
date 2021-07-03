package com.example.spectrumanalyzer.Controllers.GraphController;

import android.content.Context;
import android.graphics.Color;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;

public class GraphController {
    private static GraphController instance = null;
    private int dataSetsColor = -1;
    private LineChart graph;
    private LineDataSet lineDataSet;
    private Context context;
    ArrayList<ILineDataSet> dataSets = new ArrayList<>();

    private GraphController() {
    }

    public static GraphController GetInstance() {
        if (instance == null)
            instance = new GraphController();
        return instance;
    }
    public static ArrayList<Entry> ConvertInputArrayToGraphArray(String[] stringArray) {
        ArrayList<Entry> EntryList = new ArrayList<>();
        int Amplitude,Frequency;
        for (int i = 0; i < stringArray.length; i++) {
            Frequency = ComputeFrequency(i);
            Amplitude = Integer.parseInt(stringArray[i].trim());
            EntryList.add(new Entry(Frequency, Amplitude));
        }
        return EntryList;
    }
    private static int ComputeFrequency(int arrayIndex) {
        return 3906 * arrayIndex;
    }
    public void CreateNewChannelData(ArrayList<Entry> EntryList) {
        if (EntryList.size() <= 0) {
            dataSets.add(new LineDataSet(EntryList, ""));
        } else {
            lineDataSet = new LineDataSet(EntryList, "[#] of Channels : 2");
            lineDataSet.setDrawCircles(false);
            lineDataSet.setDrawValues(false);
            lineDataSet.setLineWidth(5);
            lineDataSet.setColor(dataSetsColor);
            lineDataSet.setCircleColor(dataSetsColor);
            lineDataSet.setHighLightColor(Color.rgb(0, 0, 255));
            dataSets.add(lineDataSet);
        }
    }

    public void ConfigureLineChart() {
        if (graph == null)
            return;

        graph.getDescription().setEnabled(false);
        setChannelsColors();
        configureXAxis();
        configureYAxis();
    }

    private void setChannelsColors() {
        dataSetsColor = Color.rgb(0, 0, 200);
    }

    private void configureYAxis() {
        graph.getAxisRight().setEnabled(false);
        YAxis yAxis = graph.getAxisLeft();
        yAxis.setAxisMinimum(0);
        yAxis.setAxisMaximum(4096);
    }

    private void configureXAxis() {
        XAxis xAxis = graph.getXAxis();
        xAxis.setAxisMinimum(0);
        xAxis.setAxisMaximum(500000);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawAxisLine(true);
    }
    public void SetLineChart(LineChart lineChart)
    {
        if(graph == null)
            graph = lineChart;
    }
    public void DisplayAllChannelsData() {
        LineData lineData = new LineData(dataSets);
        graph.setData(lineData);
        graph.invalidate();
        dataSets = new ArrayList<>();
    }

    public void SetContext(Context mainActivityContext) {
        context = mainActivityContext;
    }
}
