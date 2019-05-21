package com.allenliu.classicbt.listener;

import android.bluetooth.BluetoothDevice;

/**
 * @author AllenLiu
 * @version 1.0
 * @date 2019/5/8
 * @copyRight 四川金信石信息技术有限公司
 * @since 1.0
 */
public interface DeviceSelectListener {
    void onDeviceSelected(BluetoothDevice bluetoothDevice);
}
