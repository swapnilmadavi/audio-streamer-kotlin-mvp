package com.swapyx.audiostreamer.audiostreamer.record

import android.util.Log
import com.swapyx.audiostreamer.audiostreamer.data.audioserver.model.SessionResult
import com.swapyx.audiostreamer.audiostreamer.data.audioserver.source.AudioDataSource
import com.swapyx.audiostreamer.audiostreamer.data.audioserver.source.AudioRepository
import com.swapyx.audiostreamer.audiostreamer.data.result.source.ResultDataSource
import com.swapyx.audiostreamer.audiostreamer.data.result.source.ResultRepository
import java.util.*


class RecordPresenter(
        var recordView: RecordContract.View?,
        var audioRepository: AudioRepository
) : RecordContract.Presenter {

    init {
        recordView?.presenter = this
    }

    override fun startRecording() {
        if (recordView?.isConnected() == true) {
            recordView?.apply {
                showProgress()
                disableRecordButton()
                startRecording()
            }
        } else {
            recordView?.showNoInternetMessage()
        }
    }

    override fun stopRecording() {
        recordView?.apply {
            showProgress()
            disableRecordButton()
            stopRecording()
        }
    }

    override fun onNetworkStatusChanged(connected: Boolean) {
        if (connected) {
            if (recordView?.isConnected() != true) {
                recordView?.apply {
                    showBackOnlineMessage()
                    setOnline(true)
                }
            }
        } else {
            recordView?.apply {
                showNoInternetMessage()
                setOnline(false)
            }
        }
    }

    override fun onStreamingStarted(sId: String) {
        recordView?.apply {
            setCurrentSId(sId)
            setRecordingStatus(true)
            changeRecordButtonUi()
            setScreenONFlag()
        }
    }

    override fun onStreamingStopped() {
        recordView?.apply {
            setRecordingStatus(false)
            updateTimer("00:00")
            changeRecordButtonUi()
            cleanUpRecordingService()
            clearScreenONFlag()
        }
    }

    override fun onRecordingError() {
        recordView?.apply {
            setRecordingStatus(false)
            changeRecordButtonUi()
            clearScreenONFlag()
            showRecordingError()
        }
    }

    override fun waitingForResult(sId: String) {
        recordView?.apply {
            showResultMessage()
            showProgress()
            disableRecordButton()
        }

        audioRepository.loadSessionResult(sId, object: AudioDataSource.LoadSessionListener {
            override fun onSessionResultLoaded(result: SessionResult) {
                Log.d("RecordPresenter", result.toString())
                recordView?.apply{
                    showResult(result.toString())
                    changeRecordButtonUi()
                }

            }

            override fun onFailure() {
                recordView?.apply {
                    showResultError()
                    changeRecordButtonUi()
                }
            }

        })
    }

    override fun onUpdateTime(timeInSeconds: Int) {
        val min = timeInSeconds/60
        val sec = timeInSeconds%60

        val currentTime = String.format("%s:%s",
                String.format(Locale.US, "%02d", min),
                String.format(Locale.US, "%02d", sec))

        recordView?.updateTimer(currentTime)
    }

    override fun onDestroy() {
        recordView = null
    }

}