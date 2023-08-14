package com.lelezu.app.xianzhuan.data.repository


import android.util.Log
import com.lelezu.app.xianzhuan.data.ApiService
import com.lelezu.app.xianzhuan.data.model.ApiResponse
import com.lelezu.app.xianzhuan.data.model.Earning
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
class UserRepository(private var apiService: ApiService) : BaseRepository() {

    //获取登录信息
    suspend fun apiLogin(loginInfo: LoginInfo): ApiResponse<LoginReP> =
        withContext(Dispatchers.IO) {
            val call = apiService.getLogin(loginInfo)
            executeApiCall(call)
        }

    //用户注册
    suspend fun apiRegister(register: String): ApiResponse<LoginReP> = withContext(Dispatchers.IO) {
        val call = apiService.register(register)
        executeApiCall(call)
    }


    //获取用户信息
    suspend fun apiUserInfo(userId: String): ApiResponse<UserInfo> = withContext(Dispatchers.IO) {
        val call = apiService.getUserInfo(userId, loginToken)
        executeApiCall(call)
    }

    //获取收徒收益
    suspend fun apiEarnings(): ApiResponse<Earning> = withContext(Dispatchers.IO) {
        val call = apiService.getEarnings(loginToken)
        executeApiCall(call)
    }


}