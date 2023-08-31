package com.lelezu.app.xianzhuan

import android.app.Application
import android.content.Context
import com.lelezu.app.xianzhuan.data.ApiConstants.UM_BUSINESS_NO
import com.lelezu.app.xianzhuan.data.ApiFactory
import com.lelezu.app.xianzhuan.data.repository.SysInformRepository
import com.lelezu.app.xianzhuan.data.repository.TaskRepository
import com.lelezu.app.xianzhuan.data.repository.UserRepository
import com.umeng.commonsdk.UMConfigure


/**
 * @author:Administrator
 * @date:2023/7/19 0019
 * @description:
 *
 */
class MyApplication : Application() {

    private val apiService by lazy { ApiFactory.create() }

    val userRepository by lazy { UserRepository(apiService) }

    val taskRepository by lazy { TaskRepository(apiService) }

    val sysInformRepository by lazy { SysInformRepository(apiService) }


    //声明公共变量
    companion object {
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()
        context = this

        //友盟开始
        //调用预初始化函数
        UMConfigure.preInit(this, UM_BUSINESS_NO, "正式")


    }


}