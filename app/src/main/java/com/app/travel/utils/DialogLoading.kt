package com.app.travel.utils


import android.annotation.SuppressLint
import android.app.Activity
import androidx.appcompat.app.AlertDialog
import com.app.travel.R


class DialogLoading  // constructor of dialog class
// with parameter activity
internal constructor(  // 2 objects activity and dialog
    private val activity: Activity
) {
    private var dialog: AlertDialog? = null
    @SuppressLint("InflateParams")
    fun startLoadingdialog() {

        // adding ALERT Dialog builder object and passing activity as parameter
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity)

        // layoutinflater object and use activity to get layout inflater
        val inflater = activity.layoutInflater
        builder.setView(inflater.inflate(R.layout.loading, null))
        builder.setCancelable(false)
        dialog = builder.create()
        dialog!!.show()
    }

    // dismiss method
    fun dismissdialog() {
        dialog?.dismiss()
    }
}