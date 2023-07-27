package com.lelezu.app.xianzhuan.data.repository

import android.content.Context.MODE_PRIVATE
import android.util.Log
import com.lelezu.app.xianzhuan.MyApplication
import com.lelezu.app.xianzhuan.data.ApiService
import com.lelezu.app.xianzhuan.data.model.LoginInfo
import com.lelezu.app.xianzhuan.data.model.LoginReP
import com.lelezu.app.xianzhuan.data.model.UserInfo
import com.lelezu.app.xianzhuan.utils.ShareUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * @author:Administrator
 * @date:2023/7/21 0021
 * @description:与用户相关的数据
 *
 */
class UserRepository(private var apiService: ApiService) {

    //获取登录信息
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
                        response.body()?.data?.let { saveInfo(it) }
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


    //获取用户信息
    suspend fun apiUserInfo(): UserInfo? = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getUserInfo().execute()
            if (response.isSuccessful) {
                when (response.body()?.code) {
                    "000000" -> {
                        Log.d(
                            "用户信息", "成功 :  ${response.body()?.data?.toString()} "
                        )

                        response.body()?.data
                    }

                    else -> {
                        Log.d(
                            "用户信息", "失败${response.body()?.code}:${response.body()?.message}"
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

    //保存登录信息
    private fun saveInfo(loginReP: LoginReP) {
        //保存登录信息
        ShareUtil.putString(ShareUtil.APP_SHARED_PREFERENCES_LOGIN_TOKEN,loginReP.accessToken) //保存登录TOKEN
        ShareUtil.putString(ShareUtil.APP_SHARED_PREFERENCES_LOGIN_ID,loginReP.userId) //保存用户id
        ShareUtil.putBoolean(ShareUtil.APP_SHARED_PREFERENCES_LOGIN_STATUS,true) //保存登录状态


    }


}