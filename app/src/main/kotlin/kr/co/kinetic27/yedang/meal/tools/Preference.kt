package kr.co.kinetic27.yedang.meal.tools

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

class Preference @SuppressLint("CommitPrefEdits") constructor(mContext: Context) {
    private var mPref: SharedPreferences? = null
    private var mEditor: SharedPreferences.Editor? = null

    init {
        mPref = PreferenceManager.getDefaultSharedPreferences(mContext)
        mEditor = mPref!!.edit()
    }

    /* @SuppressLint("CommitPrefEdits")
    constructor(mContext: Context, prefName: String) {
        mPref = mContext.getSharedPreferences(prefName, 0)
        mEditor = mPref!!.edit()
    }*/

    fun getBoolean(key: String, defValue: Boolean): Boolean =
            mPref!!.getBoolean(key, defValue)

    /*fun getInt(key: String, defValue: Int): Int {
        return mPref!!.getInt(key, defValue)
    }*/

    fun getString(key: String, defValue: String): String? =
            mPref!!.getString(key, defValue)

    fun putBoolean(key: String, value: Boolean) {
        mEditor!!.putBoolean(key, value).commit()
    }

    /* fun putInt(key: String, value: Int) {
         mEditor!!.putInt(key, value).commit()
     }

     fun putString(key: String, value: String) {
         mEditor!!.putString(key, value).commit()
     }

     fun clear() {
         mEditor!!.clear().commit()
     }

     fun remove(key: String) {
         mEditor!!.remove(key).commit()
     }*/
}