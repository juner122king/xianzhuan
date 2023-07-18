package com.lelezu.app.xianzhuan.wxapi

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.tencent.mm.opensdk.constants.ConstantsAPI
import com.tencent.mm.opensdk.modelbase.BaseReq
import com.tencent.mm.opensdk.modelbase.BaseResp
import com.tencent.mm.opensdk.modelmsg.SendAuth
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler
import com.tencent.mm.opensdk.openapi.WXAPIFactory

class WXEntryActivity : Activity(), IWXAPIEventHandler {

    private lateinit var wxApi: IWXAPI

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        wxApi = WXAPIFactory.createWXAPI(this, "wx1bdc5e2f8be515eb", true)
        wxApi.registerApp("wx1bdc5e2f8be515eb")
        wxApi.handleIntent(intent, this)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        wxApi.handleIntent(intent, this)
    }

    override fun onReq(req: BaseReq) {
        // 暂不处理请求回调
    }

    override fun onResp(baseResp: BaseResp) {
        when (baseResp.type) {
            ConstantsAPI.COMMAND_SENDAUTH -> {
                val r = baseResp as SendAuth.Resp
                when (baseResp.errCode) {
                    BaseResp.ErrCode.ERR_OK -> {
                        Log.e("TAG_WECHAT_CODE", r.code)
                        // 登陆成功的结果，可以跳转到某个页面
                        finish()
                    }

                    BaseResp.ErrCode.ERR_AUTH_DENIED -> {
                        val result = "用户取消微信授权登录"
                        Log.e("ERROR", result)
                        finish()
                    }

                    BaseResp.ErrCode.ERR_USER_CANCEL -> {
                        val result = "用户拒绝微信授权登录"
                        Log.e("ERROR", result)
                        finish()
                    }

                    else -> {
                        val result = "微信授权登录失败"
                        Log.e("ERROR", result)
                        finish()
                    }
                }
            }
            ConstantsAPI.COMMAND_SENDMESSAGE_TO_WX -> {
                when (baseResp.errCode) {
                    BaseResp.ErrCode.ERR_OK -> {
                    }
                    BaseResp.ErrCode.ERR_USER_CANCEL -> {
                    }
                    else -> {
                    }
                }
            }
        }
    }

}
