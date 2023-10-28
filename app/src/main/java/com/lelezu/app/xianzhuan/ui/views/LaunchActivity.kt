package com.lelezu.app.xianzhuan.ui.views

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import cn.jiguang.api.utils.JCollectionAuth
import cn.jpush.android.api.JPushInterface
import com.fendasz.moku.planet.utils.thirdparty.baidu.util.DeviceId
import com.github.lzyzsd.jsbridge.BridgeWebView
import com.lelezu.app.xianzhuan.MyApplication
import com.lelezu.app.xianzhuan.R
import com.lelezu.app.xianzhuan.data.ApiConstants
import com.lelezu.app.xianzhuan.ui.h5.WebViewSettings
import com.lelezu.app.xianzhuan.utils.ImageViewUtil
import com.lelezu.app.xianzhuan.utils.LogUtils
import com.lelezu.app.xianzhuan.utils.ShareUtil
import com.lelezu.app.xianzhuan.utils.ShareUtil.agreePrivacy
import com.lelezu.app.xianzhuan.utils.ShareUtil.isAgreePrivacy
import com.lelezu.app.xianzhuan.utils.UUIDUtils
import com.lelezu.app.xianzhuan.wxapi.WxData
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
class LaunchActivity : BaseActivity() {
    private lateinit var dialog: AlertDialog//协议弹
    private lateinit var aDView: ImageView//协议弹
    private lateinit var tvCd: TextView//倒计时

    private var cd1 = 7000L//广告倒计时
    private var cd2 = 2000L//logo显示时间

    private lateinit var countDownTimer: CountDownTimer

    private var LOGTAG: String = "SDK_INIT"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideView()
        initSDK()//初始化第三方SDK
        initView()
        initData()
        showLogo()

        //拦截返回键
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })
    }


    private fun initSDK() {

        init163SDK()//网易SDK初始化
        initWx() //微信SDK初始化
        initJPUSHSDK()//极光SDK初始化
        initUMSDK()//友盟SDK初始化
        initZJSDK()//任务墙SDK 初始化
        //TX审核 去掉
//


    }

    private fun initView() {
        aDView = findViewById(R.id.fl_ad_view)
        tvCd = findViewById(R.id.tv_comdown)
        tvCd.setOnClickListener {
            preloadContent()//点击跳过广告
        }

    }

    private fun initData() {
        sysMessageViewModel.apiADConfig()
        //加载广告页面
        sysMessageViewModel.adconfig.observe(this) {
            ImageViewUtil.load(aDView, it.confValue.images[0])
        }

    }


    private fun showLogo() {
        Handler(Looper.getMainLooper()).postDelayed({
            if (isAgreePrivacy()) {//是否同意了隐私协议
                showTaskView()//显示广告
            } else {
                //未同意，弹出隐私协议询问窗口
                showAgreementDialog(
                    getString(R.string.text_agreement1), WebViewSettings.link101
                )
            }
        }, cd2)//显示一秒logo

    }


    //显示广告页面
    private fun showTaskView() {
        tvCd.text = "跳过${cd1 / 1000}"
        // 开始倒计时
        startCountdown(cd1) // 6秒广告
        findViewById<View>(R.id.fl_launch_view).visibility = View.INVISIBLE
    }

    private fun startCountdown(totalMillis: Long) {
        countDownTimer = object : CountDownTimer(totalMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = millisUntilFinished / 1001
                LogUtils.i("CD", secondsRemaining.toString() + "秒")
                tvCd.text = "跳过$secondsRemaining"
            }

            override fun onFinish() {
                // 倒计时完成后执行你的页面跳转操作
                preloadContent()
            }
        }

        countDownTimer.start()
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

    private fun preloadContent() {

        //判断是否已登录APP
        if (checkUserLoginStatus()) {
            // 用户已登录， 跳转到主页登录页面
            goToHomeActivity()
        } else {
            // 用户未登录，跳转到登录页面
            toLogin()
        }
    }


    private fun checkUserLoginStatus(): Boolean {
        return ShareUtil.getBoolean(ShareUtil.APP_SHARED_PREFERENCES_LOGIN_STATUS)
    }

    private fun toLogin() {

        showLoading()
        loginViewModel.getLoginConfig()
        loginViewModel.loginConfig.observe(this) {
            hideLoading()
            if (it.confValue.page == 0) {
                // 跳转到登录页面
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            } else {
                // 跳转到登录页面
                val intent = Intent(this, LoginMobileActivity::class.java)
                startActivity(intent)
            }
            finish()

        }


    }


    private fun initWx() {
        (application as MyApplication).api.registerApp(WxData.WEIXIN_APP_ID)
    }

    private fun init163SDK() {

        LogUtils.i(LOGTAG, "163SDK初始化开始")


        val config = HTProtectConfig().apply {
            var serverType = 2
            var channel = "testchannel"
        }
        val callback = HTPCallback { paramInt, paramString ->
            Log.d("易盾Test", "code is: $paramInt String is: $paramString")
            // paramInt返回200说明初始化成功
        }
        HTProtect.init(this, ApiConstants.DUN_BUSINESS_NO, callback, config)
        //易盾结束

    }

    private fun initUMSDK() {

        LogUtils.i(LOGTAG, "友盟SDK初始化开始")


        //友盟开始
        //调用预初始化函数
        UMConfigure.preInit(this, ApiConstants.UM_BUSINESS_NO, "正式")


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


        LogUtils.i(LOGTAG, "极光SDK初始化开始")
        JPushInterface.setDebugMode(false)
        JPushInterface.init(this)
        // 调整点二：隐私政策授权获取成功后调用
        JCollectionAuth.setAuth(this, true) //如初始化被拦截过，将重试初始化过程

    }


    private fun initZJSDK() {
        LogUtils.i(LOGTAG, "任务墙SDK初始化开始")
        //任务墙SDK 初始化
        ZjSdk.init(this, ApiConstants.ZJ_BUSINESS_NO, object : ZjSdk.ZjSdkInitListener {
            override fun initSuccess() {
                Log.i("任务墙SDK", "初始化成功！")
            }

            override fun initFail(code: Int, msg: String?) {
                Log.i("任务墙SDK", "初始化失败！")
            }
        })

    }

    override fun getLayoutId(): Int {

        return R.layout.activity_launch
    }

    override fun getContentTitle(): String? {
        return null
    }

    override fun isShowBack(): Boolean {
        return false
    }

    override fun onDestroy() {
        super.onDestroy()
        // 在Activity销毁时停止倒计时，避免内存泄漏
        if (::countDownTimer.isInitialized) {
            countDownTimer.cancel()
        }
    }

}