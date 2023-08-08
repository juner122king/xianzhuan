package com.lelezu.app.xianzhuan.data.repository


import android.util.Log
import com.lelezu.app.xianzhuan.data.ApiService
import com.lelezu.app.xianzhuan.data.model.LoginInfo
import com.lelezu.app.xianzhuan.data.model.LoginReP
import com.lelezu.app.xianzhuan.data.model.UserInfo
import com.lelezu.app.xianzhuan.utils.ShareUtil
import com.lelezu.app.xianzhuan.utils.ShareUtil.cleanInfo
import com.lelezu.app.xianzhuan.utils.ShareUtil.saveInfo
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
                        response.body()?.data?.let {
                            saveInfo(it)
                        }
                        response.body()?.data
                    }

                    else -> {
                        Log.d(
                            "APP登录接口login",
                            "登录失败${response.body()?.code}:${response.body()?.message}"
                        )
                        cleanInfo()

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

    //用户注册
    suspend fun apiRegister(register: String): LoginReP? = withContext(Dispatchers.IO) {
        try {
            val response = apiService.register(register).execute()
            if (response.isSuccessful) {
                when (response.body()?.code) {
                    "000000" -> {
                        Log.d(
                            "APP注册接口Register",
                            "成功 : Uid ${response.body()?.data?.userId},Token ${response.body()?.data?.accessToken}"
                        )
                        response.body()?.data?.let {
                            saveInfo(it)
                        }
                        response.body()?.data
                    }

                    else -> {
                        Log.d(
                            "AP接口Register",
                            "失败${response.body()?.code}:${response.body()?.message}"
                        )
                        cleanInfo()
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
    suspend fun apiUserInfo(userId: String): UserInfo? = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getUserInfo(
                userId, ShareUtil.getString(ShareUtil.APP_SHARED_PREFERENCES_LOGIN_TOKEN)
            ).execute()
            if (response.isSuccessful) {
                when (response.body()?.code) {
                    "000000" -> {
                        Log.d(
                            "获取用户信息", "成功 :  ${response.body()?.data?.toString()} "
                        )
                        response.body()?.data
                    }

                    else -> {
                        Log.d(
                            "获取用户信息",
                            "失败${response.body()?.code}:${response.body()?.message}"
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