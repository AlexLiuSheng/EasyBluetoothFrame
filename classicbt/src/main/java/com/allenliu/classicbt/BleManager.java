package com.allenliu.classicbt;

import android.app.Notification;
import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import com.allenliu.classicbt.listener.*;
import com.allenliu.classicbt.scan.ScanConfig;
import com.allenliu.classicbt.scan.ScanTimer;

import java.lang.reflect.Method;
import java.util.Set;

/**
 * @author AllenLiu
 * @version 1.0
 * @date 2019/5/8
 */
public class BleManager implements BleFunction {
    private BluetoothAdapter mBluetoothAdapter;
    private Context application;
    private ScanResultListener resultListener;

    private ScanTimer scanTimer;
    // Create a BroadcastReceiver for ACTION_FOUND
    private boolean isRegister = false;


    private BtReceiver mReceiver;
    private String uuid;
    private Notification notification;
    private boolean isForegroundService = false;
    private ConnectResultlistner clientConnectResultListener;
    private ConnectResultlistner serverConnectResultListener;

    public static BleManager getInstance() {
        return Holder.client;
    }

    static class Holder {
        static BleManager client = new BleManager();
    }

    public BleManager() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public ConnectResultlistner getClientConnectResultListener() {
        return clientConnectResultListener;
    }

    public void setClientConnectResultListener(ConnectResultlistner clientConnectResultListener) {
        this.clientConnectResultListener = clientConnectResultListener;
        mReceiver.setClientConnectResultListener(clientConnectResultListener);

    }

    public ConnectResultlistner getServerConnectResultListener() {
        return serverConnectResultListener;
    }

    @Override
    public void init(Context context) {
        application = context;
        mReceiver = new BtReceiver();
        registerReceiver();


    }

    @Override
    public void setConnectionUUID(String uuid) {
        this.uuid = uuid;

    }


    private void registerReceiver() {
        if (resultListener != null)
            mReceiver.setScanResultListener(resultListener);
//        if(pinResultListener!=null)
//            mReceiver.setPinResultListener(pinResultListener);
//        if(cancelPinResultListener!=null)
//            mReceiver.setCancelPinResultListener(cancelPinResultListener);
        if (clientConnectResultListener != null)
            mReceiver.setClientConnectResultListener(clientConnectResultListener);
        if (serverConnectResultListener != null)
            mReceiver.setServerConnectResultListener(serverConnectResultListener);
        if (!isRegister) {
            isRegister = true;
            // Register the BroadcastReceiver
            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);//状态改变
            filter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);//行动扫描模式改变了
            filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);//动作状态发生了变化
            filter.addAction(BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED);
            filter.addAction(BluetoothDevice.ACTION_PAIRING_REQUEST);
            filter.addAction(BluetoothA2dp.ACTION_PLAYING_STATE_CHANGED);
            //<action android:name="android.bluetooth.device.action.ACL_CONNECTED" />
            //    <action android:name="android.bluetooth.device.action.ACL_DISCONNECT_REQUESTED" />
            //    <action android:name="android.bluetooth.device.action.ACL_DISCONNECTED" />
            filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
            filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
            application.registerReceiver(mReceiver, filter); // Don't forget to unregister during onDestroy
        }
    }

    @Override
    public void setResultListener(ScanResultListener resultListener) {
        this.resultListener = resultListener;
    }

    @Override
    public boolean isSupported() {
        return mBluetoothAdapter != null;
    }

    @Override
    public Set<BluetoothDevice> getPairedDevices() {
        return mBluetoothAdapter.getBondedDevices();

    }

    @Override
    public void scan(ScanConfig scanConfig, ScanResultListener scanResultListener) {
        if (scanTimer != null) {
            scanTimer.cancel();
        }
        scanTimer = new ScanTimer(scanConfig) {
            @Override
            public void onFinish() {
                stopSearch();
            }
        };
        scanTimer.start();
        this.resultListener = scanResultListener;

        registerReceiver();
        boolean r = mBluetoothAdapter.startDiscovery();
        if (!r && resultListener != null) {
            resultListener.onError();
            resultListener.onFinish();
        }

    }

    @Override
    public void stopSearch() {
        if (isRegister) {
//            application.unregisterReceiver(mReceiver);
            isRegister = false;

            if (resultListener != null) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    resultListener.onFinish();
                });
            }
        }
        if (mBluetoothAdapter.isDiscovering())
            mBluetoothAdapter.cancelDiscovery();
        if (scanTimer != null) {
            scanTimer.cancel();
        }

    }

    @Override
    public void connect(BluetoothDevice device, ConnectResultlistner c) {
        this.clientConnectResultListener = c;
        ConnectService.start(application, device, ConnectThread.CLIENT, uuid, isForegroundService, notification);
        mReceiver.setClientConnectResultListener(clientConnectResultListener);


    }

    @Override
    public void destory() {
//        application.unregisterReceiver(mReceiver);
        stopSearch();
        application.stopService(new Intent(application, ConnectService.class));
    }

    @Override
    public void pin(BluetoothDevice device, PinResultListener resultListener) {
        if (device == null) {
            return;
        }
        mReceiver.setPinResultListener(resultListener);
        if (device.getBondState() == BluetoothDevice.BOND_NONE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                device.createBond();
            } else {
                try {
                    Method method = device.getClass().getMethod("createBond");
                    method.invoke(device);
                } catch (Exception e) {
                    e.printStackTrace();
                    resultListener.pairFailed(device);
                }
            }
        }
    }

    @Override
    public void cancelPin(BluetoothDevice device, ResultListener resultListener) {
        if (device == null) {
            return;
        }
        mReceiver.setCancelPinResultListener(resultListener);

        if (device.getBondState() != BluetoothDevice.BOND_NONE) {
            try {
                Method method = device.getClass().getMethod("removeBond");
                boolean r = (boolean) method.invoke(device);
            } catch (Exception e) {
                e.printStackTrace();
                resultListener.failed(e);
            }
        }
    }

    @Override
    public void setServerConnectResultListener(ConnectResultlistner connectResultListener) {
        this.serverConnectResultListener = connectResultListener;
        mReceiver.setServerConnectResultListener(serverConnectResultListener);

//        if (serverConnectThread != null)
//            serverConnectThread.setConnectResultlistner(connectResultListener);
    }

    @Override
    public void registerServerConnection(ConnectResultlistner connectResultListener) {
        this.serverConnectResultListener = connectResultListener;
        ConnectService.start(application, null, ConnectThread.SERVER, uuid, isForegroundService, notification);
        mReceiver.setServerConnectResultListener(serverConnectResultListener);

    }

    @Override
    public void setForegroundService(boolean foregroundService) {
        this.isForegroundService = foregroundService;
    }

    @Override
    public void setNotification(Notification notification) {
        this.notification = notification;
    }

    @Override
    public void enableDiscoverable(long time) {
        Intent discoverableIntent = new
                Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, time);
        discoverableIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        application.startActivity(discoverableIntent);
    }


}
