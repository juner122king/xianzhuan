package com.lelezu.app.xianzhuan.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


//创建一个共享的 ViewModel 类来保存倒计时时间 用来处理手机验证码倒计时
class SharedCountdownViewModel : ViewModel() {
    private val _countdownTime = MutableLiveData<Long>()
    val countdownTime: LiveData<Long> get() = _countdownTime

    fun setCountdownTime(time: Long) {
        _countdownTime.value = time
    }
}
