package com.lelezu.app.xianzhuan.ui.viewmodels

import androidx.annotation.MainThread
import androidx.annotation.Nullable
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import java.util.concurrent.atomic.AtomicBoolean

/**
 * @author:Administrator
 * @date:2023/8/23 0023
 * @description: 使用一个google大神实现的一个复写类 SingleLiveEvent，其中的机制是用一个原子 AtomicBoolean记录一次setValue。在发送一次后在将AtomicBoolean设置为false，阻止后续前台重新触发时的数据发送。
 *
 */
class SingleLiveEvent<T> : MutableLiveData<T>() {

    private val mPending = AtomicBoolean(false)

    @MainThread
    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {

        if (hasActiveObservers()) {
//            LogUtils.w("注册了多个观察员，但只有一个会收到更改通知注册了多个观察员，但只有一个会收到更改通知")
        }

        // Observe the internal MutableLiveData
        super.observe(owner, Observer<T> { t ->
            if (mPending.compareAndSet(true, false)) {
                observer.onChanged(t)
            }
        })
    }

    @MainThread
    override fun setValue(@Nullable t: T?) {
        mPending.set(true)
        super.setValue(t)
    }

    /**
     * Used for cases where T is Void, to make calls cleaner.
     */
    @MainThread
    fun call() {
        value = null
    }
}
