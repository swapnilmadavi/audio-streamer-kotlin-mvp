package com.swapyx.audiostreamer.audiostreamer.ui.record

import android.app.Dialog
import android.support.v4.app.DialogFragment
import android.os.Bundle
import android.support.v7.app.AlertDialog
import com.swapyx.audiostreamer.audiostreamer.R

class NetworkInterruptDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val alertDialogBuilder = AlertDialog.Builder(context!!)

        alertDialogBuilder.setTitle(getString(R.string.error))

        alertDialogBuilder.setMessage(getString(R.string.network_interrupt_message))

        alertDialogBuilder.setPositiveButton("OK") { _, _ ->
            // on success
        }

        return alertDialogBuilder.create()
    }

    companion object {
        fun newInstance(): NetworkInterruptDialog {
            return NetworkInterruptDialog()
        }
    }
}