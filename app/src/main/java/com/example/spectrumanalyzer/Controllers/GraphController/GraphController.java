package com.example.spectrumanalyzer.Controllers.GraphController;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;

import com.example.spectrumanalyzer.Controllers.FilterDesigner.FilterDesignController;
import com.example.spectrumanalyzer.Controllers.SettingsController.SettingsController;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class GraphController {
    private static GraphController instance = null;
    private LineChart graph;
    private int dataSetsColor = -1;
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

        ArrayList<Entry> entriesList = new ArrayList<>();

        for (int i = 0; i < stringArray.length; i++) {
            int xValue = i * ((int) FilterDesignController.GetInstance().getFrequencyResolution());
            Log.d("MY TAG", "msg = " + stringArray[i].trim());
            double yValueDouble = (double) Integer.parseInt(stringArray[i].trim());
            float yValueFloat = Float.valueOf(new DecimalFormat("#.##").format((float)((3.3*(yValueDouble)/1023.0))));
            entriesList.add(new Entry(xValue, yValueFloat));
        }
        return entriesList;
    }

    public void SetLineChart(LineChart lineChart) {
        graph = lineChart;
    }

    public void CreateNewChannelData(ArrayList<Entry> entriesList) {
        if (entriesList.size() <= 0) {
            dataSets.add(new LineDataSet(entriesList, ""));
        } else {
            lineDataSet = new LineDataSet(entriesList, "[#] of Channels " + SettingsController.GetInstance().getChannelsNumber());
            lineDataSet.setDrawCircles(false);
            lineDataSet.setDrawValues(false);
            lineDataSet.setLineWidth(5);
            lineDataSet.setColor(dataSetsColor);
            lineDataSet.setCircleColor(dataSetsColor);
            lineDataSet.setHighLightColor(Color.rgb(0, 0, 200));
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
        dataSetsColor = Color.rgb(200, 0, 0);
    }

    private void configureYAxis() {
        graph.getAxisRight().setEnabled(false);
        YAxis yAxis = graph.getAxisLeft();
        yAxis.setAxisMinimum(0);
        yAxis.setAxisMaximum((float)SettingsController.GetInstance().getAmplitudeRange());
    }

    private void configureXAxis() {
        XAxis xAxis = graph.getXAxis();
        xAxis.setAxisMinimum(0);
        xAxis.setAxisMaximum((SettingsController.GetInstance().getSampleRate()) / 2);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawAxisLine(true);
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
