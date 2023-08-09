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
import com.lelezu.app.xianzhuan.MyApplication
import com.lelezu.app.xianzhuan.MyApplication.Companion.context
import com.lelezu.app.xianzhuan.R
import com.lelezu.app.xianzhuan.data.ApiConstants
import com.lelezu.app.xianzhuan.data.ApiService
import com.lelezu.app.xianzhuan.data.model.ApiResponse
import com.lelezu.app.xianzhuan.data.model.LoginInfo
import com.lelezu.app.xianzhuan.data.model.LoginReP
import com.lelezu.app.xianzhuan.data.model.Register
import com.lelezu.app.xianzhuan.dun163api.PhoneLoginActivity
import com.lelezu.app.xianzhuan.ui.viewmodels.LoginViewModel2
import com.lelezu.app.xianzhuan.utils.ShareUtil
import com.lelezu.app.xianzhuan.utils.ToastUtils
import com.lelezu.app.xianzhuan.wxapi.WxData
import com.lelezu.app.xianzhuan.wxapi.WxLogin
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


//登录页面
class LoginActivity : AppCompatActivity(), OnClickListener {

    private lateinit var cbAgree: CheckBox//是否同意思协议按钮
    private lateinit var dialog: AlertDialog//协议弹


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContentView(R.layout.activity_login)
        cbAgree = findViewById(R.id.cb_agree_agreement)//是否同意思协议按钮
        findViewById<TextView>(R.id.bto_phome_login).setOnClickListener(this)//‘使用手机登录’按钮
        findViewById<ImageView>(R.id.bto_wx_login).setOnClickListener(this)//微信登录按钮
        findViewById<TextView>(R.id.tv_agreement).setOnClickListener(this) //打开协议按钮

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

        getService().getLogin(
            LoginInfo(
                WxData.WEIXIN_APP_ID, ApiConstants.LOGIN_METHOD_WX, null, null, wxCode
            )
        ).enqueue(object : Callback<ApiResponse<LoginReP>> {
            override fun onResponse(
                call: Call<ApiResponse<LoginReP>>, response: Response<ApiResponse<LoginReP>>
            ) {
                if (response.isSuccessful) {
                    when (response.body()?.code) {
                        "000000" -> {
                            response.body()?.data?.let {
                                saveInfo(it)
                                if (it.isNewer) {
                                    Log.i("LoginActivity", "新用户  登录成功：$it")

                                    val intent = Intent(context, PhoneLoginActivity::class.java)
                                    startActivity(intent)
                                } else {
                                    Log.i("LoginActivity", "旧用户  登录成功：$it")
                                    goToHomeActivity()
                                }
                            }
                        }

                        else -> {
                            Log.d(
                                "APP登录接口login",
                                "登录失败${response.body()?.code}:${response.body()?.message}"
                            )
                        }
                    }
                }
            }

            override fun onFailure(call: Call<ApiResponse<LoginReP>>, t: Throwable) {
            }


        })


    }

    private fun getService(): ApiService {
        val retrofit = Retrofit.Builder().baseUrl(ApiConstants.HOST)
            .addConverterFactory(GsonConverterFactory.create()).build()


        return retrofit.create(ApiService::class.java)
//        logservice.getLogin(LoginInfo(WxData.WEIXIN_APP_ID, ApiConstants.LOGIN_METHOD_WX, null, null, wxCode))

    }


    private fun goToRegister(register: Register) {
        val loginViewModel2: LoginViewModel2 by viewModels {
            LoginViewModel2.LoginViewFactory((application as MyApplication).userRepository)
        }

        loginViewModel2.getRegister(register)
        loginViewModel2.registerLoginRePLiveData.observe(this) {

            Log.i("LoginActivity", "注册返回信息：：$it")
            if (it != null) {
                if (it.isNewer) {
                    Log.i("LoginActivity", "新用户  注册成功：$it")
                    saveInfo(it)
                    val intent = Intent(context, PhoneLoginActivity::class.java)
                    startActivity(intent)
                } else {
                    Log.i("LoginActivity", "旧用户  注册成功：$it")
                    goToHomeActivity()
                }
            } else {
                Log.i("LoginActivity", "登录失败")
            }

        }


    }

    private fun goToHomeActivity() {
        finish()
        val intent = Intent(context, HomeActivity::class.java)
        startActivity(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        Log.i("LoginActivity", "方法onNewIntent()")
        //从微信授权页面返回
        if (intent != null) {
            if (intent.getStringExtra("type").equals("WX")) intent.getStringExtra("wx_code")?.let {
                getLogin(it)
            }
            if (intent.getStringExtra("type").equals("163")) {
                ToastUtils.showToast(this, "一键登录成功", 0)


                val deviceToken = ShareUtil.getString(ShareUtil.APP_163_PHONE_LOGIN_DEVICE_TOKEN)
                val mobileAccessToken =
                    ShareUtil.getString(ShareUtil.APP_163_PHONE_LOGIN_MOBILE_ACCESS_TOKEN)
                val mobileToken = ShareUtil.getString(ShareUtil.APP_163_PHONE_LOGIN_MOBILE_TOKEN)
                val userId = ShareUtil.getString(ShareUtil.APP_SHARED_PREFERENCES_LOGIN_ID)
                val loginToken = ShareUtil.getString(ShareUtil.APP_SHARED_PREFERENCES_LOGIN_TOKEN)


                Log.i(
                    "登录信息",
                    "loginToken:${loginToken},deviceToken:${deviceToken},mobileAccessToken:${mobileAccessToken},mobileToken:${mobileToken},userId:${userId}"
                )


                //请求注册接口
                goToRegister(Register(deviceToken, mobileAccessToken, mobileToken, null, userId))
            }
        }
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


    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.tv_agreement -> showAgreementDialog() // 显示协议弹窗
            R.id.bto_wx_login -> wxLoginInit() // 微信登录按钮
            R.id.bto_phome_login -> wxLoginInit() // 使用手机登录
        }

    }


    //保存登录信息
    private fun saveInfo(loginReP: LoginReP) {

        //保存登录信息
        ShareUtil.putString(
            ShareUtil.APP_SHARED_PREFERENCES_LOGIN_TOKEN, loginReP.accessToken
        ) //保存登录TOKEN
        ShareUtil.putString(ShareUtil.APP_SHARED_PREFERENCES_LOGIN_ID, loginReP.userId) //保存用户id

        if (loginReP.isNewer) ShareUtil.putBoolean(
            ShareUtil.APP_SHARED_PREFERENCES_LOGIN_STATUS, false
        ) //保存登录状态
        else ShareUtil.putBoolean(ShareUtil.APP_SHARED_PREFERENCES_LOGIN_STATUS, true)
    }

    //清除登录信息
    private fun cleanInfo() {
        ShareUtil.clean(ShareUtil.APP_SHARED_PREFERENCES_LOGIN_TOKEN) //清空登录TOKEN
        ShareUtil.clean(ShareUtil.APP_SHARED_PREFERENCES_LOGIN_ID) //清空用户id
        ShareUtil.putBoolean(ShareUtil.APP_SHARED_PREFERENCES_LOGIN_STATUS, false) //保存登录状态
    }


}