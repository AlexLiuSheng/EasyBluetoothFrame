package com.allenliu.btdemo

import android.bluetooth.BluetoothDevice
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.allenliu.classicbt.BleManager
import com.allenliu.classicbt.CLog
import com.allenliu.classicbt.Connect
import com.allenliu.classicbt.listener.ConnectResultlistner
import com.allenliu.classicbt.listener.PinResultListener
import java.lang.Exception

/**
 * @author AllenLiu
 * @version 1.0
 * @date 2019/5/20
 * @copyRight 四川金信石信息技术有限公司
 * @since 1.0
 */
class MyAdapter(var context: MainActivity, d: List<BluetoothDevice>) : RecyclerView.Adapter<MyViewHolder>() {
    var data: List<BluetoothDevice> = d


    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item, p0, false))
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(p0: MyViewHolder, p1: Int) {
        p0.tv.text = data[p1].name
        p0.tv.setOnClickListener {
            CLog.e("click connect")
            context.showLoading()
            if (BleManager.getInstance().pairedDevices.contains(data[p1])) {
                connect(context, data[p1])
            } else {
                BleManager.getInstance().pin(
                    data[p1]
                ) { connect(context, data[p1]) }
            }


        }
    }


}

private fun connect(context: MainActivity, d: BluetoothDevice) {
    BleManager.getInstance().connect(d, object : ConnectResultlistner {
        override fun disconnected() {

        }

        override fun connectSuccess(connect: Connect?) {
            CLog.e(" connect success")
            context.dismiss()
            context.connect = connect
            context.t("connect success")
            context.read()

        }

        override fun connectFailed(e: Exception?) {

            context.dismiss()
            context.t("connect failed:${e?.message}")

//                    e?.printStackTrace()
        }
    })
}

class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val tv: TextView = itemView.findViewById(R.id.tv)
}