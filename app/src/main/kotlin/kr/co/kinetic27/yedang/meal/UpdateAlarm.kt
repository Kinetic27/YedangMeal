package kr.co.kinetic27.yedang.meal

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.util.*

class UpdateAlarm(private val mContext: Context) {
    private val mAlarm: AlarmManager = mContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private var mPendingIntent: PendingIntent? = null
    private val mCalendar: Calendar = Calendar.getInstance()

    fun autoUpdate() {
        val year = mCalendar.get(Calendar.YEAR)
        val month = mCalendar.get(Calendar.MONTH)
        val day = mCalendar.get(Calendar.DAY_OF_MONTH)

        val mIntent = Intent(mContext, BroadCast::class.java)
        mIntent.action = BapTool.ACTION_UPDATE
        mPendingIntent = PendingIntent.getBroadcast(mContext, 0, mIntent, 0)
        mCalendar.set(year, month, day + 1, 1, 0)
        mAlarm.setRepeating(AlarmManager.RTC_WAKEUP, mCalendar.timeInMillis, (4 * 24 * 60 * 60 * 1000).toLong(), mPendingIntent)
    }

    fun sundayUpdate() {
        val year = mCalendar.get(Calendar.YEAR)
        val month = mCalendar.get(Calendar.MONTH)
        val dayOfWeek = mCalendar.get(Calendar.DAY_OF_WEEK)

        // 저번주 일요일을 구한다
        mCalendar.add(Calendar.DAY_OF_WEEK, -1 * (dayOfWeek - 1))
        // 다음주 일요일을 구한다
        mCalendar.add(Calendar.DAY_OF_WEEK, 7)

        val day = mCalendar.get(Calendar.DAY_OF_MONTH)

        val mIntent = Intent(mContext, BroadCast::class.java)
        mIntent.action = BapTool.ACTION_UPDATE
        mPendingIntent = PendingIntent.getBroadcast(mContext, 0, mIntent, 0)
        mCalendar.set(year, month, day, 1, 0)
        mAlarm.setRepeating(AlarmManager.RTC_WAKEUP, mCalendar.timeInMillis, (7 * 24 * 60 * 60 * 1000).toLong(), mPendingIntent)
    }

    fun saturdayUpdate() {
        val year = mCalendar.get(Calendar.YEAR)
        val month = mCalendar.get(Calendar.MONTH)
        val dayOfWeek = mCalendar.get(Calendar.DAY_OF_WEEK)

        // 저번주 일요일을 구한다
        mCalendar.add(Calendar.DAY_OF_WEEK, -1 * (dayOfWeek - 1))
        // 다음주 토요일을 구한다
        mCalendar.add(Calendar.DAY_OF_WEEK, 6)

        val day = mCalendar.get(Calendar.DAY_OF_MONTH)

        val mIntent = Intent(mContext, BroadCast::class.java)
        mIntent.action = BapTool.ACTION_UPDATE
        mPendingIntent = PendingIntent.getBroadcast(mContext, 0, mIntent, 0)
        mCalendar.set(year, month, day, 1, 0)
        mAlarm.setRepeating(AlarmManager.RTC_WAKEUP, mCalendar.timeInMillis, (7 * 24 * 60 * 60 * 1000).toLong(), mPendingIntent)
    }

    fun wifiOFF() {
        val year = mCalendar.get(Calendar.YEAR)
        val month = mCalendar.get(Calendar.MONTH)
        val day = mCalendar.get(Calendar.DAY_OF_MONTH)
        val hour = mCalendar.get(Calendar.HOUR_OF_DAY)

        val mIntent = Intent(mContext, BroadCast::class.java)
        mIntent.action = BapTool.ACTION_UPDATE
        mPendingIntent = PendingIntent.getBroadcast(mContext, 0, mIntent, 0)
        mCalendar.set(year, month, day, hour + 2, 0)
        mAlarm.set(AlarmManager.RTC_WAKEUP, mCalendar.timeInMillis, mPendingIntent)
    }

    /*fun cancel() {
        val mIntent = Intent(mContext, BroadCast::class.java)
        mIntent.action = BapTool.ACTION_UPDATE
        mPendingIntent = PendingIntent.getBroadcast(mContext, 0, mIntent, 0)

        mAlarm.cancel(mPendingIntent)
    }*/

}
