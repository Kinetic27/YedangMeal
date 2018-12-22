package kr.co.kinetic27.yedang.meal

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.wifi.WifiManager

/**
* Created by Kinetic on 2015-12-02.
*/
object Tools {

    /**
     * https://stackoverflow.com/questions/49819923/kotlin-checking-network-status-using-connectivitymanager-returns-null-if-networ
     */
    fun isOnline(mContext: Context): Boolean {
        val connectivityManager = mContext.getSystemService(Context.CONNECTIVITY_SERVICE)
        return if (connectivityManager is ConnectivityManager) {
            val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
            networkInfo?.isConnected ?: false
        } else false
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
