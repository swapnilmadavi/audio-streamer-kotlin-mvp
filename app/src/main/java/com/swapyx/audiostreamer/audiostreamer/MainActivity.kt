package com.swapyx.audiostreamer.audiostreamer

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CollapsingToolbarLayout
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.TextView
import kotlin.math.abs

class MainActivity : AppCompatActivity() {

    private val TAG = MainActivity::class.java.simpleName
    private lateinit var appBar: AppBarLayout
    private lateinit var collapsingToolbarLayout: CollapsingToolbarLayout
    private lateinit var toolbar: Toolbar
    private lateinit var tapText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        appBar = findViewById(R.id.appbar_main)
        collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar_main)
        toolbar = findViewById(R.id.toolbar_main)
        tapText = findViewById(R.id.homescreen_tap_text)

        setSupportActionBar(toolbar)

        // Disable Title to stop title transition
        collapsingToolbarLayout.isTitleEnabled = false

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

}
