package com.example.spectrumanalyzer.Controllers.GraphController;

import android.content.Context;
import android.graphics.Color;
import android.widget.TextView;

import com.example.spectrumanalyzer.R;
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
    private LineDataSet lineDataSet1,lineDataSet2;
    private Context context;
    ArrayList<ILineDataSet> dataSets = new ArrayList<>();
    public static String highInputVoltageFlag = "Off";

    private GraphController() {

    }

    public static GraphController GetInstance() {
        if (instance == null) {
            instance = new GraphController();
        }
        return instance;
    }
    public static ArrayList<Entry> ConvertInputArrayToGraphArray(String[] stringArray) {
        ArrayList<Entry> EntryList = new ArrayList<>();
        float Frequency;
        float Amplitude;

        for (int i = 0; i < stringArray.length; i++) {
            if(stringArray[i].length() != 4)
                stringArray[i] = "0000";
            Frequency = ComputeFrequency(i);
            Amplitude = ComputeAmplitude(stringArray[i]);
            EntryList.add(new Entry(Frequency, Amplitude));
        }
        updateHIVF(EntryList);
        return EntryList;
    }
    public static void setHIVF(String str) {
        highInputVoltageFlag = str;
    }
    public static String getHIVF(){
        return highInputVoltageFlag;
    }
    public static void updateHIVF(ArrayList<Entry> EntryList){
        if(EntryList.get(0).getY() > 2048)
            setHIVF("On");
        else{
            setHIVF("Off");
        }
    }
    public static ArrayList<Entry> ComputeChannelsMainFrequency(ArrayList<Entry> Spectrum) {
        ArrayList<Entry> mainFreqs = new ArrayList<>();

        float F, A, maxiOne = 0, maxiTwo = 0, maxOne = 0, maxTwo = 0;
        for (int i = 1; i < Spectrum.size(); i++) {
            A = Spectrum.get(i).getY();
            F = Spectrum.get(i).getX();
            if (maxOne < A) {
                maxTwo = maxOne;
                maxiTwo = maxiOne;
                maxOne = A;
                maxiOne = F;
            } else if (maxTwo < A) {
                maxTwo = A;
                maxiTwo = F;
            }
        }
        mainFreqs.add(new Entry(maxiOne, maxOne));
        mainFreqs.add(new Entry(maxiTwo, maxTwo));
        return mainFreqs;
    }

    private static int ComputeAmplitude(String Value) {
        return Integer.parseInt(Value.trim());
    }
    private static float ComputeFrequency(int arrayIndex) {
        return (float)3.9*arrayIndex;
    }
    public void CreateNewChannelData(ArrayList<Entry> EntryList) {
        if (EntryList.size() <= 0) {
            dataSets.add(new LineDataSet(EntryList, ""));
        } else {
            lineDataSet1 = new LineDataSet(EntryList, "[#] of Channels : 2");
            lineDataSet1.setDrawCircles(false);
            lineDataSet1.setDrawValues(false);
            lineDataSet1.setLineWidth(5);
            lineDataSet1.setColor(dataSetsColor);
            lineDataSet1.setCircleColor(dataSetsColor);
            lineDataSet1.setHighLightColor(Color.rgb(0, 0, 255));
            dataSets.add(lineDataSet1);
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
        yAxis.setAxisMaximum(6144);
    }

    private void configureXAxis() {
        XAxis xAxis = graph.getXAxis();
        xAxis.setAxisMinimum(0);
        xAxis.setAxisMaximum(500);
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
