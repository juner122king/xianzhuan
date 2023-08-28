package com.lelezu.app.xianzhuan.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.provider.Settings
import com.lelezu.app.xianzhuan.MyApplication
import com.lelezu.app.xianzhuan.data.model.LoginReP
import com.lelezu.app.xianzhuan.data.model.Register
import java.util.regex.Pattern

/**
 * @author:Administrator
 * @date:2023/7/20 0020
 * @description:
 *
 */
object ShareUtil {

    private var sps: SharedPreferences? = null

    private const val APP_SHARED_PREFERENCES_KEY: String = "ApiPrefs"

    private const val APP_NETWORK_IS_CONNECTED: String = "IS_CONNECTED"//网络是否连接


    const val APP_PRIVACY_AGREEMENT_AGREE: String = "PRIVACY_AGREEMENT"//是否同意隐私协议
    const val APP_USER_AGREEMENT_AGREE: String = "USER_AGREEMENT"//是否同意用户协议


    //用户登录
    const val APP_SHARED_PREFERENCES_LOGIN_TOKEN: String = "LoginToken"
    const val APP_SHARED_PREFERENCES_LOGIN_ID: String = "LoginId"
    const val APP_SHARED_PREFERENCES_RECOMMEND_USERID: String = "recommendUserId"
    const val APP_SHARED_PREFERENCES_LOGIN_STATUS: String = "LoginStatus"


    //网易token
    const val APP_163_PHONE_LOGIN_DEVICE_TOKEN: String = "deviceToken"
    const val APP_163_PHONE_LOGIN_MOBILE_ACCESS_TOKEN: String = "mobileAccessToken"
    const val APP_163_PHONE_LOGIN_MOBILE_TOKEN: String = "mobileToken"

    const val APP_DEVICE_ANDROIDID: String = "AndroidID"
    const val APP_DEVICE_SIM: String = "SIM"
    const val APP_DEVICE_IMSI: String = "IMSI"
    const val APP_DEVICE_MAC: String = "MAC"

    //图片下载
    const val APP_TASK_PIC_DOWN_URL: String = "PIC_DOWN_URL"


    const val TAGMYTASK = "isMyTask"


    private val applicationContext by lazy { MyApplication.context }

    private fun getSps(): SharedPreferences {
        if (sps == null) {
            sps = applicationContext.getSharedPreferences(
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

    fun getString(key: String): String {
        if (key.isNotBlank()) {
            val sps: SharedPreferences = getSps()
            return sps.getString(key, "").toString()
        }
        return ""
    }

    private fun putBoolean(key: String, value: Boolean) {

        val editor: SharedPreferences.Editor = getSps().edit()
        editor.putBoolean(key, value)
        editor.apply()

    }

    fun getBoolean(key: String): Boolean {

        val sps: SharedPreferences = getSps()
        return sps.getBoolean(key, false)

    }

    private fun clean(key: String) {

        val editor: SharedPreferences.Editor = getSps().edit()
        editor.remove(key)
        editor.apply()
    }


    //保存登录信息
    fun saveInfo(loginReP: LoginReP) {

        //保存登录信息
        putString(
            APP_SHARED_PREFERENCES_LOGIN_TOKEN, loginReP.accessToken
        ) //保存登录TOKEN
        putString(APP_SHARED_PREFERENCES_LOGIN_ID, loginReP.userId) //保存用户id

        if (loginReP.isNewer) putBoolean(
            APP_SHARED_PREFERENCES_LOGIN_STATUS, false
        ) //保存登录状态
        else putBoolean(APP_SHARED_PREFERENCES_LOGIN_STATUS, true)
    }

    //清除登录信息
    fun cleanInfo() {
        clean(APP_SHARED_PREFERENCES_LOGIN_TOKEN) //清空登录TOKEN
        clean(APP_SHARED_PREFERENCES_LOGIN_ID) //清空用户id
        clean(APP_SHARED_PREFERENCES_RECOMMEND_USERID) //recommendUserIdid
        putBoolean(APP_SHARED_PREFERENCES_LOGIN_STATUS, false) //保存登录状态
    }


    //从粘贴板上保存邀请人id 内容格式：#h5WSCZw25nq11#1690966178059579393#
    fun putRecommendUserId(text: String) {
        LogUtils.i("邀请码：$text")
        // 查找第二个和第三个#之间的内容
        val pattern = "#[^#]*#(\\d+)#"

        val regex = Pattern.compile(pattern)
        val matcher = regex.matcher(text)

        if (matcher.find()) {
            val recommendUserId = matcher.group(1)
            LogUtils.i("邀请人id：$recommendUserId")
            putString(APP_SHARED_PREFERENCES_RECOMMEND_USERID, recommendUserId)
        }


    }

    //获取注册请求体对象
    fun getRegister(): Register {


        val deviceToken = getString(APP_163_PHONE_LOGIN_DEVICE_TOKEN)
        val mobileAccessToken = getString(APP_163_PHONE_LOGIN_MOBILE_ACCESS_TOKEN)
        val mobileToken = getString(APP_163_PHONE_LOGIN_MOBILE_TOKEN)
        val userId = getString(APP_SHARED_PREFERENCES_LOGIN_ID)
        val recommendUserId = getString(APP_SHARED_PREFERENCES_RECOMMEND_USERID)
        val sim = getString(APP_DEVICE_SIM)
        val imsi = getString(APP_DEVICE_IMSI)
        val androidId = getString(APP_DEVICE_MAC)
        val mac = getString(APP_DEVICE_ANDROIDID)
        return Register(
            deviceToken,
            mobileAccessToken,
            mobileToken,
            recommendUserId,
            userId,
            sim,
            imsi,
            androidId,
            mac
        )
    }


    @SuppressLint("HardwareIds")
    fun putAndroidID(context: Context) {
        putString(
            APP_DEVICE_ANDROIDID,
            Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
        )
    }


    //同意隐私协议
    fun agreePrivacy() {
        putBoolean(APP_PRIVACY_AGREEMENT_AGREE, true)
    }

    //不同意隐私协议
    fun disAgreePrivacy() {
        putBoolean(APP_PRIVACY_AGREEMENT_AGREE, false)
    }

    //是否同意隐私协议
    fun isAgreePrivacy(): Boolean {
        return getBoolean(APP_PRIVACY_AGREEMENT_AGREE)
    }


    //同意用户协议
    fun agreeAgreement() {
        putBoolean(APP_USER_AGREEMENT_AGREE, true)
    }

    //不同意用户协议
    fun disAgreeAgreement() {
        putBoolean(APP_USER_AGREEMENT_AGREE, false)
    }

    //是否同意用户协议
    private fun isAgreeUserAgreement(): Boolean {
        return getBoolean(APP_USER_AGREEMENT_AGREE)
    }


    //是否同意用户协议与隐私协议
    fun isAgreeUserAgreementAndPrivacy(): Boolean {
        return isAgreeUserAgreement() && isAgreePrivacy()
    }


    fun isConnected(): Boolean {
        return getBoolean(APP_NETWORK_IS_CONNECTED)
    }

    fun putConnected(b: Boolean) {
        putBoolean(APP_NETWORK_IS_CONNECTED, b)
    }
}