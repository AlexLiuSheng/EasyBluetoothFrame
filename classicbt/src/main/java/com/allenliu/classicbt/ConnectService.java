package com.allenliu.classicbt;

import android.app.*;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.IBinder;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

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

    private int getAppIcon(){
        PackageManager pm=getPackageManager();
        ApplicationInfo applicationInfo= null;
        try {
            applicationInfo = pm.getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            return applicationInfo.icon;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;

    }
    private Notification buildNotification() {
        String channelId="BluetoothForegroundServiceID";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(channelId, "BluetoothForegroundService1", NotificationManager.IMPORTANCE_LOW);
            notificationChannel.enableLights(false);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(false);
            NotificationManager manager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
            manager.createNotificationChannel(notificationChannel);
        }
        return new NotificationCompat.Builder(this,channelId)
                .setContentTitle(getString(R.string.bluetooth_service))
//                .setTicker(getString(R.string.bluetooth_service_is_running))
                .setContentText(getString(R.string.bluetooth_service_is_running))

                .setSmallIcon(getAppIcon())
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
        CLog.e("stop service");

        if (serverConnectThread != null) {
            serverConnectThread.cancel();
        }
        if (connectThread != null)
            connectThread.cancel();
        stopForeground(true);

    }
}
