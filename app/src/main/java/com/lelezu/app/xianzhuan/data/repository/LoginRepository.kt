package com.lelezu.app.xianzhuan.data.repository

import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.lelezu.app.xianzhuan.data.ApiConstants
import com.lelezu.app.xianzhuan.data.ApiService
import com.lelezu.app.xianzhuan.data.model.LoginInfo
import com.lelezu.app.xianzhuan.data.model.LoginReP
import com.lelezu.app.xianzhuan.ui.viewmodels.LoginViewModel
import com.lelezu.app.xianzhuan.wxapi.WxData.WEIXIN_APP_ID
import kotlinx.coroutines.launch

class LoginRepository {
    private val viewModel: LoginViewModel = LoginViewModel()
    suspend fun getGetLogin(
        loginMethod: String, wechatCode: String, deviceToken: String
    ): LoginReP? {
        val loginInfo = LoginInfo(
            WEIXIN_APP_ID, deviceToken, loginMethod, "", wechatCode
        )
        return viewModel.userGetLogin(loginInfo)
    }

}
