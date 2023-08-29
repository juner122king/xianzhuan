package com.lelezu.app.xianzhuan.ui.views

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import cn.jiguang.api.utils.JCollectionAuth
import cn.jpush.android.api.JPushInterface
import com.github.lzyzsd.jsbridge.BridgeWebView
import com.lelezu.app.xianzhuan.MyApplication
import com.lelezu.app.xianzhuan.MyApplication.Companion.context
import com.lelezu.app.xianzhuan.R
import com.lelezu.app.xianzhuan.data.ApiConstants
import com.lelezu.app.xianzhuan.ui.h5.WebViewSettings
import com.lelezu.app.xianzhuan.utils.ShareUtil
import com.lelezu.app.xianzhuan.utils.ShareUtil.agreePrivacy
import com.lelezu.app.xianzhuan.utils.ShareUtil.isAgreePrivacy
import com.lelezu.app.xianzhuan.wxapi.WxLogin
import com.netease.htprotect.HTProtect
import com.netease.htprotect.HTProtectConfig
import com.netease.htprotect.callback.HTPCallback
import com.umeng.commonsdk.UMConfigure
import com.zj.zjsdk.ZjSdk

/**  APP启动屏
1.登录/注册判断：在启动app时，直接通过本地存储判断用户是否已登录，处于已登录时调用登录接口判断账号是否正常状态，正常则直接到启动屏后跳转到首页；
2.账号封禁：启动时判断当前账号是否违规，已违规则跳转到违规封禁页面；
3.登录失效：当启动app时，判断本地的登录时效是否已失效，失效则直接提示用户重新授权登录；
4.在校验自动登录没问题后，则跳转到广告页，用户未登录状态下，则需要登录成功后方进行首页数据加载；
5.广告页：需要在对应的时间内加载首页的数据；*/
@SuppressLint("CustomSplashScreen")
class LaunchActivity : AppCompatActivity() {
    private lateinit var dialog: AlertDialog//协议弹
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launch)



        if (isAgreePrivacy()) {//是否同意了隐私协议
            preloadContent()//进行页面跳转
        } else {
            Handler(Looper.getMainLooper()).postDelayed({
                //未同意，弹出隐私协议询问窗口
                showAgreementDialog(
                    getString(R.string.text_agreement1), WebViewSettings.link101
                )
            }, 1000)//等1秒
        }
    }

    private fun preloadContent() {

        initSDK()//初始化第三方SDK ，等待2秒

        Handler(Looper.getMainLooper()).postDelayed({
            //判断是否已登录APP
            if (checkUserLoginStatus()) {
                // 用户已登录， 跳转到主页登录页面
                toHome()
            } else {
                // 用户未登录，跳转到登录页面
                toLogin()
            }
        }, 2000) // 延迟 2000 毫秒（即 2 秒）

    }

    private fun initSDK() {


        init163SDK()//网易SDK初始化

        WxLogin.initWx(this) //微信SDK初始化

        initUMSDK()//友盟SDK初始化

        initJPUSHSDK()//极光SDK初始化

//        initZJSDK()//任务墙SDK 初始化

        ShareUtil.putAndroidID(this) //获取Android ID

    }


    // 显示协议弹窗
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


    fun onDisagreeButtonClick(view: View) {

        dialog.dismiss()
        finish()//不同意直接退出应用
    }

    fun onAgreeButtonClick(view: View) {
        // 处理同意的逻辑
        dialog.dismiss()
        agreePrivacy()
        preloadContent()
    }


    private fun checkUserLoginStatus(): Boolean {
        return ShareUtil.getBoolean(ShareUtil.APP_SHARED_PREFERENCES_LOGIN_STATUS)
    }

    private fun toLogin() {

        // 跳转到登录页面
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun toHome() {

        // 用户已登录， 跳转到主页登录页面
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }


    private fun init163SDK() {
        val config = HTProtectConfig().apply {
            var serverType = 2
            var channel = "testchannel"
        }
        val callback = HTPCallback { paramInt, paramString ->
            Log.d("易盾Test", "code is: $paramInt String is: $paramString")
            // paramInt返回200说明初始化成功
        }
        HTProtect.init(MyApplication.context, ApiConstants.DUN_BUSINESS_NO, callback, config)
        //易盾结束

    }

    private fun initUMSDK() {

        //正式初始化函数
        UMConfigure.init(
            this,
            ApiConstants.UM_BUSINESS_NO,
            getString(R.string.app_name),
            UMConfigure.DEVICE_TYPE_PHONE,
            ""
        )
        UMConfigure.setLogEnabled(true)
        //友盟结束

    }

    private fun initJPUSHSDK() {

        //极光SDK开始
        JPushInterface.setDebugMode(false)
        // 调整点一：初始化代码前增加setAuth调用
//        val isPrivacyReady// app根据是否已弹窗获取隐私授权来赋值
//        if(!isPrivacyReady){
//            JCollectionAuth.setAuth(context, false); // 后续初始化过程将被拦截
//        }
        JPushInterface.init(this)
        // 调整点二：隐私政策授权获取成功后调用
        JCollectionAuth.setAuth(context, true) //如初始化被拦截过，将重试初始化过程
        //极光SDK结束
    }


    private fun initZJSDK() {

        // 任务墙SDK 初始化
        ZjSdk.init(this, ApiConstants.ZJ_BUSINESS_NO, object : ZjSdk.ZjSdkInitListener {
            override fun initSuccess() {
                Log.i("任务墙SDK", "初始化成功！")
            }

            override fun initFail(code: Int, msg: String?) {
                Log.i("任务墙SDK", "初始化失败！")
            }
        })

    }
}