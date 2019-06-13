package com.allenliu.classicbt;

import android.app.Notification;
import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import com.allenliu.classicbt.listener.ConnectResultlistner;

public class ConnectService extends Service {
    private ConnectThread connectThread;
    private ConnectThread serverConnectThread;
    private final static String DEVICE = "DEVICE";
    private final static String MODE = "MODE";
    private final static String UUID = "UUID";
    private final static String LISTENER = "LISTENER";
    private final static String FOREGROUND="FOREGROUND";
    private final static String NOTIFICATION="NOTIFICATION";


    public ConnectService() {
    }

    public static void start(Context context,  BluetoothDevice bluetoothDevice, int mode, String uuid,boolean isForegroundService,@Nullable Notification notification) {
        Intent intent = new Intent(context, ConnectService.class);
        intent.putExtra(DEVICE, bluetoothDevice);
        intent.putExtra(MODE, mode);
        intent.putExtra(UUID, uuid);
        intent.putExtra(FOREGROUND,isForegroundService);
        intent.putExtra(NOTIFICATION,notification);
        if(isForegroundService) {
            ContextCompat.startForegroundService(context, intent);
        }else{
            context.startService(intent);
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        boolean isForeground=intent.getBooleanExtra(FOREGROUND,false);
        if(isForeground){
            Notification notification=intent.getParcelableExtra(NOTIFICATION)!=null?intent.getParcelableExtra(NOTIFICATION):buildNotification();
          startForeground(1,notification);
        }
        init(intent);
        return super.onStartCommand(intent, flags, startId);
    }

    private Notification buildNotification() {
        String channelId="BluetoothForegroundService";
        return new NotificationCompat.Builder(this,channelId)
                .setContentTitle(getString(R.string.bluetooth_service))
                .setTicker(getString(R.string.bluetooth_service_is_running))
                .build();
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
