package com.solutions.lorrea.kgf;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;


public class MyService extends Service {
    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;

    OutputStream mmOutputStream;
    InputStream mmInputStream;
    Thread workerThread;

    byte[] readBuffer;
    int readBufferPosition;
    int counter;
    volatile boolean stopWorker;

    static final int JOB_1 = 1;
    Boolean connectedBa = false;


    Messenger mMessenger = new Messenger(new IncomingHandler());



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!connectedBa){
//            Toast.makeText(MyService.this, "TEST", Toast.LENGTH_LONG).show();
            findBT();
        }
        else{
//            Toast.makeText(MyService.this, "Connected NA!!", Toast.LENGTH_LONG).show();
        }
        return START_STICKY;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();

    }


    public void findBT() {

        try {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

            if (mBluetoothAdapter == null) {
                Toast.makeText(MyService.this,"Bluetooth not supported", Toast.LENGTH_LONG).show();
            }

            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter
                    .getBondedDevices();
            if (pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {
                    //BlueTooth Printer
                    if (device.getName().equals("BlueTooth Printer")) {
                        mmDevice = device;
                        break;
                    }
                }
            }
//            Toast.makeText(MyService.this,"Bluetooth device found", Toast.LENGTH_LONG).show();
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            closeBT();
            openBT();
        } catch (IOException e) {
            Toast.makeText(MyService.this,e.toString(), Toast.LENGTH_LONG).show();
            connectedBa = false;
        }

    }

    // Tries to open a connection to the bluetooth printer device
    public void openBT() throws IOException {
        try {
            // Standard SerialPortService ID
            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
            mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
            try {
                mmSocket.connect();
            }
            catch (IOException e) {
                try {

                    mmSocket = (BluetoothSocket) mmDevice.getClass().getMethod("createRfcommSocket", new Class[]{int.class}).invoke(mmDevice, 1);
                    mmSocket.connect();

                    Log.e("", "Connected");
                } catch (Exception e2) {
                    Toast.makeText(MyService.this, e2.toString(), Toast.LENGTH_LONG).show();
                }
            }
            mmOutputStream = mmSocket.getOutputStream();
            mmInputStream = mmSocket.getInputStream();

            beginListenForData();

//            Toast.makeText(MyService.this,"Connected", Toast.LENGTH_LONG).show();
            connectedBa = true;
            Intent i = new Intent("com.solutions.lorrea.kgf").putExtra("some_msg", "Connected");
            this.sendBroadcast(i);
            this.stopSelf();
            connectedBa = true;
        } catch (NullPointerException e) {
//            Toast.makeText(MyService.this,e.toString(), Toast.LENGTH_LONG).show();
            connectedBa = false;
            e.printStackTrace();
        } catch (Exception e) {
//            Toast.makeText(MyService.this,e.toString(), Toast.LENGTH_LONG).show();
            connectedBa = false;
            e.printStackTrace();
        }
    }

    // After opening a connection to bluetooth printer device,
// we have to listen and check if a data were sent to be printed.
    public void beginListenForData() {
        try {
            final Handler handler = new Handler();

            // This is the ASCII code for a newline character
            final byte delimiter = 10;

            stopWorker = false;
            readBufferPosition = 0;
            readBuffer = new byte[1024];

            workerThread = new Thread(new Runnable() {
                public void run() {
                    while (!Thread.currentThread().isInterrupted()
                            && !stopWorker) {

                        try {

                            int bytesAvailable = mmInputStream.available();
                            if (bytesAvailable > 0) {
                                byte[] packetBytes = new byte[bytesAvailable];
                                mmInputStream.read(packetBytes);
                                for (int i = 0; i < bytesAvailable; i++) {
                                    byte b = packetBytes[i];
                                    if (b == delimiter) {
                                        byte[] encodedBytes = new byte[readBufferPosition];
                                        System.arraycopy(readBuffer, 0,
                                                encodedBytes, 0,
                                                encodedBytes.length);
                                        final String data = new String(
                                                encodedBytes, "US-ASCII");
                                        readBufferPosition = 0;

                                        handler.post(new Runnable() {
                                            public void run() {
//                                                myLabel.setText(data);
                                            }
                                        });
                                    } else {
                                        readBuffer[readBufferPosition++] = b;
                                    }
                                }
                            }

                        } catch (IOException ex) {
                            stopWorker = true;
                        }

                    }
                }
            });

            workerThread.start();
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendData(String x) throws IOException {
        try {

            // the text typed by the user
            String msg = x;
            msg += "\n";
            Toast.makeText(MyService.this, msg, Toast.LENGTH_LONG).show();

            mmOutputStream.write(msg.getBytes());

            // tell the user data were sent
            Toast.makeText(MyService.this,"Data sent", Toast.LENGTH_LONG).show();

        } catch (NullPointerException e) {
            Toast.makeText(MyService.this,e.toString(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } catch (Exception e) {
            Toast.makeText(MyService.this,e.toString(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    void closeBT() throws IOException {
        try {
            stopWorker = true;
            mmOutputStream.close();
            mmInputStream.close();
            mmSocket.close();
            Toast.makeText(MyService.this,"Bluetooth Closed", Toast.LENGTH_LONG).show();
            connectedBa = false;
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    class IncomingHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            ArrayList aL = (ArrayList) msg.obj;
            String messageToPrint = aL.get(0).toString();
//                Toast.makeText(MyService.this, "123 send potang ina", Toast.LENGTH_LONG).show();
            try {

                findBT();
                openBT();
                sendData(messageToPrint);
            } catch (IOException e) {
                e.printStackTrace();
            }
            super.handleMessage(msg);
        }
    }





}



/*
    page load
    check if connected ang printer

    if not connected
        check if open ang bt
            if ndi, open bt

        findbt
        open bt


    if connected
        do nothing







 */