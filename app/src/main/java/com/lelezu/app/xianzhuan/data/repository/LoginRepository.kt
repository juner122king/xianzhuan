package com.lelezu.app.xianzhuan.data.repository

import android.util.Log
import com.lelezu.app.xianzhuan.data.ApiConstants
import com.lelezu.app.xianzhuan.data.model.LoginInfo
import com.lelezu.app.xianzhuan.data.model.LoginReP
import com.lelezu.app.xianzhuan.ui.viewmodels.LoginViewModel
import com.lelezu.app.xianzhuan.wxapi.WxData.WEIXIN_APP_ID

class LoginRepository {
    private val viewModel: LoginViewModel = LoginViewModel()


    suspend fun wxLogin(
        wxCode: String
    ): LoginReP? {
        return viewModel.apiLogin(getLoginInfo(ApiConstants.LOGIN_METHOD_WX, wxCode, null, null))
    }

    suspend fun phoneLogin(
        mobileToken: String, mobileAccessToken: String
    ): LoginReP? {
        return viewModel.apiLogin(
            getLoginInfo(
                ApiConstants.LOGIN_METHOD_PHONE, null, mobileToken, mobileAccessToken
            )
        )
    }

    private fun getLoginInfo(
        loginMethod: String, wxCode: String?, mobileToken: String?, mobileAccessToken: String?
    ): LoginInfo {
        Log.d(
            "APP登录接口login",
            "WEIXIN_APP_ID：${WEIXIN_APP_ID},loginMethod:${loginMethod},mobileToken:${mobileToken},mobileAccessToken:${mobileAccessToken},wxCode:${wxCode}"
        )
        return LoginInfo(
            WEIXIN_APP_ID, loginMethod, mobileToken, mobileAccessToken, wxCode
        );

    }

}