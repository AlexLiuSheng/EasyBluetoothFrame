package com.allenliu.classicbt;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.allenliu.classicbt.listener.ConnectResultlistner;
import com.allenliu.classicbt.listener.PinResultListener;
import com.allenliu.classicbt.listener.ResultListener;
import com.allenliu.classicbt.listener.ScanResultListener;

/**
 * @author AllenLiu
 * @version 1.0
 * @date 2019/5/9

 */
public class BtReceiver extends BroadcastReceiver {


    private ScanResultListener scanResultListener;
    private PinResultListener pinResultListener;
    private ResultListener cancelPinResultListener;
    private final String TAG = "BtReceiver";
   private ConnectResultlistner serverConnectResultListener;
   private ConnectResultlistner clientConnectResultListener;
    public void setPinResultListener(PinResultListener pinResultListener) {
        this.pinResultListener = pinResultListener;
    }

    public void setCancelPinResultListener(ResultListener cancelPinResultListener) {
        this.cancelPinResultListener = cancelPinResultListener;
    }

    public void setScanResultListener(ScanResultListener scanResultListener) {
        this.scanResultListener = scanResultListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        // Get the BluetoothDevice object from the Intent
        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        // When discovery finds a device
        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
            // Add the name and address to an array adapter to show in a ListView
            if (scanResultListener != null) {
                scanResultListener.onDeviceFound(device);
            }
            //配对请求
        } else if (BluetoothDevice.ACTION_PAIRING_REQUEST.equals(action)) {
//            try {
//
//                //1.确认配对
//                Method setPairingConfirmation = device.getClass().getDeclaredMethod("setPairingConfirmation", boolean.class);
//                setPairingConfirmation.invoke(device, true);
//                //2.终止有序广播
//                abortBroadcast();//如果没有将广播终止，则会出现一个一闪而过的配对框。
//                //3.调用setPin方法进行配对...
////                boolean ret = ClsUtils.setPin(device.getClass(), device, pin);
//                Method removeBondMethod = device.getClass().getDeclaredMethod("setPin", new Class[]{byte[].class});
//                Boolean returnValue = (Boolean) removeBondMethod.invoke(device, new Object[]{pin.getBytes()});
//            } catch (Exception e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//                if (pinResultListener != null) {
//                    pinResultListener.pairFailed(device);
//                }
//
//            }
        } else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
            switch (device.getBondState()) {
                case BluetoothDevice.BOND_NONE:
                    Log.d(TAG, "取消配对");
                    if (pinResultListener != null)
                        pinResultListener.pairFailed(device);
                    break;
                case BluetoothDevice.BOND_BONDING:
                    Log.d(TAG, "配对中");
                    if (pinResultListener != null)
                        pinResultListener.pairing(device);
                    break;
                case BluetoothDevice.BOND_BONDED:
                    Log.d(TAG, "配对成功");
                    if (pinResultListener != null)
                        pinResultListener.paired(device);
                    break;

            }
        }else if ((BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)&&intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1)
                == BluetoothAdapter.STATE_OFF)||BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)){

                if(serverConnectResultListener!=null)
                    serverConnectResultListener.disconnected();
                if(clientConnectResultListener!=null)
                    clientConnectResultListener.disconnected();


        }

    }

    public void setServerConnectResultListener(ConnectResultlistner serverConnectResultListener) {
        this.serverConnectResultListener=serverConnectResultListener;
    }

    public void setClientConnectResultListener(ConnectResultlistner clientConnectResultListener) {
        this.clientConnectResultListener=clientConnectResultListener;
    }
}
