package com.swapyx.audiostreamer.audiostreamer.record

class RecordPresenter(var recordView: RecordContract.View?) : RecordContract.Presenter {

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
        }
    }

    override fun onStreamingStopped() {
        recordView?.apply {
            setRecordingStatus(false)
            changeRecordButtonUi()
            cleanUpRecordingService()
        }
    }

    override fun onRecordingError() {
        recordView?.apply {
            setRecordingStatus(false)
            changeRecordButtonUi()
            showRecordingError()
        }
    }

    override fun onDestroy() {
        recordView = null
    }

}