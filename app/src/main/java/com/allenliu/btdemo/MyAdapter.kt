package com.allenliu.btdemo

import android.bluetooth.BluetoothDevice
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.allenliu.classicbt.BleManager
import com.allenliu.classicbt.CLog
import com.allenliu.classicbt.Connect
import com.allenliu.classicbt.listener.ConnectResultlistner
import com.allenliu.classicbt.listener.PinResultListener
import java.lang.Exception


class MyAdapter(var context: MainActivity,val resourceID:Int, d: List<BluetoothDevice>) : ArrayAdapter<BluetoothDevice>(context, resourceID, d) {
    var data: List<BluetoothDevice> = d

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val deviceInfo:BluetoothDevice?=getItem(position)
        val name=deviceInfo?.name?:""
        val rssi = deviceInfo?.rssi ?: 0
        val view: View = LayoutInflater.from(context).inflate(resourceID, parent, false)
        val headImg = view.findViewById<ImageView>(R.id.iv_type)





        return view
    }

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