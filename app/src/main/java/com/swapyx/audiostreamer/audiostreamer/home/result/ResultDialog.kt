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
import java.io.IOException


class ResultDialog : DialogFragment(), MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener {

    private lateinit var sessionScoreText: TextView
    private lateinit var sessionTimeStampText: TextView
    private lateinit var sessionLengthText: TextView
    private lateinit var progress: FABProgressCircle
    private lateinit var playStopButton: FloatingActionButton
    private lateinit var closeButton: ImageButton
    private lateinit var audioUrl: String

    private var mediaPlayer: MediaPlayer? = null

    private var playing = false

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
        }
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        readData()

        initMediaPlayer()

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
                stopAudio()
            }
        }

        closeButton.setOnClickListener {
            if (dialog.isShowing){
                dialog.dismiss()
            }
        }
    }

    private fun readData() {
        val result = arguments?.getParcelable<SessionResult>(ARGUMENT_SESSION_RESULT)
        if (result != null) {
            sessionScoreText.text = getString(R.string.your_score, result.data.score.toString())
            sessionTimeStampText.text = result.timestamp.toString()
            sessionLengthText.text = result.data.length.toString()
            audioUrl = "http://192.168.1.100:8000/file/${result.sId}"
        }
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
        //Invoked when the media source is ready for playback.
        playAudio()
    }

    private fun playAudio() {
        mediaPlayer?.start()
    }

    private fun stopAudio() {
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

    companion object {
        private val TAG = ResultDialog::class.java.simpleName
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