package com.example.spectrumanalyzer.Screens.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.ColorSpace;
import android.os.Bundle;

import android.os.SystemClock;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.spectrumanalyzer.Controllers.Bluetooth.BluetoothController;
import com.example.spectrumanalyzer.Controllers.Bluetooth.BluetoothSocketThread;
import com.example.spectrumanalyzer.Controllers.GraphController.GraphController;
import com.example.spectrumanalyzer.R;

import com.github.mikephil.charting.charts.LineChart;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    // Widgets
    private LineChart lineChart;
    Button bluetoothBtn,trackInputLevelBtn1,trackInputLevelBtn2,gainControlBtn1,gainControlBtn2,playBtn,pauseBtn,resetBtn;
    static TextView graphXLabel, graphYLabel,hivfLabel1,hivfLabel2,saModeLabel,gainControlLabel1,gainControlLabel2,btSocketStatusLabel;
    String hivf_ch1,hivf_ch2;    /**
     * Called when the activity is first created.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Initialize Play/Pause and Reset buttons handlers
        initActivityWidgets();
        initActivityLabels();
        setBtnsOnClickListener();
        SetupSpectrumChartGraph();
    }
    private void initActivityLabels() {
        initSystemModeLabel();
        initBluetoothSocketStatusLabel();
        resetGainControlFeature();
        resetHighVoltageTrackFeature();
    }
    private void setBtnsOnClickListener() {
        setInputVoltageTrackOnClickListener();
        setGainControlOnClickListener();
        setBluetoothOnClickListener();
        setPlayBtnOnClickListener();
        setResetBtnOnClickListener();
        setPauseBtnOnClickListener();
    }
    private void initActivityWidgets() {
        playBtn = findViewById(R.id.play_btn);
        resetBtn = findViewById(R.id.reset_btn);
        pauseBtn = findViewById(R.id.pause_btn);
        bluetoothBtn = findViewById(R.id.bluetooth_btn);
        gainControlBtn1 = findViewById(R.id.gain_control_btn_1);
        gainControlBtn2 = findViewById(R.id.gain_control_btn_2);
        trackInputLevelBtn1= findViewById(R.id.voltage_track_btn_1);
        trackInputLevelBtn2 = findViewById(R.id.voltage_track_btn_2);

        saModeLabel = findViewById(R.id.sys_mode);
        hivfLabel1 = findViewById(R.id.hivf_mode1);
        hivfLabel2 = findViewById(R.id.hivf_mode2);
        btSocketStatusLabel = findViewById(R.id.socket_status);
        gainControlLabel1 = findViewById(R.id.gain_control_mode_1);
        gainControlLabel2 = findViewById(R.id.gain_control_mode_2);

        graphYLabel = findViewById(R.id.graph_y_label);
        graphXLabel = findViewById(R.id.graph_x_label);
        lineChart = findViewById(R.id.activity_main_linechart);

    }

    private void SetupSpectrumChartGraph() {
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
    private void setPlayBtnOnClickListener() {
        playBtn.setOnClickListener(v -> {
            if (BluetoothController.GetInstance().GetCurrentBTState() == BluetoothController.btState.CONNECTED) {
                if(saModeLabel.getText().equals(" Play")){
                    Toast.makeText(getBaseContext(),"System is already Playing!",Toast.LENGTH_SHORT).show();
                } else{
                    playMode();
                }
            }
            else{
                BluetoothController.GetInstance().toastSystemMessage(getApplicationContext());
            }
        });
    }
    private void setPauseBtnOnClickListener() {
        pauseBtn.setOnClickListener(v -> {
            if (BluetoothController.GetInstance().GetCurrentBTState() == BluetoothController.btState.CONNECTED) {
                if(saModeLabel.getText().equals(" Pause")){
                    Toast.makeText(getBaseContext(),"System is already Paused!",Toast.LENGTH_SHORT).show();
                } else{
                    if(!saModeLabel.getText().equals(" Play")){
                        Toast.makeText(getBaseContext(),"System isn't Playing!",Toast.LENGTH_SHORT).show();
                    }else{
                        pauseMode();
                    }
                }
            }
            else{
                BluetoothController.GetInstance().toastSystemMessage(getApplicationContext());
            }
        });
    }
    private void setResetBtnOnClickListener() {
        resetBtn.setOnClickListener(v -> {
            if (BluetoothController.GetInstance().GetCurrentBTState() == BluetoothController.btState.CONNECTED) {
                if(saModeLabel.getText().equals(" Play")){
                    Toast.makeText(getBaseContext(),"System should be Paused first!",Toast.LENGTH_SHORT).show();
                }else{
                    resetMode();
                }
            }
            else{
                BluetoothController.GetInstance().toastSystemMessage(getApplicationContext());
            }
        });
    }
    private void resetGainControlFeature() {
        gainControlLabel1.setText(" Idle");
        gainControlLabel2.setText(" Idle");
        gainControlBtn1.setText("Ctrl Ch1 Gain");
        gainControlBtn2.setText("Ctrl Ch2 Gain");
        gainControlLabel1.setTextColor(Color.rgb(0,0,0));
        gainControlLabel2.setTextColor(Color.rgb(0,0,0));
    }
    private void resetHighVoltageTrackFeature() {
        hivfLabel1.setText(" Idle");
        hivfLabel2.setText(" Idle");
        hivfLabel1.setTextColor(Color.rgb(0,0,0));
        hivfLabel2.setTextColor(Color.rgb(0,0,0));
        trackInputLevelBtn1.setText("Trigger Tracker1");
        trackInputLevelBtn2.setText("Trigger Tracker2");

    }
    private void setGainControlOnClickListener() {
        gainControlBtn1.setOnClickListener(v -> {
            if (BluetoothController.GetInstance().GetCurrentBTState() == BluetoothController.btState.CONNECTED) {
                if(saModeLabel.getText().equals(" Play")){
                    Toast.makeText(getBaseContext(),"System should be Paused First!",Toast.LENGTH_SHORT).show();
                }else{
                    if (gainControlBtn1.getText().equals("Ctrl Ch1 Gain")) {
                        gainControlBtn1.setText("Disable Ch1 Ctrl");
                        gainControlLabel1.setText("On");
                        GraphController.GetInstance().updateGainControlAdjustment("ch1EnableGainControl");
                        gainControlLabel1.setTextColor(Color.rgb(0,200,0));
                        BluetoothController.GetInstance().Write("Enable1!");
                    } else {
                        gainControlBtn1.setText("Ctrl Ch1 Gain");
                        gainControlLabel1.setText("Off");
                        GraphController.GetInstance().updateGainControlAdjustment("ch1DisableGainControl");
                        gainControlLabel1.setTextColor(Color.rgb(200,0,0));
                        BluetoothController.GetInstance().Write("Disable1!");
                    }
                }
            } else {
                BluetoothController.GetInstance().toastSystemMessage(getApplicationContext());
        }
            });
        gainControlBtn2.setOnClickListener(v -> {
            if (BluetoothController.GetInstance().GetCurrentBTState() == BluetoothController.btState.CONNECTED) {
                if(saModeLabel.getText().equals(" Play")){
                    Toast.makeText(getBaseContext(),"System should be Paused First!",Toast.LENGTH_SHORT).show();
                }else{
                    if (gainControlBtn2.getText().equals("Ctrl Ch2 Gain")) {
                        gainControlBtn2.setText("Disable Ch2 Ctrl");
                        GraphController.GetInstance().updateGainControlAdjustment("ch2EnableGainControl");
                        gainControlLabel2.setText("On");
                        gainControlLabel2.setTextColor(Color.rgb(0,200,0));
                        BluetoothController.GetInstance().Write("Enable2!");
                    } else {
                        gainControlBtn2.setText("Ctrl Ch2 Gain");
                        gainControlLabel2.setText("Off");
                        GraphController.GetInstance().updateGainControlAdjustment("ch2DisableGainControl");
                        gainControlLabel2.setTextColor(Color.rgb(200,0,0));
                        BluetoothController.GetInstance().Write("Disable2!");
                    }
                }
            } else {
                BluetoothController.GetInstance().toastSystemMessage(getApplicationContext());
            }
        });
    }
    private void initSpectrumGraph(){
        GraphController.GetInstance().DisplayAllChannelsData();
    }
    private void setInputVoltageTrackOnClickListener() {
        trackInputLevelBtn1.setOnClickListener(v -> {
            if (BluetoothController.GetInstance().GetCurrentBTState() == BluetoothController.btState.CONNECTED) {
                if (saModeLabel.getText().equals(" Play")) {
                    Toast.makeText(getBaseContext(), "System should be Paused First!", Toast.LENGTH_SHORT).show();
                } else {
                    if (trackInputLevelBtn1.getText().equals("Trigger Tracker1")) {
                        BluetoothController.GetInstance().Write("1trigger!");
                        trackInputLevelBtn1.setText("Track Ch1 Level");

                    } else {
                        BluetoothController.GetInstance().Write("1track!");
                        trackInputLevelBtn1.setText("Trigger Tracker1");
                        SystemClock.sleep(250);
                        hivf_ch1 = GraphController.GetInstance().getHIVFLabel(1);
                        if (hivf_ch1 == "High") {
                            hivfLabel1.setText(" Exceeded");
                            hivfLabel1.setTextColor(Color.rgb(200, 0, 0));

                        } else {
                            hivfLabel1.setText(" Fitted");
                            hivfLabel1.setTextColor(Color.rgb(0, 200, 0));
                        }
                    }
                }
            } else {
                BluetoothController.GetInstance().toastSystemMessage(getApplicationContext());
            }
        });
        trackInputLevelBtn2.setOnClickListener(v -> {
            if (BluetoothController.GetInstance().GetCurrentBTState() == BluetoothController.btState.CONNECTED) {
                if (saModeLabel.getText().equals(" Play")) {
                    Toast.makeText(getBaseContext(), "System should be Paused First!", Toast.LENGTH_SHORT).show();
                }else{
                    if (trackInputLevelBtn2.getText().equals("Trigger Tracker2")) {
                        BluetoothController.GetInstance().Write("2trigger!");
                        trackInputLevelBtn2.setText("Track Ch2 Level");

                    } else {
                        BluetoothController.GetInstance().Write("2track!");
                        trackInputLevelBtn2.setText("Trigger Tracker2");
                        SystemClock.sleep(1000);
                        hivf_ch2 = GraphController.GetInstance().getHIVFLabel(2);
                        if (hivf_ch2 == "High") {
                            hivfLabel2.setText(" Exceeded");
                            hivfLabel2.setTextColor(Color.rgb(200, 0, 0));
                        } else {
                            hivfLabel2.setText(" Fitted");
                            hivfLabel2.setTextColor(Color.rgb(0, 200, 0));
                        }
                    }
                }
            }else{
                    BluetoothController.GetInstance().toastSystemMessage(getApplicationContext());
                }
        });
    }
    private void initSystemModeLabel(){
        saModeLabel.setText(" Idle");
        saModeLabel.setTextColor(Color.rgb(0,0,0));
    }
    private void initBluetoothSocketStatusLabel(){
        if (BluetoothController.GetInstance().GetCurrentBTState() == BluetoothController.btState.CONNECTED) {
            String deviceNameConnectedTo = BluetoothController.GetInstance().GetDeviceNameConnectedTo();
            if (deviceNameConnectedTo.isEmpty()) {
                btSocketStatusLabel.setText(" Disconnected");
                btSocketStatusLabel.setTextColor(Color.rgb(200,0,0));
            }
            else {
                btSocketStatusLabel.setText(" Connected to " + deviceNameConnectedTo.trim());
                btSocketStatusLabel.setTextColor(Color.rgb(0,200,0));
            }
        }else{
            btSocketStatusLabel.setText(" Disconnected");
            btSocketStatusLabel.setTextColor(Color.rgb(200,0,0));
        }
    }
    private void setBluetoothOnClickListener() {
        bluetoothBtn.setOnClickListener(v -> {
            Intent bluetoothIntent = new Intent(MainActivity.this,BluetoothActivity.class);
            startActivity(bluetoothIntent);
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        GraphController.GetInstance().ConfigureLineChart();
        resetMode();
    }

    private void pauseMode() {
            BluetoothController.GetInstance().Write("Pause!");
            saModeLabel.setTextColor(Color.rgb(200,0,0));
            saModeLabel.setText(" Pause");
    }

    private void playMode() {
            BluetoothController.GetInstance().Write("Play!");
            saModeLabel.setTextColor(Color.rgb(0,200,0));
            saModeLabel.setText(" Play");
    }

    private void resetMode() {
        if (BluetoothController.GetInstance().GetCurrentBTState() == BluetoothController.btState.CONNECTED) {
            BluetoothController.GetInstance().Write("Reset!");
        }
        initBluetoothSocketStatusLabel();
        resetHighVoltageTrackFeature();
        resetGainControlFeature();
        initActivityLabels();
        initSpectrumGraph();
    }
}