package com.example.spectrumanalyzer.Screens.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.spectrumanalyzer.Controllers.Bluetooth.BluetoothController;
import com.example.spectrumanalyzer.Controllers.FilterDesigner.FilterDesignController;
import com.example.spectrumanalyzer.Controllers.GraphController.GraphController;
import com.example.spectrumanalyzer.R;
import com.example.spectrumanalyzer.Controllers.SettingsController.SettingsController;
import com.example.spectrumanalyzer.Screens.AnalyzerGuideActivity;
import com.example.spectrumanalyzer.Screens.AnalyzerSettingsActivity;
import com.example.spectrumanalyzer.Screens.BluetoothActivity;
import com.example.spectrumanalyzer.Screens.FilterDesignerActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    // Widgets
    Button playPauseBtn;
    private LineChart lineChart;

    TextView settingsSampleRate, settingsFftPoints, settingsDesignFilter, graphXLabel, graphYLabel, designFilterAppliedText, dfValueText;

    /**
     * Called when the activity is first created.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

//        Initialize Play/Pause and Reset buttons handlers
        initWidgets();
        initPlayPauseBtn();

//        Setup chart data graph
        SetupChartGraph();
    }

    private void initWidgets() {
        lineChart = findViewById(R.id.activity_main_linechart);
        playPauseBtn = findViewById(R.id.playPauseButton);
        settingsSampleRate = findViewById(R.id.main_settings_sample_rate);
        settingsFftPoints = findViewById(R.id.main_settings_fft_points);
        settingsDesignFilter = findViewById(R.id.main_settings_design_filter);
        designFilterAppliedText = findViewById(R.id.main_settings_design_filter_applied);
        graphYLabel = findViewById(R.id.graph_y_label);
        graphXLabel = findViewById(R.id.graph_x_label);
        graphXLabel.setText("Frequency [Hz]");
        dfValueText = findViewById(R.id.main_df_value);
    }

    private void SetupChartGraph() {
        GraphController.GetInstance().SetContext(getApplicationContext());

//        Get reference of line chart data displayed at MainActivity screen.
//        Send reference to line chart data to GraphController
        GraphController.GetInstance().SetLineChart(lineChart);
//        Configure line chart like X axis min/max values, title, legends, etc.
        GraphController.GetInstance().ConfigureLineChart();

        CustomMarkerView mv = new CustomMarkerView(MainActivity.this, R.layout.custom_marker_view_layout);
        mv.setChartView(lineChart);
        lineChart.setMarker(mv);
    }

    private void initPlayPauseBtn() {
        playPauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (playPauseBtn.getText().equals("Play")) {
                    if (BluetoothController.GetInstance().GetCurrentBTState() == BluetoothController.btState.CONNECTED) {
                        playPauseBtn.setText("Pause");
                        BluetoothController.GetInstance().Write("Play");
                    } else {
                        BluetoothController.GetInstance().DisplayBluetoothDisconnectedText(getApplicationContext());
                    }
                } else {
                    PauseConnection();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        GraphController.GetInstance().ConfigureLineChart();
        settingsSampleRate.setText(String.valueOf(SettingsController.GetInstance().getSampleRate()));
        settingsFftPoints.setText(String.valueOf(SettingsController.GetInstance().getFftPointsNumber()));
        settingsDesignFilter.setText(FilterDesignController.GetInstance().getDesignFilterMode() ? "On" : "Off");
        dfValueText.setText("df (Hz) : " + FilterDesignController.GetInstance().getFrequencyResolution());
        if (settingsDesignFilter.getText().equals("On")) {
            String responseMethod = FilterDesignController.GetInstance().getResponseMethod();
            String responseTechnique = FilterDesignController.GetInstance().getResponseTechnique();
            String responseType = FilterDesignController.GetInstance().getResponseType();
            String fc1Value = FilterDesignController.GetInstance().getFc1Value();
            String fc2Value = FilterDesignController.GetInstance().getFc2Value();
            designFilterAppliedText.setText("   "+responseMethod + ",  " + responseTechnique + ",  " + responseType + ", fc1 = " + fc1Value + ", fc2 = " + fc2Value);
        } else {
            designFilterAppliedText.setText("");
        }

        graphYLabel.setText(" Amplitude [Volts]");
        GraphController.GetInstance().CreateNewChannelData(new ArrayList<Entry>());
        GraphController.GetInstance().DisplayAllChannelsData();
    }

    private void PauseConnection() {
        if (playPauseBtn.getText().equals("Pause")) {
            playPauseBtn.setText("Play");
            BluetoothController.GetInstance().Write("Pause");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
//            Open bluetooth screen
            case R.id.bluetoothConnectMenu:
                Intent serverIntent = new Intent(this, BluetoothActivity.class);
                startActivity(serverIntent);
                PauseConnection();
                return true;

//                Open SettingsController screen
            case R.id.analyzerSettingsMenu:
                Intent analyzerSettingsIntent = new Intent(this, AnalyzerSettingsActivity.class);
                startActivity(analyzerSettingsIntent);
                PauseConnection();
                return true;

//                Open Filter Design screen
            case R.id.filterDesignerMenu:
                Intent filterDesignIntent = new Intent(this, FilterDesignerActivity.class);
                startActivity(filterDesignIntent);
                PauseConnection();
                return true;
            case R.id.analyzerGuideMenu:
                Intent analyzerGuideIntent = new Intent(this, AnalyzerGuideActivity.class);
                startActivity(analyzerGuideIntent);
                PauseConnection();
                return true;
        }
        return false;
    }
}