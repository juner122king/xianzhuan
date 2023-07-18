package com.lelezu.app.xianzhuan.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent


/**处理接收到的广播消息并将倒计时时间传递给相关的观察者（Activity）
 *
 */
class CountdownReceiver : BroadcastReceiver() {
    companion object {
        const val ACTION_TICK = "com.example.countdown.ACTION_TICK"
        const val EXTRA_COUNTDOWN_TIME = "countdown_time"
    }

    private val listeners: MutableList<CountdownListener> = mutableListOf()

    fun addListener(listener: CountdownListener) {
        listeners.add(listener)
    }

    fun removeListener(listener: CountdownListener) {
        listeners.remove(listener)
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == ACTION_TICK) {
            val countdownTime = intent.getLongExtra(EXTRA_COUNTDOWN_TIME, 0)
            notifyListeners(countdownTime)
        }
    }

    private fun notifyListeners(countdownTime: Long) {
        for (listener in listeners) {
            listener.onTick(countdownTime)
        }
    }

    interface CountdownListener {
        fun onTick(countdownTime: Long)
    }
}
