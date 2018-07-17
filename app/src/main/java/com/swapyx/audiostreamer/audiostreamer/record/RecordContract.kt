package com.swapyx.audiostreamer.audiostreamer.record

import com.swapyx.audiostreamer.audiostreamer.BasePresenter
import com.swapyx.audiostreamer.audiostreamer.BaseView
import com.swapyx.audiostreamer.audiostreamer.data.audioserver.model.SessionResult

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

        fun onRecordingCompleted()

        fun showRecordingError()

        fun showProgress()

        fun hideProgress()

        fun enableRecordButton()

        fun disableRecordButton()

        fun showResultMessage()

        fun updateTimer(currentTime: String)

        fun setResult(result: SessionResult)

        fun showResultError()

        fun setScreenONFlag()

        fun clearScreenONFlag()

        fun returnToHome()

        fun stopAndUnbindService()

        fun setSessionCancelled()

    }

    interface Presenter : BasePresenter {
        fun startRecording()

        fun stopRecording()

        fun onNetworkStatusChanged(connected: Boolean)

        fun onStreamingStarted(sId: String)

        fun onStreamingStopped()

        fun onRecordingError()

        fun fetchSessionResult(sId: String)

        fun onUpdateTime(timeInSeconds: Int)

        fun onAbortClicked()
    }
}