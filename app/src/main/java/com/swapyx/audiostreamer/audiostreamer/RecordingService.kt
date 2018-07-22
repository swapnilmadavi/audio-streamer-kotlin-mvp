package com.swapyx.audiostreamer.audiostreamer

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.media.AudioFormat
import android.media.MediaRecorder
import android.os.IBinder
import android.util.Log

import android.media.AudioRecord
import android.os.AsyncTask
import android.os.Binder
import android.os.Handler
import com.swapyx.audiostreamer.audiostreamer.data.RemoteClientProvider
import okhttp3.*
import okio.ByteString
import org.json.JSONObject
import android.app.PendingIntent
import android.support.v4.app.NotificationCompat
import com.swapyx.audiostreamer.audiostreamer.ui.record.RecordActivity


class RecordingService : Service() {

    private var TAG = RecordingService::class.java.simpleName

    // Binder given to clients
    private val binder = LocalBinder()

    private var recordingAsyncTask: RecordingAsyncTask? = null
    private lateinit var client: OkHttpClient
    private lateinit var webSocket: WebSocket
    private var streamServiceListener: StreamServiceListener? = null

    private lateinit var request: Request

    private lateinit var listener: RecordingService.StreamSocketListener

    private lateinit var messageHandler: Handler

    private lateinit var timeHandler: Handler

    private var recording = false

    private var timeInSeconds = 0

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun onCreate() {
        super.onCreate()
        client = RemoteClientProvider.provideRemoteClient()

        request = Request.Builder().url("ws://192.168.1.101:8000/stream").build()
        listener = StreamSocketListener()

        messageHandler = Handler{ msg ->
            when (msg.what) {
                0 -> streamServiceListener?.onStreamingStarted(msg.obj as String)
                1 -> streamServiceListener?.onStreamingStopped()
                2 -> streamServiceListener?.onRecordingError()
            }
            true
        }

        timeHandler = Handler{ msg ->
            when (msg.what) {
                0 -> {
                    streamServiceListener?.onUpdateTime(timeInSeconds)
                    timeInSeconds++
                    this.timeHandler.sendEmptyMessageDelayed(0, 1000)
                }
            }
            true
        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.d(TAG, "service started")
        return Service.START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()

        if (recording) {
            if (recordingAsyncTask != null) {
                recordingAsyncTask!!.cancel(true)
            }
        }

        try {
            timeHandler.removeMessages(0)
            messageHandler.removeMessages(0)
            messageHandler.removeMessages(1)
            messageHandler.removeMessages(2)
        } catch (e: Exception) {
            Log.d(TAG, e.localizedMessage)
        }
    }

    fun setStreamServiceListener(listener: StreamServiceListener) {
        streamServiceListener = listener
        Log.d(TAG, "stream service listener set")
    }

    fun startRecording() {
        Log.d(TAG, "start recording")
        webSocket = client.newWebSocket(request, listener)
        startForeground(NOTIFICATION_ID, getNotification());
    }

    fun stopRecording() {
        Log.d(TAG, "stop recording")
        recordingAsyncTask!!.cancel(true)
        stopForeground(true)
    }


    /**
     * Returns the [NotificationCompat] used as part of the foreground service.
     */
    private fun getNotification(): Notification {

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentText("Recording...")
                .setContentTitle(getString(R.string.app_name))
                .setOngoing(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setWhen(System.currentTimeMillis())

        // The PendingIntent to launch activity.
        val resultIntent = Intent(this, RecordActivity::class.java)
        val resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT)
        builder.setContentIntent(resultPendingIntent)

        return builder.build()
    }


    inner class RecordingAsyncTask : AsyncTask<Void, Void, Void>() {

        private var recorder: AudioRecord? = null

        override fun doInBackground(vararg params: Void?): Void? {
            recorder = AudioRecord(MediaRecorder.AudioSource.MIC,
                    RECORDING_RATE, CHANNEL_IN, AUDIO_FORMAT, BUFFER_SIZE)
            try {
                val buffer = ByteArray(BUFFER_SIZE)

                timeInSeconds = 0

                timeHandler.sendEmptyMessage(0)

                recorder!!.startRecording()
                recording = true

                while (!isCancelled) {

                    val read = recorder!!.read(buffer, 0, buffer.size)

                    webSocket.send(ByteString.of(buffer,0,buffer.size))
                }

            } catch (e : Exception) {
                Log.e(TAG, "Recording interrupted: ${e.localizedMessage}")
            } finally {
                recorder!!.apply {
                    stop()
                    release()
                }

                try {
                    timeHandler.removeMessages(0)
                } catch (e: Exception) {

                }

                webSocket.close(1000, "STOP")

                val m = messageHandler.obtainMessage(1)
                messageHandler.sendMessage(m)

                recording = false
                recorder = null
            }

            return null
        }

        override fun onPostExecute(result: Void?) {
            recordingAsyncTask = null
        }

        override fun onCancelled() {
            super.onCancelled()
            recordingAsyncTask = null
        }
    }

    inner class StreamSocketListener : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket?, response: Response?) {
            Log.d(TAG, "onOpen -> ${response.toString()}")
        }

        override fun onFailure(webSocket: WebSocket?, t: Throwable?, response: Response?) {
            Log.d(TAG, "onFailure -> ${t.toString()}, ${response.toString()}")
        }

        override fun onClosing(webSocket: WebSocket?, code: Int, reason: String?) {
            Log.d(TAG, "onClosing -> code: $code, reason: $reason")
        }

        override fun onMessage(webSocket: WebSocket?, text: String?) {
            Log.d(TAG, "onMessage -> $text")
            try{
                val json = JSONObject(text)
                if (json.length() == 1) {
                    val sId = json.getString("sid")
                    val m = messageHandler.obtainMessage(0, sId)
                    messageHandler.sendMessage(m)
                    recordingAsyncTask = RecordingAsyncTask()
                    recordingAsyncTask!!.execute()
                }
            } catch (e : Exception) {
                Log.d(TAG, "onMessage error=> ${e.localizedMessage}")
                val m = messageHandler.obtainMessage(2)
                messageHandler.sendMessage(m)
            }
        }

        override fun onMessage(webSocket: WebSocket?, bytes: ByteString?) {
            Log.d(TAG, "onMessage -> ${bytes!!.hex()}")
        }

        override fun onClosed(webSocket: WebSocket?, code: Int, reason: String?) {
            Log.d(TAG, "onClosed -> code: $code, reason: $reason")
        }
    }

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    inner class LocalBinder : Binder() {
        internal// Return this instance of RecordingService so clients can call public methods
        val service: RecordingService
            get() = this@RecordingService
    }

    interface StreamServiceListener {
        fun onStreamingStarted(sId: String)
        fun onStreamingStopped()
        fun onRecordingError()
        fun onUpdateTime(timeInSeconds: Int)
    }

    companion object {
        /**
         * The identifier for the notification displayed for the foreground service.
         */
        private const val NOTIFICATION_ID = 12345678


        private const val CHANNEL_ID = "channel_01"

        private const val RECORDING_RATE = 16000

        private const val CHANNEL_IN = AudioFormat.CHANNEL_IN_MONO

        private const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT

        private const val BUFFER_SIZE_FACTOR = 2

        private val BUFFER_SIZE = AudioRecord.getMinBufferSize(RECORDING_RATE,
                CHANNEL_IN, AUDIO_FORMAT) * BUFFER_SIZE_FACTOR
    }
}
