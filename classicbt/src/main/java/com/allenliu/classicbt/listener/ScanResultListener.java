package com.allenliu.classicbt.listener;

import android.bluetooth.BluetoothDevice;

public interface ScanResultListener {
        void onDeviceFound(BluetoothDevice device);

        void onFinish();

        void onError();
    }