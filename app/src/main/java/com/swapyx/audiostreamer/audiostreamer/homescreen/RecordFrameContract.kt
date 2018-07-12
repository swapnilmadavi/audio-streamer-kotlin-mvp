package com.swapyx.audiostreamer.audiostreamer.homescreen

import com.swapyx.audiostreamer.audiostreamer.BasePresenter
import com.swapyx.audiostreamer.audiostreamer.BaseView

interface RecordFrameContract {

    interface View : BaseView<Presenter> {
        fun isConnected() : Boolean

        fun openRecordScreen()

        fun showNoInternetMessage()
    }

    interface Presenter : BasePresenter {
        fun openRecordScreen()
    }
}