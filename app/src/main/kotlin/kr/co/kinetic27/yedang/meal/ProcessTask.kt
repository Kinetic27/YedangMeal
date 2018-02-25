package kr.co.kinetic27.yedang.meal

import android.content.Context
import android.os.AsyncTask
import android.util.Log
import java.lang.ref.WeakReference

/**
* Created by Kinetic on 2015-02-17.
*/
abstract class ProcessTask(mContext: Context) : AsyncTask<Int, Int, Long>() {
    private val activityReference: WeakReference<Context> = WeakReference(mContext)
    abstract fun onPreDownload()

    abstract fun onUpdate(progress: Int)

    abstract fun onFinish(result: Long)

    override fun onPreExecute() {
        super.onPreExecute()
        onPreDownload()
    }

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

                BapTool.saveBapData(activity!!, calendar, lunch, lunchKcal)
            } catch (e: Exception) {
                Log.e("ProcessTask Error", "Message : " + e.message)
                Log.e("ProcessTask Error", "LocalizedMessage : " + e.localizedMessage)

                e.printStackTrace()
                -1L
            }

            return 0L
    }

    override fun onProgressUpdate(vararg values: Int?) {
        onUpdate(values[0]!!)
    }

    override fun onPostExecute(result: Long?) {
        super.onPostExecute(result)

        onFinish(result!!)
    }
}
