package com.app.travel.utils

import android.content.Context
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class MyDialog(val context: Context) {

    fun ok(message: String){
        MaterialAlertDialogBuilder(context).setTitle(message)
            .setCancelable(false)
            .setNegativeButton("Ok") { dialog, _ ->
                dialog.dismiss()
            }.show()
        return
    }
}