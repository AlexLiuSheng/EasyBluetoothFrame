package com.allenliu.btdemo

import android.app.Dialog
import android.content.Context
import android.widget.ProgressBar
import android.widget.Toast


lateinit var pd: Dialog
fun Context.showLoading() {
    pd= Dialog(this,R.style.CustomDialogTheme)
    pd.setContentView(ProgressBar(this))
    pd.show()
}

fun Context.dismiss() {
    pd.dismiss()
}

fun Context.t(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_LONG).show()

}