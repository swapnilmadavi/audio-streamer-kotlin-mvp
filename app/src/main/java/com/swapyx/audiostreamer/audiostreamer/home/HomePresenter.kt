package com.swapyx.audiostreamer.audiostreamer.home

import com.swapyx.audiostreamer.audiostreamer.data.audioserver.model.SessionResult

class HomePresenter(var recordView: HomeContract.View?) : HomeContract.Presenter{

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
                setPastSessionsDataAsDirty()
            } else {
                showSessionFailed()
            }
        }
    }

    override fun onDestroy() {
        recordView = null
    }

}