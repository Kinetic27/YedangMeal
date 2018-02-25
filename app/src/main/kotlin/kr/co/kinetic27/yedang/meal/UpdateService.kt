package kr.co.kinetic27.yedang.meal

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.support.v4.app.NotificationCompat
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

    private val wifiError = -1
    private val netError = -2
    private val getError = -3
    private val success = 1

    private val downloadId = 1027
    private val notificationId = 1004

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
                    mNotification(wifiError)
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

    @SuppressLint("StaticFieldLeak")
    inner class BapDownloadTask(mContext: Context) : ProcessTask(mContext) {

        override fun onPreDownload() {
            startServiceNotification()
        }

        override fun onUpdate(progress: Int) {

        }

        override fun onFinish(result: Long) {
            val nm = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.cancel(downloadId)

            if (result == -1L) {
                // 급식 다운로드 실패
                if (showNotification)
                    mNotification(getError)
                stopSelf()
                return
            }

            // 급식 다운로드 성공
            if (showNotification)
                mNotification(success)
            stopSelf()
        }
    }

    private fun startServiceNotification() {
        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


        val mCompatBuilder = NotificationCompat.Builder(this, "Notify")
        mCompatBuilder.setSmallIcon(R.drawable.ic_file_download)
        mCompatBuilder.setTicker(getString(R.string.bapUpdateNotification_ticker))
        mCompatBuilder.setWhen(System.currentTimeMillis())
        //        mCompatBuilder.setAutoCancel(true);
        mCompatBuilder.setContentTitle(getString(R.string.bapUpdateNotification_title))
        mCompatBuilder.setContentText(getString(R.string.bapUpdateNotification_msg))
        mCompatBuilder.setContentIntent(null)
        mCompatBuilder.setOngoing(true)

        nm.notify(downloadId, mCompatBuilder.build())
    }


    private fun mNotification(notificationCode: Int) {
        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        var pendingIntent = PendingIntent.getService(this, 0, Intent(this, UpdateService::class.java), PendingIntent.FLAG_UPDATE_CURRENT)

        val mCompatBuilder = NotificationCompat.Builder(this)
        mCompatBuilder.setSmallIcon(R.drawable.ic_file_download)
        mCompatBuilder.setTicker(getString(R.string.bapUpdateNotification_notification))
        mCompatBuilder.setWhen(System.currentTimeMillis())
        mCompatBuilder.setAutoCancel(true)

        var mTitle = ""
        var mText = ""

        when (notificationCode) {
            wifiError -> {
                mTitle = getString(R.string.bapUpdate_Error_Net_title)
                mText = getString(R.string.bapUpdate_Error_Net_msg)
            }
            netError -> {
                mTitle = getString(R.string.bapUpdate_Error_Net_title)
                mText = getString(R.string.bapUpdate_Error_Net_msg)
            }
            getError -> {
                mTitle = getString(R.string.bapUpdate_Error_get_title)
                mText = getString(R.string.bapUpdate_Error_get_msg)
            }
            success -> {
                mTitle = getString(R.string.bapUpdate_Success_title)
                mText = getString(R.string.bapUpdate_Success_msg)
                pendingIntent = PendingIntent.getActivity(this, 0, Intent(this, BapActivity::class.java), PendingIntent.FLAG_UPDATE_CURRENT)
            }
        }

        mCompatBuilder.setContentIntent(pendingIntent)
        mCompatBuilder.setContentTitle(mTitle)
        mCompatBuilder.setContentText(mText)

        nm.notify(notificationId, mCompatBuilder.build())
    }
}
