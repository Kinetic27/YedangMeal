package kr.co.kinetic27.yedang.meal

import android.content.Context
import android.content.SharedPreferences

/**
 * Created by Kinetic on 2018-02-24.
 */
class IntroManager(private var context: Context) {

    private var sharedPreferences: SharedPreferences = context.getSharedPreferences("first", 0)
    private var editor: SharedPreferences.Editor  = sharedPreferences.edit()

    fun setFirst(isFirst: Boolean?) {

        editor.putBoolean("check", isFirst!!)
        editor.commit()
    }

    fun check(): Boolean {
        return sharedPreferences.getBoolean("check", true)
    }
}