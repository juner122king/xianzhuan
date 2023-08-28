package com.lelezu.app.xianzhuan.utils

import android.util.Log

/**
 * @author:Administrator
 * @date:2023/8/16 0016
 * @description:
 *
 */
object LogUtils {
    private var isLoggingEnabled = true // 默认开启日志记录

    fun enableLogging() {
        isLoggingEnabled = true
    }

    fun disableLogging() {
        isLoggingEnabled = false
    }

    fun v(message: String) {
        if (isLoggingEnabled) {
            Log.v(getTag(), message)
        }
    }

    fun d(message: String) {
        if (isLoggingEnabled) {
            Log.d(getTag(), message)
        }
    }

    fun i(message: String) {
        if (isLoggingEnabled) {
            Log.i(getTag(), message)
        }
    }

    fun i(tag:String,message: String) {
        if (isLoggingEnabled) {
            Log.i(tag, message)
        }
    }

    fun w(message: String) {
        if (isLoggingEnabled) {
            Log.w(getTag(), message)
        }
    }

    fun e(message: String) {
        if (isLoggingEnabled) {
            Log.e(getTag(), message)
        }
    }

    private fun getTag(): String {
        val stackTrace = Thread.currentThread().stackTrace
        if (stackTrace.size >= 4) {
            val caller = stackTrace[3]
            return caller.className.substringAfterLast('.')
        }
        return "Unknown"
    }
}
