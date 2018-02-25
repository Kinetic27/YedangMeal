package kr.co.kinetic27.yedang.meal

import android.content.Context
import android.net.ConnectivityManager
import android.net.wifi.WifiManager


/**
* Created by Kinetic on 2015-12-02.
*/
object Tools {

    /**
     * http://stackoverflow.com/questions/1560788/how-to-check-internet-access-on-android-inetaddress-never-timeouts
     */
    fun isOnline(mContext: Context): Boolean {
        val mManager = mContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = mManager.activeNetworkInfo
        return netInfo != null && netInfo.isConnectedOrConnecting
    }

    fun isWifi(mContext: Context): Boolean {
        val wifiMgr = mContext.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        return if (wifiMgr.isWifiEnabled) {
            val wifiInfo = wifiMgr.connectionInfo
            wifiInfo.networkId != -1
        } else {
            false
        }
    }
}
