package com.example.spectrumanalyzer.Components.Bluetooth;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.SystemClock;

import com.example.spectrumanalyzer.Screens.Activities.Bluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class BTSocket extends Thread {
    private final BluetoothSocket mSocket;
    private final InputStream mInStream;
    private final OutputStream mOutStream;
    private final Handler mHandler;

    public BTSocket(BluetoothSocket socket, Handler handler) {
        mSocket = socket;
        mHandler = handler;
        InputStream tmpInStream = null;
        OutputStream tmpOutStream = null;
        // Get the input and output streams, using temp objects because member streams are final
        try {
            tmpInStream = mSocket.getInputStream();
            tmpOutStream = mSocket.getOutputStream();
        } catch (IOException e) {
        }
        mInStream = tmpInStream;
        mOutStream = tmpOutStream;
    }

    @Override
    public void run() {
        // buffer store for the stream
        int data_buffer_max_capacity = 640,tracking_status_length = 10;
        int bytes,delta,offset,bytes_available;
        int buffer_length = data_buffer_max_capacity+tracking_status_length;
        byte[] buffer = new byte[buffer_length];
        while (mSocket.isConnected()) {
            delta = data_buffer_max_capacity + tracking_status_length;
            offset = 0;
            // Keep looping to listen for received messages
            try {
                    while (offset < buffer_length && delta > 0) {
                        bytes_available = mInStream.available();
                        if (bytes_available > 0) {
                            bytes = mInStream.read(buffer, offset, delta);
                            offset += bytes;
                            delta -= bytes;
                            // Send the obtained bytes to the UI Activity via handler
                        }
                    }
                mHandler.obtainMessage(Bluetooth.MESSAGE_READ, data_buffer_max_capacity, -1, buffer).sendToTarget();
            }catch (IOException ioException) {
                    ioException.printStackTrace();
            }
        }
    }
    public void write(String input) {
        byte[] bytes = input.getBytes();  //converts input String into bytes
        try {
            mOutStream.write(bytes);
        } catch (IOException e) {
        }
    }
}