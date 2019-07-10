package com.allenliu.classicbt.listener;

/**
 * author : AllenLiu
 * date   : 2019/5/10 5:49 PM
 * desc   :
 */
public interface TransferProgressListener {
    void transfering(int progress);
    void transferSuccess(byte[] bytes);
    void transferFailed(Exception exception);



}
