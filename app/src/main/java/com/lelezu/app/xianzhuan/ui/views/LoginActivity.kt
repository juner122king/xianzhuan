package com.lelezu.app.xianzhuan.ui.views

import ToastUtils
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.lelezu.app.xianzhuan.MyApplication.Companion.context
import com.lelezu.app.xianzhuan.R
import com.lelezu.app.xianzhuan.data.repository.LoginRepository
import com.lelezu.app.xianzhuan.dun163api.PhoneLoginActivity
import com.lelezu.app.xianzhuan.wxapi.WxLogin
import com.netease.htprotect.result.AntiCheatResult
import kotlinx.coroutines.launch


//登录页面
class LoginActivity : AppCompatActivity() {

    private lateinit var cbAgree: CheckBox//是否同意思协议按钮
    private lateinit var dialog: AlertDialog//协议弹窗
    private lateinit var loginRepository: LoginRepository//
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.i("LoginActivity", "方法onCreate()")
        setContentView(R.layout.activity_login)
        loginRepository = LoginRepository()

        val btoPhoneLogin = findViewById<TextView>(R.id.bto_phome_login)//‘使用手机登录’按钮
        val btoWxLogin = findViewById<ImageView>(R.id.bto_wx_login)//微信登录按钮
        val tvAgreement = findViewById<TextView>(R.id.tv_agreement)//打开协议按钮

        cbAgree = findViewById<CheckBox>(R.id.cb_agree_agreement)//是否同意思协议按钮

        //点击 ‘使用手机登录’的动作
        btoPhoneLogin.setOnClickListener {
            startActivity(Intent(this, PhoneLoginActivity::class.java))
        }
        //打开协议
        tvAgreement.setOnClickListener {
            // 显示弹窗
            showAgreementDialog()

        }


        //微信Api初始化
        WxLogin.initWx(this)
        btoWxLogin.setOnClickListener {
            wxLoginInit()
        }

    }

    private fun getLogin(wxCode: String) {
        Log.i("LoginActivity", "开始执行登录请求方法getLogin")
        // 用户进行登录，执行登录接口
        lifecycleScope.launch {
            val loginReP = loginRepository.wxLogin(wxCode)

            if (loginReP != null) {
                // 处理登录成功逻辑

                //保存登录信息
                val sharedPreferences = getSharedPreferences("ApiPrefs", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putString("LoginToken", loginReP.accessToken)
                editor.putString("LoginId", loginReP.userId)
                editor.putBoolean("LoginStatus", true)
                editor.apply()


                context?.let { ToastUtils.showToast(it, "微信登录成功！") }
                finish()
                val intent = Intent(context, HomeActivity::class.java)
                startActivity(intent)
            } else {
                // 处理登录失败逻辑
            }
        }


    }


    override fun onRestart() {
        super.onRestart()
        Log.i("LoginActivity", "方法onRestart()")
    }

    override fun onStart() {
        super.onStart()
        Log.i("LoginActivity", "方法onStart()")
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        Log.i("LoginActivity", "方法onNewIntent()")
        //从微信授权页面返回
        if (intent != null) {
            intent.getStringExtra("wx_code")?.let { getLogin(it) }
        };

    }

    override fun onResume() {
        super.onResume()
        Log.i("LoginActivity", "方法onResume()")
    }

    // 显示用户协议弹窗
    private fun showAgreementDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_user_agreement, null)
        val textContent = dialogView.findViewById<TextView>(R.id.tvContent)
        // 设置协议内容
        textContent.text = getString(R.string.agreement_content)

        // 创建弹窗
        val builder = AlertDialog.Builder(this)
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

    //微信登录
    private fun wxLoginInit() {

        when (cbAgree.isChecked) {
            true -> {
                WxLogin.longWx();
            }

            else -> {
                ToastUtils.showToast(this, "请同意隐私政策")

            }
        }

    }
}