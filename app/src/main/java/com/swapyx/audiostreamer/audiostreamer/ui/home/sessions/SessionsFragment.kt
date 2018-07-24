package com.swapyx.audiostreamer.audiostreamer.ui.home.sessions

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast

import com.swapyx.audiostreamer.audiostreamer.R
import com.swapyx.audiostreamer.audiostreamer.ui.custom.DividerItemDecorator
import com.swapyx.audiostreamer.audiostreamer.data.audioserver.model.Session
import com.swapyx.audiostreamer.audiostreamer.data.audioserver.model.SessionResult

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [SessionsFragment.SessionsListListener] interface
 * to handle interaction events.
 * Use the [SessionsFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class SessionsFragment : Fragment(), SessionsContract.View {

    override lateinit var presenter: SessionsContract.Presenter

    private var listener: SessionsListListener? = null

    private lateinit var adapter: SessionsAdapter

    private lateinit var pastSessionsList: RecyclerView

    private lateinit var errorLabel: TextView

    private lateinit var swipeContainer: SwipeRefreshLayout

    private var dataLoaded = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_sessions, container, false)
        with(view) {
            pastSessionsList = findViewById(R.id.past_sessions_list)
            errorLabel = findViewById(R.id.error_text)
            swipeContainer = findViewById(R.id.swipeContainer)
        }

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val layoutManager = LinearLayoutManager(context)

        adapter = SessionsAdapter {
            Toast.makeText(context, "${it.sid} clicked!", Toast.LENGTH_SHORT).show()
            presenter.loadSession(it.sid)
        }

        pastSessionsList.layoutManager = layoutManager

        val dividerItemDecoration = DividerItemDecorator(
                ContextCompat.getDrawable(context!!, R.drawable.list_divider)!!)

        pastSessionsList.addItemDecoration(dividerItemDecoration)

        pastSessionsList.adapter = adapter

        swipeContainer.setOnRefreshListener {
            presenter.loadPastSessions()
        }

        swipeContainer.isRefreshing = true

        //presenter.loadPastSessions()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            listener = context as SessionsListListener
        } catch (e: ClassCastException) {
            throw ClassCastException(context.toString() + " must implement SessionsListListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun hideProgress() {
        swipeContainer.isRefreshing = false
    }

    override fun updateList(sessionList: List<Session>) {
        pastSessionsList.post{
            adapter.updateList(sessionList)
        }
    }

    override fun clearList() {
        pastSessionsList.post {
            adapter.clearList()
        }
    }

    override fun showList() {
        pastSessionsList.visibility = View.VISIBLE
    }

    override fun hideList() {
        pastSessionsList.visibility = View.GONE
    }

    override fun showError(error: String) {
        errorLabel.text = error
        showErrorLabel()
    }

    override fun hideErrorText() {
        errorLabel.visibility = View.GONE
    }

    override fun setDataLoaded(dataLoaded: Boolean) {
        this.dataLoaded = dataLoaded
    }

    fun isDataLoaded() = dataLoaded

    override fun isConnectedToNetwork(): Boolean {
        return listener?.isConnectedToNetwork() ?: false
    }

    override fun showResultDialog() {
        listener?.showPendingResultDialog()
    }

    override fun relayResult(result: SessionResult) {
        Log.d(TAG, "relayResult")
        listener?.setResultForDialog(result)
    }

    override fun showResultError() {
        listener?.showResultError()
    }

    override fun dismissResultDialog() {
        listener?.dismissResultDialog()
    }

    private fun showErrorLabel() {
        errorLabel.visibility = View.VISIBLE
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface SessionsListListener {

        fun isConnected() : Boolean

        fun isConnectedToNetwork(): Boolean

        fun showPendingResultDialog()

        fun setResultForDialog(result: SessionResult)

        fun showResultError()

        fun dismissResultDialog()
    }

    companion object {
        private val TAG = SessionsFragment::class.java.simpleName
        @JvmStatic
        fun newInstance() =
                SessionsFragment()
    }
}
