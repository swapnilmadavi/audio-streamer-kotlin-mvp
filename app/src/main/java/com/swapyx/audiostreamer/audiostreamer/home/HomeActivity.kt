package com.swapyx.audiostreamer.audiostreamer.home

import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager.CONNECTIVITY_ACTION
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CollapsingToolbarLayout
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.swapyx.audiostreamer.audiostreamer.R
import com.swapyx.audiostreamer.audiostreamer.record.RecordActivity
import com.swapyx.audiostreamer.audiostreamer.util.showToastMessage
import kotlin.math.abs
import com.swapyx.audiostreamer.audiostreamer.NetworkChangeReceiver
import com.swapyx.audiostreamer.audiostreamer.data.audioserver.model.SessionData
import com.swapyx.audiostreamer.audiostreamer.data.audioserver.model.SessionResult
import com.swapyx.audiostreamer.audiostreamer.home.result.ResultDialog


class HomeActivity : AppCompatActivity(), RecordFrameContract.View, NetworkChangeReceiver.NetworkChangeListener {

    override lateinit var presenter: RecordFrameContract.Presenter

    private val TAG = HomeActivity::class.java.simpleName
    private lateinit var appBar: AppBarLayout
    private lateinit var collapsingToolbarLayout: CollapsingToolbarLayout
    private lateinit var toolbar: Toolbar
    private lateinit var tapText: TextView
    private lateinit var recordButton: ImageButton

    private lateinit var networkChangeReceiver: NetworkChangeReceiver

    private var online = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        appBar = findViewById(R.id.appbar_main)
        collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar_main)
        toolbar = findViewById(R.id.toolbar_main)
        tapText = findViewById(R.id.homescreen_tap_text)
        recordButton = findViewById(R.id.homescreen_record_button)

        setSupportActionBar(toolbar)

        // Disable Title to stop title transition
        collapsingToolbarLayout.isTitleEnabled = false

        setUpAppBar()

        networkChangeReceiver = NetworkChangeReceiver(this)

        RecordFramePresenter(this)

        recordButton.setOnClickListener {
            presenter.openRecordScreen()
        }
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(networkChangeReceiver, IntentFilter(CONNECTIVITY_ACTION))
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(networkChangeReceiver)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // Check which request we're responding to
        if (requestCode == RECORD_REQUEST_CODE) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                val result =  data?.extras?.getParcelable<SessionResult>(RecordActivity.SESSION_RESULT)
                showToastMessage(result.toString(), Toast.LENGTH_LONG)
                showResultDialog()
            }
        }

    }

    private fun showResultDialog() {
        val sessionData = SessionData(4, 12.56, 145123)
        val sessionResult = SessionResult("XBSGDCB", true, 554221, sessionData)

        val fm = supportFragmentManager
        val abortRecordingDialogFragment = ResultDialog.newInstance(sessionResult)
        abortRecordingDialogFragment.show(fm, "fragment_result")
    }

    override fun isConnected() = online

    override fun onNetworkStatusChanged(connected: Boolean) {
        if (connected) {
            if (!online) {
                showToastMessage("Back online", Toast.LENGTH_SHORT)
                online = true
            }
        } else {
            showNoInternetMessage()
            online = false
        }
    }

    override fun openRecordScreen() {
        /*val intent = Intent(this, RecordActivity::class.java)
        startActivityForResult(intent, RECORD_REQUEST_CODE)*/
        showResultDialog()
    }

    override fun showNoInternetMessage() {
        showToastMessage("No internet connection", Toast.LENGTH_SHORT)
    }

    private fun setUpAppBar() {
        appBar.addOnOffsetChangedListener(object : AppBarLayout.OnOffsetChangedListener {
            var isShowing = false
            var scrollRange = -1
            var difference = -1
            var offset = 0

            override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout!!.totalScrollRange
                    difference = scrollRange - toolbar.height
                }

                offset = abs(verticalOffset)

                if (offset >= difference) {
                    if(isShowing) {
                        isShowing = false
                        hideTapText()
                    }
                } else if (!isShowing) {
                    isShowing = true
                    showTapText()
                }
            }
        })
    }

    private fun hideTapText() {
        tapText.visibility = View.INVISIBLE
    }

    private fun showTapText() {
        tapText.visibility = View.VISIBLE
    }

    companion object {
        const val RECORD_REQUEST_CODE = 1
    }

}
