package com.allenliu.classicbt.listener;

import android.bluetooth.BluetoothDevice;

/**
 * author : AllenLiu

 * date   : 2019/5/10 4:59 PM
 * desc   :
 */
public interface PinResultListener {
    default void startPair(BluetoothDevice device){}
    default void pairing(BluetoothDevice device){}
    void paired(BluetoothDevice device);
    default void pairFailed(BluetoothDevice device){}
}
