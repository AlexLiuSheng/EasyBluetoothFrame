package com.allenliu.classicbt.scan;

import android.os.CountDownTimer;

/**
 * @author AllenLiu
 * @version 1.0
 * @date 2019/5/8

 */
public class ScanTimer extends CountDownTimer {

    public ScanTimer(ScanConfig config) {
        super(config.getScanTime(), 1000);
    }

    @Override
    public void onTick(long millisUntilFinished) {

    }

    @Override
    public void onFinish() {

    }


}
