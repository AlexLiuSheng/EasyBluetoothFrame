package com.allenliu.classicbt;

import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import com.allenliu.classicbt.listener.ConnectResultlistner;

public class ConnectService extends Service {
    private ConnectThread connectThread;
    private ConnectThread serverConnectThread;
    private final static String DEVICE = "DEVICE";
    private final static String MODE = "MODE";
    private final static String UUID = "UUID";
    private final static String LISTENER = "LISTENER";

    public ConnectService() {
    }

    public static void start(Context context,  BluetoothDevice bluetoothDevice, int mode, String uuid) {
        Intent intent = new Intent(context, ConnectService.class);
        intent.putExtra(DEVICE, bluetoothDevice);
        intent.putExtra(MODE, mode);
        intent.putExtra(UUID, uuid);
        context.startService(intent);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        init(intent);
        return super.onStartCommand(intent, flags, startId);
    }

    private void init(Intent intent) {
        int mode = intent.getIntExtra(MODE, ConnectThread.CLIENT);
        switch (mode) {
            case ConnectThread.CLIENT:
                BluetoothDevice device = intent.getParcelableExtra(DEVICE);
                if (connectThread != null)
                    connectThread.cancel();
                connectThread = new ConnectThread(device,BleManager.getInstance().getClientConnectResultListener(),  ConnectThread.CLIENT, intent.getStringExtra(UUID));
                connectThread.start();
                break;
            case ConnectThread.SERVER:
                if (serverConnectThread != null) {
                    serverConnectThread.cancel();
                }
                    serverConnectThread = new ConnectThread(null,BleManager.getInstance().getServerConnectResultListener(), ConnectThread.SERVER, intent.getStringExtra(UUID));
                    serverConnectThread.start();

                break;
        }

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (serverConnectThread != null) {
            serverConnectThread.cancel();
        }
        if (connectThread != null)
            connectThread.cancel();
    }
}
