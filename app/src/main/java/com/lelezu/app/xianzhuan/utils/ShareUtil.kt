package com.lelezu.app.xianzhuan.utils

import android.content.Context
import android.content.SharedPreferences
import com.lelezu.app.xianzhuan.MyApplication

/**
 * @author:Administrator
 * @date:2023/7/20 0020
 * @description:
 *
 */
object ShareUtil {

    private var sps: SharedPreferences? = null

    private const val APP_SHARED_PREFERENCES_KEY: String = "ApiPrefs"

    const val APP_SHARED_PREFERENCES_LOGIN_TOKEN: String = "LoginToken"
    const val APP_SHARED_PREFERENCES_LOGIN_ID: String = "LoginId"
    const val APP_SHARED_PREFERENCES_LOGIN_STATUS: String = "LoginStatus"


    //网易token
    const val APP_163_PHONE_LOGIN_DEVICE_TOKEN: String = "deviceToken"
    const val APP_163_PHONE_LOGIN_MOBILE_ACCESS_TOKEN: String = "mobileAccessToken"
    const val APP_163_PHONE_LOGIN_MOBILE_TOKEN: String = "mobileToken"


    private val applicationContext by lazy { MyApplication.context }

    private fun getSps(): SharedPreferences {
        if (sps == null) {
            sps = applicationContext?.getSharedPreferences(
                APP_SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE
            )
        }
        return sps!!
    }

    fun putString(key: String, value: String?) {
        if (!value.isNullOrBlank()) {
            val editor: SharedPreferences.Editor = getSps().edit()
            editor.putString(key, value)
            editor.apply()
        }
    }

    fun getString(key: String): String? {
        if (key.isNotBlank()) {
            val sps: SharedPreferences = getSps()
            return sps.getString(key, "")
        }
        return null
    }

    fun putBoolean(key: String, value: Boolean) {

        val editor: SharedPreferences.Editor = getSps().edit()
        editor.putBoolean(key, value)
        editor.apply()

    }

    fun getBoolean(key: String): Boolean {

        val sps: SharedPreferences = getSps()
        return sps.getBoolean(key, false)

    }

    fun clean(key: String) {

        val editor: SharedPreferences.Editor = getSps().edit()
        editor.remove(key)
        editor.apply()
    }

}