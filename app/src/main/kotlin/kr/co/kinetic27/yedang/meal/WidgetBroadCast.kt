package kr.co.kinetic27.yedang.meal

import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import java.util.*

class WidgetBroadCast : BroadcastReceiver() {

    override fun onReceive(mContext: Context, mIntent: Intent) {
        val action = mIntent.action

        val appWidgetManager = AppWidgetManager.getInstance(mContext)

        val appWidgetIds = appWidgetManager.getAppWidgetIds(ComponentName(mContext, BapWidget::class.java))

        for (appWidgetId in appWidgetIds)
            BapWidget.updateAppWidget(mContext, appWidgetManager, appWidgetId, false)

        if (Intent.ACTION_BOOT_COMPLETED == action) {
            // 24시간마다 앱 위젯 업데이트하기
            val mCalendar = Calendar.getInstance()
            val mAlarm = mContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            val mIntentDate = Intent(mContext, WidgetBroadCast::class.java)
            val mPending = PendingIntent.getBroadcast(mContext, 0, mIntentDate, 0)
            mCalendar.set(mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH) + 1, 1, 0)
            mAlarm.setRepeating(AlarmManager.RTC_WAKEUP, mCalendar.timeInMillis, (24 * 60 * 60 * 1000).toLong(), mPending)
        }
    }
}
