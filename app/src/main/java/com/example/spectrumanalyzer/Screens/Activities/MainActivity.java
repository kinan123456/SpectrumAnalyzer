package com.example.spectrumanalyzer.Screens.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
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
    Button bluetoothBtn,trackInputLevelBtn,gainControlBtn,playPauseBtn;
    TextView graphXLabel, graphYLabel,hivfLabel,mBluetoothStatus,saModeLabel;
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
        initBluetoothBtn();
        initGainControlBtn();
        initInputVoltageTrackBtn();
//        Setup chart data graph
        SetupChartGraph();
    }

    private void initWidgets() {
        lineChart = findViewById(R.id.activity_main_linechart);
        playPauseBtn = findViewById(R.id.playPauseButton);
        bluetoothBtn = findViewById(R.id.bluetoothButton);
        gainControlBtn = findViewById(R.id.gainControl);
        trackInputLevelBtn = findViewById(R.id.voltageTrack);
        saModeLabel = findViewById(R.id.sa_mode);
        hivfLabel = findViewById(R.id.ctrl_mode);
        graphYLabel = findViewById(R.id.graph_y_label);
        graphXLabel = findViewById(R.id.graph_x_label);
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
            if (playPauseBtn.getText().equals("Play")) {
                ResumeConnection();
            } else {
                PauseConnection();
            }
        });
    }
    private void initGainControlBtn() {
        gainControlBtn.setOnClickListener(v -> {
            if (gainControlBtn.getText().equals("Enable Control")) {
                if (BluetoothController.GetInstance().GetCurrentBTState() == BluetoothController.btState.CONNECTED) {
                    gainControlBtn.setText("Disable Control");
                    BluetoothController.GetInstance().Write("Enable!");
                    String checkHIVF = checkInputVoltageLevel();
                    updateHIVFLabel(checkHIVF);
                } else {
                    BluetoothController.GetInstance().DisplayBluetoothDisconnectedText(getApplicationContext());
                }
            } else {
                gainControlBtn.setText("Enable Control");
                BluetoothController.GetInstance().Write("Disable!");                }
            });
    }
    private void initInputVoltageTrackBtn() {
        trackInputLevelBtn.setOnClickListener(v -> {
            String highInputVoltageFlag = checkInputVoltageLevel();
            updateHIVFLabel(highInputVoltageFlag);
        });
    }
    private static String checkInputVoltageLevel() {
            String checkHighInputVoltageLevel = GraphController.getHIVF();
            if (checkHighInputVoltageLevel == "True") {
                return "True";
            } else {
                return "False";
            }
    }
    private static void updateHIVFLabel(String newLabel) {
        GraphController.setHIVF(newLabel);
    }
    private void initBluetoothBtn() {
        bluetoothBtn.setOnClickListener(v -> {
            PauseConnection();
            Intent serverIntent = new Intent(MainActivity.this,BluetoothActivity.class);
            startActivity(serverIntent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mBTAdapter =  BluetoothAdapter.getDefaultAdapter(); // get a handle on the bluetooth radio
        mBluetoothStatus = findViewById(R.id.bluetooth_status);
        GraphController.GetInstance().ConfigureLineChart();
        if (!mBTAdapter.isEnabled()) {
            mBluetoothStatus.setText("Bluetooth is Off");
        } else {
            String deviceNameConnectedTo = BluetoothController.GetInstance().GetDeviceNameConnectedTo();
            if (deviceNameConnectedTo.isEmpty())
                mBluetoothStatus.setText("Disconnected");
            else
                mBluetoothStatus.setText("Connected to " + deviceNameConnectedTo);
        }
        GraphController.GetInstance().CreateNewChannelData(new ArrayList<Entry>());
        GraphController.GetInstance().DisplayAllChannelsData();
    }
    private void PauseConnection() {
        if (playPauseBtn.getText().equals("Pause")) {
            playPauseBtn.setText("Play");
            saModeLabel.setText("|Mode: Pause|");
            BluetoothController.GetInstance().Write("Pause!");
        }
    }
    private void ResumeConnection() {
            if (BluetoothController.GetInstance().GetCurrentBTState() == BluetoothController.btState.CONNECTED) {
                playPauseBtn.setText("Pause");
                saModeLabel.setText("|Mode: Play |");
                BluetoothController.GetInstance().Write("Play!");
            }
            else {
                    BluetoothController.GetInstance().DisplayBluetoothDisconnectedText(getApplicationContext());
                }
        }
    }
