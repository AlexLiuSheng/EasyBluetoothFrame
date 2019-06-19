package com.allenliu.classicbt;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Looper;
import com.allenliu.classicbt.listener.BleFunction;
import com.allenliu.classicbt.listener.ConnectResultlistner;

import java.io.IOException;
import java.util.UUID;


/**
 * @author AllenLiu
 * @version 1.0
 * @date 2019/5/8

 */
public class ConnectThread extends Thread {
    private final BluetoothDevice bluetoothDevice;
    private BluetoothSocket socket;
    private final BluetoothServerSocket serverSocket;

    private ConnectResultlistner connectResultlistner;
    private Handler mainHandler;
    private Connect connect;
    public static final int CLIENT=1;
    public static final int SERVER=2;
    private int mode=CLIENT;
    private String uuidStr="00001101-0000-1000-8000-00805F9B34FB";

    public ConnectThread(BluetoothDevice bluetoothDevice, ConnectResultlistner connectResultlistner, int mode,String uuidS) {
        this.mode=mode;
        this.connectResultlistner = connectResultlistner;
        this.bluetoothDevice = bluetoothDevice;
        mainHandler = new Handler(Looper.getMainLooper());
//        BluetoothSocket tmp = null;
        BluetoothServerSocket tmp2 = null;
        if(uuidS!=null)
        this.uuidStr=uuidS;
        UUID uuid;
        try {
             uuid= UUID.fromString(uuidStr);
        }catch (IllegalArgumentException e){
            uuidStr="00001101-0000-1000-8000-00805F9B34FB";
             uuid= UUID.fromString(uuidStr);
        }
        // Get a BluetoothSocket to connect with the given BluetoothDevice
        try {
            if(mode==CLIENT)
            socket = bluetoothDevice.createRfcommSocketToServiceRecord(uuid);
            else
                tmp2 = BluetoothAdapter.getDefaultAdapter().listenUsingRfcommWithServiceRecord(BluetoothAdapter.getDefaultAdapter().getName(),uuid);
        } catch (Exception e) {
            postFailed(e);
        }
        serverSocket=tmp2;
    }

    public void setConnectResultlistner(ConnectResultlistner connectResultlistner) {
        this.connectResultlistner = connectResultlistner;
    }

    @Override
    public void run() {
        // Cancel discovery because it will slow down the connection
        BleManager.getInstance().stopSearch();
        try {
            // Connect the device through the socket. This will block
            // until it succeeds or throws an exception
            if(mode==CLIENT) {
                if (socket != null && !socket.isConnected()) {
                    socket.connect();
                    // Do work to manage the connection (in a separate thread)
                    manageConnectedSocket(socket);
                }
            }else{
                if(serverSocket!=null){
                   socket= serverSocket.accept();
                    serverSocket.close();
                    manageConnectedSocket(socket);
                }
            }
        } catch (IOException connectException) {
            // Unable to connect; close the socket and get out
            postFailed(connectException);

        }


    }

    private void manageConnectedSocket(BluetoothSocket socket) {
        connect = new Connect(socket);
        postSuccess(connect);

    }

    /**
     * Will cancel an in-progress connection, and close the socket
     */
    public void cancel() {
        try {
            if (connect != null)
                connect.cancel();
            if(socket!=null)
            socket.close();
            if(serverSocket!=null)
                serverSocket.close();
        } catch (Exception e) {
        }
    }

    private void postSuccess(Connect connect) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                if (connectResultlistner != null) {
                    CLog.e("invoke");
                    connectResultlistner.connectSuccess(connect);
                }else{

                }
            }
        });

    }

    private void postFailed(Exception e) {
        cancel();
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                if (connectResultlistner != null)
                    connectResultlistner.connectFailed(e);
            }
        });
    }
}
