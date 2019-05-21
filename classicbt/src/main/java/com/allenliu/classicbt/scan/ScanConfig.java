package com.allenliu.classicbt.scan;

/**
 * @author AllenLiu
 * @version 1.0
 * @date 2019/5/8
 * @copyRight 四川金信石信息技术有限公司
 * @since 1.0
 */
public class ScanConfig {
    private long scanTime;

    public ScanConfig(long scanTime) {
        this.scanTime = scanTime;
    }

    public long getScanTime() {
        return scanTime;
    }

    public void setScanTime(long scanTime) {
        this.scanTime = scanTime;
    }
}
