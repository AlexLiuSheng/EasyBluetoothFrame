package com.allenliu.classicbt.listener;

import android.bluetooth.BluetoothDevice;

/**
 * @author AllenLiu
 * @version 1.0
 * @date 2019/5/8

 */
public interface DeviceSelectListener {
    void onDeviceSelected(BluetoothDevice bluetoothDevice);
}
