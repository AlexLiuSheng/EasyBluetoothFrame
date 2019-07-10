package com.allenliu.classicbt.listener;

import android.os.Parcel;
import android.os.Parcelable;
import com.allenliu.classicbt.CLog;
import com.allenliu.classicbt.Connect;

import java.io.Serializable;

/**
 * @author AllenLiu
 * @version 1.0
 * @date 2019/5/8

 */
public interface ConnectResultlistner  {

     void connectSuccess(Connect connect);

     void connectFailed(Exception e);
     void disconnected();



}
