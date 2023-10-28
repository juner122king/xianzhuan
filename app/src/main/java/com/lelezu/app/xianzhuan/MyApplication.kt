package com.lelezu.app.xianzhuan

import android.app.Application
import android.content.Context
import android.os.Build
import android.provider.Settings
import android.text.TextUtils
import cn.hutool.core.util.IdUtil
import com.fendasz.moku.planet.utils.thirdparty.baidu.util.DeviceId
import com.hjq.toast.ToastUtils
import com.lelezu.app.xianzhuan.data.ApiFactory
import com.lelezu.app.xianzhuan.data.repository.SysInformRepository
import com.lelezu.app.xianzhuan.data.repository.TaskRepository
import com.lelezu.app.xianzhuan.data.repository.UserRepository
import com.lelezu.app.xianzhuan.utils.LogUtils
import com.lelezu.app.xianzhuan.utils.ShareUtil
import com.lelezu.app.xianzhuan.utils.UUIDUtils
import com.lelezu.app.xianzhuan.wxapi.WxData
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import java.util.UUID


/**
 * @author:Administrator
 * @date:2023/7/19 0019
 * @description:
 *
 */
class MyApplication : Application() {


    //声明公共变量
    companion object {
        lateinit var context: Context


    }

    private val apiService by lazy { ApiFactory.create() }

    val userRepository by lazy { UserRepository(apiService) }

    val taskRepository by lazy { TaskRepository(apiService) }

    val sysInformRepository by lazy { SysInformRepository(apiService) }

    val api by lazy {
        WXAPIFactory.createWXAPI(this, WxData.WEIXIN_APP_ID, true)
    }


    override fun onCreate() {
        super.onCreate()
        context = this
        //获取设备ID
        UUIDUtils.getDeviceID(this)
        // 初始化 Toast 框架
        ToastUtils.init(this)

    }

}