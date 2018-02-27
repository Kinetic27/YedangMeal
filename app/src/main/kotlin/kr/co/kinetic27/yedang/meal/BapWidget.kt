package kr.co.kinetic27.yedang.meal

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.RemoteViews
import java.lang.ref.WeakReference
import java.util.*

/**
 * Implementation of App Widget functionality.
 */
class BapWidget : AppWidgetProvider() {

    override fun onUpdate(mContext: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds)
            updateAppWidget(mContext, appWidgetManager, appWidgetId, false )

    }

    override fun onReceive(mContext: Context, mIntent: Intent) {
        super.onReceive(mContext, mIntent)

        val mAction = mIntent.action
        if (mAction == AppWidgetManager.ACTION_APPWIDGET_UPDATE) {
            val appWidgetManager = AppWidgetManager.getInstance(mContext)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(ComponentName(mContext, BapWidget::class.java))

            for (appWidgetId in appWidgetIds)
                updateAppWidget(mContext, appWidgetManager, appWidgetId, false)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }


    private class BapDownloadTask(mBap: Context) : ProcessTask(mBap) {

        private val activityReference: WeakReference<Context> = WeakReference(mBap)

        override fun onPostExecute(result: Long?) {
            super.onPreExecute()
            val activity = activityReference.get()
            val mIntent = Intent(activity, WidgetBroadCast::class.java)
            activity!!.sendBroadcast(mIntent)
        }
    }

    override fun onAppWidgetOptionsChanged(mContext: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int, newOptions: Bundle) {
        super.onAppWidgetOptionsChanged(mContext, appWidgetManager, appWidgetId, newOptions)
        updateAppWidget(mContext, appWidgetManager, appWidgetId, false)
    }

    companion object {

        internal fun updateAppWidget(mContext: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int, ifNotUpdate: Boolean) {
            val mViews = RemoteViews(mContext.packageName, R.layout.widget_bap)
            val mCalendar = Calendar.getInstance()
            val year = mCalendar.get(Calendar.YEAR)
            val month = mCalendar.get(Calendar.MONTH)
            val day = mCalendar.get(Calendar.DAY_OF_MONTH)
            val mData = BapTool.restoreBapData(mContext, year, month, day)

            val mBundle = appWidgetManager.getAppWidgetOptions(appWidgetId)
            val maxWidth = mBundle.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH)
            val maxHeight = mBundle.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT)
            //Log.v("Widgets", "$maxHeight / $maxWidth")
            if (maxWidth >= 320 && maxHeight >= 320 ) {
                mViews.setTextViewTextSize(R.id.mLunchTitle, TypedValue.COMPLEX_UNIT_SP, 30.toFloat())
                mViews.setTextViewTextSize(R.id.mLunch, TypedValue.COMPLEX_UNIT_SP, 25.toFloat())
                mViews.setTextViewTextSize(R.id.mCalender, TypedValue.COMPLEX_UNIT_SP, 33.toFloat())
            } else if(maxHeight >= 250) {
                mViews.setTextViewTextSize(R.id.mLunchTitle, TypedValue.COMPLEX_UNIT_SP, 22.toFloat())
                mViews.setTextViewTextSize(R.id.mLunch, TypedValue.COMPLEX_UNIT_SP, 17.toFloat())
                mViews.setTextViewTextSize(R.id.mCalender, TypedValue.COMPLEX_UNIT_SP, 17.toFloat())
            } else {
                mViews.setTextViewTextSize(R.id.mLunchTitle, TypedValue.COMPLEX_UNIT_SP, 20.toFloat())
                mViews.setTextViewTextSize(R.id.mLunch, TypedValue.COMPLEX_UNIT_SP, 12.toFloat())
                mViews.setTextViewTextSize(R.id.mCalender, TypedValue.COMPLEX_UNIT_SP, 17.toFloat())
            }

            mViews.setTextViewText(R.id.mCalender, mData.calender)

            if (mData.isBlankDay) {
                // 데이터 없음
                if (Tools.isOnline(mContext)) {
                    // Only Wifi && Not Wifi
                    if (Preference(mContext).getBoolean("updateWiFi", true)) {
                        mViews.setViewVisibility(R.id.lunch_kcal, View.GONE)
                        mViews.setTextViewText(R.id.mLunch, mContext.getString(R.string.widget_no_data))
                    } else if (ifNotUpdate) {
                        val mProcessTask = BapDownloadTask(mContext as BapActivity)
                        mProcessTask.execute(year, month, day)
                    }
                } else {

                    mViews.setViewVisibility(R.id.lunch_kcal, View.GONE)
                    mViews.setTextViewText(R.id.mLunch, mContext.getString(R.string.widget_no_data))
                }
            } else {

                // 데이터 있음

                var mTodayMeal: String = mData.lunch!!
                mTodayMeal = if (BapTool.mStringCheck(mTodayMeal)) {
                    mViews.setViewVisibility(R.id.lunch_kcal, View.GONE)
                    mContext.getString(R.string.no_data_lunch)
                } else {
                    mViews.setViewVisibility(R.id.lunch_kcal, View.VISIBLE)
                    BapTool.replaceString(mTodayMeal)
                }
                mViews.setTextViewText(R.id.day_of_the_week, mData.dayOfTheWeek)
                mViews.setTextViewText(R.id.mLunch, mTodayMeal)
                mViews.setTextViewText(R.id.lunch_kcal, String.format(mContext.getString(R.string.kcal_message_msg), mData.lunchKcal))
            }

            val layoutPendingIntent = PendingIntent.getActivity(mContext, 0, Intent(mContext, BapActivity::class.java), 0)
            mViews.setOnClickPendingIntent(R.id.mWidgetLayout, layoutPendingIntent)

            val mIntent = Intent(mContext, WidgetBroadCast::class.java)
            val updatePendingIntent = PendingIntent.getBroadcast(mContext, 0, mIntent, 0)
            mViews.setOnClickPendingIntent(R.id.mUpdateLayout, updatePendingIntent)

            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetId, mViews)
        }
    }
}
