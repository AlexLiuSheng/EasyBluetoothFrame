package com.allenliu.classicbt.listener;

import java.nio.ByteOrder;

/**
 * @author AllenLiu
 * @date 2019/8/26
 * 定义一个完整数据包的开头或者结尾
 * @see PacketDefineListner
 */
public class PacketDefine {
    /**
     * 字节长度
     */
    public int byteLength;
    /**
     * int字节值
     */
    public int byteValue;

    public ByteOrder byteOrder=ByteOrder.BIG_ENDIAN;

    public PacketDefine(int byteLength, int byteValue) {
        this.byteLength = byteLength;
        this.byteValue = byteValue;
    }

    public PacketDefine(int byteLength, int byteValue, ByteOrder byteOrder) {
        this.byteLength = byteLength;
        this.byteValue = byteValue;
        this.byteOrder = byteOrder;
    }
}
