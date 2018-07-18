package com.swapyx.audiostreamer.audiostreamer.home

import com.swapyx.audiostreamer.audiostreamer.data.audioserver.model.SessionResult

class RecordFramePresenter(var recordView: RecordFrameContract.View?) : RecordFrameContract.Presenter{

    init {
        recordView?.presenter = this
    }

    override fun openRecordScreen() {
        recordView?.apply {
            if (isConnected()) {
                openRecordScreen()
            } else {
                showNoInternetMessage()
            }
        }
    }

    override fun onResultOK(result: SessionResult?) {
        recordView?.apply {
            if (result != null) {
                setResultFlagOnReturn()
                setResultOnReturn(result)
            } else {
                showSessionFailed()
            }
        }
    }

    override fun onDestroy() {
        recordView = null
    }

}