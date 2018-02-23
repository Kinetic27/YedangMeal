package kr.co.kinetic27.yedang.meal

import android.content.Context
import android.net.ConnectivityManager
import android.os.AsyncTask
import android.support.design.widget.FloatingActionButton
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import com.fourmob.datetimepicker.date.DatePickerDialog
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper
import java.lang.ref.WeakReference
import java.util.*

@Suppress("NAME_SHADOWING")
/**
* Created by Kinetic on 2018-01-16.
*/

class BapActivity : BaseActivity() {
    override var viewId: Int = R.layout.activity_bap
    override var toolbarId: Int? = R.id.toolbar

    private var mAdapter: BapAdapter? = null
    private var recyclerView: RecyclerView? = null
    private var mCalendar: Calendar? = null
    private var mSecondCal: Calendar? = null
    private val items = ArrayList<BapData>()

    private var year: Int = 0
    private var month: Int = 0
    private var day: Int = 0
    private var dayOfWeek: Int = 0
    private var week: Int = 0

    private var mProgressBar: ProgressBar? = null
    private var mProcessTask: BapDownloadTask? = null

    internal var mSwipeRefreshLayout: SwipeRefreshLayout? = null

    override fun onCreate() {
        showActionBar()
        setToolbarTitle("예당고 급식")
        getCalendarInstance(true)
        mProgressBar = findViewById(R.id.progressbar)
        recyclerView = findViewById(R.id.mRecyclerView)
        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
        recyclerView!!.adapter = BapAdapter(items)
        mAdapter = recyclerView!!.adapter as BapAdapter?

        mSwipeRefreshLayout = findViewById(R.id.mSwipeRefreshLayout)
        mSwipeRefreshLayout!!.setOnRefreshListener {
            getCalendarInstance(true)
            getBapList(true)
            if (mSwipeRefreshLayout!!.isRefreshing)
                mSwipeRefreshLayout!!.isRefreshing = false
        }

        val mFab = findViewById<FloatingActionButton>(R.id.mFab)
        mFab.setOnClickListener { setCalenderBap() }

        getBapList(true)
    }

    private fun getCalendarInstance(getInstance: Boolean) {
        if (getInstance || mCalendar == null)
            mCalendar = Calendar.getInstance()
        year = mCalendar!!.get(Calendar.YEAR)
        month = mCalendar!!.get(Calendar.MONTH)
        day = mCalendar!!.get(Calendar.DAY_OF_MONTH)
        dayOfWeek = mCalendar!!.get(Calendar.DAY_OF_WEEK)
        week = mCalendar!!.get(Calendar.WEEK_OF_MONTH)
    }

    private fun getBapList(isUpdate: Boolean) {
        val isNetwork = isOnline(this)

        mAdapter!!.clearData()
        mAdapter!!.notifyDataSetChanged()

        getCalendarInstance(false)

        // 이번주 월요일 날짜를 가져온다
        mCalendar!!.add(Calendar.DATE, 2 - dayOfWeek)
        mSecondCal = mCalendar

        for (i in 0..4) {
            val year = mCalendar!!.get(Calendar.YEAR)
            val month = mCalendar!!.get(Calendar.MONTH)
            val day = mCalendar!!.get(Calendar.DAY_OF_MONTH)

            val mData = BapTool.restoreBapData(this, year, month, day)

            if (mData.isBlankDay) {
                if (isUpdate && isNetwork) {
                    mProgressBar!!.visibility = View.VISIBLE
                    mProcessTask = BapDownloadTask(this)
                    mProcessTask!!.execute(year, month, day)
                } else {
                    val builder = AlertDialog.Builder(this, R.style.AppCompatErrorAlertDialogStyle)
                    builder.setTitle(R.string.no_network_title)
                    builder.setMessage(R.string.no_network_msg)
                    builder.setPositiveButton(android.R.string.ok, null)
                    builder.show()
                }
                return
            }

            items.add(BapData(mData.calender!!, mData.dayOfTheWeek!!, BapTool.replaceString(mData.lunch!!), mData.lunchKcal!!))
            mCalendar!!.add(Calendar.DATE, 1)
        }

        mCalendar!!.set(year, month, day)
        recyclerView!!.adapter.notifyDataSetChanged()

        if (mAdapter!!.itemCount > 0) {
            recyclerView!!.scrollToPosition(0)
            if (dayOfWeek in 2..6 && mAdapter!!.itemCount == 5)
                recyclerView!!.smoothScrollToPosition(dayOfWeek - 2)
        }
    }

    private fun setCalenderBap() {
        getCalendarInstance(false)

        val year = mCalendar!!.get(Calendar.YEAR)
        val month = mCalendar!!.get(Calendar.MONTH)
        val day = mCalendar!!.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog.newInstance({ _, year, month, day ->
            mCalendar!!.set(year, month, day)
            getCalendarInstance(false)
            getBapList(true)
        }, year, month, day, false)

        datePickerDialog.setYearRange(2008, year + 2)
        datePickerDialog.setCloseOnSingleTapDay(false)
        datePickerDialog.show(supportFragmentManager, "Tag")
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_bap, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        when (id) {
            R.id.action_calender -> {
                setCalenderBap()

                return true

            }
            R.id.action_refresh -> {
                val isNetwork = isOnline(this)

                if (isNetwork) {
                    getCalendarInstance(false)

                    mCalendar!!.add(Calendar.DATE, 2 - dayOfWeek)

                    val mPrefLunchName = BapTool.getBapStringFormat(year, month, day, 1)

                    val mPref = applicationContext.getSharedPreferences("BapData", 0)
                    val edit = mPref.edit()
                    edit.remove(mPrefLunchName)
                    edit.apply()
                    getBapList(true)
                } else {
                    val builder = AlertDialog.Builder(this, R.style.AppCompatErrorAlertDialogStyle)
                    builder.setTitle(R.string.no_network_title)
                    builder.setMessage(R.string.no_network_msg)
                    builder.setPositiveButton(android.R.string.ok, null)
                    builder.show()
                }

                return true
            }
            R.id.action_today -> {
                getCalendarInstance(true)
                getBapList(true)

                return true

            }
            else -> return super.onOptionsItemSelected(item)
        }

    }

    private class BapDownloadTask internal constructor(mContext: BapActivity) : AsyncTask<Int, Int, Long>() {
        private val activityReference: WeakReference<BapActivity> = WeakReference(mContext)

        internal fun onFinish(result: Long) {
            val activity = activityReference.get()
            activity!!.mProgressBar!!.visibility = View.GONE
            if (result == (-1).toLong()) {
                val builder = AlertDialog.Builder(activity, R.style.AppCompatErrorAlertDialogStyle)
                builder.setTitle(R.string.I_do_not_know_the_error_title)
                builder.setMessage(R.string.I_do_not_know_the_error_message)
                builder.setPositiveButton(android.R.string.ok, null)
                builder.show()

                return
            }

            activity.getBapList(false)
            if (activity.mSwipeRefreshLayout!!.isRefreshing)
                activity.mSwipeRefreshLayout!!.isRefreshing = false
        }


        /**
         * params[0] : year
         * params[1] : month
         * params[2] : day
         */
        override fun doInBackground(vararg params: Int?): Long {
            val activity = activityReference.get()
            publishProgress(5)

            val countryCode = "goe.go.kr" // 접속 할 교육청 도메인
            val schulCode = "J100005580" // 학교 고유 코드
            val schulCrseScCode = "4" // 학교 종류 코드 1
            val schulKndScCode = "04" // 학교 종류 코드 2

            val year = Integer.toString(params[0]!!)
            var month = Integer.toString(params[1]!! + 1)
            var day = Integer.toString(params[2]!!)

            if (month.length <= 1)
                month = "0" + month
            if (day.length <= 1)
                day = "0" + day

            try {
                val calendar = MealLibrary.getDateNew(countryCode, schulCode,
                        schulCrseScCode, schulKndScCode, year, month, day)
                val lunch = MealLibrary.getMealNew(countryCode, schulCode,
                        schulCrseScCode, schulKndScCode, "2", year, month, day)
                val lunchKcal = MealLibrary.getKcalNew(countryCode, schulCode,
                        schulCrseScCode, schulKndScCode, "2", year, month, day)

                activity!!.mProgressBar!!.visibility = View.VISIBLE

                BapTool.saveBapData(activity, calendar, lunch, lunchKcal)
            } catch (e: Exception) {
                Log.e("ProcessTask Error", "Message : " + e.message)
                Log.e("ProcessTask Error", "LocalizedMessage : " + e.localizedMessage)

                e.printStackTrace()
                -1L
            }

            return 0L
        }

        override fun onPostExecute(result: Long?) {
            super.onPostExecute(result)
            onFinish(result!!)
        }

    }

    private fun isOnline(mContext: Context): Boolean {
        val mManager = mContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return mManager.activeNetworkInfo != null && mManager.activeNetworkInfo.isConnectedOrConnecting
    }

    override fun attachBaseContext(newBase: Context) = super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))

}
