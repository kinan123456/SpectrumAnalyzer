package com.example.spectrumanalyzer.Screens.Activities;

import android.content.Context;
import android.widget.TextView;

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
        tvContent.setText(String.format("Voltage: %.2f[V]", (3.3*e.getY()/4096.0)) +"\n"+ String.format("Frequency: %.2f[KHz]",e.getX()));

        // this will perform necessary layout
        super.refreshContent(e, highlight);
    }

    private MPPointF mOffset;
    @Override
    public MPPointF getOffset() {

        if (mOffset == null) {
            // center the marker horizontally and vertically
            mOffset = new MPPointF(60,
                    -getHeight() - 60);
        }
        return mOffset;
    }
}