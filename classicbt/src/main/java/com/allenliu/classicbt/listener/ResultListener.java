package com.allenliu.classicbt.listener;

/**
 * author : AllenLiu

 * date   : 2019/5/10 4:43 PM
 * desc   :
 */
public interface ResultListener {
    void success();
    void failed(Exception e);
}
