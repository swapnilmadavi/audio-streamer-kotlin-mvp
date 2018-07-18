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
import android.os.Parcelable
import android.support.design.widget.FloatingActionButton
import android.widget.ImageButton
import android.widget.TextView
import com.github.jorgecastilloprz.FABProgressCircle
import com.swapyx.audiostreamer.audiostreamer.data.audioserver.model.SessionResult

class ResultDialog : DialogFragment() {

    private lateinit var sessionNameText: TextView
    private lateinit var sessionScoreText: TextView
    private lateinit var sessionTimeStampText: TextView
    private lateinit var sessionLengthText: TextView
    private lateinit var progress: FABProgressCircle
    private lateinit var playStopButton: FloatingActionButton
    private lateinit var closeButton: ImageButton

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_result, container)
        with(view) {
            sessionNameText = findViewById(R.id.session_name_text)
            closeButton = findViewById(R.id.close_button)
            progress = findViewById(R.id.progressCircle_result)
            playStopButton = findViewById(R.id.fab_result_play_stop)
            sessionScoreText = findViewById(R.id.score_text)
            sessionTimeStampText = findViewById(R.id.session_timestamp_text)
            sessionLengthText = findViewById(R.id.session_length_text)
        }
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        playStopButton.setOnClickListener {

        }

        closeButton.setOnClickListener {
            if (dialog.isShowing){
                dialog.dismiss()
            }
        }

        readData()
    }

    private fun readData() {
        val result = arguments?.getParcelable<SessionResult>(ARGUMENT_SESSION_RESULT)
        if (result != null) {
            sessionNameText.text = result.sId
            sessionScoreText.text = getString(R.string.your_score, result.data.score.toString())
            sessionTimeStampText.text = result.timestamp.toString()
            sessionLengthText.text = result.data.length.toString()
        }
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
        private const val ARGUMENT_SESSION_RESULT = "session_result"

        fun newInstance(result: Parcelable): ResultDialog {
            return ResultDialog().apply {
                arguments = Bundle().apply {
                    putParcelable(ARGUMENT_SESSION_RESULT, result)
                }
            }
        }
    }
}