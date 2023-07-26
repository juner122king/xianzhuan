package com.lelezu.app.xianzhuan.ui.views

import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.lelezu.app.xianzhuan.R
import com.lelezu.app.xianzhuan.receiver.CountdownReceiver


//手机验证码输入页面
class SetSMSCodeActivity : AppCompatActivity(), CountdownReceiver.CountdownListener {
    private lateinit var textView: TextView
    private lateinit var countdownReceiver: CountdownReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_smscode)

        textView = findViewById(R.id.t_countdown)

        countdownReceiver = CountdownReceiver()
        countdownReceiver.addListener(this)

        registerReceiver(countdownReceiver, IntentFilter(CountdownReceiver.ACTION_TICK))
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(countdownReceiver)
    }

    override fun onTick(countdownTime: Long) {
        runOnUiThread {
            textView.text = (countdownTime / 1000).toString()
        }
    }
}