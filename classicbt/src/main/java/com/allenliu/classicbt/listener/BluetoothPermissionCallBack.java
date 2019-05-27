package com.allenliu.classicbt.listener;

/**
 * @author AllenLiu
 * @version 1.0
 * @date 2019/5/27
 * @copyRight 四川金信石信息技术有限公司
 * @since 1.0
 */
public interface BluetoothPermissionCallBack {
    void onBlueToothEnabled();
    void permissionFailed();
}
