package com.example.spectrumanalyzer.Controllers.Bluetooth;

import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

public class BluetoothController {
    //    Constant variable - used when sending new Settings array values to Server - Key is Settings to let server notify new Settings change.
    private final String settingsKeyword = "Settings";

    //    The name of the device our app is connected to.
    private String deviceNameConnectedTo = "";

    //    Bluetooth state can be unknown, connected or disconnected.
    public enum btState {DISCONNECTED, CONNECTED, UNKNOWN}

    private static BluetoothController instance = null;

    //    Initial bluetooth state to unknown.
    private btState currentBtState = btState.UNKNOWN;

    //    Thread for bluetooth connection between client and server.
    private ConnectedThread mConnectedThread;

    private final String CommaSeparator = ",";

    private BluetoothController() {
    }

    /**
     * Singleton design pattern - Return the same instance (one in total) while app is alive.
     *
     * @return BluetoothController class instance
     */
    public static BluetoothController GetInstance() {
        if (instance == null)
            instance = new BluetoothController();

        return instance;
    }

    /**
     * Runs thread that connects to bluetooth and listens for messages read/write operations.
     *
     * @param mBTSocket A connected or connecting bluetooth socket.
     * @param mHandler  Allows to send or process message object associated with a thread's message queue.
     */
    public void initConnectedThread(BluetoothSocket mBTSocket, Handler mHandler) {
        mConnectedThread = new ConnectedThread(mBTSocket, mHandler);
        mConnectedThread.start();
    }

    /**
     * Return the current bluetooth state value.
     *
     * @return bluetooth state
     */
    public btState GetCurrentBTState() {
        return currentBtState;
    }

    public void SetCurrentBTState(btState newState) {
        currentBtState = newState;
    }

    public String GetDeviceNameConnectedTo() {
        return deviceNameConnectedTo;
    }

    public void SetDeviceNameConnectedTo(String name) {
        deviceNameConnectedTo = name;
    }

    /**
     * Send data to the remote device
     *
     * @param msg Message to send to server
     */
    public void Write(String msg) {
        if (mConnectedThread.isAlive()) {
            mConnectedThread.write(msg);
        }
    }

    /**
     * Constructs a message representation that will be sent to server.
     * Final result would like something like this: "Settings,1024,2,128,Linear".
     *
     * @param newSampleRateValue New sample rate value
     * @param newChannelsNumber  New channels number value
     * @param newNumFftPoints    New FFT points value
     * @param newAmplitudeRange  New amplitude range value
     * @return String representation of all parameters
     */
    public String CreateMessageFromInputParams(String newAmplitudeRange, String newSampleRateValue, String newChannelsNumber, String newNumFftPoints) {
        return settingsKeyword +
                CommaSeparator +
                newAmplitudeRange +
                CommaSeparator +
                newSampleRateValue +
                CommaSeparator +
                newChannelsNumber +
                CommaSeparator +
                newNumFftPoints;
    }

    /**
     * Displays a toast message that bluetooth isn't connected yet.
     *
     * @param context Activity context
     */
    public void DisplayBluetoothDisconnectedText(Context context) {
        Toast.makeText(context, "Bluetooth is not connected yet.", Toast.LENGTH_SHORT).show();
    }

    /**
     * Shutdown the connection
     */
    public void Cancel() {
        mConnectedThread.cancel();
    }
}