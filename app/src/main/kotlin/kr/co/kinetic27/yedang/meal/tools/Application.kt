package kr.co.kinetic27.yedang.meal.tools

/**
* Created by Kinetic on 2018-02-22.
*/

import com.linkedin.android.shaky.EmailShakeDelegate
import com.linkedin.android.shaky.Shaky
import kr.co.kinetic27.yedang.meal.R
import uk.co.chrisjenx.calligraphy.CalligraphyConfig

class Application : android.app.Application() {
    override fun onCreate() {
        super.onCreate()

        Shaky.with(this, EmailShakeDelegate("aheui@kakao.com"))

        CalligraphyConfig.initDefault(CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/NanumSquareR.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        )
    }
}
