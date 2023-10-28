package com.lelezu.app.xianzhuan.data

import com.lelezu.app.xianzhuan.utils.LogUtils
import com.lelezu.app.xianzhuan.utils.ShareUtil
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * @author:Administrator
 * @date:2023/7/24 0024
 * @description: 用于初始化并生成APi接口实例
 *
 */
abstract class ApiFactory {


    companion object {

        fun create(): ApiService {

            val deviceId = ShareUtil.getString(ShareUtil.APP_SHARED_PREFERENCES_DEVICE_ID)
            LogUtils.i(
                "ApiFactory", "设备ID:${deviceId}"
            )

            //Log查看过滤器，上线版去掉
            val logInterceptor = HttpLoggingInterceptor()
            logInterceptor.level = HttpLoggingInterceptor.Level.BODY
            logInterceptor.level = HttpLoggingInterceptor.Level.HEADERS

            // 创建自定义的 Interceptor 用于添加请求头
            val headerInterceptor = Interceptor { chain ->
                val originalRequest = chain.request()
                // 构建新的请求，添加请求头
                val newRequest = originalRequest.newBuilder().addHeader("Device", deviceId).build()
                return@Interceptor chain.proceed(newRequest)
            }


            val okHttpClient = OkHttpClient.Builder().addInterceptor(logInterceptor)
                .addInterceptor(headerInterceptor) // 添加请求头的拦截器
                .build()

            val retrofit = Retrofit.Builder().baseUrl(ApiConstants.HOST)
                .addConverterFactory(GsonConverterFactory.create()).client(okHttpClient).build()
            return retrofit.create(ApiService::class.java)

        }


    }
}