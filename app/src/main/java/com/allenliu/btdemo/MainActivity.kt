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
import java.lang.Double.parseDouble
import java.nio.ByteBuffer
import com.allenliu.btdemo.MainActivity.eclair as eclair1

class MainActivity : AppCompatActivity(), BluetoothPermissionCallBack {
    private lateinit var list: ArrayList<BluetoothDevice>
    var connect: Connect? = null

 public class eclair
{
    var lux: String ?="0"

    var xred: String ?="0"

    var ygreen: String ?="0"

    var zblue: String ?="0"
   /* var colorHex: String ?= (String.format("%02X",xred)+String.format("%02X",ygreen)+String.format("%02X",zblue))*/

    var colortemp:String ?="0"

   /* var inforecieve: String? ="0"*/






}

    final var d1="R"
   final var d2="G"
    final  var d3="B"
    final  var d4="C"
 final   var d5="L"
    final   var d6="N"

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
        registerServer()
       /*btn1.setOnClickListener {
            t("register server success.you can connnect device now.")
            registerServer()
        }*/
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
var receive="0"
        connect?.read(object : TransferProgressListener {

            override fun transferSuccess(bytes: ByteArray?) {
                t("received message")
                bytes?.let { it1 ->
                   receive= String(it1)
             eclair11.xred=sgntreat(receive,d1,d2)
             eclair11.ygreen=sgntreat(receive,d2,d3)
                    eclair11.zblue=sgntreat(receive,d3,d4)
                    eclair11.colortemp=sgntreat(receive,d4,d5)
                    eclair11.lux=sgntreat(receive,d5,d6)

                   if (eclair11.xred!="0"){ RReceive.text=eclair11.xred.toString()}
                    if (eclair11.ygreen!="0"){  GReceive.text=eclair11.ygreen.toString()}
                        if (eclair11.zblue!="0"){  BReceive.text=eclair11.zblue.toString()}
                            if (eclair11.colortemp!="0"){  CReceive.text=eclair11.colortemp.toString()}
                    if (eclair11.lux!="0"){LuxReceive.text=eclair11.lux.toString()}
                   /* CsharpReceive.text=eclair11.colorHex*/
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

    fun sgntreat(a:String ,b:String ,c:String ): String {
       if (a==null)
       {
           return "0"
       }else {
           val parts1 = a!!.split(b, c)
           var flag = false
           /*isDigit(parts1[1]) == true*/
           if (parts1.isNotEmpty()&&parts1.size>=2) {
               if (parts1[1].length <= 5) {
                   if (isDigit(parts1[1]) == true) {
                       return parts1[1]
                   } else {
                       return "0"
                   }

               } else {
                   return "0"
               }
           }else{
               return "0"
           }

       }

    }

    fun isDigit(a:String): Boolean {
        var numeric = true

        try {
            val num = parseDouble(a)
        } catch (e: NumberFormatException) {
            numeric = false
        }
return numeric
    }



    //善后
    override fun onDestroy() {
        super.onDestroy()
        BleManager.getInstance().destory()
    }


}
