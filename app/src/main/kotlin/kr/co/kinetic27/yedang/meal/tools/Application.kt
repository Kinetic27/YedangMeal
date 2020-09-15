package kr.co.kinetic27.yedang.meal.tools

/**
* Created by Kinetic on 2018-02-22.
*/

import com.linkedin.android.shaky.EmailShakeDelegate
import com.linkedin.android.shaky.Shaky
import io.github.inflationx.calligraphy3.CalligraphyConfig
import io.github.inflationx.calligraphy3.CalligraphyInterceptor
import io.github.inflationx.viewpump.ViewPump
import kr.co.kinetic27.yedang.meal.R


class Application : android.app.Application() {
    override fun onCreate() {
        super.onCreate()

        Shaky.with(this, EmailShakeDelegate("aheui@kakao.com"))

        ViewPump.init(ViewPump.builder()
                .addInterceptor(CalligraphyInterceptor(
                        CalligraphyConfig.Builder()
                                .setDefaultFontPath("fonts/NanumSquareR.ttf")
                                .setFontAttrId(R.attr.fontPath)
                                .build()))
                .build())
    }
}
