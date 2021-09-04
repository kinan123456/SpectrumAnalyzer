package com.example.spectrumanalyzer.Components.Graph;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class Controller extends AppCompatActivity {
    private static Controller instance = null;
    private int dataSetsColor = -1;
    private LineChart graph;
    private Context context;
    private String hivfLabel1 = "", hivfLabel2 = "";
    ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
    private int ch1HighVoltageLevelFlag, ch2HighVoltageLevelFlag, ch1GainCtrlFlag = 1, ch2GainCtrlFlag = 1;
    public final int enableControl = 2, disableControl = 1;
    public final int highVoltage = 1, lowVoltage = 0;
    private float Frequency, Amplitude,voltAC,voltDC;
    private int arrayLength;

    private Controller() {
    }
//static : singleton (single instna5ce in the whole app), GraphController.GetInstance.Method...
    // we can use a class without members as static type,
    public static Controller getInstance() {
        if (instance == null) {
            instance = new Controller();
        }
        return instance;
    }
    public void resetDataGraph(){
        dataSets.clear();
        //dataSets = new ArrayList<ILineDataSet>();
    }
    public ArrayList<Entry> convertInputArrayToGraphArray(String[] stringArray, int ch) {
        ArrayList<Entry> EntryList = new ArrayList<Entry>();
        int i = 0;
        for (String str : stringArray) {
            try{
                Amplitude = computeAmplitude(str);
                Frequency = computeFrequency(i);
            }catch(IllegalArgumentException IOExeption){
                updateVoltageLevelTracker(str.trim());
            }
            EntryList.add(new Entry(Frequency, Amplitude));
            i = i + 1;
        }
        return EntryList;
    }

    private float computeFrequency(int indx) {
        return (float) (1.9525 * indx);
    }

    private int computeAmplitude(String Value) {
        int intAmplitude = Integer.parseInt(Value.trim());
        if(intAmplitude == 0){
            intAmplitude+=1;
        }
        return intAmplitude;
    }
    private float getGainControlFactor(int ch){
        return ch == 1 ? ch1GainCtrlFlag : ch2GainCtrlFlag;
    }
    public void updateVoltageLevelTracker(String msg) {
        switch (msg) {
            case "ch1High":
                ch1HighVoltageLevelFlag = highVoltage;
                ch2HighVoltageLevelFlag = lowVoltage;
                break;
            case "ch2High":
                ch1HighVoltageLevelFlag = lowVoltage;
                ch2HighVoltageLevelFlag = highVoltage;
                break;
            case "ch12Low":
                ch1HighVoltageLevelFlag = lowVoltage;
                ch2HighVoltageLevelFlag = lowVoltage;
                break;
            case "ch12High":
                ch1HighVoltageLevelFlag = highVoltage;
                ch2HighVoltageLevelFlag = highVoltage;
                ;
                break;
            default: {
            }
            break;
        }
        setHIVFLabel();
    }

    public void updateGainControlAdjustment(String msg) {
        switch (msg) {
            case "ch1EnableGainControl":
                ch1GainCtrlFlag = enableControl;
                break;
            case "ch2EnableGainControl":
                ch2GainCtrlFlag = enableControl;
                break;
            case "ch1DisableGainControl":
                ch2GainCtrlFlag = disableControl;
                break;
            case "ch2DisableGainControl":
                ch2GainCtrlFlag = disableControl;
                break;
            default: {
            }
            break;
        }
    }

    private void setHIVFLabel() {
        hivfLabel1 = (ch1HighVoltageLevelFlag == highVoltage ? "High" : "Low");
        hivfLabel2 = (ch2HighVoltageLevelFlag == highVoltage ? "High" : "Low");
    }

    public String getHIVFLabel(int channel) {
        return channel == 1 ? hivfLabel1 : hivfLabel2;
    }

    public void createNewChannelData(ArrayList<Entry> EntryList, int ch) {
        LineDataSet lineDataSet = new LineDataSet(EntryList, "Ch" + ch);
        lineDataSet.setDrawCircles(false);
        lineDataSet.setDrawValues(false);
        lineDataSet.setLineWidth(5);
        lineDataSet.setColor(
                ch == 1 ? Color.rgb(200, 0, 0) : Color.rgb(0, 0, 200)
        );

        dataSets.add(lineDataSet);
    }

    public void configLineChart() {
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
        yAxis.setAxisMaximum(4500);
        yAxis.setDrawLabels(true);

    }

    private void configureXAxis() {
        XAxis xAxis = graph.getXAxis();
        xAxis.setAxisMinimum(0);
        xAxis.setAxisMaximum(250);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawLabels(true);
    }

    public void setLineChart(LineChart lineChart) {
        if (graph == null)
            graph = lineChart;
    }

    public void displayAllChannelsData() {
        LineData lineData = new LineData(dataSets);
        graph.setData(lineData);
        graph.getLegend().setDrawInside(true);
        graph.getLegend().setYOffset(150);
        graph.getLegend().setXOffset(225);
        graph.invalidate();
        dataSets = new ArrayList<ILineDataSet>();
    }
    public void setContext(Context mainActivityContext) {
        context = mainActivityContext;
    }

}
