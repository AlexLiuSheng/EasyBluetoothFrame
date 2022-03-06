package com.allenliu.btdemo

import android.app.ProgressDialog
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.allenliu.classicbt.BleManager
import com.allenliu.classicbt.Connect
import com.allenliu.classicbt.listener.BluetoothPermissionCallBack
import com.allenliu.classicbt.listener.ConnectResultlistner
import com.allenliu.classicbt.listener.ScanResultListener
import com.allenliu.classicbt.scan.ScanConfig
import kotlinx.android.synthetic.main.activity_main.*

class ScanActivty:AppCompatActivity() , BluetoothPermissionCallBack {
    private lateinit var list: ArrayList<BluetoothDevice>
    var connect: Connect? = null
    var listView: ListView? = null
    var listViewAdapter: Adapter? = null
    var connectDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.listlayout)
        val swipRefreshLayout: SwipeRefreshLayout = findViewById(R.id.swipe_layout)
        swipRefreshLayout.setColorSchemeColors(0x01a4ef)
        swipRefreshLayout.setOnRefreshListener {
            //清空数据
            list.clear()
            listViewAdapter?.notifyDataSetChanged()
            /*ECBLE.stopBluetoothDevicesDiscovery()*/
            Handler().postDelayed({
                swipRefreshLayout.isRefreshing = false
                onBlueToothEnabled()
            }, 1000)
        }
        /*recyclerview.layoutManager =
            SwipeRefreshLayout(this)
        list = ArrayList()
        recyclerview.adapter = MyAdapter(this, list)
        permissionCallBack.start()*/
        listView = findViewById<ListView>(R.id.list_view1)
        listViewAdapter = Adapter(this, R.layout.list_item, deviceListData)
        listView?.adapter = listViewAdapter
        listView?.setOnItemClickListener { adapterView: AdapterView<*>, view1: View, i: Int, l: Long ->
            showConnectDialog()
            ECBLE.easyConnect(deviceListData.get(i).name) {
                hideConnectDialog()
                if (it) {
//                    showToast("连接成功")
                    //跳转设备页
                    runOnUiThread {
                        startActivity(Intent().setClass(this, DeviceActivity().javaClass))
                    }
                } else {
                    showToast("连接失败")
                }
            }
        }
        listRefresh();

    }


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

    override fun permissionFailed() {

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

    fun registerServer() {
        BleManager.getInstance().registerServerConnection(object : ConnectResultlistner {
            override fun disconnected() {
                t("bluetooth has disconnected")
                BleManager.getInstance().destory()
                registerServer()
            }

            override fun connectSuccess(connect: Connect?) {
                this@ScanActivty.connect = connect
                runOnUiThread {
                    startActivity(Intent().setClass(this@ScanActivty, MainActivity().javaClass))
                }
               // read()
            }

            override fun connectFailed(e: Exception?) {

            }

        })
    }













}




