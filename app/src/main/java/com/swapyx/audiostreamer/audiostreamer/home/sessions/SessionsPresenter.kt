package com.swapyx.audiostreamer.audiostreamer.home.sessions

import com.swapyx.audiostreamer.audiostreamer.data.audioserver.model.Session
import com.swapyx.audiostreamer.audiostreamer.data.audioserver.source.AudioDataSource
import com.swapyx.audiostreamer.audiostreamer.data.audioserver.source.AudioRepository

class SessionsPresenter(
        var sessionsView: SessionsContract.View?,
        private var audioRepository: AudioRepository
) : SessionsContract.Presenter {

    init {
        sessionsView?.presenter = this
    }

    override fun loadPastSessions() {
        audioRepository.loadPastSessions(object : AudioDataSource.LoadPastSessionsListener {
            override fun onPastSessionsLoaded(sessionList: List<Session>) {
                if (sessionList.isEmpty()) {
                    with(sessionsView!!) {
                        hideProgress()
                        showError("No past sessions")
                    }
                } else {
                    with(sessionsView!!) {
                        hideProgress()
                        updateList(sessionList)
                        showList()
                    }
                }
            }

            override fun onFailure() {
                with(sessionsView!!) {
                    hideProgress()
                    showError("Fetching failed")
                }
            }
        })
    }

    override fun onDestroy() {
        sessionsView = null
    }

}