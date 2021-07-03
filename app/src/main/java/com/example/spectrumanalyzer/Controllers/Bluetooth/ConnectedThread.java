package com.example.spectrumanalyzer.Controllers.Bluetooth;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;

import com.example.spectrumanalyzer.Screens.Activities.BluetoothActivity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ConnectedThread extends Thread {
    private final BluetoothSocket mSocket;
    private final InputStream mInStream;
    private final OutputStream mOutStream;
    private final Handler mHandler;

    public ConnectedThread(BluetoothSocket socket, Handler handler) {
        mSocket = socket;
        mHandler = handler;
        InputStream tmpInStream = null;
        OutputStream tmpOutStream = null;

        // Get the input and output streams, using temp objects because member streams are final
        try {
            tmpInStream = socket.getInputStream();
            tmpOutStream = socket.getOutputStream();
        } catch (IOException e) {
        }

        mInStream = tmpInStream;
        mOutStream = tmpOutStream;
    }

    @Override
    public void run() {
        // buffer store for the stream
        int max_capacity = 639;
        int delta = max_capacity;
        int offset = 0;
        int bytes = 0;
        byte[] buffer = new byte[max_capacity];

        while (true) {
            // Keep looping to listen for received messages
            while (offset < max_capacity) {
                try {
                    if (mInStream.available() > 0) {
                        //read bytes from input buffer
                        if ((offset < max_capacity) && (delta > 0)) {
                            bytes = mInStream.read(buffer, offset, delta);

                            if (bytes > -1) {
                                offset += bytes;
                                delta -= bytes;

                            } else {
                                break;
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
            // Send the obtained bytes to the UI Activity via handler
            mHandler.obtainMessage(BluetoothActivity.MESSAGE_READ, max_capacity, -1, buffer).sendToTarget();
            offset = 0;
            delta = max_capacity;
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