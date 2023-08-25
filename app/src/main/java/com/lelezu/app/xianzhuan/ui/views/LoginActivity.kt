package com.lelezu.app.xianzhuan.ui.views

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.github.lzyzsd.jsbridge.BridgeWebView
import com.lelezu.app.xianzhuan.MyApplication.Companion.context
import com.lelezu.app.xianzhuan.R
import com.lelezu.app.xianzhuan.data.model.LoginReP
import com.lelezu.app.xianzhuan.dun163api.PhoneLoginActivity
import com.lelezu.app.xianzhuan.ui.h5.WebViewSettings
import com.lelezu.app.xianzhuan.utils.ShareUtil.agreeAgreement
import com.lelezu.app.xianzhuan.utils.ShareUtil.agreePrivacy
import com.lelezu.app.xianzhuan.utils.ShareUtil.disAgreeAgreement
import com.lelezu.app.xianzhuan.utils.ShareUtil.disAgreePrivacy
import com.lelezu.app.xianzhuan.utils.ShareUtil.isAgreeUserAgreementAndPrivacy
import com.lelezu.app.xianzhuan.wxapi.WxLogin


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
                showLoading()
                WxLogin.longWx()  //微信登录
            }

            else -> {
                showToast("请同意隐私政策")
            }
        }
    }

    private fun getLogin(wxCode: String) {
        Log.i("LoginActivity", "开始执行登录请求方法getLogin")
        loginViewModel.getLoginInfo(wxCode)
        loginViewModel.loginRePLiveData.observe(this) {
            hideLoading()
            onLogin(it)
        }

    }

    private fun goToRegister() {
        loginViewModel.getRegister()
        loginViewModel.registerLoginRePLiveData.observe(this) {
            hideLoading()
            onLogin(it)
        }
    }

    private fun onLogin(it: LoginReP) {
        Log.i(
            "LoginActivity登录与注册",
            "用户ID:${it.userId},token：${it.accessToken},新用户？：${it.isNewer}"
        )
        if (it.isNewer) {
            startActivity(Intent(context, PhoneLoginActivity::class.java))
        } else {
            goToHomeActivity()
        }
    }


    private fun goToHomeActivity() {

        finish()
        startActivity(Intent(context, HomeActivity::class.java))
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent != null) {
            Log.i("LoginActivity上级页面：", "${intent.getStringExtra("type")}")
            //从微信授权页面返回
            if (intent.getStringExtra("type").equals("WX")) intent.getStringExtra("wx_code")?.let {
                getLogin(it)
            }
            if (intent.getStringExtra("type").equals("163")) {
                //请求注册接口
                goToRegister()
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
            R.id.bto_phome_login -> wxLoginInit() // 使用手机登录
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


    /**
     *    //监听焦点变化再获取剪切板数据 Android 10以及以上版本限制了对剪贴板数据的访问
     * @param hasFocus Boolean
     */
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) getClipBoar()
    }
}