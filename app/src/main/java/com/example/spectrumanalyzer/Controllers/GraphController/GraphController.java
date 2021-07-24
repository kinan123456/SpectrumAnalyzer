package com.example.spectrumanalyzer.Controllers.GraphController;

import android.app.AppComponentFactory;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.widget.TextView;

import com.example.spectrumanalyzer.R;
import com.example.spectrumanalyzer.Screens.Activities.MainActivity;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.MPPointF;

import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;

public class GraphController extends AppCompatActivity {
    private static GraphController instance = null;
    private int dataSetsColor = -1;
    private LineChart graph;
    private Context context;
    private String hivfLabel1 = "", hivfLabel2 = "";
    ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
    private int ch1HighVoltageLevelFlag, ch2HighVoltageLevelFlag, ch1GainCntrlFlag, ch2GainCntrlFlag;
    public final int enableControl = 1, disableControl = 2;
    public final int highVoltage = 1, lowVoltage = 2;
    private String microController_msg;
    private float Frequency, Amplitude;
    private int arrayLength;

    private GraphController() {
    }

    public static GraphController GetInstance() {
        if (instance == null) {
            instance = new GraphController();
        }
        return instance;
    }

    public ArrayList<Entry> ConvertInputArrayToGraphArray(String[] stringArray, int ch) {
        ArrayList<Entry> EntryList = new ArrayList<Entry>();
        int i = 0;
        for (String str : stringArray) {
            try {
                Amplitude = ComputeAmplitude(str);
                Frequency = ComputeFrequency(i);
                EntryList.add(new Entry(Frequency, Amplitude));
                i = i + 1;
            } catch (NumberFormatException numberFormatException) {
                microController_msg = str;
            }
        }
        Log.i("myTag",String.valueOf(i));
        updateVoltageLevelTracker(microController_msg);
        return EntryList;
    }

    private float ComputeFrequency(int indx) {
        return (float) (1.9525 * indx);
    }

    private int ComputeAmplitude(String Value) {
        return Integer.parseInt(Value.trim());
    }

    public void updateVoltageLevelTracker(String msg) {
        switch (msg) {
            case "ch1High.":
                ch1HighVoltageLevelFlag = highVoltage;
                ch2HighVoltageLevelFlag = lowVoltage;
                break;
            case "ch2High.":
                ch1HighVoltageLevelFlag = lowVoltage;
                ch2HighVoltageLevelFlag = highVoltage;
                break;
            case "ch12Low.":
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
        setHIVFLabel(1);
        setHIVFLabel(2);
    }

    public void updateGainControlAdjustment(String msg) {
        switch (msg) {
            case "ch1EnableGainControl":
                ch1GainCntrlFlag = enableControl;
                break;
            case "ch2EnableGainControl":
                ch2HighVoltageLevelFlag = enableControl;
                break;
            case "ch1DisableGainControl":
                ch1HighVoltageLevelFlag = disableControl;
                break;
            case "ch2DisableGainControl":
                ch2HighVoltageLevelFlag = disableControl;
                break;
            default: {
            }
            break;
        }
    }

    private void setHIVFLabel(int channel) {
        if (channel == 1) {
            if (ch1HighVoltageLevelFlag == highVoltage) {
                hivfLabel1 = "High";

            } else {
                hivfLabel1 = "Low";
            }
        } else {
            if (ch2HighVoltageLevelFlag == highVoltage) {
                hivfLabel2 = "High";

            } else {
                hivfLabel2 = "Low";

            }
        }
    }

    public String getHIVFLabel(int channel) {
        return channel == 1 ? hivfLabel1 : hivfLabel2;
    }

    public void CreateNewChannelData(ArrayList<Entry> EntryList, int channel) {
        LineDataSet lineDataSet = new LineDataSet(EntryList, "Ch" + String.valueOf(channel));
        lineDataSet.setDrawCircles(false);
        lineDataSet.setDrawValues(false);
        lineDataSet.setLineWidth(5);
        lineDataSet.setColor(
                channel == 1 ? Color.rgb(200, 0, 0) : Color.rgb(0, 0, 200)
        );

        dataSets.add(lineDataSet);
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
        xAxis.setAxisMaximum(250);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawAxisLine(true);
    }

    public void SetLineChart(LineChart lineChart) {
        if (graph == null)
            graph = lineChart;
    }

    public void DisplayAllChannelsData() {
        LineData lineData = new LineData(dataSets);

        graph.setData(lineData);

        graph.invalidate();
        dataSets = new ArrayList<ILineDataSet>();
    }

    public void SetContext(Context mainActivityContext) {
        context = mainActivityContext;
    }
}
