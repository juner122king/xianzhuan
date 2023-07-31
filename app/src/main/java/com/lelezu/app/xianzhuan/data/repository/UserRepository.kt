package com.lelezu.app.xianzhuan.data.repository


import android.util.Log
import com.lelezu.app.xianzhuan.MyApplication
import com.lelezu.app.xianzhuan.data.ApiService
import com.lelezu.app.xianzhuan.data.model.LoginInfo
import com.lelezu.app.xianzhuan.data.model.LoginReP
import com.lelezu.app.xianzhuan.data.model.Register
import com.lelezu.app.xianzhuan.data.model.UserInfo
import com.lelezu.app.xianzhuan.utils.ShareUtil
import com.lelezu.app.xianzhuan.utils.ToastUtils
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
    suspend fun apiRegister(register: Register): LoginReP? = withContext(Dispatchers.IO) {
        Log.d(
            "APP注册接口Register", "请求体信息 : register： $register"
        )

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
                        MyApplication.context?.let {
                            ToastUtils.showToast(
                                it, "登录失败：${response.body()?.message}", 0
                            )
                        }

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
    suspend fun apiUserInfo(userId:String): UserInfo? = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getUserInfo(userId).execute()
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


    //保存登录信息
    private fun saveInfo(loginReP: LoginReP) {

        //保存登录信息
        ShareUtil.putString(
            ShareUtil.APP_SHARED_PREFERENCES_LOGIN_TOKEN, loginReP.accessToken
        ) //保存登录TOKEN
        ShareUtil.putString(ShareUtil.APP_SHARED_PREFERENCES_LOGIN_ID, loginReP.userId) //保存用户id

        if (loginReP.isNewer) ShareUtil.putBoolean(
            ShareUtil.APP_SHARED_PREFERENCES_LOGIN_STATUS, false
        ) //保存登录状态
        else ShareUtil.putBoolean(ShareUtil.APP_SHARED_PREFERENCES_LOGIN_STATUS, true)
    }

    //清除登录信息
    private fun cleanInfo() {
        ShareUtil.clean(ShareUtil.APP_SHARED_PREFERENCES_LOGIN_TOKEN) //清空登录TOKEN
        ShareUtil.clean(ShareUtil.APP_SHARED_PREFERENCES_LOGIN_ID) //清空用户id
        ShareUtil.putBoolean(ShareUtil.APP_SHARED_PREFERENCES_LOGIN_STATUS, false) //保存登录状态
    }


}