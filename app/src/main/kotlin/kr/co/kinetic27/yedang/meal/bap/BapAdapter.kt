package kr.co.kinetic27.yedang.meal.bap

import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.bap_item.view.*
import kr.co.kinetic27.yedang.meal.R
import kr.co.kinetic27.yedang.meal.tools.BapTool
import java.text.SimpleDateFormat
import java.util.*


/**
* Created by Kinetic on 2018-01-16.
*/

internal class BapAdapter(private val mData: ArrayList<BapData>) : RecyclerView.Adapter<BapAdapter.Holder>() {


    override fun onBindViewHolder(holder: Holder, position: Int) {
        val data = mData[position]

        with(holder.itemView) {

            setOnLongClickListener {
                with(context) {
                    startActivity(Intent.createChooser(Intent().apply {
                        action = Intent.ACTION_SEND
                        type = "text/plain"
                        addCategory(Intent.CATEGORY_DEFAULT)
                        putExtra(Intent.EXTRA_TITLE, data.mCalender)
                        putExtra(Intent.EXTRA_TEXT, String.format(getString(R.string.shareBap_message_msg), data.mCalender, data.mLunch, data.mLunchKcal))
                    }, getString(R.string.shareBap_title)))
                    true
                }
            }

            if (BapTool.mStringCheck(data.mLunch)) {
                data.mLunch = context.resources.getString(R.string.no_data_lunch)
                lunch_kcal.visibility = View.GONE
            } else lunch_kcal.visibility = View.VISIBLE

            title_layout.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary))
            calender!!.text = data.mCalender
            day_of_the_week!!.text = data.mDayOfTheWeek
            lunch!!.text = data.mLunch
            lunch_kcal!!.text = String.format(context.getString(R.string.kcal_message_msg), data.mLunchKcal)


            val currentTime = Calendar.getInstance()
            val year = currentTime.get(Calendar.YEAR)
            val month = currentTime.get(Calendar.MONTH) + 1
            val day = currentTime.get(Calendar.DAY_OF_MONTH)

            val mCal = GregorianCalendar()
            mCal.time = SimpleDateFormat("yyyy년 MM월 dd일", Locale.KOREA).parse(data.mCalender)
            if (mCal.get(Calendar.YEAR) == year && mCal.get(Calendar.MONTH) + 1 == month && mCal.get(Calendar.DAY_OF_MONTH) == day) today.visibility = View.VISIBLE else today.visibility = View.GONE
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = Holder(parent)


    override fun getItemCount(): Int = mData.size

    fun clearData() = mData.clear()

    inner class Holder(parent: ViewGroup)
        : RecyclerView.ViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.bap_item, parent, false))
}
