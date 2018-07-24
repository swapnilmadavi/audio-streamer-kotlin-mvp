package com.swapyx.audiostreamer.audiostreamer.ui.home.sessions

import com.swapyx.audiostreamer.audiostreamer.BasePresenter
import com.swapyx.audiostreamer.audiostreamer.BaseView
import com.swapyx.audiostreamer.audiostreamer.data.audioserver.model.Session
import com.swapyx.audiostreamer.audiostreamer.data.audioserver.model.SessionResult

interface SessionsContract {

    interface View : BaseView<Presenter> {

        fun hideProgress()

        fun updateList(sessionList: List<Session>)

        fun showList()

        fun showError(error: String)

        fun isConnectedToNetwork(): Boolean

        fun setDataLoaded(dataLoaded: Boolean)

        fun showResultDialog()

        fun relayResult(result: SessionResult)

        fun showResultError()

        fun dismissResultDialog()

        fun clearList()

        fun hideList()

        fun hideErrorText()
    }

    interface Presenter : BasePresenter {

        fun loadPastSessions()

        fun loadSession(sid: String)
    }
}