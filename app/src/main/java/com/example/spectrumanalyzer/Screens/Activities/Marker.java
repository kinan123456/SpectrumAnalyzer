package com.example.spectrumanalyzer.Screens.Activities;

import android.content.Context;
import android.widget.TextView;

import com.example.spectrumanalyzer.R;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;

public class Marker extends com.github.mikephil.charting.components.MarkerView {

    private TextView tvContent;
    private float voltLevel,freqComponent;
    public Marker(Context context, int layoutResource) {
        super(context, layoutResource);

        // find your layout components
        tvContent = (TextView) findViewById(R.id.tvContent);
    }
    public float computeADCFSRVoltage(float spectAmplitude) {
        return ((float)(3.3 * spectAmplitude / 4096.0));
    }
    // callbacks everytime the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        voltLevel = computeADCFSRVoltage(e.getY());
        freqComponent = e.getX();
        tvContent.setText(String.format("Voltage: %.2f[V]", voltLevel)
                          + "\n" +
                          String.format("Frequency: %.2f[KHz]", freqComponent));
        super.refreshContent(e, highlight);
        // this will perform necessary layout
    }
}