package com.example.spectrumanalyzer.Components.Bluetooth;

import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

public class Controller {
    //    Constant variable - used when sending new Settings array values to Server - Key is Settings to let server notify new Settings change.
    //    The name of the device our app is connected to.
    private String deviceNameConnectedTo = "";
    //    Bluetooth state can be unknown, connected or disconnected.
    public enum btState {UNKNOWN,CONNECTING,CONNECTED,DISCONNECTED}
    private static Controller instance = null;
    //    Initial bluetooth state to unknown.
    private btState currentBtState = btState.DISCONNECTED;
    //    Thread for bluetooth connection between client and server.
    private BTSocket mSocketThread;
    private Controller() {
    }
    /**
     * Singleton design pattern - Return the same instance (one in total) while app is alive.
     * @return BluetoothController class instance
     */
    public static Controller getInstance() {
        if (instance == null)
            instance = new Controller();
        return instance;
    }
    /**
     * Runs thread that connects to bluetooth and listens for messages read/write operations.
     * @param mBTSocket A connected or connecting bluetooth socket.
     * @param mHandler  Allows to send or process message object associated with a thread's message queue.
     */

    public void initBluetoothSocketThread(BluetoothSocket mBTSocket, Handler mHandler) {
        mSocketThread = new BTSocket(mBTSocket, mHandler);
        mSocketThread.start();
    }

    /**
     * Return the current bluetooth state value.
     * @return bluetooth state
     */
    public btState getCurrentBTState() {
        return currentBtState;
    }
    public void setCurrentBTState(btState newState) {
        currentBtState = newState;
    }
    public String getDeviceNameConnectedTo() {
        return deviceNameConnectedTo;
    }
    public void setDeviceNameConnectedTo(String name) {
        deviceNameConnectedTo = name;
    }

    /**
     * Send data to the remote device
     * @param msg Message to send to server
     */
    public void writeMsg(String msg) {
        if (mSocketThread.isAlive()) {
            mSocketThread.write(msg);
        }
    }
    /**
     * Displays a toast message
     * @param context Activity context
     */
    public void toastSystemMessage(Context context) {
        Toast.makeText(context, "Bluetooth isn't connected yet!", Toast.LENGTH_SHORT).show();
    }
}
