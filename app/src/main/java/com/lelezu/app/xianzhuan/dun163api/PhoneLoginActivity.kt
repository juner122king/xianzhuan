package com.lelezu.app.xianzhuan.dun163api

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.lelezu.app.xianzhuan.MyApplication
import com.lelezu.app.xianzhuan.ui.views.LoginActivity
import com.lelezu.app.xianzhuan.utils.ShareUtil
import com.lelezu.app.xianzhuan.utils.ToastUtils
import com.netease.htprotect.HTProtect
import com.netease.htprotect.result.AntiCheatResult
import com.netease.nis.quicklogin.QuickLogin
import com.netease.nis.quicklogin.listener.QuickLoginPreMobileListener
import com.netease.nis.quicklogin.listener.QuickLoginTokenListener

/**
 * @author:Administrator
 * @date:2023/7/28 0028
 * @description:    step1 先请求《风控引擎》接口：异步获取凭证  获取 =（deviceToken)：异步获取凭证 https://support.dun.163.com/documents/761315885761396736?docId=778684309123280896#%E5%BC%82%E6%AD%A5%E8%8E%B7%E5%8F%96%E5%87%AD%E8%AF%81%EF%BC%88getTokenAsync%EF%BC%89
step2 再求《号码认证》接口：预取号
step3 再拉取授权页确认  获取 =（mobileAccessToken，mobileToken）

 *
 */
class PhoneLoginActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        QuickLogin.getInstance().init(MyApplication.context, "23b26346411243b79a760cefaadabe09")
        QuickLogin.getInstance().setDebugMode(true)
        QuickLogin.getInstance().setUnifyUiConfig(UiConfigs.getDConfig(this))

        HTProtect.getTokenAsync(
            3000, "e377b3fedaec37da3be3a08a8c202e71"
        ) {
            Log.d("易盾风控引擎Api  code:", it!!.code.toString());
            if (it!!.code == AntiCheatResult.OK) {
                // 调用成功，获取token
                Log.d("易盾风控引擎Api  token:", it.token)
                ShareUtil.putString(ShareUtil.APP_163_PHONE_LOGIN_DEVICE_TOKEN, it.token)
                initQuickLogin()
            } else {
                ToastUtils.showToast(baseContext, "您的手机号或设备异常：${it.codeStr}", 0)
                finish()
            }
        }
    }

    private fun initQuickLogin() {

        QuickLogin.getInstance().prefetchMobileNumber(object : QuickLoginPreMobileListener() {
            override fun onGetMobileNumberSuccess(YDToken: String, mobileNumber: String) {
                Log.d(
                    "易盾号码认证Api", "预取号成功"
                )
                onePass()
            }

            override fun onGetMobileNumberError(YDToken: String, msg: String) {
                Log.d(
                    "易盾号码认证Api", "预取号失败：${msg}"
                )
                ToastUtils.showToast(baseContext, "易盾号码认证:预取号失败：${msg}", 0)
                finish()

            }
        })

    }


    private fun onePass() {

        QuickLogin.getInstance().onePass(object : QuickLoginTokenListener() {
            override fun onGetTokenSuccess(token: String?, accessCode: String?) {
                Log.d(
                    "易盾号码认证Api", "一键登录成功: 易盾token${token}运营商token${accessCode}"
                )

                //保存token
                ShareUtil.putString(
                    ShareUtil.APP_163_PHONE_LOGIN_MOBILE_ACCESS_TOKEN, accessCode
                )
                ShareUtil.putString(ShareUtil.APP_163_PHONE_LOGIN_MOBILE_TOKEN, token)
                QuickLogin.getInstance().quitActivity()
                finish()
                val intent = Intent(baseContext, LoginActivity::class.java)
                intent.putExtra("type", "163")
                startActivity(intent)
            }

            override fun onGetTokenError(YDToken: String?, msg: String?) {
                Log.d(
                    "易盾号码认证Api", "一键登录失败：${msg}"
                )
                finish()
                ToastUtils.showToast(baseContext, "一键登录失败：${msg}", 0)
            }

            // 取消登录包括按物理返回键返回
            override fun onCancelGetToken() {
                QuickLogin.getInstance().quitActivity()
                finish()

                Log.d("易盾号码认证Api", "用户取消登录/包括物理返回")

            }
        })
    }

}