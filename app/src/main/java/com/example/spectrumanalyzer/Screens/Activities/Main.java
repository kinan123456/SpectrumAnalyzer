package com.example.spectrumanalyzer.Screens.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.spectrumanalyzer.Components.Graph.Controller;
import com.example.spectrumanalyzer.R;

import com.github.mikephil.charting.charts.LineChart;

public class Main extends AppCompatActivity {

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
        Controller.getInstance().setContext(getApplicationContext());

//        Get reference of line chart data displayed at MainActivity screen.
//        Send reference to line chart data to GraphController
        Controller.getInstance().setLineChart(lineChart);
//        Configure line chart like X axis min/max values, title, legends, etc.
        Controller.getInstance().configLineChart();
        Marker mv = new Marker(Main.this, R.layout.custom_marker_view_layout);
        mv.setChartView(lineChart);
        lineChart.setMarker(mv);
    }
    private void setPlayBtnOnClickListener() {
        playBtn.setOnClickListener(v -> {
            if (com.example.spectrumanalyzer.Components.Bluetooth.Controller.getInstance().getCurrentBTState() == com.example.spectrumanalyzer.Components.Bluetooth.Controller.btState.CONNECTED) {
                if(saModeLabel.getText().equals(" Play")){
                    Toast.makeText(getBaseContext(),"System is already Playing!",Toast.LENGTH_SHORT).show();
                } else{
                    playMode();
                }
            }
            else{
                com.example.spectrumanalyzer.Components.Bluetooth.Controller.getInstance().toastSystemMessage(getApplicationContext());
            }
        });
    }
    private void setPauseBtnOnClickListener() {
        pauseBtn.setOnClickListener(v -> {
            if (com.example.spectrumanalyzer.Components.Bluetooth.Controller.getInstance().getCurrentBTState() == com.example.spectrumanalyzer.Components.Bluetooth.Controller.btState.CONNECTED) {
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
                com.example.spectrumanalyzer.Components.Bluetooth.Controller.getInstance().toastSystemMessage(getApplicationContext());
            }
        });
    }
    private void setResetBtnOnClickListener() {
        resetBtn.setOnClickListener(v -> {
            if (com.example.spectrumanalyzer.Components.Bluetooth.Controller.getInstance().getCurrentBTState() == com.example.spectrumanalyzer.Components.Bluetooth.Controller.btState.CONNECTED) {
                if(saModeLabel.getText().equals(" Play")){
                    Toast.makeText(getBaseContext(),"System should be Paused first!",Toast.LENGTH_SHORT).show();
                }else{
                    resetMode();
                }
            }
            else{
                com.example.spectrumanalyzer.Components.Bluetooth.Controller.getInstance().toastSystemMessage(getApplicationContext());
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
            if (com.example.spectrumanalyzer.Components.Bluetooth.Controller.getInstance().getCurrentBTState() == com.example.spectrumanalyzer.Components.Bluetooth.Controller.btState.CONNECTED) {
                if(saModeLabel.getText().equals(" Play")){
                    Toast.makeText(getBaseContext(),"System should be Paused First!",Toast.LENGTH_SHORT).show();
                }else{
                    if (gainControlBtn1.getText().equals("Ctrl Ch1 Gain")) {
                        gainControlBtn1.setText("Rst Gain Ctrl1");
                        gainControlLabel1.setText("On");
                        Controller.getInstance().updateGainControlAdjustment("ch1EnableGainControl");
                        gainControlLabel1.setTextColor(Color.rgb(0,200,0));
                        com.example.spectrumanalyzer.Components.Bluetooth.Controller.getInstance().writeMsg("Enable Gain Control1!");
                    } else {
                        gainControlBtn1.setText("Ctrl Ch1 Gain");
                        gainControlLabel1.setText("Off");
                        Controller.getInstance().updateGainControlAdjustment("ch1DisableGainControl");
                        gainControlLabel1.setTextColor(Color.rgb(200,0,0));
                        com.example.spectrumanalyzer.Components.Bluetooth.Controller.getInstance().writeMsg("Disable Gain Control1!");
                    }
                }
            } else {
                com.example.spectrumanalyzer.Components.Bluetooth.Controller.getInstance().toastSystemMessage(getApplicationContext());
        }
            });
        gainControlBtn2.setOnClickListener(v -> {
            if (com.example.spectrumanalyzer.Components.Bluetooth.Controller.getInstance().getCurrentBTState() == com.example.spectrumanalyzer.Components.Bluetooth.Controller.btState.CONNECTED) {
                if(saModeLabel.getText().equals(" Play")){
                    Toast.makeText(getBaseContext(),"System should be Paused First!",Toast.LENGTH_SHORT).show();
                }else{
                    if (gainControlBtn2.getText().equals("Ctrl Ch2 Gain")) {
                        gainControlBtn2.setText("Rst Gain Ctrl2");
                        Controller.getInstance().updateGainControlAdjustment("ch2EnableGainControl");
                        gainControlLabel2.setText("On");
                        gainControlLabel2.setTextColor(Color.rgb(0,200,0));
                        com.example.spectrumanalyzer.Components.Bluetooth.Controller.getInstance().writeMsg("Enable Gain Control2!");
                    } else {
                        gainControlBtn2.setText("Ctrl Ch2 Gain");
                        gainControlLabel2.setText("Off");
                        Controller.getInstance().updateGainControlAdjustment("ch2DisableGainControl");
                        gainControlLabel2.setTextColor(Color.rgb(200,0,0));
                        com.example.spectrumanalyzer.Components.Bluetooth.Controller.getInstance().writeMsg("Disable Gain Control2!");
                    }
                }
            } else {
                com.example.spectrumanalyzer.Components.Bluetooth.Controller.getInstance().toastSystemMessage(getApplicationContext());
            }
        });
    }
    private void initSpectrumGraph(){
        Controller.getInstance().resetDataGraph();
        Controller.getInstance().displayAllChannelsData();
    }
    private void setInputVoltageTrackOnClickListener() {
        trackInputLevelBtn1.setOnClickListener(v -> {
            if (com.example.spectrumanalyzer.Components.Bluetooth.Controller.getInstance().getCurrentBTState() == com.example.spectrumanalyzer.Components.Bluetooth.Controller.btState.CONNECTED) {
                if (saModeLabel.getText().equals(" Play")) {
                    Toast.makeText(getBaseContext(), "System should be Paused First!", Toast.LENGTH_SHORT).show();
                } else {
                    if (trackInputLevelBtn1.getText().equals("Trigger Tracker1")) {
                        trackInputLevelBtn1.setText("Track Ch1 Level");
                        com.example.spectrumanalyzer.Components.Bluetooth.Controller.getInstance().writeMsg("Trigger Tracker1!");

                    } else {
                        trackInputLevelBtn1.setText("Trigger Tracker1");
                        com.example.spectrumanalyzer.Components.Bluetooth.Controller.getInstance().writeMsg("Track Ch1!");
                        hivf_ch1 = Controller.getInstance().getHIVFLabel(1);
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
                com.example.spectrumanalyzer.Components.Bluetooth.Controller.getInstance().toastSystemMessage(getApplicationContext());
            }
        });
        trackInputLevelBtn2.setOnClickListener(v -> {
            if (com.example.spectrumanalyzer.Components.Bluetooth.Controller.getInstance().getCurrentBTState() == com.example.spectrumanalyzer.Components.Bluetooth.Controller.btState.CONNECTED) {
                if (saModeLabel.getText().equals(" Play")) {
                    Toast.makeText(getBaseContext(), "System should be Paused First!", Toast.LENGTH_SHORT).show();
                }else{
                    if (trackInputLevelBtn2.getText().equals("Trigger Tracker2")) {
                        trackInputLevelBtn2.setText("Track Ch2 Level");
                        com.example.spectrumanalyzer.Components.Bluetooth.Controller.getInstance().writeMsg("Trigger Tracker2!");

                    } else {
                        trackInputLevelBtn2.setText("Trigger Tracker2");
                        com.example.spectrumanalyzer.Components.Bluetooth.Controller.getInstance().writeMsg("Track Ch2!");
                        hivf_ch2 = Controller.getInstance().getHIVFLabel(2);
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
                    com.example.spectrumanalyzer.Components.Bluetooth.Controller.getInstance().toastSystemMessage(getApplicationContext());
                }
        });
    }
    private void initSystemModeLabel(){
        saModeLabel.setText(" Idle");
        saModeLabel.setTextColor(Color.rgb(0,0,0));
    }
    private void initBluetoothSocketStatusLabel(){
        if (com.example.spectrumanalyzer.Components.Bluetooth.Controller.getInstance().getCurrentBTState() == com.example.spectrumanalyzer.Components.Bluetooth.Controller.btState.CONNECTED) {
            String deviceNameConnectedTo = com.example.spectrumanalyzer.Components.Bluetooth.Controller.getInstance().getDeviceNameConnectedTo();
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
            Intent bluetoothIntent = new Intent(Main.this, Bluetooth.class);
            startActivity(bluetoothIntent);
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        Controller.getInstance().configLineChart();
        resetMode();
    }

    private void pauseMode() {
            com.example.spectrumanalyzer.Components.Bluetooth.Controller.getInstance().writeMsg("Pause!");
            saModeLabel.setTextColor(Color.rgb(200,0,0));
            saModeLabel.setText(" Pause");
    }

    private void playMode() {
            com.example.spectrumanalyzer.Components.Bluetooth.Controller.getInstance().writeMsg("Play!");
            saModeLabel.setTextColor(Color.rgb(0,200,0));
            saModeLabel.setText(" Play");
    }

    private void resetMode() {
        if (com.example.spectrumanalyzer.Components.Bluetooth.Controller.getInstance().getCurrentBTState() == com.example.spectrumanalyzer.Components.Bluetooth.Controller.btState.CONNECTED) {
            com.example.spectrumanalyzer.Components.Bluetooth.Controller.getInstance().writeMsg("Reset!");
        }
        initBluetoothSocketStatusLabel();
        resetHighVoltageTrackFeature();
        resetGainControlFeature();
        initActivityLabels();
        initSpectrumGraph();
    }
}