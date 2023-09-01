package com.lelezu.app.xianzhuan.wxapi

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.lelezu.app.xianzhuan.ui.views.LoginActivity
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

        wxApi = WXAPIFactory.createWXAPI(this, WxData.WEIXIN_APP_ID, true)
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
                        Log.e("TAG_WECHAT_CODE", "微信授权成功！ code：" + r.code)

                        finish()
                        val intent = Intent(this, LoginActivity::class.java)
                        intent.putExtra("wx_code", r.code)
                        intent.putExtra("type", "WX")
                        startActivity(intent)
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

            else -> {
                //分享回调
                Log.i("微信回调baseResp：", "${baseResp.type}")
                finish()
            }
        }
    }
}
