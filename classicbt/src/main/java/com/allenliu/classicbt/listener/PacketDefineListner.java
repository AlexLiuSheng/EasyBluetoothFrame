package com.allenliu.classicbt.listener;

/**
 * @author AllenLiu
 * @date 2019/8/26
 */
public interface PacketDefineListner {
    byte[] getPacketStart();

    byte[] getPacketEnd();
}
