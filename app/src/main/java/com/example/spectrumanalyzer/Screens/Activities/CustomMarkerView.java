package com.example.spectrumanalyzer.Screens.Activities;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.spectrumanalyzer.Controllers.GraphController.GraphController;
import com.example.spectrumanalyzer.R;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

public class CustomMarkerView extends MarkerView {

    private TextView tvContent;

    public CustomMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);

        // find your layout components
        tvContent = (TextView) findViewById(R.id.tvContent);
    }

    // callbacks everytime the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        float Voltage = e.getY()*(float)(3.3 / 4096.0);
        float Frequency = e.getX();
        tvContent.setText(String.format("Voltage: %.2f[V]", Voltage)
                          + "\n" +
                          String.format("Frequency: %.2f[KHz]", Frequency));
        super.refreshContent(e, highlight);
        // this will perform necessary layout
    }
}