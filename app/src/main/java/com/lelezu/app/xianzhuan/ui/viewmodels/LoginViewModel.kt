package com.lelezu.app.xianzhuan.ui.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import cn.hutool.core.codec.Base64
import com.google.gson.Gson
import com.lelezu.app.xianzhuan.data.ApiConstants
import com.lelezu.app.xianzhuan.data.model.LoginInfo
import com.lelezu.app.xianzhuan.data.model.LoginReP
import com.lelezu.app.xianzhuan.data.model.UserInfo
import com.lelezu.app.xianzhuan.data.repository.UserRepository
import com.lelezu.app.xianzhuan.utils.AesTool
import com.lelezu.app.xianzhuan.utils.ShareUtil
import com.lelezu.app.xianzhuan.wxapi.WxData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


/**
 *
 * @property userRepository UserRepository
 * @property loginRePLiveData MutableLiveData<LoginReP?>
 * @constructor 用户信息相关的
 *
 */
class LoginViewModel(private val userRepository: UserRepository) : BaseViewModel() {

    val loginRePLiveData: MutableLiveData<LoginReP> = MutableLiveData()
    val registerLoginRePLiveData: MutableLiveData<LoginReP> = MutableLiveData()
    val userInfo: MutableLiveData<UserInfo> = MutableLiveData()


    fun getLoginInfo(wxCode: String) = viewModelScope.launch(Dispatchers.IO) {
        val loginReP =
            userRepository.apiLogin(loginInfo(ApiConstants.LOGIN_METHOD_WX, wxCode, "", ""))
        handleApiResponse(loginReP, loginRePLiveData)
    }

    private fun loginInfo(
        loginMethod: String, wxCode: String?, mobileToken: String?, mobileAccessToken: String?
    ): LoginInfo {
        return LoginInfo(
            WxData.WEIXIN_APP_ID, loginMethod, mobileToken, mobileAccessToken, wxCode
        )

    }

    fun getUserInfo(userId: String) = viewModelScope.launch {
        val rep = userRepository.apiUserInfo(userId)
        handleApiResponse(rep, userInfo)
    }


    //注册
    fun getRegister() = viewModelScope.launch {

        val o = Gson().toJson(ShareUtil.getRegister())
        val o64 = Base64.encode(o, "UTF-8")
        val en64 = AesTool.encryptStr(o64)

        val apiListResponse = userRepository.apiRegister(en64)
        handleApiResponse(apiListResponse, registerLoginRePLiveData)
    }


    class LoginViewFactory(private val repository: UserRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST") return LoginViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}