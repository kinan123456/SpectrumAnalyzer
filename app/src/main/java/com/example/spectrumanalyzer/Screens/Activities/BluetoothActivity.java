package com.example.spectrumanalyzer.Screens.Activities;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.spectrumanalyzer.Controllers.Bluetooth.BluetoothController;
import com.example.spectrumanalyzer.Controllers.GraphController.GraphController;
import com.example.spectrumanalyzer.R;
import com.github.mikephil.charting.data.Entry;

import java.io.IOError;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;

public class BluetoothActivity extends AppCompatActivity {

    // defines for identifying shared types between calling functions
    // REQUEST_ENABLE_BT: used to identify adding bluetooth names
    // MESSAGE_READ: used in bluetooth handler to identify message update
    // CONNECTING_STATUS: used in bluetooth handler to identify message status
    public final static int REQUEST_ENABLE_BT = 1, MESSAGE_READ = 2, CONNECTING_STATUS = 3;

    // GUI Components
    private Button mTurnOnOffBtn, mListPairedDevicesBtn;
    private ListView mDevicesListView;
    public static TextView mSocketStatus, mBluetoothStatus;
    private BroadcastReceiver mReceiver;
    private ArrayAdapter<String> mBTArrayAdapter;
    private Set<BluetoothDevice> mPairedDevices;
    private Handler mHandler; // Our main handler that will receive callback notifications
    private BluetoothSocket mBTSocket = null; // bi-directional client-to-client data path
    private BluetoothAdapter mBTAdapter;
    private int ch = 1;
    String readMessage;
    String[] serverDataBuffer;
    ArrayList<Entry> EntryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);
        checkLocationPermission();
        initActivityWidgets();
        initTurnOnOffButton();
        initAdapter();
        initHandler();
        initReceiver();
    }

    // Enter here after user selects "yes" or "no" to enabling bluetooth
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent Data) {
        // Check which request we're responding to
        super.onActivityResult(requestCode, resultCode, Data);
        if (requestCode == REQUEST_ENABLE_BT) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                bluetoothOn();
            } else {
                try {
                    bluetoothOff();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void initTurnOnOffButton() {
        if (mBluetoothStatus.getText().equals(" Enabled")) {
            mTurnOnOffBtn.setText("Turn Bluetooth Off");
        } else {
            mTurnOnOffBtn.setText("Turn Bluetooth On");
        }
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Ask for location permission if not already allowed
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }
    }

    private void initReceiver() {
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    //Device found
                    Toast.makeText(getBaseContext(), "Device found", Toast.LENGTH_SHORT).show();
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    mBTArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                } else if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
                    //Device is now connected
                    Toast.makeText(getBaseContext(), "Device is now connected", Toast.LENGTH_SHORT).show();

                } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                    //Done searching
                    Toast.makeText(getBaseContext(), "Done searching", Toast.LENGTH_SHORT).show();

                } else if (BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED.equals(action)) {
                    //Device is about to disconnect
                    Toast.makeText(getBaseContext(), "Device is about to disconnect", Toast.LENGTH_SHORT).show();

                } else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                    //Device has disconnected
                    Toast.makeText(getBaseContext(), "Device has disconnected", Toast.LENGTH_SHORT).show();

                }
            }
        };
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mBTAdapter = BluetoothAdapter.getDefaultAdapter(); // get a handle on the bluetooth radio
        if (!mBTAdapter.isEnabled()) {
            mBluetoothStatus.setText(" Disabled");
            mSocketStatus.setText(" Disconnected");
            mTurnOnOffBtn.setText("Turn Bluetooth On");
            mBluetoothStatus.setTextColor(Color.rgb(200, 0, 0));
            mSocketStatus.setTextColor(Color.rgb(200, 0, 0));
        } else {
            mBluetoothStatus.setText(" Enabled");
            mBluetoothStatus.setTextColor(Color.rgb(0, 200, 0));
            mTurnOnOffBtn.setText("Turn Bluetooth Off");
            if (BluetoothController.GetInstance().GetCurrentBTState() == BluetoothController.btState.DISCONNECTED) {
                mSocketStatus.setText(" Disconnected");
                mSocketStatus.setTextColor(Color.rgb(200, 0, 0));
            } else {
                String deviceNameConnectedTo = BluetoothController.GetInstance().GetDeviceNameConnectedTo();
                mSocketStatus.setText(" Connected to " + deviceNameConnectedTo.trim());
                mSocketStatus.setTextColor(Color.rgb(0, 200, 0));
            }

        }
    }

    private void setEnableDisableOnClickListener() {
        mTurnOnOffBtn.setOnClickListener(v -> {
            if (mTurnOnOffBtn.getText().equals("Turn Bluetooth On")) {
                bluetoothOn();
            } else {
                try {
                    bluetoothOff();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void initBluetoothArrayAdapter() {
        mBTArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        if (mBTArrayAdapter == null) {
            // Device does not support Bluetooth
            mBluetoothStatus.setText(" Not Supported");
            mBluetoothStatus.setTextColor(Color.rgb(255, 0, 0));
        } else {
            setEnableDisableOnClickListener();
            setDeviceListViewOnClickListener();
        }
    }

    private void setDeviceListViewOnClickListener() {
        mDevicesListView.setAdapter(mBTArrayAdapter); // assign model to view
        AdapterView.OnItemClickListener mDeviceClickListener = (parent, view, position, id) -> {
            if (!mBTAdapter.isEnabled()) {
                Toast.makeText(getBaseContext(), "Bluetooth is Off", Toast.LENGTH_SHORT).show();
                mBluetoothStatus.setText(" Disabled");
                mBluetoothStatus.setTextColor(Color.rgb(200, 0, 0));
                mSocketStatus.setText(" Idle");
                mSocketStatus.setTextColor(Color.rgb(0, 0, 0));
            }
            new Thread() {
                @Override
                public void run() {
                    String info = ((TextView) view).getText().toString();
                    // Get the device MAC address, which is the last 17 chars in the View
                    final String address = info.substring(info.length() - 17);
                    // Spawn a new thread to avoid blocking the GUI running thread
                    BluetoothDevice device = mBTAdapter.getRemoteDevice(address);
                    try {
                        BluetoothActivity.this.runOnUiThread(() -> {
                            mSocketStatus.setText(" Creating...");
                            mSocketStatus.setTextColor(Color.rgb(0, 255, 0));
                        });
                        createBluetoothSocket(device);
                    } catch (
                            IOException e) {
                        Log.i("myTag", "failed to create socket");
                        BluetoothActivity.this.runOnUiThread(() -> {
                            mSocketStatus.setText(" Creation Failed");
                            mSocketStatus.setTextColor(Color.rgb(200, 0, 0));
                        });
                    }
                    // Establish the Bluetooth socket connection.
                    mHandler.obtainMessage(BluetoothActivity.CONNECTING_STATUS, -1, -1).sendToTarget();
                    try {
                        Log.i("myTag", "socket connection");
                        BluetoothActivity.this.runOnUiThread(() -> {
                            mSocketStatus.setText(" Connecting...");
                            mSocketStatus.setTextColor(Color.rgb(0, 255, 0));
                        });
                        BluetoothController.GetInstance().SetCurrentBTState(BluetoothController.btState.CONNECTING);
                        mBTSocket.connect();
                    } catch (IOException ioException) {
                        Log.i("myTag", "failed to connect socket");
                        BluetoothActivity.this.runOnUiThread(() -> {
                            mSocketStatus.setText(" Disconnecting...");
                            mSocketStatus.setTextColor(Color.rgb(255, 0, 0));
                        });
                        // Set the bluetooth stateMachine to connected
                        try {
                            mBTSocket.close();
                            BluetoothActivity.this.runOnUiThread(() -> {
                                mSocketStatus.setText(" Closed");
                                mSocketStatus.setTextColor(Color.rgb(200, 0, 0));
                                BluetoothController.GetInstance().SetDeviceNameConnectedTo("");
                            });
                            Log.i("myTag", "socket closed connection");
                            BluetoothController.GetInstance().SetCurrentBTState(BluetoothController.btState.DISCONNECTED);
                        } catch (IOException e) {
                            e.printStackTrace();
                            BluetoothActivity.this.runOnUiThread(() -> {
                                mSocketStatus.setText(" Disconnected");
                                mSocketStatus.setTextColor(Color.rgb(200, 0, 0));
                            });
                            BluetoothController.GetInstance().SetCurrentBTState(BluetoothController.btState.UNKNOWN);
                        }
                        BluetoothActivity.this.runOnUiThread(() -> {
                            mSocketStatus.setText(" Connection Failed");
                            mSocketStatus.setTextColor(Color.rgb(255, 0, 0));
                        });
                    }
                    if (mBTSocket != null && mBTSocket.isConnected() == true) {
                        BluetoothController.GetInstance().SetCurrentBTState(BluetoothController.btState.CONNECTED);
                        final String name = info.substring(0, info.length() - 17);
                        BluetoothController.GetInstance().SetDeviceNameConnectedTo(name);
                        BluetoothActivity.this.runOnUiThread(() -> {
                            mSocketStatus.setText(" Connected to " + name.trim());
                            mSocketStatus.setTextColor(Color.rgb(0, 200, 0));
                        });
                        BluetoothController.GetInstance().initBluetoothSocketThread(mBTSocket, mHandler);
                    } else {
                        BluetoothActivity.this.runOnUiThread(() -> {
                            mSocketStatus.setText(" Connection Failed");
                            mSocketStatus.setTextColor(Color.rgb(255, 0, 0));
                        });
                    }
                }
            }.start();
        };
        mDevicesListView.setOnItemClickListener(mDeviceClickListener);
        mListPairedDevicesBtn.setOnClickListener(v -> {
            if (mListPairedDevicesBtn.getText().equals("List Paired Devices")) {
                listPairedDevices();
            } else {
                unlistPairedDevices();
            }
        });
    }

    private void initAdapter() {
        mBTAdapter = BluetoothAdapter.getDefaultAdapter(); // get a handle on the bluetooth radio
    }

    private void initHandler() {
        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == MESSAGE_READ) {
                    try {
                        readMessage = new String((byte[]) msg.obj, "UTF-8");
                        if (!readMessage.isEmpty()) {
                            serverDataBuffer = readMessage.split(",");
                            EntryList = GraphController.GetInstance().ConvertInputArrayToGraphArray(serverDataBuffer, ch);
                            GraphController.GetInstance().CreateNewChannelData(EntryList, ch);
                            if (ch == 2) {
                                ch = 1;
                                GraphController.GetInstance().DisplayAllChannelsData();
                            } else {
                                ch = 2;
                            }
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
    }

    private void initActivityWidgets() {
        mTurnOnOffBtn = findViewById(R.id.enableDisableBtn);
        mListPairedDevicesBtn = findViewById(R.id.paired_btn);
        mBluetoothStatus = findViewById(R.id.bluetooth_status);
        mSocketStatus = findViewById(R.id.socket_status);
        mDevicesListView = findViewById(R.id.devices_list_view);
        initBluetoothArrayAdapter();
    }

    private void bluetoothOn() {
        if (!mBTAdapter.isEnabled()) {
            mBluetoothStatus.setText(" Enabling...");
            mBluetoothStatus.setTextColor(Color.rgb(0, 255, 0));
            mTurnOnOffBtn.setText("Turn Bluetooth Off");
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            mBluetoothStatus.setTextColor(Color.rgb(0, 200, 0));
            mTurnOnOffBtn.setText("Turn Bluetooth Off");
            mBluetoothStatus.setText("Enabled");
        }
    }

    private void listPairedDevices() {
        unlistPairedDevices();
        mPairedDevices = mBTAdapter.getBondedDevices();
        if (mBTAdapter.isEnabled()) {
            // put it's one to the adapter
            mListPairedDevicesBtn.setText("UnList Paired Devices");
            for (BluetoothDevice device : mPairedDevices)
                mBTArrayAdapter.add(device.getName() + "\n" + device.getAddress());

        } else
            Toast.makeText(getApplicationContext(), "Bluetooth is Off", Toast.LENGTH_SHORT).show();
    }

    private void unlistPairedDevices() {
        mBTArrayAdapter.clear();
        mListPairedDevicesBtn.setText("List Paired Devices");
    }

    private void bluetoothOff() throws IOException {
        if (mBTSocket != null && mBTSocket.isConnected() == true) {
            mBTSocket.close();
        }
        mBTAdapter.disable(); // turn off
        mTurnOnOffBtn.setText("Turn Bluetooth On");
        mBluetoothStatus.setText(" Disabled");
        mBluetoothStatus.setTextColor(Color.rgb(200, 0, 0));
        mSocketStatus.setText(" Idle");
        mSocketStatus.setTextColor(Color.rgb(0, 0, 0));
        BluetoothController.GetInstance().SetDeviceNameConnectedTo("");
        BluetoothController.GetInstance().SetCurrentBTState(BluetoothController.btState.DISCONNECTED);
        unlistPairedDevices();
    }

    private void createBluetoothSocket(BluetoothDevice device) throws IOException {
        BluetoothSocket tmp = null;
        try {
            final Method m = device.getClass().getMethod("createRfcommSocket", new Class[]{int.class});
            tmp = (BluetoothSocket) m.invoke(device, 1);
            BluetoothActivity.this.runOnUiThread(() -> {
                mSocketStatus.setText(" Creating RFCOMM. Socket");
                mSocketStatus.setTextColor(Color.rgb(0, 255, 0));
            });
        } catch (Exception e) {
            BluetoothActivity.this.runOnUiThread(() -> {
                mSocketStatus.setText(" Connection Failed");
                mSocketStatus.setTextColor(Color.rgb(255, 0, 0));
            });
        }
        mBTSocket = tmp;
    }
}
