package com.swapyx.audiostreamer.audiostreamer.record

import com.swapyx.audiostreamer.audiostreamer.BasePresenter
import com.swapyx.audiostreamer.audiostreamer.BaseView

interface RecordContract {

    interface View : BaseView<Presenter> {
        fun isConnected() : Boolean

        fun startRecording()

        fun showNoInternetMessage()

        fun showBackOnlineMessage()

        fun setOnline(online: Boolean)

        fun stopRecording()

        fun setCurrentSId(sId: String)

        fun setRecordingStatus(status: Boolean)

        fun changeRecordButtonUi()

        fun cleanUpRecordingService()

        fun showRecordingError()

        fun showProgress()

        fun hideProgress()

        fun enableRecordButton()

        fun disableRecordButton()
    }

    interface Presenter : BasePresenter {
        fun startRecording()

        fun stopRecording()

        fun onNetworkStatusChanged(connected: Boolean)

        fun onStreamingStarted(sId: String)

        fun onStreamingStopped()

        fun onRecordingError()
    }
}