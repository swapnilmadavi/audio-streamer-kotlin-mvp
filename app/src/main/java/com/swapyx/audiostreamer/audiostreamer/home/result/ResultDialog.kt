package com.swapyx.audiostreamer.audiostreamer.home.result

import android.support.v4.app.DialogFragment
import android.os.Bundle
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import com.swapyx.audiostreamer.audiostreamer.R
import android.view.Gravity
import android.view.WindowManager
import android.graphics.Point

class ResultDialog : DialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_result, container)
        dialog.setTitle("Simple Dialog")
        return view
    }

    override fun onResume() {
        matchToWindowSize()
        super.onResume()
    }

    private fun matchToWindowSize() {
        // Store access variables for window and blank point
        val window = dialog.window
        val size = Point()
        // Store dimensions of the screen in `size`
        val display = window!!.windowManager.defaultDisplay
        display.getSize(size)
        // Set the width of the dialog proportional to 75% of the screen width
        window.setLayout((size.x * 0.9).toInt(), WindowManager.LayoutParams.WRAP_CONTENT)
        window.setGravity(Gravity.CENTER)
    }

    companion object {
        fun newInstance(): ResultDialog {
            return ResultDialog()
        }
    }
}