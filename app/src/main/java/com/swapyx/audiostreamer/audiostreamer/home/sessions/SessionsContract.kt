package com.swapyx.audiostreamer.audiostreamer.home.sessions

import com.swapyx.audiostreamer.audiostreamer.BasePresenter
import com.swapyx.audiostreamer.audiostreamer.BaseView
import com.swapyx.audiostreamer.audiostreamer.data.audioserver.model.Session

interface SessionsContract {

    interface View : BaseView<Presenter> {

        fun hideProgress()

        fun updateList(sessionList: List<Session>)

        fun showList()

        fun showError(error: String)

        fun isConnectedToNetwork(): Boolean

        fun setDataLoaded(dataLoaded: Boolean)

    }

    interface Presenter : BasePresenter {

        fun loadPastSessions()
    }
}