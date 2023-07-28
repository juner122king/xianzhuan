package com.lelezu.app.xianzhuan.ui.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.lelezu.app.xianzhuan.data.ApiConstants
import com.lelezu.app.xianzhuan.data.model.LoginInfo
import com.lelezu.app.xianzhuan.data.model.LoginReP
import com.lelezu.app.xianzhuan.data.model.Register
import com.lelezu.app.xianzhuan.data.model.UserInfo
import com.lelezu.app.xianzhuan.data.repository.UserRepository
import com.lelezu.app.xianzhuan.wxapi.WxData
import com.netease.htprotect.HTProtect
import com.netease.htprotect.result.AntiCheatResult
import com.netease.nis.quicklogin.QuickLogin
import com.netease.nis.quicklogin.listener.QuickLoginPreMobileListener
import com.netease.nis.quicklogin.listener.QuickLoginTokenListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


/**
 *
 * @property userRepository UserRepository
 * @property loginRePLiveData MutableLiveData<LoginReP?>
 * @constructor 用户信息相关的
 *
 */
class LoginViewModel2(private val userRepository: UserRepository) : ViewModel() {

    val loginRePLiveData: MutableLiveData<LoginReP?> = MutableLiveData()
    val registerLoginRePLiveData: MutableLiveData<LoginReP?> = MutableLiveData()


    val userInfo: MutableLiveData<UserInfo?> = MutableLiveData()


    fun getLoginInfo(wxCode: String) = viewModelScope.launch(Dispatchers.IO) {
        val loginReP =
            userRepository.apiLogin(loginInfo(ApiConstants.LOGIN_METHOD_WX, wxCode, "", ""))
        loginRePLiveData.postValue(loginReP)
    }

    private fun loginInfo(
        loginMethod: String, wxCode: String?, mobileToken: String?, mobileAccessToken: String?
    ): LoginInfo {
        Log.d(
            "APP登录接口login",
            "WEIXIN_APP_ID：${WxData.WEIXIN_APP_ID},loginMethod:${loginMethod},mobileToken:${mobileToken},mobileAccessToken:${mobileAccessToken},wxCode:${wxCode}"
        )
        return LoginInfo(
            WxData.WEIXIN_APP_ID, loginMethod, mobileToken, mobileAccessToken, wxCode
        );

    }

    fun getUserInfo() = viewModelScope.launch {
        val rep = userRepository.apiUserInfo()

        userInfo.postValue(rep)
    }


    //注册
    fun getRegister(register: Register) = viewModelScope.launch {

        var registerLoginReP = userRepository.apiRegister(register)
        registerLoginRePLiveData.postValue(registerLoginReP)

    }


    class LoginViewFactory(private val repository: UserRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(LoginViewModel2::class.java)) {
                @Suppress("UNCHECKED_CAST") return LoginViewModel2(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}