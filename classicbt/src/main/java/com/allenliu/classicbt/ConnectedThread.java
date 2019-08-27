package com.allenliu.classicbt;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Looper;
import com.allenliu.classicbt.listener.PacketDefineListener;
import com.allenliu.classicbt.listener.TransferProgressListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashSet;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author AllenLiu
 * @version 1.0
 * @date 2019/5/8
 */
public class ConnectedThread implements Runnable {
    public static final int READ = 0;
    public static final int WRITE = 1;
    private int mode = WRITE;
    private BluetoothSocket mmSocket;
    private InputStream mmInStream;
    private OutputStream mmOutStream;
    private volatile LinkedBlockingQueue<byte[]> queue;

    private Handler handler;
    private TransferProgressListener transferProgressListener;
    private PacketDefineListener packetDefineListener;
    /**
     * 判断是否是一个完整的数据包
     */
    private boolean isCompleteDataPacket = false;

    public ConnectedThread(BluetoothSocket socket, int mode, TransferProgressListener transferProgressListener) {
        queue = new LinkedBlockingQueue<byte[]>();
        this.transferProgressListener = transferProgressListener;
        init(socket, mode);
    }

    public void setTransferProgressListener(TransferProgressListener transferProgressListener) {
        this.transferProgressListener = transferProgressListener;
    }

    public void setPacketDefineListener(PacketDefineListener packetDefineListener) {
        this.packetDefineListener = packetDefineListener;
    }

    public synchronized void write(byte[] bytes) {
        CLog.e("put bytes to queue");
        try {
            queue.put(bytes);
        } catch (InterruptedException e) {
            e.printStackTrace();
            handleFailed(e);
        }
    }


    private void init(BluetoothSocket socket, int mode) {
        handler = new Handler(Looper.getMainLooper());
        mmSocket = socket;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;
        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mmInStream = tmpIn;
        mmOutStream = tmpOut;
        this.mode = mode;
    }

    public void run() {
        if (mode == READ) {
            read();
        } else {
            write();
        }
    }

    private void read() {
        byte[] buffer = new byte[1024];  // buffer store for the stream
        int bytes; // bytes returned from read()
        // Keep listening to the InputStream until an exception occurs
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        while (true) {
            try {
                // Read from the InputStream
                long count = 0;
                int progress = 0;
                while (count == 0) {
                    count = mmInStream.available();
                }
//                CLog.e("total:" + count);
                float current = 0;
                //每次读取之前判断是否已经组装了一个完整的数据包
                //如果是 将字节加入新的输出流
                if (isCompleteDataPacket) {
                    isCompleteDataPacket = false;
                    byteArrayOutputStream = new ByteArrayOutputStream();
                }

                do {
                    bytes = mmInStream.read(buffer);
                    //如果已经定义了数据包头，验证一下是否满足
                    if (byteArrayOutputStream.size() == 0 && !isPacketStart(bytes, buffer)) {
                        isCompleteDataPacket = true;
                        handleFailed(new RuntimeException("data packet header is invalid"));
                        break;
                    }
//                        CLog.e("read bytes:" + bytes);
                    if (bytes > 0) {
                        current += bytes;
                        progress = (int) ((current / count) * 100);
                        byteArrayOutputStream.write(buffer,0,bytes);
                        handleTransfering(progress);
                    } else {
                        break;
                    }


                } while (mmInStream.available() > 0);
                CLog.e("current segment read success:" + bytes);
                //判断是否已经到达结尾了
                if (isPacketEnd(bytes, buffer))
                     handleSuccessed(byteArrayOutputStream.toByteArray());


            } catch (IOException e) {
                e.printStackTrace();
                handleFailed(e);
                break;
            }

        }
    }

    /**
     * 数据包是否结束，默认大端模式
     *
     * @param total
     * @param buffer
     * @return
     */
    private boolean isPacketEnd(int total, byte[] buffer) {
        if (packetDefineListener != null && total != -1) {
            byte[] packetDefine = packetDefineListener.getPacketEnd();
            int endLength = packetDefine.length;
            ByteBuffer byteBuffer = ByteBuffer.allocate(endLength);
            byteBuffer.put(buffer, total - endLength, endLength);
            CLog.e("defined foot:"+Arrays.toString(packetDefine)+"current foot:"+Arrays.toString(byteBuffer.array()));

            if (Arrays.equals(byteBuffer.array(),packetDefine)) {
                isCompleteDataPacket = true;
                return true;
            } else {
                isCompleteDataPacket = false;
                return false;
            }

        }
        isCompleteDataPacket = true;
        return true;
    }

    private boolean isPacketStart(int total, byte[] buffer) {
        if (packetDefineListener != null && total != -1) {
            byte[] packetDefine = packetDefineListener.getPacketStart();
            int startLength = packetDefine.length;
            ByteBuffer byteBuffer = ByteBuffer.allocate(startLength);
            byteBuffer.put(buffer, 0, startLength);
            CLog.e("defined header:"+Arrays.toString(packetDefine)+"current header:"+Arrays.toString(byteBuffer.array()));
            return Arrays.equals(byteBuffer.array(), packetDefine);

        }
        return true;
    }


    private void handleTransfering(int progress) {
        handler.post(() -> {
            if (transferProgressListener != null)
                transferProgressListener.transfering(progress);
        });
    }

    private void handleSuccessed(byte[] bytes) {
        handler.post(() -> {
            if (transferProgressListener != null)
                transferProgressListener.transferSuccess(bytes);
        });
    }

    private void handleFailed(Exception message) {
        handler.post(() -> {
            if (transferProgressListener != null)
                transferProgressListener.transferFailed(message);
        });
    }

    /* Call this from the main activity to send data to the remote device */
    private void write() {

        while (true) {
            try {

                HashSet<byte[]> set = new HashSet<>();
                int size = queue.drainTo(set);
                if (size > 0) {
                    int index = 0;
                    for (byte[] bytes : set) {
                        mmOutStream.write(bytes);
                        handleTransfering((int) ((((++index) / (float) size)) * 100));
                    }
//                    mmOutStream.close();
                    handleSuccessed(null);
                }
            } catch (IOException e) {
                e.printStackTrace();
                handleFailed(e);
                break;
            }
        }

    }


}
