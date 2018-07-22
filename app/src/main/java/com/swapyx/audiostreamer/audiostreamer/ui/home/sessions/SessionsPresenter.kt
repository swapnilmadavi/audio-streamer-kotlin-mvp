package com.swapyx.audiostreamer.audiostreamer.ui.home.sessions

import android.util.Log
import com.swapyx.audiostreamer.audiostreamer.data.audioserver.model.Session
import com.swapyx.audiostreamer.audiostreamer.data.audioserver.model.SessionResult
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
        if (sessionsView?.isConnectedToNetwork() == true) {
            fetchPastSessions()
        } else {
            with(sessionsView!!) {
                hideProgress()
                showError("No internet connection")
            }
        }
    }

    private fun fetchPastSessions() {
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
                        setDataLoaded(true)
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

    override fun loadSession(sid: String) {
        sessionsView?.showResultDialog()

        fetchSessionResult(sid)
    }

    private fun fetchSessionResult(sid: String) {
        audioRepository.loadSessionResult(sid, object: AudioDataSource.LoadSessionResultListener {
            override fun onSessionResultLoaded(result: SessionResult) {
                Log.d("RecordPresenter", result.toString())
                sessionsView?.relayResult(result)
            }

            override fun onFailure() {
                sessionsView?.apply {
                    showResultError()
                    dismissResultDialog()
                }
            }

        })
    }

    override fun onDestroy() {
        sessionsView = null
    }

    fun setDataAsDirty() {
        sessionsView?.setDataLoaded(false)
    }

}