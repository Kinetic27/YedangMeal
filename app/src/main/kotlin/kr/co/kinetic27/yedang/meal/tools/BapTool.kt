@file:Suppress("NAME_SHADOWING")

package kr.co.kinetic27.yedang.meal.tools

import android.content.Context
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object BapTool {

    const val ACTION_UPDATE = "ACTION_BAP_UPDATE"

    private fun getBapStringFormat(year: Int, month: Int, day: Int, type: Int): String {
        var month = month
        /**
         * Format : year-month-day-TYPE
         */
        // Calendar의 month는 1이 부족하므로 1을 더해줌
        month += 1
        return year.toString() + "-" + month + "-" + day + "-" + type
    }

    /**
     * Pref Name Format : 2015-02-17-TYPE_index
     * ex) 2015-02-17-1_3
     */
    fun saveBapData(mContext: Context, calender: Array<String?>, lunch: Array<String?>, lunchKcal: Array<String?>) {
        val mPref = mContext.getSharedPreferences("BapData", 0)
        val edit = mPref.edit()
        val mFormat = SimpleDateFormat("yyyy.MM.dd(E)",
                Locale.KOREA)

        for (index in calender.indices) {
            try {
                val mDate = Calendar.getInstance()
                mDate.time = mFormat.parse(calender[index].toString())!!

                val year = mDate.get(Calendar.YEAR)
                val month = mDate.get(Calendar.MONTH)
                val day = mDate.get(Calendar.DAY_OF_MONTH)

                val mPrefLunchName = getBapStringFormat(year, month, day, 1)
                val mPrefLunchKcalName = getBapStringFormat(year, month, day, 2)

                var mLunch = lunch[index]
                var mLunchKcal = lunchKcal[index]

                if (!MealLibrary.isMealCheck(mLunch)) mLunch = ""
                if (!MealLibrary.isMealCheck(mLunchKcal)) mLunchKcal = ""

                edit.putString(mPrefLunchName, mLunch)
                edit.putString(mPrefLunchKcalName, mLunchKcal)
                edit.apply()

            } catch (e: ParseException) {
                e.printStackTrace()
            }

        }
    }

    /**
     * Format : 2015-2-11-2
     */
    fun restoreBapData(mContext: Context, year: Int, month: Int, day: Int): RestoreBapDateClass {
        val mPref = mContext.getSharedPreferences("BapData", 0)
        val mCalenderFormat = SimpleDateFormat("yyyy년 MM월 dd일", Locale.KOREA)
        val mDayOfWeekFormat = SimpleDateFormat("E요일", Locale.KOREA)
        val mDate = Calendar.getInstance()
        mDate.set(year, month, day)

        val mData = RestoreBapDateClass()
        val mPrefLunchName = getBapStringFormat(year, month, day, 1)
        val mPrefLunchKcalName = getBapStringFormat(year, month, day, 2)

        with(mData) {
            calender = mCalenderFormat.format(mDate.time)
            dayOfTheWeek = mDayOfWeekFormat.format(mDate.time)
            lunch = mPref.getString(mPrefLunchName, null)
            lunchKcal = mPref.getString(mPrefLunchKcalName, null)

            if (lunch == null || lunchKcal == null) {
                isBlankDay = true
            }
        }

        return mData
    }

    class RestoreBapDateClass {
        var calender: String? = null
        var dayOfTheWeek: String? = null
        var lunch: String? = null
        var lunchKcal: String? = null
        var isBlankDay = false
    }

    fun mStringCheck(mString: String?): Boolean {
        return mString == null || "" == mString || " " == mString
    }

    fun replaceString(mString: String): String = mString.split("\n").joinToString("\n") { it ->
        when {
            it.indexOf("(") != -1 -> it.substring(0, it.indexOf("("))
            else -> it
        }
    }.trim()
}