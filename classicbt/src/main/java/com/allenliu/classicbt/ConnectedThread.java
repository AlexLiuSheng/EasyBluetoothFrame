package com.allenliu.classicbt;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Looper;
import com.allenliu.classicbt.listener.TransferProgressListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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


    public ConnectedThread(BluetoothSocket socket, int mode, TransferProgressListener transferProgressListener) {
        init(socket, mode);
        queue = new LinkedBlockingQueue<byte[]>();
        this.transferProgressListener = transferProgressListener;
    }

    public void setTransferProgressListener(TransferProgressListener transferProgressListener) {
        this.transferProgressListener = transferProgressListener;
    }

    public synchronized void write(byte[] bytes) {
        CLog.e("put bytes to queue");
        try {
            queue.put(bytes);
        } catch (InterruptedException e) {
            e.printStackTrace();
            handleFailed(e.getMessage());
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
        while (true) {
            try {
                // Read from the InputStream
                long count = 0;
                int progress = 0;
                while (count == 0) {
                    count = mmInStream.available();
                }
                CLog.e("total:" + count);
                float current = 0;

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                do {
                    bytes = mmInStream.read(buffer);
                    CLog.e("read bytes:" + bytes);
                    if (bytes > 0) {
                        current += bytes;
                        progress = (int) ((current / count) * 100);
                        byteArrayOutputStream.write(buffer);
                        handleTransfering(progress);
                    } else {
                        break;
                    }

                } while (mmInStream.available() > 0);
                CLog.e("read success:" + bytes);
                handleSuccessed(byteArrayOutputStream.toByteArray());

            } catch (IOException e) {
                e.printStackTrace();
                handleFailed(e.getMessage());
                break;
            }

        }
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

    private void handleFailed(String message) {
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
                handleFailed(e.getMessage());
                break;
            }
        }

    }


}
