package com.lelezu.app.xianzhuan.ui.views

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.github.lzyzsd.jsbridge.BridgeWebView
import com.lelezu.app.xianzhuan.R
import com.lelezu.app.xianzhuan.data.ApiConstants
import com.lelezu.app.xianzhuan.data.model.LoginReP
import com.lelezu.app.xianzhuan.dun163api.UiConfigs
import com.lelezu.app.xianzhuan.ui.h5.WebViewSettings
import com.lelezu.app.xianzhuan.utils.LogUtils
import com.lelezu.app.xianzhuan.utils.ShareUtil
import com.lelezu.app.xianzhuan.utils.ShareUtil.agreeAgreement
import com.lelezu.app.xianzhuan.utils.ShareUtil.agreePrivacy
import com.lelezu.app.xianzhuan.utils.ShareUtil.disAgreeAgreement
import com.lelezu.app.xianzhuan.utils.ShareUtil.disAgreePrivacy
import com.lelezu.app.xianzhuan.utils.ShareUtil.isAgreeUserAgreementAndPrivacy
import com.lelezu.app.xianzhuan.wxapi.WxLogin
import com.netease.htprotect.HTProtect
import com.netease.htprotect.result.AntiCheatResult
import com.netease.nis.quicklogin.QuickLogin
import com.netease.nis.quicklogin.listener.QuickLoginPreMobileListener
import com.netease.nis.quicklogin.listener.QuickLoginTokenListener


//登录页面
class LoginActivity : BaseActivity(), OnClickListener {

    private lateinit var cbAgree: CheckBox//是否同意思协议按钮
    private lateinit var dialog: AlertDialog//协议弹窗
    private var dialogType: Int = 1//弹窗的协议类型  1为隐私协议  2为用户协议

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        cbAgree = findViewById(R.id.cb_agree_agreement)//是否同意思协议按钮
        findViewById<TextView>(R.id.bto_phome_login).setOnClickListener(this)//‘使用手机登录’按钮
        findViewById<ImageView>(R.id.bto_wx_login).setOnClickListener(this)//微信登录按钮
        findViewById<TextView>(R.id.tv_agreement).setOnClickListener(this) //打开用户使用协议
        findViewById<TextView>(R.id.tv_agreement2).setOnClickListener(this) //打开《隐私政策》


        //点击时自动保存同意状态
        cbAgree.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                agreePrivacy()
                agreeAgreement()
            }
        }

    }


    private fun wxLoginInit() {
        when (cbAgree.isChecked) {
            true -> {

                WxLogin.longWx()  //微信登录
            }

            else -> {
                showToast("请同意隐私政策")
            }
        }
    }

    private fun phoneLoginInit() {
        startActivity(Intent(this, LoginMobileActivity::class.java))
    }

    private fun getLogin(wxCode: String) {
        loginViewModel.getLoginInfo(wxCode)
        loginViewModel.loginRePLiveData.observe(this) {
            hideLoading()
            onLogin(it)
        }

    }

    private fun goToRegister() {
        getClipBoar()//注册前获取剪切板是否有邀请码

        showLoading()
        loginViewModel.getRegister()
        loginViewModel.registerLoginRePLiveData.observe(this) {
            hideLoading()
            onLogin(it)
        }
    }

    private fun onLogin(it: LoginReP) {

        if (it.isNewer) {
            gotoPhoneRegister()

        } else {
            goToHomeActivity()
        }
    }

    private fun gotoPhoneRegister() {
        showToast("号码获取中，请稍等...")
        showLoading()


        QuickLogin.getInstance().init(this, ApiConstants.DUN_PHONE_BUSINESS_ID)
        QuickLogin.getInstance().setDebugMode(true)
        QuickLogin.getInstance().setUnifyUiConfig(UiConfigs.getDConfig(this))
        HTProtect.getTokenAsync(
            3000, ApiConstants.DUN_RISK_BUSINESS_ID
        ) {
            LogUtils.i("易盾风控引擎Api  code:", it!!.code.toString());
            if (it.code == AntiCheatResult.OK) {
                // 调用成功，获取token
                ShareUtil.putString(ShareUtil.APP_163_PHONE_LOGIN_DEVICE_TOKEN, it.token)

                initQuickLogin()
            } else {
                showToast("您的手机号或设备异常：${it.codeStr}")
                hideLoading()

            }


        }

    }

    private fun initQuickLogin() {

        QuickLogin.getInstance().prefetchMobileNumber(object : QuickLoginPreMobileListener() {
            override fun onGetMobileNumberSuccess(token: String, mobileNumber: String) {
                LogUtils.i(
                    "易盾号码认证Api", "预取号成功"
                )
                hideLoading()
                onePass()
            }

            override fun onGetMobileNumberError(token: String, msg: String) {

                LogUtils.i("易盾号码认证Api", "预取号失败：${msg}")
                showToast("请打开流量联网后重试")
                hideLoading()
            }
        })
    }

    private fun onePass() {
        QuickLogin.getInstance().onePass(object : QuickLoginTokenListener() {
            override fun onGetTokenSuccess(token: String?, accessCode: String?) {
                //保存token
                ShareUtil.putString(
                    ShareUtil.APP_163_PHONE_LOGIN_MOBILE_ACCESS_TOKEN, accessCode
                )
                ShareUtil.putString(ShareUtil.APP_163_PHONE_LOGIN_MOBILE_TOKEN, token)
                QuickLogin.getInstance().quitActivity()
                LogUtils.i("易盾号码认证Api", "一键登录成功")
                //请求注册接口
                goToRegister()

            }

            override fun onGetTokenError(token: String?, msg: String?) {

                showToast("易盾号码认证Api:一键登录失败：${msg}")
                QuickLogin.getInstance().quitActivity()

            }

            // 取消登录包括按物理返回键返回
            override fun onCancelGetToken() {
                QuickLogin.getInstance().quitActivity()
                LogUtils.i("易盾号码认证Api", "用户取消登录/包括物理返回")

            }
        })
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent != null) {
            //从微信授权页面返回
            if (intent.getStringExtra("type").equals("WX")) intent.getStringExtra("wx_code")?.let {
                showLoading()
                getLogin(it)
            }
        }
    }


    // 显示用户协议弹窗
    private fun showAgreementDialog(title: String, link: String) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_user_agreement, null)
        val webView = dialogView.findViewById<BridgeWebView>(R.id.webView)
        val tvTitle = dialogView.findViewById<TextView>(R.id.tvTitle)
        // 设置标题
        tvTitle.text = title

        WebViewSettings.setDefaultWebSettings(webView)

        webView.setInitialScale(200)
        webView.loadUrl(link)


        // 创建弹窗
        val builder = AlertDialog.Builder(this, R.style.backDialog)
        builder.setView(dialogView)
        dialog = builder.create()

        // 显示弹窗
        dialog.show()

    }

    // 不同意按钮点击事件
    fun onDisagreeButtonClick(view: View) {
        // 在这里处理不同意的逻辑
        // 关闭弹窗
        dialog.dismiss()
        // 取消勾选父Activity上的CheckBox
        cbAgree.isChecked = false

        if (dialogType == 1) {
            disAgreePrivacy()
        } else {
            disAgreeAgreement()
        }


    }

    // 同意按钮点击事件
    fun onAgreeButtonClick(view: View) {
        // 在这里处理同意的逻辑
        // 关闭弹窗
        dialog.dismiss()

        if (dialogType == 1) {
            agreePrivacy()
        } else {
            agreeAgreement()
        }

        // 两项目同意才勾选父Activity上的CheckBox
        if (isAgreeUserAgreementAndPrivacy()) cbAgree.isChecked = true
    }


    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.tv_agreement -> {
                //显示《用户使用协议》
                showAgreementDialog(
                    getString(R.string.text_agreement), WebViewSettings.link100
                )
                dialogType = 2
            }

            R.id.tv_agreement2 -> {
                // 显示《隐私政策》
                showAgreementDialog(
                    getString(R.string.text_agreement1), WebViewSettings.link101
                )
                dialogType = 1
            }

            R.id.bto_wx_login -> wxLoginInit() // 微信登录按钮
            R.id.bto_phome_login -> phoneLoginInit() // 使用手机登录
        }

    }

    override fun getLayoutId(): Int {
        return R.layout.activity_login
    }

    override fun getContentTitle(): String? {
        return null
    }

    override fun isShowBack(): Boolean {
        return false
    }




}