package com.swapyx.audiostreamer.audiostreamer.home.sessions

import com.swapyx.audiostreamer.audiostreamer.data.audioserver.source.AudioRepository

class SessionsPresenter(
        var sessionsView: SessionsContract.View?,
        var audioRepository: AudioRepository
) : SessionsContract.Presenter {

    init {
        sessionsView?.presenter = this
    }

    override fun onDestroy() {
        sessionsView = null
    }

}