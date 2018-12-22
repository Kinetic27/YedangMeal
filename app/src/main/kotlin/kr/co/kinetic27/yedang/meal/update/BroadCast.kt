package kr.co.kinetic27.yedang.meal.update

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kr.co.kinetic27.yedang.meal.tools.BapTool
import kr.co.kinetic27.yedang.meal.tools.Preference
import java.util.*

class BroadCast : BroadcastReceiver() {
    private lateinit var mPref: Preference

    override fun onReceive(mContext: Context, mIntent: Intent) {
        val action = mIntent.action
        mPref = Preference(mContext)

        val autoUpdate = mPref.getBoolean("autoBapUpdate", false)
        if (!autoUpdate)
            return

        /**
         * 1 : 자동
         * 0 : 매주 토요일
         * -1 : 매주 일요일
         */
        val updateLife = Integer.parseInt(mPref.getString("updateLife", "0")!!)

        val mCalendar = Calendar.getInstance()
        val dayOfWeek = mCalendar.get(Calendar.DAY_OF_WEEK)

        /**
         * 부팅후 실행
         */
        if (Intent.ACTION_BOOT_COMPLETED == action) {
            if (dayOfWeek == Calendar.SUNDAY && updateLife == -1 || dayOfWeek == Calendar.SATURDAY && updateLife == 0) {
                if (haveToUpdate(mContext, mCalendar)) {
                    mContext.startService(Intent(mContext, UpdateService::class.java))
                }
            }

            val updateAlarm = UpdateAlarm(mContext)
            when (updateLife) {
                1 -> updateAlarm.autoUpdate()
                0 -> updateAlarm.saturdayUpdate()
                -1 -> updateAlarm.sundayUpdate()
            }

        } else if (BapTool.ACTION_UPDATE == action) {
            if (haveToUpdate(mContext, mCalendar))
                mContext.startService(Intent(mContext, UpdateService::class.java))
        }
    }

    private fun haveToUpdate(mContext: Context, mCalendar: Calendar): Boolean {
        val year = mCalendar.get(Calendar.YEAR)
        val month = mCalendar.get(Calendar.MONTH)
        val day = mCalendar.get(Calendar.DAY_OF_MONTH)

        val mData = BapTool.restoreBapData(mContext, year, month, day)

        return mData.isBlankDay
    }
}
