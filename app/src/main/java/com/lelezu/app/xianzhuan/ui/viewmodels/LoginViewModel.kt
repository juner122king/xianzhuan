package com.lelezu.app.xianzhuan.ui.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import cn.hutool.core.codec.Base64
import com.google.gson.Gson
import com.lelezu.app.xianzhuan.data.ApiConstants
import com.lelezu.app.xianzhuan.data.ApiConstants.MOBILE_PASSWORD
import com.lelezu.app.xianzhuan.data.model.ChatList
import com.lelezu.app.xianzhuan.data.model.ChatMessage
import com.lelezu.app.xianzhuan.data.model.Earning
import com.lelezu.app.xianzhuan.data.model.Follows
import com.lelezu.app.xianzhuan.data.model.LoginConfig
import com.lelezu.app.xianzhuan.data.model.LoginInfo
import com.lelezu.app.xianzhuan.data.model.LoginReP
import com.lelezu.app.xianzhuan.data.model.Related
import com.lelezu.app.xianzhuan.data.model.UserInfo
import com.lelezu.app.xianzhuan.data.model.Vip
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

    val earning: MutableLiveData<Earning> = MutableLiveData()

    val vip: MutableLiveData<Vip> = MutableLiveData()


    val related: MutableLiveData<Related> = MutableLiveData()
    val follow: MutableLiveData<Follows> = MutableLiveData()
    val isFollow: MutableLiveData<Boolean> = MutableLiveData()

    val isLogOut: MutableLiveData<Boolean> = MutableLiveData()

    val isRecord: MutableLiveData<Boolean> = MutableLiveData()//是否已读成功

    val chatMessage: MutableLiveData<MutableList<ChatMessage>> = MutableLiveData()
    val sendMessage: MutableLiveData<MutableList<ChatMessage>> = MutableLiveData()
    val loginConfig: MutableLiveData<LoginConfig> = MutableLiveData()

    val chatList: MutableLiveData<List<ChatList>> = MutableLiveData()


    fun getLoginInfo(wxCode: String) = viewModelScope.launch(Dispatchers.IO) {
        val loginReP =
            userRepository.apiLogin(loginInfo(ApiConstants.LOGIN_METHOD_WX, wxCode, "", ""))
        handleApiResponse(loginReP, loginRePLiveData)
    }


    //手机号与密码登录方式
    fun getLoginInfo(mobilePhone: String, encryptPwd: String) =
        viewModelScope.launch(Dispatchers.IO) {

            val o64 = Base64.encode(encryptPwd, "UTF-8")
            val en64Pwd = AesTool.encryptStr(o64)

            val loginReP = userRepository.apiLogin(
                LoginInfo(
                    "", MOBILE_PASSWORD, "", "", "", "", mobilePhone, en64Pwd
                )
            )
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

    fun apiEarnings() = viewModelScope.launch {
        val rep = userRepository.apiEarnings()
        handleApiResponse(rep, earning)
    }

    fun vipRest() = viewModelScope.launch {
        val rep = userRepository.vipRest()
        handleApiResponse(rep, vip)
    }


    fun getRelated() = viewModelScope.launch {
        val rep = userRepository.apiRelated()
        handleApiResponse(rep, related)
    }





    /**
     *
     * @param userId String 用户iD
     * @return Job 获取关注与粉丝数
     */
    fun follows(userId:String) = viewModelScope.launch {
        val rep = userRepository.apiFollows(userId)
        handleApiResponse(rep, follow)
    }
  /**
     *
     * @param userId String 用户iD
     * @return Job 获取关注与粉丝数
     */
    fun onFollows(userId:String) = viewModelScope.launch {
        val rep = userRepository.onFollows(userId)
        handleApiResponse(rep, isFollow)
    }


    /**
     *
     * @param receiverUserId String 接收者（雇主）
     * @return 合并双方的发送消息
     */
    fun apiRecord(receiverUserId: String) = viewModelScope.launch {
        val rep = userRepository.apiRecord(receiverUserId)
        chatMessage.postValue(rep)
    }

  /**
     *
     * @return 雇主列表
     */
    fun apiContactors() = viewModelScope.launch {
        val rep = userRepository.apiContactors()
      handleApiResponse(rep, chatList)
    }


    /**
     *
     * @param senderUserId String 接收者（雇主）
     * @return 是否成功已读
     */
    fun isRecord(senderUserId: String) = viewModelScope.launch {
        val rep = userRepository.isRecord(senderUserId)//标记已读消息
        handleApiResponse(rep, isRecord)
    }

    /**
     *
     * @param receiveId String 接收者（雇主）
     * @return 合并双方的发送消息
     */
    fun apiSend(receiveId: String, content: String, type: Int) = viewModelScope.launch {
        val rep = userRepository.sendRecord(receiveId, content, type)//我发送的消息
        handleApiListResponse(rep, sendMessage)
    }

    /**
     *
     * @param receiveId String 接收者（雇主）
     * @return 用户退出登录
     */
    fun logout() = viewModelScope.launch {
        val rep = userRepository.logout()//

        handleApiResponse(rep, isLogOut)
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


    //获取登录配置，
    fun getLoginConfig()= viewModelScope.launch {


        val apiListResponse = userRepository.loginConfig()
        handleApiResponse(apiListResponse, loginConfig)
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