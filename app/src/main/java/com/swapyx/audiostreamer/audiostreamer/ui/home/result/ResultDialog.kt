package com.swapyx.audiostreamer.audiostreamer.ui.home.result

import android.content.Context
import android.support.v4.app.DialogFragment
import android.os.Bundle
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import com.swapyx.audiostreamer.audiostreamer.R
import android.view.Gravity
import android.view.WindowManager
import android.graphics.Point
import android.media.AudioAttributes
import android.media.AudioManager
import android.os.Parcelable
import android.support.design.widget.FloatingActionButton
import android.widget.ImageButton
import android.widget.TextView
import com.github.jorgecastilloprz.FABProgressCircle
import com.swapyx.audiostreamer.audiostreamer.data.audioserver.model.SessionResult
import android.media.MediaPlayer
import android.os.Build
import android.util.Log
import android.widget.ProgressBar
import java.io.IOException


class ResultDialog : DialogFragment(), MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener {

    private lateinit var sessionScoreText: TextView
    private lateinit var sessionTimeStampText: TextView
    private lateinit var sessionLengthText: TextView
    private lateinit var progress: FABProgressCircle
    private lateinit var playStopButton: FloatingActionButton
    private lateinit var closeButton: ImageButton
    private lateinit var resultProgress: ProgressBar
    private lateinit var audioUrl: String

    private var listener: ResultDialogListener? = null

    private var mediaPlayer: MediaPlayer? = null

    private var result: SessionResult? = null

    private var showPending = false

    private var audioPrepared = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_result, container)
        with(view) {
            closeButton = findViewById(R.id.close_button)
            progress = findViewById(R.id.progressCircle_result)
            playStopButton = findViewById(R.id.fab_result_play_stop)
            sessionScoreText = findViewById(R.id.score_text)
            sessionTimeStampText = findViewById(R.id.session_timestamp_text)
            sessionLengthText = findViewById(R.id.session_length_text)
            resultProgress = findViewById(R.id.result_progress)
        }
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        readData()

        if (showPending) {
            showProgress()
            hideFields()
            disablePlayStopButton()
        } else {
            if (result != null) {
                setFields(result!!)
                initMediaPlayer()
            }
        }

        playStopButton.setOnClickListener {
            if (mediaPlayer?.isPlaying == false) {
                if (!audioPrepared) {
                    progress.show()
                    playStopButton.isEnabled = false
                    mediaPlayer?.prepareAsync()
                } else {
                    playAudio()
                }
            } else {
                audioPrepared = false
                stopAudio()
            }
        }

        closeButton.setOnClickListener {
            if (dialog.isShowing){
                if (!showPending) {
                    listener?.refreshSessionList()
                }
                dialog.dismiss()
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            listener = context as ResultDialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException(context.toString() + " must implement ResultDialogListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    private fun readData() {
        result = arguments?.getParcelable<SessionResult>(ARGUMENT_SESSION_RESULT)
        showPending = arguments?.getBoolean(ARGUMENT_SHOW_PENDING) ?: false
    }

    private fun setFields(result: SessionResult) {
        sessionScoreText.text = getString(R.string.your_score, result.data.score.toString())
        sessionTimeStampText.text = result.timestamp.toString()
        sessionLengthText.text = result.data.length.toString()
        audioUrl = "http://192.168.1.101:8000/file/${result.sId}"
    }

    private fun initMediaPlayer() {
        mediaPlayer = MediaPlayer()

        //Set up MediaPlayer event listeners
        with(mediaPlayer!!) {
            setOnCompletionListener(this@ResultDialog)
            setOnErrorListener(this@ResultDialog)
            setOnPreparedListener(this@ResultDialog)
            //setOnBufferingUpdateListener(this)
            //setOnSeekCompleteListener(this)
            //setOnInfoListener(this)

            //Reset so that the MediaPlayer is not pointing to another data source
            reset()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                setAudioAttributes(AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build())
            } else {
                setAudioStreamType(AudioManager.STREAM_MUSIC)
            }

            try {
                // Set the data source to the mediaFile location
                setDataSource(audioUrl)
            } catch (e: IOException) {
                Log.d(TAG, "Set source=> ${e.localizedMessage}")
            }
        }
    }

    override fun onResume() {
        matchToWindowSize()
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mediaPlayer != null) {
            stopAudio()
            mediaPlayer?.release()
        }
    }

    fun setAndShowResult(result: SessionResult) {
        Log.d(TAG, "setAndShowResult")
        this.result = result
        setFields(result)
        initMediaPlayer()
        hideProgress()
        showFields()
        enablePlayStopButton()
    }

    private fun enablePlayStopButton() {
        playStopButton.isEnabled = true
    }

    private fun disablePlayStopButton() {
        playStopButton.isEnabled = false
    }

    private fun showFields() {
        sessionScoreText.visibility = View.VISIBLE
        sessionTimeStampText.visibility = View.VISIBLE
        sessionLengthText.visibility = View.VISIBLE
    }

    private fun hideFields() {
        sessionScoreText.visibility = View.GONE
        sessionTimeStampText.visibility = View.GONE
        sessionLengthText.visibility = View.GONE
    }

    private fun showProgress() {
        resultProgress.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        resultProgress.visibility = View.GONE
    }

    override fun onCompletion(mp: MediaPlayer?) {
        Log.d(TAG, "onCompletion")
        if (mediaPlayer == null) return
        if (mediaPlayer?.isPlaying == true) {
            stopAudio()
        }
    }

    override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        when (what) {
            MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK -> Log.d("MediaPlayer Error", "MEDIA ERROR NOT VALID FOR PROGRESSIVE PLAYBACK $extra")
            MediaPlayer.MEDIA_ERROR_SERVER_DIED -> Log.d("MediaPlayer Error", "MEDIA ERROR SERVER DIED $extra")
            MediaPlayer.MEDIA_ERROR_UNKNOWN -> Log.d("MediaPlayer Error", "MEDIA ERROR UNKNOWN $extra")
        }
        return false
    }

    override fun onPrepared(mp: MediaPlayer?) {
        Log.d(TAG, "onPrepared")
        audioPrepared = true
        progress.hide()
        playStopButton.isEnabled = true
        //Invoked when the media source is ready for playback.
        playAudio()
    }

    private fun playAudio() {
        playStopButton.setImageResource(R.drawable.ic_stop_blue_24dp)
        mediaPlayer?.start()
    }

    private fun stopAudio() {
        playStopButton.setImageResource(R.drawable.ic_play_arrow_blue_24dp)
        mediaPlayer?.stop()
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

    interface ResultDialogListener {

        fun refreshSessionList()
    }

    companion object {
        private val TAG = ResultDialog::class.java.simpleName
        private const val ARGUMENT_SHOW_PENDING = "show_pending"
        private const val ARGUMENT_SESSION_RESULT = "session_result"

        fun newInstance(showPending: Boolean = false, result: Parcelable?): ResultDialog {
            return ResultDialog().apply {
                arguments = Bundle().apply {
                    putBoolean(ARGUMENT_SHOW_PENDING, showPending)
                    putParcelable(ARGUMENT_SESSION_RESULT, result)
                }
            }
        }
    }
}