package com.lelezu.app.xianzhuan.ui.views

import android.animation.Animator
import android.animation.AnimatorInflater
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.lelezu.app.xianzhuan.R
import com.lelezu.app.xianzhuan.receiver.CountdownReceiver
import com.lelezu.app.xianzhuan.ui.viewmodels.SharedCountdownViewModel
import com.lelezu.app.xianzhuan.wxapi.WXEntryActivity
import com.tencent.mm.opensdk.constants.ConstantsAPI
import com.tencent.mm.opensdk.modelmsg.SendAuth
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.WXAPIFactory


//登录页面
class LoginActivity : AppCompatActivity() {

    private lateinit var sharedViewModel: SharedCountdownViewModel
    private lateinit var buttonGetCode: Button
    private lateinit var cbAgree: CheckBox//是否同意思协议按钮
    private lateinit var dialog: AlertDialog//协议弹窗
    private lateinit var countdownReceiver: CountdownReceiver

    private var countdownTimer: CountDownTimer? = null

    private var iwxapi: IWXAPI? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        val btoPhoneLogin = findViewById<TextView>(R.id.bto_phome_login)//‘使用手机登录’按钮
        val btoWxLogin = findViewById<ImageView>(R.id.bto_wx_login)//微信登录按钮
        val viewPhoneCode = findViewById<View>(R.id.view_phone_code)//手机输入框与获取验证码按钮 的组合View
        val WxLogin = findViewById<View>(R.id.bto_wx_login)//微信登录按钮
        val tvAgreement = findViewById<TextView>(R.id.tv_agreement)//打开协议按钮

        buttonGetCode = findViewById<Button>(R.id.buttonGetCode)//获取验证码按钮
        cbAgree = findViewById<CheckBox>(R.id.cb_agree_agreement)//是否同意思协议按钮


        //点击 ‘使用手机登录’的动作
        btoPhoneLogin.setOnClickListener {
            btoWxLogin.visibility = View.GONE
            btoPhoneLogin.visibility = View.GONE
            viewPhoneCode.visibility = View.VISIBLE

            //点击动画
            val animSet = AnimatorSet()
            // 显示 anotherView 动画（从右到左出现）
            val showAnimator =
                AnimatorInflater.loadAnimator(this, R.animator.slide_in_right) as Animator
            showAnimator.setTarget(viewPhoneCode)
            animSet.playTogether(showAnimator)
            animSet.interpolator = AccelerateInterpolator()
            animSet.duration = 300
            animSet.start()
        }

        //获取验证码按钮的动作
        buttonGetCode.setOnClickListener {
            startCountdown(60000, 1000)
            //页面跳转到验证手机号码页面
            val intent = Intent(this, SetSMSCodeActivity::class.java)
            startActivity(intent)

        }
        //打开协议
        tvAgreement.setOnClickListener {

            // 显示弹窗
            showAgreementDialog()

        }


        //微信登录
        iwxapi = WXAPIFactory.createWXAPI(this, "wx1bdc5e2f8be515eb", true)
        // 将应用的appId注册到微信
        iwxapi?.registerApp("wx1bdc5e2f8be515eb")
        // 动态监听微信启动广播进行注册到微信
        registerReceiver(object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                // 将该app注册到微信
                iwxapi?.registerApp("wx1bdc5e2f8be515eb")
            }
        }, IntentFilter(ConstantsAPI.ACTION_REFRESH_WXAPP))
        WxLogin.setOnClickListener {
            WxLoginInit()
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


    //微信登录
    private fun WxLoginInit() {

        when (cbAgree.isChecked) {
            true -> {

             //等待签名处理完
//                val req = SendAuth.Req()
//                req.scope = "snsapi_userinfo"
//                req.state = "wechat_sdk_demo_test"
//                iwxapi?.sendReq(req)

                //直接进入主页
                ToastUtils.showToast(this, "登录成功")

                startActivity(Intent(this, HomeActivity::class.java))


            }

            else -> {
                ToastUtils.showToast(this, "请同意隐私政策")

            }
        }


    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(countdownReceiver)
        countdownTimer?.cancel() // 取消倒计时计时器
    }


    //注册倒计时广播
    private fun registerReceiver() {
        //初始化ViewMode
        sharedViewModel = ViewModelProvider(this)[SharedCountdownViewModel::class.java]
        sharedViewModel.countdownTime.observe(this) { countdownTime ->
            buttonGetCode.text = (countdownTime / 1000).toString()
        }
        countdownReceiver = CountdownReceiver()
        countdownReceiver.addListener(object : CountdownReceiver.CountdownListener {
            override fun onTick(countdownTime: Long) {
                sharedViewModel.setCountdownTime(countdownTime)
            }
        })
        registerReceiver(countdownReceiver, IntentFilter(CountdownReceiver.ACTION_TICK))

    }


    //验证码获取倒计
    private fun startCountdown(millisInFuture: Long, countDownInterval: Long) {
        countdownTimer?.cancel() // 取消之前的计时器
        countdownTimer = object : CountDownTimer(millisInFuture, countDownInterval) {
            override fun onTick(millisUntilFinished: Long) {
                sharedViewModel.setCountdownTime(millisUntilFinished)

                //发送广播消息
                val intent = Intent(CountdownReceiver.ACTION_TICK)
                intent.putExtra(CountdownReceiver.EXTRA_COUNTDOWN_TIME, millisUntilFinished)
                sendBroadcast(intent)
            }

            override fun onFinish() {
                sharedViewModel.setCountdownTime(0)
            }
        }.start()
    }
}