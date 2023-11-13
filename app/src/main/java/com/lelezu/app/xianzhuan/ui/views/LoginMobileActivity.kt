package com.lelezu.app.xianzhuan.ui.views

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.github.lzyzsd.jsbridge.BridgeWebView
import com.lelezu.app.xianzhuan.R
import com.lelezu.app.xianzhuan.ui.h5.WebViewSettings
import com.lelezu.app.xianzhuan.utils.ShareUtil
class LoginMobileActivity : BaseActivity(), View.OnClickListener {

    private lateinit var textPwd: EditText
    private lateinit var textNum: EditText


    private lateinit var cbAgree: CheckBox//是否同意思协议按钮
    private lateinit var dialog: AlertDialog//协议弹窗
    private var dialogType: Int = 1//弹窗的协议类型  1为隐私协议  2为用户协议


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
    }


    @SuppressLint("ClickableViewAccessibility")
    private fun initView() {
        textPwd = findViewById(R.id.tv_pwd)
        textNum = findViewById(R.id.tv_num)

        cbAgree = findViewById(R.id.cb_agree_agreement)//是否同意思协议按钮


        //密码显示监听
        findViewById<CheckBox>(R.id.iv_pwd).setOnCheckedChangeListener { _, b ->
            if (b) showPassword()
            else hidePassword()
        }


        findViewById<View>(R.id.iv_back_wx).setOnClickListener(this)
        findViewById<View>(R.id.tv_login).setOnClickListener(this)

        findViewById<TextView>(R.id.tv_agreement).setOnClickListener(this) //打开用户使用协议
        findViewById<TextView>(R.id.tv_agreement2).setOnClickListener(this) //打开《隐私政策》


    }


    private fun showPassword() {
        textPwd.transformationMethod = null
    }

    private fun hidePassword() {
        textPwd.transformationMethod = PasswordTransformationMethod()
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
            ShareUtil.disAgreePrivacy()
        } else {
            ShareUtil.disAgreeAgreement()
        }


    }

    // 同意按钮点击事件
    fun onAgreeButtonClick(view: View) {
        // 在这里处理同意的逻辑
        // 关闭弹窗
        dialog.dismiss()

        if (dialogType == 1) {
            ShareUtil.agreePrivacy()
        } else {
            ShareUtil.agreeAgreement()
        }

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

            R.id.iv_back_wx -> finish() //返回微信登录页面
            R.id.tv_login -> login() //手机登录
        }

    }

    private fun login() {
        val pwdText = textPwd.text.toString().trim()
        val numText = textNum.text.toString().trim()
        if (cbAgree.isChecked) {
            when {
                pwdText.isNotEmpty() && numText.isNotEmpty() -> {

                    //实际登录
                    getLogin(numText, pwdText)//

                }
                else -> showToast("账号或密码不能为空!")
            }
        } else showToast("请同意隐私政策!")

    }

    private fun getLogin(numText: String, pwdText: String) {
        loginViewModel.getLoginInfo(numText, pwdText)
        loginViewModel.loginRePLiveData.observe(this) {
            hideLoading()
            goToHomeActivity()
        }

    }


    override fun getLayoutId(): Int {
        return (R.layout.activity_login_mobile)
    }

    override fun getContentTitle(): String? {
        return getString(R.string.title_phone_login)
    }

    override fun isShowBack(): Boolean {
        return true
    }

}