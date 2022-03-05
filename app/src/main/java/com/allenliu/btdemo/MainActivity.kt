package com.allenliu.btdemo

import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.allenliu.classicbt.BleManager
import com.allenliu.classicbt.BluetoothPermissionHandler
import com.allenliu.classicbt.CLog
import com.allenliu.classicbt.Connect
import com.allenliu.classicbt.listener.*
import com.allenliu.classicbt.scan.ScanConfig
import kotlinx.android.synthetic.main.activity_main.*
import java.nio.ByteBuffer
import com.allenliu.btdemo.MainActivity.eclair as eclair1

class MainActivity : AppCompatActivity(), BluetoothPermissionCallBack {
    private lateinit var list: ArrayList<BluetoothDevice>
    var connect: Connect? = null

 public class eclair
{
    var lux: Int = 0
        get() = 0
        set(value) {
            field=value
        }
    var xred: Int = 0
        get() = 0
        set(value) {
         field=value
        }
    var ygreen: Int = 0
        get() = 0
        set(value) {
            field =value
        }
    var zblue: Int = 0
        get() = 0
        set(value) {
            field=value
        }
    var colorHex= (String.format("%02X",xred)+String.format("%02X",ygreen)+String.format("%02X",zblue))
    var colortemp: Int = 0
        get() = 0
        set(value) {
            field=value
        }
    var inforecieve: String? =null

    constructor(
        lux: Int,
        xred: Int,
        ygreen: Int,
        zblue: Int,
        colorHex: String,
        colortemp: Int,
        inforecieve: String?
    ) {
        this.lux = lux
        this.xred = xred
        this.ygreen = ygreen
        this.zblue = zblue
        this.colorHex = colorHex
        this.colortemp = colortemp
        this.inforecieve = inforecieve
    }

    constructor()


}

    final var d1="R"
   final var d2="G"
    final  var d3="B"
    final  var d4="C"
 final   var d5="L"

    //包 的开头结尾定义
    val start = "".toByteArray()
    val end = "".toByteArray()

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

    //扫描发现 设备并放入列表
    override fun onBlueToothEnabled() {

        BleManager.getInstance().init(application)
        BleManager.getInstance().setForegroundService(true)
       /* btn2.setOnClickListener {*/
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
        //}

       btn1.setOnClickListener {
            t("register server success.you can connnect device now.")
            registerServer()
        }
       /* btn3.setOnClickListener {
            write()
        }
        btnDiscoverable.setOnClickListener {
            BleManager.getInstance().enableDiscoverable(300)
        }*/

    }

    //连接设备
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

    //Activity的主函数
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        recyclerview.layoutManager =
            LinearLayoutManager(this)
        list = ArrayList()
        recyclerview.adapter = MyAdapter(this, list)
        permissionCallBack.start()
    }

    //检测设备列表中是否重复
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

    //数据收
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

        val eclair11 =eclair()

        connect?.read(object : TransferProgressListener {

            override fun transferSuccess(bytes: ByteArray?) {
                t("received message")
                bytes?.let { it1 ->



                    eclair11.inforecieve= String(it1)
                     val parts= eclair11.inforecieve!!.split(d1,d2,d3,d4,d5)
                    eclair11.xred=parts[1].toInt()
                    eclair11.ygreen=parts[2].toInt()
                    eclair11.zblue=parts[3].toInt()
                    eclair11.colortemp=parts[4].toInt()
                    eclair11.lux=parts[5].toInt()
                    LuxReceive.text = eclair11.lux.toString()
                    RReceive.text=eclair11.xred.toString()
                    GReceive.text=eclair11.ygreen.toString()
                    BReceive.text=eclair11.zblue.toString()
                    CReceive.text=eclair11.colortemp.toString()
                    CsharpReceive.text=eclair11.colorHex
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

    //发数据
    /*private fun write() {
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
    }*/

    //善后
    override fun onDestroy() {
        super.onDestroy()
        BleManager.getInstance().destory()
    }
}
