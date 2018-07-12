package com.swapyx.audiostreamer.audiostreamer.homescreen

class RecordFramePresenter(var recordView: RecordFrameContract.View?) : RecordFrameContract.Presenter{

    init {
        recordView?.presenter = this
    }

    override fun openRecordScreen() {
        if (recordView?.isConnected() == true) {
            recordView?.openRecordScreen()
        } else {
            recordView?.showNoInternetMessage()
        }
    }

    override fun onDestroy() {
        recordView = null
    }

}