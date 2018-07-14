package com.swapyx.audiostreamer.audiostreamer.record

import android.Manifest
import android.content.*
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import com.github.jorgecastilloprz.FABProgressCircle
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.widget.Toolbar
import android.widget.Toast
import com.swapyx.audiostreamer.audiostreamer.NetworkChangeReceiver

import com.swapyx.audiostreamer.audiostreamer.R
import com.swapyx.audiostreamer.audiostreamer.RecordingService
import com.swapyx.audiostreamer.audiostreamer.util.showToastMessage
import com.swapyx.audiostreamer.audiostreamer.RecordingService.LocalBinder
import android.os.IBinder
import android.util.Log
import android.view.WindowManager
import android.widget.TextView
import com.swapyx.audiostreamer.audiostreamer.data.RemoteClient
import com.swapyx.audiostreamer.audiostreamer.data.result.source.ResultRepository
import com.swapyx.audiostreamer.audiostreamer.data.result.source.remote.ResultRemoteDataSource


class RecordActivity : AppCompatActivity(), RecordContract.View,
        NetworkChangeReceiver.NetworkChangeListener, RecordingService.StreamServiceListener {

    override lateinit var presenter: RecordContract.Presenter

    private var TAG = RecordActivity::class.java.simpleName
    private lateinit var fabProgressCircle: FABProgressCircle
    private lateinit var recordButton: FloatingActionButton
    private lateinit var timerText: TextView

    private lateinit var networkChangeReceiver: NetworkChangeReceiver

    private var recordingService: RecordingService? = null

    private var currentSId: String = ""

    private var bound = false


    private var online = true

    private var recording = false

    /** Defines callbacks for service binding, passed to bindService()  */
    private val serviceConnection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName,
                                        service: IBinder) {
            // We've bound to RecordingService, cast the IBinder and get RecordingService instance
            val binder = service as LocalBinder
            recordingService = binder.service
            bound = true
            recordingService?.setStreamServiceListener(this@RecordActivity)
            if (!recording) {
                recordingService?.startRecording()
            }
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            bound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_record)

        setSupportActionBar(findViewById<Toolbar>(R.id.toolbar_record))
        supportActionBar?.apply {
            setDisplayShowTitleEnabled(false)
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        fabProgressCircle = findViewById<FABProgressCircle>(R.id.fabProgressCircle)
        recordButton = findViewById<FloatingActionButton>(R.id.record_fab)
        timerText = findViewById<TextView>(R.id.timer_text)

        timerText.text = "00:00"

        networkChangeReceiver = NetworkChangeReceiver(this)

        RecordPresenter(
                this,
                ResultRepository(ResultRemoteDataSource.getInstance(RemoteClient.client))
        )

        recordButton.setOnClickListener {
            startStopRecording()
        }

        if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.RECORD_AUDIO), REQUEST_RECORD_AUDIO_PERMISSION)
        }
    }

    override fun onStart() {
        super.onStart()

        if (recording) {
            // Bind to RecordingService
            val intent = Intent(this, RecordingService::class.java)
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(networkChangeReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(networkChangeReceiver)
    }

    override fun onStop() {
        super.onStop()
        if (bound) {
            unbindService(serviceConnection)
            bound = false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (recording) {
            try {
                val intent = Intent(this, RecordingService::class.java)
                stopService(intent)
            } catch (e: Exception) {
                Log.d(TAG, "onDestroy error => ${e.localizedMessage}")
            }
        }

        presenter.onDestroy()
    }

    override fun onNetworkStatusChanged(connected: Boolean) {
        presenter.onNetworkStatusChanged(connected)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {
        var permissionToRecordAccepted = false
        when (requestCode) {
            REQUEST_RECORD_AUDIO_PERMISSION ->
                permissionToRecordAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
        }
        if (!permissionToRecordAccepted) finish()
    }

    override fun isConnected() = online

    override fun setOnline(online: Boolean) {
        this.online = online
    }

    override fun showBackOnlineMessage() {
        showToastMessage("Back online", Toast.LENGTH_SHORT)
    }

    override fun showNoInternetMessage() {
        showToastMessage("No internet connection", Toast.LENGTH_SHORT)
    }

    override fun setCurrentSId(sId: String) {
        currentSId = sId
    }

    override fun setRecordingStatus(status: Boolean) {
        recording = status
    }

    override fun startRecording() {
        startRecordingService()
    }

    override fun stopRecording() {
        stopRecordingService()
    }

    override fun onStreamingStarted(sId: String) {
        presenter.onStreamingStarted(sId)
    }

    override fun onStreamingStopped() {
        presenter.onStreamingStopped()
    }

    override fun onRecordingError() {
        presenter.onRecordingError()
    }

    override fun changeRecordButtonUi() {
        hideProgress()
        enableRecordButton()
        changeRecordButtonIcon()
    }

    private fun startRecordingService() {
        val intent = Intent(this, RecordingService::class.java)
        startService(intent)
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    private fun stopRecordingService() {
        recordingService?.stopRecording()
    }

    override fun cleanUpRecordingService() {
        val intent = Intent(this, RecordingService::class.java)
        if (bound) {
            unbindService(serviceConnection)
            bound = false
        }
        stopService(intent)

        presenter.waitingForResult(currentSId)
    }

    override fun showProgress() {
        fabProgressCircle.show()
    }

    override fun hideProgress() {
        fabProgressCircle.hide()
    }

    override fun enableRecordButton() {
        recordButton.isEnabled = true
    }

    override fun disableRecordButton() {
        recordButton.isEnabled = false
    }

    override fun setScreenONFlag() {
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    override fun clearScreenONFlag() {
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    override fun showRecordingError() {
        showToastMessage("Recording error", Toast.LENGTH_SHORT)
    }

    override fun showResultMessage() {
        showToastMessage("Fetching results", Toast.LENGTH_SHORT)
    }

    override fun showResult(result: String) {
        showToastMessage(result, Toast.LENGTH_LONG)
    }

    override fun showResultError() {
        showToastMessage("Failed to fetch the result", Toast.LENGTH_SHORT)
    }

    override fun onUpdateTime(timeInSeconds: Int) {
        presenter.onUpdateTime(timeInSeconds)
    }

    override fun updateTimer(currentTime: String) {
        timerText.text = currentTime
    }

    private fun changeRecordButtonIcon() {
        if (recording) {
            recordButton.setImageResource(R.drawable.ic_stop_red_44dp)
        } else {
            recordButton.setImageResource(R.drawable.ic_mic_red_44dp)
        }
    }

    private fun startStopRecording() {
        if (!recording) {
            presenter.startRecording()
        } else {
            presenter.stopRecording()
        }
    }

    companion object {
        private const val REQUEST_RECORD_AUDIO_PERMISSION = 200
    }
}
