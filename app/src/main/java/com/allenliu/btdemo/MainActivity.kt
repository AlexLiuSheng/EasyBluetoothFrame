package com.allenliu.btdemo

import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.allenliu.classicbt.BleManager
import com.allenliu.classicbt.BluetoothPermissionHandler
import com.allenliu.classicbt.CLog
import com.allenliu.classicbt.Connect
import com.allenliu.classicbt.listener.*
import com.allenliu.classicbt.scan.ScanConfig
import kotlinx.android.synthetic.main.activity_main.*
import java.nio.ByteBuffer

class MainActivity : AppCompatActivity(), BluetoothPermissionCallBack {
    private lateinit var list: ArrayList<BluetoothDevice>
    var connect: Connect? = null
    /**
     * 自定义每一个完整的数据包的开头和结束字节
     */
    val start = "开始\n".toByteArray()
    val end = "结束\n".toByteArray()
    private val permissionCallBack = BluetoothPermissionHandler(this, this)
    override fun permissionFailed() {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        permissionCallBack.onActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionCallBack.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onBlueToothEnabled() {

        BleManager.getInstance().init(application)
        BleManager.getInstance().setForegroundService(true)
        btn2.setOnClickListener {
            BleManager.getInstance().scan(ScanConfig(5000), object : ScanResultListener {
                override fun onDeviceFound(device: BluetoothDevice?) {
                    if (!isContained(device!!)) {
                        list.add(device)
                        recyclerview.adapter?.notifyDataSetChanged()
                    }
                }

                override fun onFinish() {
                }

                override fun onError() {
                }
            })
        }
        btn1.setOnClickListener {
            t("register server success.you can connnect device now.")
            registerServer()
        }
        btn3.setOnClickListener {
            write()
        }
        btnDiscoverable.setOnClickListener {
            BleManager.getInstance().enableDiscoverable(300)
        }

    }

    fun registerServer() {
        BleManager.getInstance().registerServerConnection(object : ConnectResultlistner {
            override fun disconnected() {
                t("bluetooth has disconnected")
                BleManager.getInstance().destory()
                registerServer()
            }

            override fun connectSuccess(connect: Connect?) {
                this@MainActivity.connect = connect
                read()
            }

            override fun connectFailed(e: Exception?) {

            }

        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        recyclerview.layoutManager = LinearLayoutManager(this)
        list = ArrayList()
        recyclerview.adapter = MyAdapter(this, list)
        permissionCallBack.start()
    }

    private fun isContained(result: BluetoothDevice): Boolean {
        if (result.name == null || "null".equals(result.name, ignoreCase = true))
            return true
        for (device in list) {
            if (result.address == device.address) {
                return true
            }
        }
        return false
    }

    fun read() {
//        val a:Int= -0x146f1470
//        val buffer=ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(a)
//        val b:Byte= 0x03
//        val buffer2=ByteBuffer.allocate(1).order(ByteOrder.LITTLE_ENDIAN).put(b)
        connect?.setReadPacketVerifyListener(object : PacketDefineListener {
            override fun getPacketStart(): ByteArray {
                return start

            }

            override fun getPacketEnd(): ByteArray {
                return end

            }
        })
        connect?.read(object : TransferProgressListener {

            override fun transferSuccess(bytes: ByteArray?) {
                t("received message")
                bytes?.let { it1 ->
                    tvReceive.text = String(it1)
                }

                CLog.e("read string")
            }

            override fun transferFailed(msg: Exception) {
                msg.printStackTrace()
//                msg.message?.run {
//                    t(this)
//                }

            }

            override fun transfering(progress: Int) {
                CLog.e("read progress:$progress")
            }
        })
    }

    private fun write() {
        val text = et.text.toString()
        val bytes = text.toByteArray()

        val b = ByteBuffer.allocate(start.size + end.size + bytes.size)
        b.put(start)
        b.put(bytes)
        b.put(end)

        connect?.write(b.array(), object : TransferProgressListener {
//            override fun disconnected() {
//                t("bluetooth has disconnected")
//
//            }

            override fun transferSuccess(bytes: ByteArray?) {
                t("send message successful")
            }

            override fun transferFailed(msg: Exception) {
                msg.message?.run {
                    t(this)
                }

            }

            override fun transfering(progress: Int) {
                CLog.e("write progress:$progress")
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        BleManager.getInstance().destory()
    }
}
