package com.lelezu.app.xianzhuan.ui.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import cn.hutool.core.codec.Base64
import com.google.gson.Gson
import com.lelezu.app.xianzhuan.data.ApiConstants
import com.lelezu.app.xianzhuan.data.model.ChatMessage
import com.lelezu.app.xianzhuan.data.model.LoginInfo
import com.lelezu.app.xianzhuan.data.model.LoginReP
import com.lelezu.app.xianzhuan.data.model.Related
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


    val related: MutableLiveData<Related> = MutableLiveData()

    val chatMessage: MutableLiveData<MutableList<ChatMessage>> = MutableLiveData()
    val sendMessage: MutableLiveData<MutableList<ChatMessage>> = MutableLiveData()


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


    fun getRelated() = viewModelScope.launch {
        val rep = userRepository.apiRelated()
        handleApiResponse(rep, related)
    }


    /**
     *
     * @param receiverUserId String 接收者（雇主）
     * @return 合并双方的发送消息
     */
    fun apiRecord(receiverUserId: String) = viewModelScope.launch {
        val rep = userRepository.apiRecord(receiverUserId)//我发送的消息
        chatMessage.postValue(rep)
    }


    /**
     *
     * @param receiveId String 接收者（雇主）
     * @return 合并双方的发送消息
     */
    fun apiSend(receiveId: String, content: String, isImage: Boolean) = viewModelScope.launch {
        val rep = userRepository.sendRecord(receiveId, content, isImage)//我发送的消息

        handleApiListResponse(rep, sendMessage)
    }


    //注册
    fun getRegister() = viewModelScope.launch {

        Log.i("登录请求体对象", "Register:" + ShareUtil.getRegister().toString())
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