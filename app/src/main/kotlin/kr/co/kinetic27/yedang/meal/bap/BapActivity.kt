package kr.co.kinetic27.yedang.meal.bap

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import io.github.yavski.fabspeeddial.FabSpeedDial
import io.github.yavski.fabspeeddial.SimpleMenuListenerAdapter
import kr.co.kinetic27.yedang.meal.R
import kr.co.kinetic27.yedang.meal.Tools
import kr.co.kinetic27.yedang.meal.setting.SettingsActivity
import kr.co.kinetic27.yedang.meal.tools.Application
import kr.co.kinetic27.yedang.meal.tools.BapTool
import kr.co.kinetic27.yedang.meal.tools.BaseActivity
import kr.co.kinetic27.yedang.meal.tools.ProcessTask
import java.lang.ref.WeakReference
import java.util.*
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog
import io.github.inflationx.viewpump.ViewPumpContextWrapper

@Suppress("NAME_SHADOWING")
/**
* Created by Kinetic on 2018-01-16.
*/

class BapActivity : BaseActivity() {
    override var viewId: Int = R.layout.activity_bap
    override var toolbarId: Int? = R.id.toolbar
    private var application: Application? = null

    private var mAdapter: BapAdapter? = null
    private var recyclerView: RecyclerView? = null
    private var mCalendar: Calendar? = null
    private val items = ArrayList<BapData>()

    private var year: Int = 0
    private var month: Int = 0
    private var day: Int = 0
    private var dayOfWeek: Int = 0
    private var week: Int = 0

    private var mProcessTask: BapDownloadTask? = null

    internal var mSwipeRefreshLayout: SwipeRefreshLayout? = null
    private var pDialog: SweetAlertDialog? = null

    override fun onCreate() {
        application = applicationContext as Application

        showActionBar()
        setToolbarTitle(resources.getString(R.string.app_name))
        getCalendarInstance(true)

        recyclerView = findViewById(R.id.mRecyclerView)
        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recyclerView!!.adapter = BapAdapter(items)
        mAdapter = recyclerView!!.adapter as BapAdapter?

        mSwipeRefreshLayout = findViewById(R.id.mSwipeRefreshLayout)
        mSwipeRefreshLayout!!.setOnRefreshListener {
            getCalendarInstance(true)
            getBapList(true)
            if (mSwipeRefreshLayout!!.isRefreshing)
                mSwipeRefreshLayout!!.isRefreshing = false
        }

        getBapList(true)

        val fabSpeedDial = findViewById<FabSpeedDial>(R.id.fab_menu_list)
        fabSpeedDial.setMenuListener(object : SimpleMenuListenerAdapter() {
            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.fab_today -> {
                        getCalendarInstance(true)
                        getBapList(true)
                    }

                    R.id.fab_calender -> setCalenderBap()

                    R.id.fab_setting ->
                        startActivity(Intent(this@BapActivity, SettingsActivity::class.java))
                }
                return false
            }
        })
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
        val isNetwork = Tools.isOnline(this)

        mAdapter!!.clearData()
        mAdapter!!.notifyDataSetChanged()

        getCalendarInstance(false)

        // 이번주 월요일 날짜를 가져온다
        mCalendar!!.add(Calendar.DATE, 2 - dayOfWeek)
        Log.d("getData", "${Calendar.DATE}, 2 - $dayOfWeek")

        (0..4).forEach { _ ->
            val year = mCalendar!!.get(Calendar.YEAR)
            val month = mCalendar!!.get(Calendar.MONTH)
            val day = mCalendar!!.get(Calendar.DAY_OF_MONTH)

            val mData = BapTool.restoreBapData(this, year, month, day)

            if (mData.isBlankDay) {
                if (isUpdate && isNetwork) {
                    pDialog = SweetAlertDialog(this@BapActivity, SweetAlertDialog.PROGRESS_TYPE).apply {
                        progressHelper.barColor = ContextCompat.getColor(this@BapActivity, R.color.colorPrimary)
                        titleText = "Loading"
                        setCancelable(false)
                    }
                    mProcessTask = BapDownloadTask(this)
                    mProcessTask!!.execute(year, month, day)
                    pDialog!!.show()
                } else {
                    SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText(resources.getString(R.string.no_network_title))
                            .setContentText(resources.getString(R.string.no_network_msg))
                            .show()
                }
                return
            }

            items.add(BapData(mData.calender!!, mData.dayOfTheWeek!!, BapTool.replaceString(mData.lunch!!), mData.lunchKcal!!))
            mCalendar!!.add(Calendar.DATE, 1)
        }

        mCalendar!!.set(year, month, day)
        recyclerView!!.adapter?.notifyDataSetChanged()

        if (mAdapter!!.itemCount > 0) {
            recyclerView!!.scrollToPosition(0)
            if (dayOfWeek in 2..6)
                recyclerView!!.smoothScrollToPosition(dayOfWeek - 2)
        }
    }

    private fun setCalenderBap() {
        getCalendarInstance(false)

        val nowYear = mCalendar!!.get(Calendar.YEAR)
        val nowMonth = mCalendar!!.get(Calendar.MONTH)
        val nowDay = mCalendar!!.get(Calendar.DAY_OF_MONTH)

        /*DatePickerDialog.newInstance({ _, year, month, day ->
            mCalendar!!.set(year, month, day)
            getCalendarInstance(false)
            getBapList(true)
        }, year, month, day, false).apply {

            setDateConstraints(MonthAdapter.CalendarDay(2008, 1, 1), MonthAdapter.CalendarDay(year + 2, 1, 1))
            setCloseOnSingleTapDay(false)
            show(supportFragmentManager, "Tag")
        }*/

        DatePickerDialog.newInstance({ _, year, month, day ->
            mCalendar!!.set(year, month, day)
            getCalendarInstance(false)
            getBapList(true)
        }, nowYear, nowMonth, nowDay).apply {
            minDate = Calendar.getInstance().apply { set(2008, 1, 1) }
            maxDate = Calendar.getInstance().apply { set(year + 2, 1, 1) }
            version = DatePickerDialog.Version.VERSION_2
            accentColor = ContextCompat.getColor(this@BapActivity, R.color.colorPrimary)
            show(supportFragmentManager, "Tag")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_bap, menu)
        return true

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.action_today -> {
                getCalendarInstance(true)
                getBapList(true)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    }

    private class BapDownloadTask(mBap: BapActivity): ProcessTask(mBap) {
        private val activityReference: WeakReference<BapActivity> = WeakReference(mBap)

        override fun onPostExecute(result: Long?) {
            super.onPostExecute(result)

            val activity = activityReference.get()
            activity!!.pDialog!!.hide()
            if (result == (-1).toLong()) {
                SweetAlertDialog(activity, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText(activity.resources.getString(R.string.I_do_not_know_the_error_title))
                        .setContentText(activity.resources.getString(R.string.I_do_not_know_the_error_message))
                        .show()

                return
            }

            activity.getBapList(false)
            if (activity.mSwipeRefreshLayout!!.isRefreshing)
                activity.mSwipeRefreshLayout!!.isRefreshing = false
        }
    }

    override fun attachBaseContext(newBase: Context) = super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase))

    override fun onBackPressed() {

        SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE).apply {
            titleText = "Are you sure?"
            contentText = "앱을 종료하시겠습니까?"
            cancelText = "아니요"
            setCancelClickListener(null)
            confirmText = "네"
            setConfirmClickListener { sDialog ->
                sDialog.dismissWithAnimation()
                finish()
            }
            show()
        }
    }
}