package com.lelezu.app.xianzhuan.data

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
            //Log查看过滤器，上线版去掉
            val logInterceptor = HttpLoggingInterceptor()
            logInterceptor.level = HttpLoggingInterceptor.Level.BODY
            logInterceptor.level = HttpLoggingInterceptor.Level.HEADERS
            val okHttpClient = OkHttpClient.Builder().addInterceptor(logInterceptor).build()

            val retrofit = Retrofit.Builder().baseUrl(ApiConstants.HOST)
                .addConverterFactory(GsonConverterFactory.create()).client(okHttpClient).build()
            return retrofit.create(ApiService::class.java)

        }


    }
}