package com.lelezu.app.xianzhuan.ui.views

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.github.lzyzsd.jsbridge.BridgeWebView
import com.lelezu.app.xianzhuan.MyApplication
import com.lelezu.app.xianzhuan.MyApplication.Companion.context
import com.lelezu.app.xianzhuan.R
import com.lelezu.app.xianzhuan.data.model.LoginReP
import com.lelezu.app.xianzhuan.dun163api.PhoneLoginActivity
import com.lelezu.app.xianzhuan.ui.h5.WebViewSettings
import com.lelezu.app.xianzhuan.ui.viewmodels.LoginViewModel2
import com.lelezu.app.xianzhuan.utils.ToastUtils
import com.lelezu.app.xianzhuan.wxapi.WxLogin


//登录页面
class LoginActivity : AppCompatActivity(), OnClickListener {

    private lateinit var cbAgree: CheckBox//是否同意思协议按钮
    private lateinit var dialog: AlertDialog//协议弹
    private val loginViewModel2: LoginViewModel2 by viewModels {
        LoginViewModel2.LoginViewFactory((application as MyApplication).userRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContentView(R.layout.activity_login)
        cbAgree = findViewById(R.id.cb_agree_agreement)//是否同意思协议按钮
        findViewById<TextView>(R.id.bto_phome_login).setOnClickListener(this)//‘使用手机登录’按钮
        findViewById<ImageView>(R.id.bto_wx_login).setOnClickListener(this)//微信登录按钮
        findViewById<TextView>(R.id.tv_agreement).setOnClickListener(this) //打开用户使用协议
        findViewById<TextView>(R.id.tv_agreement2).setOnClickListener(this) //打开《隐私政策》

    }

    private fun wxLoginInit() {

        when (cbAgree.isChecked) {
            true -> {
                WxLogin.longWx()  //微信登录
            }

            else -> {
                ToastUtils.showToast(this, "请同意隐私政策")
            }
        }
    }

    private fun getLogin(wxCode: String) {
        Log.i("LoginActivity", "开始执行登录请求方法getLogin")
        loginViewModel2.getLoginInfo(wxCode)
        loginViewModel2.loginRePLiveData.observe(this) {
            onLogin(it)
        }

    }

    private fun goToRegister() {
        loginViewModel2.getRegister()
        loginViewModel2.registerLoginRePLiveData.observe(this) {
            onLogin(it)
        }
    }

    private fun onLogin(it: LoginReP?) {
        if (it != null) {
            Log.i(
                "LoginActivity登录与注册",
                "用户ID:${it.userId},token：${it.accessToken},新用户？：${it.isNewer}"
            )
            if (it.isNewer) {

                startActivity(Intent(context, PhoneLoginActivity::class.java))
            } else {
                goToHomeActivity()
            }
        }else{
            ToastUtils.showToast(this, "登录失败！请重试！")
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
    }

    // 同意按钮点击事件
    fun onAgreeButtonClick(view: View) {
        // 在这里处理同意的逻辑
        // 关闭弹窗
        dialog.dismiss()
        // 勾选父Activity上的CheckBox
        cbAgree.isChecked = true
    }


    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.tv_agreement -> showAgreementDialog(
                getString(R.string.text_agreement), WebViewSettings.link100
            ) // 显示《用户使用协议》
            R.id.tv_agreement2 -> showAgreementDialog(
                getString(R.string.text_agreement1), WebViewSettings.link100
            ) // 显示《隐私政策》
            R.id.bto_wx_login -> wxLoginInit() // 微信登录按钮
            R.id.bto_phome_login -> wxLoginInit() // 使用手机登录
        }

    }


}