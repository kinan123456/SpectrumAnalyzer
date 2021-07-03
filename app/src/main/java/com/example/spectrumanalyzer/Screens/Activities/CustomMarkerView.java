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
        tvContent.setText("Amp. " + e.getY() + " [Volts]\nFreq. " + e.getX() + " [Hz]");

        // this will perform necessary layout
        super.refreshContent(e, highlight);
    }

    private MPPointF mOffset;
    @Override
    public MPPointF getOffset() {

        if (mOffset == null) {
            // center the marker horizontally and vertically
            mOffset = new MPPointF(3906,
                    -getHeight() - 60);
        }
        return mOffset;
    }
}