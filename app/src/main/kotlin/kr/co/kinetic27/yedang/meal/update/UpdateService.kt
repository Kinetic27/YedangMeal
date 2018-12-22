package kr.co.kinetic27.yedang.meal.update

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import kr.co.kinetic27.yedang.meal.R
import kr.co.kinetic27.yedang.meal.Tools
import kr.co.kinetic27.yedang.meal.bap.BapActivity
import kr.co.kinetic27.yedang.meal.tools.Preference
import kr.co.kinetic27.yedang.meal.tools.ProcessTask
import java.lang.ref.WeakReference
import java.util.*

/**
* Created by Kinetic on 2015-12-01.
*/

class UpdateService : Service() {

    private lateinit var mCalendar: Calendar
    private lateinit var mPref: Preference
    private var mProcessTask: BapDownloadTask? = null
    private var showNotification: Boolean = false
    private var onlyWIFI: Boolean = false

    override fun onCreate() {
        super.onCreate()

        mCalendar = Calendar.getInstance()
        mPref = Preference(applicationContext)
        showNotification = mPref.getBoolean("updateNotifi", false)
        onlyWIFI = mPref.getBoolean("updateWiFi", true)

        if (Tools.isOnline(applicationContext)) {
            // 네트워크 연결됨
            if (onlyWIFI && !Tools.isWifi(applicationContext)) {
                // 와이파이에서만 업데이트 && 와이파이 연결안됨
                if (showNotification) {
                    // 상단바 알림
                    val updateAlarm = UpdateAlarm(this)
                    updateAlarm.wifiOFF()
                    val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    val pendingIntent = PendingIntent.getService(this, 0, Intent(this, UpdateService::class.java), PendingIntent.FLAG_UPDATE_CURRENT)

                    val mCompatBuilder = NotificationCompat.Builder(this, "Notify")
                    mCompatBuilder.setSmallIcon(R.drawable.ic_file_download)
                    mCompatBuilder.setTicker(getString(R.string.bapUpdateNotification_notification))
                    mCompatBuilder.setWhen(System.currentTimeMillis())
                    mCompatBuilder.setAutoCancel(true)
                    mCompatBuilder.setContentIntent(pendingIntent)
                    mCompatBuilder.setContentTitle(getString(R.string.no_wifi_title))
                    mCompatBuilder.setContentText(getString(R.string.no_wifi_msg))
                    nm.notify(1027, mCompatBuilder.build())
                }
                stopSelf()
            }

            // 토요일일경우 하루를 추가해줌
            if (mCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY)
                mCalendar.add(Calendar.DATE, 1)

            mProcessTask = BapDownloadTask(this)
            mProcessTask!!.execute(mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH))
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    class BapDownloadTask(mContext: Context) : ProcessTask(mContext) {
        private val netError = -2
        private val getError = -3
        private val success = 1

        private val notificationId = 1004
        private val downloadId = 1027
        private val activityReference: WeakReference<Context> = WeakReference(mContext)

        override fun onPreExecute() {
            super.onPreExecute()
            val activity = activityReference.get()

            val nm = activity!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


            val mCompatBuilder = NotificationCompat.Builder(activity, "Notify")
            mCompatBuilder.setSmallIcon(R.drawable.ic_file_download)
            mCompatBuilder.setTicker(activity.getString(R.string.bapUpdateNotification_ticker))
            mCompatBuilder.setWhen(System.currentTimeMillis())
            //        mCompatBuilder.setAutoCancel(true);
            mCompatBuilder.setContentTitle(activity.getString(R.string.bapUpdateNotification_title))
            mCompatBuilder.setContentText(activity.getString(R.string.bapUpdateNotification_msg))
            mCompatBuilder.setContentIntent(null)
            mCompatBuilder.setOngoing(true)

            nm.notify(downloadId, mCompatBuilder.build())
        }

        override fun onPostExecute(result: Long) {
            val activity = activityReference.get()
            (activity!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).cancel(downloadId)

            if (result == -1L) {
                // 급식 다운로드 실패
                if (Preference(activity).getBoolean("updateNotifi", false))
                    mNotification(getError)
                activity.stopService(Intent(activity, UpdateService::class.java))
                return
            }

            // 급식 다운로드 성공
            if (Preference(activity).getBoolean("updateNotifi", false))
                mNotification(success)
            activity.stopService(Intent(activity, UpdateService::class.java))
        }

        private fun mNotification(notificationCode: Int) {
            val activity = activityReference.get()
            val nm = activity!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            var pendingIntent = PendingIntent.getService(activity, 0, Intent(activity, UpdateService::class.java), PendingIntent.FLAG_UPDATE_CURRENT)

            val mCompatBuilder = NotificationCompat.Builder(activity, "Notify")
            mCompatBuilder.setSmallIcon(R.drawable.ic_file_download)
            mCompatBuilder.setTicker(activity.getString(R.string.bapUpdateNotification_notification))
            mCompatBuilder.setWhen(System.currentTimeMillis())
            mCompatBuilder.setAutoCancel(true)

            var mTitle = ""
            var mText = ""

            when (notificationCode) {
                netError -> {
                    mTitle = activity.getString(R.string.bapUpdate_Error_Net_title)
                    mText = activity.getString(R.string.bapUpdate_Error_Net_msg)
                }
                getError -> {
                    mTitle = activity.getString(R.string.bapUpdate_Error_get_title)
                    mText = activity.getString(R.string.bapUpdate_Error_get_msg)
                }
                success -> {
                    mTitle = activity.getString(R.string.bapUpdate_Success_title)
                    mText = activity.getString(R.string.bapUpdate_Success_msg)
                    pendingIntent = PendingIntent.getActivity(activity, 0, Intent(activity, BapActivity::class.java), PendingIntent.FLAG_UPDATE_CURRENT)
                }
            }

            mCompatBuilder.setContentIntent(pendingIntent)
            mCompatBuilder.setContentTitle(mTitle)
            mCompatBuilder.setContentText(mText)

            nm.notify(notificationId, mCompatBuilder.build())
        }
    }



}
