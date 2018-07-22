package com.swapyx.audiostreamer.audiostreamer.ui.record

import android.app.Dialog
import android.content.Context
import android.support.v4.app.DialogFragment
import android.os.Bundle
import android.support.v7.app.AlertDialog


class AbortRecordingDialog : DialogFragment() {

    private var listener: AbortRecordingDialogListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val alertDialogBuilder = AlertDialog.Builder(context!!)
        alertDialogBuilder.setMessage("Abort session?")
        alertDialogBuilder.setPositiveButton("Abort") { dialog, which ->
            // on success
            listener?.onAbortClicked()
        }
        alertDialogBuilder.setNegativeButton("Cancel") { dialog, which ->
            dialog?.dismiss()
        }

        return alertDialogBuilder.create()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            listener = context as AbortRecordingDialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException(context.toString() + " must implement OnHeadlineSelectedListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface AbortRecordingDialogListener {
        fun onAbortClicked()
    }

    companion object {
        fun newInstance(): AbortRecordingDialog {
            return AbortRecordingDialog()
        }
    }
}