package com.example.spectrumanalyzer.Screens.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;

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
    Button playPauseBtn;
    private LineChart lineChart;
    Button bluetoothBtn;
    TextView graphXLabel, graphYLabel;
    private BluetoothAdapter mBTAdapter;
    private TextView mBluetoothStatus;
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
//        Setup chart data graph
        SetupChartGraph();
    }

    private void initWidgets() {
        lineChart = findViewById(R.id.activity_main_linechart);
        playPauseBtn = findViewById(R.id.playPauseButton);
        bluetoothBtn = findViewById(R.id.bluetoothButton);
        graphYLabel = findViewById(R.id.graph_y_label);
        graphXLabel = findViewById(R.id.graph_x_label);
        graphXLabel.setText("Frequency [Hz]");
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
                        BluetoothController.GetInstance().Write("Play!");
                    } else {
                        BluetoothController.GetInstance().DisplayBluetoothDisconnectedText(getApplicationContext());
                    }
                } else {
                    PauseConnection();
                }
            }
        });
    }
    private void initBluetoothBtn() {
        bluetoothBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PauseConnection();
                Intent serverIntent = new Intent(MainActivity.this,BluetoothActivity.class);
                startActivity(serverIntent);
            }
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
        graphYLabel.setText(" Amplitude [Volts]");
        GraphController.GetInstance().CreateNewChannelData(new ArrayList<Entry>());
        GraphController.GetInstance().DisplayAllChannelsData();
    }
    private void PauseConnection() {
        if (playPauseBtn.getText().equals("Pause")) {
            playPauseBtn.setText("Play");
            BluetoothController.GetInstance().Write("Pause!");
        }
    }
}