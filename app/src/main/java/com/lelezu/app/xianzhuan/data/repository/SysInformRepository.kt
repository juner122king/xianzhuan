package com.lelezu.app.xianzhuan.data.repository

import android.content.Context.MODE_PRIVATE
import android.util.Log
import com.lelezu.app.xianzhuan.MyApplication
import com.lelezu.app.xianzhuan.data.ApiService
import com.lelezu.app.xianzhuan.data.model.ApiResponse
import com.lelezu.app.xianzhuan.data.model.Config
import com.lelezu.app.xianzhuan.data.model.ListData
import com.lelezu.app.xianzhuan.data.model.LoginInfo
import com.lelezu.app.xianzhuan.data.model.LoginReP
import com.lelezu.app.xianzhuan.data.model.Message
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * @author:Administrator
 * @date:2023/7/25 0021
 * @description:与系统消息相关的数据
 *
 */
class SysInformRepository(private var apiService: ApiService) {

    //获取系统消息列表
    suspend fun apiGetList(current:Int,size:Int): ListData<Message>? = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getSysMessageList(current,size).execute()
            if (response.isSuccessful) {
                when (response.body()?.code) {
                    "000000" -> {
                        Log.d(
                            "APP接口inform/page",
                            "获取成功 : ToString: ${response.body()?.data?.toString()}"
                        )
                        response.body()?.data
                    }
                    else -> {
                        Log.d(
                            "APP接口inform/page",
                            "请求失败${response.body()?.code}:${response.body()?.message}"
                        )
                        null
                    }
                }
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }


    //获取系统配置信息
    suspend fun apiConfig(confType:String,configKey:String): Config? = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getConfig(confType,configKey).execute()
            if (response.isSuccessful) {
                when (response.body()?.code) {
                    "000000" -> {
                        Log.d(
                            "APP登录接口/config",
                            "获取成功 : ToString: ${response.body()?.data?.toString()}"
                        )
                        response.body()?.data
                    }
                    else -> {
                        Log.d(
                            "APP登录接口/config",
                            "请求失败${response.body()?.code}:${response.body()?.message}"
                        )
                        null
                    }
                }
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

}