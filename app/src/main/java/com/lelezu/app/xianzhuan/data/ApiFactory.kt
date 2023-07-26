package com.lelezu.app.xianzhuan.data

import android.content.Context
import com.lelezu.app.xianzhuan.MyApplication
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
public abstract class ApiFactory {


    companion object {

        fun create(context: Context): ApiService {
            val token = context.getSharedPreferences("ApiPrefs", Context.MODE_PRIVATE)
                ?.getString("LoginToken", "")

            //Log查看过滤器，上线版去掉
            val logInterceptor = HttpLoggingInterceptor()
            logInterceptor.level = HttpLoggingInterceptor.Level.BODY
            logInterceptor.level = HttpLoggingInterceptor.Level.HEADERS

            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(logInterceptor)
                .addInterceptor { chain ->
                    val originalRequest = chain.request()
                    val newRequest =
                        originalRequest.newBuilder().header("Authorization", token!!).build()
                    chain.proceed(newRequest)
                }.build()

            val retrofit = Retrofit.Builder().baseUrl(ApiConstants.HOST)
                .addConverterFactory(GsonConverterFactory.create()).client(okHttpClient).build()
            return retrofit.create(ApiService::class.java)


        }


    }
}