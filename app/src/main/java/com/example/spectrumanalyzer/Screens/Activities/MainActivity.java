package com.example.spectrumanalyzer.Screens.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;

import android.widget.Button;
import android.widget.TextView;

import com.example.spectrumanalyzer.Controllers.Bluetooth.BluetoothController;
import com.example.spectrumanalyzer.Controllers.GraphController.GraphController;
import com.example.spectrumanalyzer.R;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    // Widgets
    private LineChart lineChart;
    private BluetoothAdapter mBTAdapter;
    Button bluetoothBtn,trackInputLevelBtn,gainControlBtn,playPauseBtn,resetBtn;
    static TextView graphXLabel, graphYLabel,hivfLabel,mBluetoothStatus,mSocketStatus,saModeLabel;
    String checkHIVF;
    /**
     * Called when the activity is first created.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

//        Initialize Play/Pause and Reset buttons handlers
        initWidgets();
        initResetBtn();
        initPlayPauseBtn();
        initBluetoothBtn();
        initGainControlBtn();
        initInputVoltageTrackBtn();
//        Setup chart data graph
        SetupChartGraph();
    }

    private void initWidgets() {
        lineChart = findViewById(R.id.activity_main_linechart);
        playPauseBtn = findViewById(R.id.playPauseButton);
        resetBtn = findViewById(R.id.resetButton);
        bluetoothBtn = findViewById(R.id.bluetoothButton);
        gainControlBtn = findViewById(R.id.gainControl);
        trackInputLevelBtn = findViewById(R.id.voltageTrack);
        saModeLabel = findViewById(R.id.sa_mode);
        hivfLabel = findViewById(R.id.hivf_mode);
        graphYLabel = findViewById(R.id.graph_y_label);
        graphXLabel = findViewById(R.id.graph_x_label);
        mSocketStatus = findViewById(R.id.socket_status);
        mBluetoothStatus = findViewById(R.id.bluetooth_status);
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
        playPauseBtn.setOnClickListener(v -> {
            if (BluetoothController.GetInstance().GetCurrentBTState() == BluetoothController.btState.CONNECTED) {
                if(playPauseBtn.getText() == "Play"){
                playMode();
                } else{
                    if(playPauseBtn.getText() == "Pause"){
                        pauseMode();
                    }
                }
            }
            else{
                BluetoothController.GetInstance().toastSystemMessage(getApplicationContext());
            }
        });
    }
    private void initResetBtn() {
        resetBtn.setOnClickListener(v -> {
            if (BluetoothController.GetInstance().GetCurrentBTState() == BluetoothController.btState.CONNECTED) {
                resetMode();
            }
            else{
                BluetoothController.GetInstance().toastSystemMessage(getApplicationContext());
            }
        });
    }
    private void initGainControlBtn() {
        gainControlBtn.setOnClickListener(v -> {
            if (BluetoothController.GetInstance().GetCurrentBTState() == BluetoothController.btState.CONNECTED) {
                if (gainControlBtn.getText().equals("Enable Control")) {
                    BluetoothController.GetInstance().Write("Enable!");
                    gainControlBtn.setText("Disable Control");
                    checkHIVF = checkInputVoltageLevel();
                    updateHIVFLabel(checkHIVF);
                } else {
                    gainControlBtn.setText("Enable Control");
                    BluetoothController.GetInstance().Write("Disable!");
                }
            } else {
                BluetoothController.GetInstance().toastSystemMessage(getApplicationContext());
        }
            });

    }
    private void initInputVoltageTrackBtn() {
            trackInputLevelBtn.setOnClickListener(v -> {
                if (BluetoothController.GetInstance().GetCurrentBTState() == BluetoothController.btState.CONNECTED) {
                    checkHIVF = checkInputVoltageLevel();
                    updateHIVFLabel(checkHIVF);
                } else {
                    BluetoothController.GetInstance().toastSystemMessage(getApplicationContext());
                }
            });
    }
    private static String checkInputVoltageLevel() {
            String checkHighInputVoltageLevel = GraphController.getHIVF();
            if (checkHighInputVoltageLevel == "True") {
                return "On";
            } else {
                return "Off";
            }
    }
    private static void updateHIVFLabel(String newLabel) {
        if(newLabel == "Off") {
            hivfLabel.setText("|HIVF: " + newLabel + "  |");
        }else{
            hivfLabel.setText("|HIVF:  " + newLabel + "  |");
        }
    }
    private void initBluetoothBtn() {
        bluetoothBtn.setOnClickListener(v -> {
            Intent bluetoothIntent = new Intent(MainActivity.this,BluetoothActivity.class);
            startActivity(bluetoothIntent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        GraphController.GetInstance().ConfigureLineChart();
        //BluetoothActivity.displayBluetoothSocketStatus();
        resetMode();
    }

    private void pauseMode() {
        if (BluetoothController.GetInstance().GetCurrentBTState() == BluetoothController.btState.CONNECTED) {
            BluetoothController.GetInstance().Write("Pause!");
            saModeLabel.setText("|Mode: Pause|");
            playPauseBtn.setText("Play");
        }
        else{
            saModeLabel.setText("|Mode: Idle |");
        }
    }
    private void playMode() {
            if (BluetoothController.GetInstance().GetCurrentBTState() == BluetoothController.btState.CONNECTED) {
                saModeLabel.setText("|Mode: Play |");
                BluetoothController.GetInstance().Write("Play!");
                } else {
                saModeLabel.setText("|Mode: Idle |");
                BluetoothController.GetInstance().toastSystemMessage(getApplicationContext());
                }
        }

    private void resetMode() {
        if (BluetoothController.GetInstance().GetCurrentBTState() == BluetoothController.btState.CONNECTED) {
            BluetoothController.GetInstance().Write("Reset!");
        }
        GraphController.GetInstance().CreateNewChannelData(new ArrayList<>());
        GraphController.GetInstance().DisplayAllChannelsData();        saModeLabel.setText("|Mode: Idle |");
        hivfLabel.setText("|HIVF: Idle |");
    }
}