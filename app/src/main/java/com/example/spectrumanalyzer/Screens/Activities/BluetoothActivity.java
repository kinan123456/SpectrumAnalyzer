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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;

public class BluetoothActivity extends AppCompatActivity {

    private final String TAG = BluetoothActivity.class.getSimpleName();

    // defines for identifying shared types between calling functions
    private final static int REQUEST_ENABLE_BT = 1; // used to identify adding bluetooth names
    public final static int MESSAGE_READ = 2; // used in bluetooth handler to identify message update
    private final static int CONNECTING_STATUS = 3; // used in bluetooth handler to identify message status

    // GUI Components
    private Button mTurnOnOffBtn, mListPairedDevicesBtn;
    private ListView mDevicesListView;
    private TextView mBluetoothStatus;

    private BroadcastReceiver mReceiver;
    private BluetoothAdapter mBTAdapter;
    private ArrayAdapter<String> mBTArrayAdapter;
    private Set<BluetoothDevice> mPairedDevices;

    private Handler mHandler; // Our main handler that will receive callback notifications
    private BluetoothSocket mBTSocket = null; // bi-directional client-to-client data path

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        mTurnOnOffBtn = findViewById(R.id.turnOnOff);
        mListPairedDevicesBtn = findViewById(R.id.paired_btn);

        mBTArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        mBTAdapter = BluetoothAdapter.getDefaultAdapter(); // get a handle on the bluetooth radio

        mDevicesListView = findViewById(R.id.devices_list_view);
        mDevicesListView.setAdapter(mBTArrayAdapter); // assign model to view
        mDevicesListView.setOnItemClickListener(mDeviceClickListener);

        mBluetoothStatus = findViewById(R.id.bluetooth_status);

        // Ask for location permission if not already allowed
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);


        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == MESSAGE_READ) {
                    String readMessage;
                    String[] serverDataBuffer;
                    ArrayList<Entry> EntryList1, EntryList2;

                    try {
                        readMessage = new String((byte[]) msg.obj, "UTF-8");
                        if (!readMessage.isEmpty()) {
                            serverDataBuffer = readMessage.split(" ");
                            EntryList1 = GraphController.ConvertInputArrayToGraphArray(serverDataBuffer);
                            GraphController.GetInstance().CreateNewChannelData(EntryList1);
                            GraphController.GetInstance().DisplayAllChannelsData();
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        if (mBTArrayAdapter == null) {
            // Device does not support Bluetooth
            Toast.makeText(getApplicationContext(), "Bluetooth device not found!", Toast.LENGTH_SHORT).show();
            mBluetoothStatus.setText("Bluetooth not supported");
        } else {
            mTurnOnOffBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTurnOnOffBtn.getText().equals("Turn Bluetooth On")) {
                        mTurnOnOffBtn.setText("Turn Bluetooth OFF");
                        bluetoothOn();
                    } else {
                        mTurnOnOffBtn.setText("Turn Bluetooth ON");
                        bluetoothOff();
                    }
                }
            });

            mListPairedDevicesBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listPairedDevices();
                }
            });
        }

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
        registerReceiver(mReceiver,filter);
    }

    private void bluetoothOn() {
        if (!mBTAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);

        } else {
            Toast.makeText(getApplicationContext(), "Bluetooth is already on", Toast.LENGTH_SHORT).show();
        }
    }

    // Enter here after user selects "yes" or "no" to enabling radio
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent Data) {
        // Check which request we're responding to
        super.onActivityResult(requestCode, resultCode, Data);
        if (requestCode == REQUEST_ENABLE_BT) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                mBluetoothStatus.setText("Bluetooth is ON");
            } else {
                mBluetoothStatus.setText("Bluetooth is OFF");
            }
        }
    }

    private void bluetoothOff() {
        mBTAdapter.disable(); // turn off
        mBluetoothStatus.setText("Bluetooth is OFF");
    }

    private void listPairedDevices() {
        mBTArrayAdapter.clear();
        mPairedDevices = mBTAdapter.getBondedDevices();
        if (mBTAdapter.isEnabled()) {
            // put it's one to the adapter
            for (BluetoothDevice device : mPairedDevices)
                mBTArrayAdapter.add(device.getName() + "\n" + device.getAddress());
        } else
            Toast.makeText(getApplicationContext(), "Bluetooth is OFF", Toast.LENGTH_SHORT).show();
    }

    private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            if (!mBTAdapter.isEnabled()) {
                Toast.makeText(getBaseContext(), "Bluetooth is OFF", Toast.LENGTH_SHORT).show();
                return;
            }

            mBluetoothStatus.setText("Connecting...");

            // Get the device MAC address, which is the last 17 chars in the View
            String info = ((TextView) view).getText().toString();
            final String address = info.substring(info.length() - 17);
            final String name = info.substring(0, info.length() - 17);

            // Spawn a new thread to avoid blocking the GUI one
            new Thread() {
                @Override
                public void run() {
                    boolean fail = false;

                    BluetoothDevice device = mBTAdapter.getRemoteDevice(address);

                    try {
                        createBluetoothSocket(device);
                    } catch (IOException e) {
                        fail = true;
                        Toast.makeText(getBaseContext(), "Socket creation failed", Toast.LENGTH_SHORT).show();
                    }
                    // Establish the Bluetooth socket connection.
                    try {
                        mBTSocket.connect();
                        // Set the bluetooth stateMachine to connected
                    } catch (IOException e) {
                        try {
                            fail = true;
                            mBTSocket.close();
                            mHandler.obtainMessage(CONNECTING_STATUS, -1, -1)
                                    .sendToTarget();
                            BluetoothController.GetInstance().SetCurrentBTState(BluetoothController.btState.UNKNOWN);

                            BluetoothActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                }
                            });

                        } catch (IOException e2) {
                            //insert code to deal with this
                            Toast.makeText(getBaseContext(), "Socket creation failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                    if (!fail) {
                        BluetoothController.GetInstance().SetCurrentBTState(BluetoothController.btState.CONNECTED);
                        BluetoothController.GetInstance().initConnectedThread(mBTSocket, mHandler);
                        mHandler.obtainMessage(CONNECTING_STATUS, 1, -1, name)
                                .sendToTarget();

                        BluetoothController.GetInstance().SetDeviceNameConnectedTo(name);

                        BluetoothActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mBluetoothStatus.setText("Connected to " + name);
                            }
                        });
                    }
                }
            }.start();
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        mBTAdapter = BluetoothAdapter.getDefaultAdapter(); // get a handle on the bluetooth radio
        if (!mBTAdapter.isEnabled()) {
            mBluetoothStatus.setText("Bluetooth is OFF");
        } else {
            String deviceNameConnectedTo = BluetoothController.GetInstance().GetDeviceNameConnectedTo();
            if (deviceNameConnectedTo.isEmpty())
                mBluetoothStatus.setText("Disconnected");
            else
                mBluetoothStatus.setText("Connected to " + deviceNameConnectedTo);
        }
    }

    private void createBluetoothSocket(BluetoothDevice device) throws IOException {
        BluetoothSocket tmp = null;

        try {
            final Method m = device.getClass().getMethod("createRfcommSocket", new Class[]{int.class});
            tmp = (BluetoothSocket) m.invoke(device, 1);
        } catch (Exception e) {
            Log.e(TAG, "Could not create Insecure RFComm Connection", e);
        }

        mBTSocket = tmp;
    }
};

 //   public void InitBluetoothState() {
   //     IntentFilter filter1 = new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED);
       // IntentFilter filter2 = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
     //   IntentFilter filter3 = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        //this.registerReceiver(mReceiver, filter1);
        //this.registerReceiver(mReceiver, filter2);
        //this.registerReceiver(mReceiver, filter3);
    //}
