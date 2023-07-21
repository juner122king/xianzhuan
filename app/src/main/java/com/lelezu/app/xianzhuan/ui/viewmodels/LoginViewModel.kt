package com.lelezu.app.xianzhuan.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.lelezu.app.xianzhuan.data.ApiConstants
import com.lelezu.app.xianzhuan.data.ApiService
import com.lelezu.app.xianzhuan.data.model.LoginInfo
import com.lelezu.app.xianzhuan.data.model.LoginReP
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LoginViewModel : ViewModel() {
    private val apiService: ApiService

    //初始化
    init {
        val retrofit = Retrofit.Builder().baseUrl(ApiConstants.HOST)
            .addConverterFactory(GsonConverterFactory.create()).build()
        apiService = retrofit.create(ApiService::class.java)
    }

    suspend fun apiLogin(loginInfo: LoginInfo): LoginReP? = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getLogin(loginInfo).execute()

            if (response.isSuccessful) {
                when (response.body()?.code) {
                    "000000" -> {
                        Log.d(
                            "APP登录接口login",
                            "登录成功 : Uid ${response.body()?.data?.userId},Token ${response.body()?.data?.accessToken}"
                        )
                        response.body()?.data
                    }

                    else -> {
                        Log.d(
                            "APP登录接口login",
                            "登录失败${response.body()?.code}:${response.body()?.message}"
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