package com.lelezu.app.xianzhuan.ui.views

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import cn.hutool.core.codec.Base64
import com.google.gson.Gson
import com.lelezu.app.xianzhuan.R
import com.lelezu.app.xianzhuan.utils.AesTool
import com.lelezu.app.xianzhuan.utils.DeviceUtils
import com.lelezu.app.xianzhuan.utils.ShareUtil
import okio.Utf8
import java.util.concurrent.TimeUnit


/**  APP启动屏
1.登录/注册判断：在启动app时，直接通过本地存储判断用户是否已登录，处于已登录时调用登录接口判断账号是否正常状态，正常则直接到启动屏后跳转到首页；

2.账号封禁：启动时判断当前账号是否违规，已违规则跳转到违规封禁页面；

3.登录失效：当启动app时，判断本地的登录时效是否已失效，失效则直接提示用户重新授权登录；

4.在校验自动登录没问题后，则跳转到广告页，用户未登录状态下，则需要登录成功后方进行首页数据加载；

5.广告页：需要在对应的时间内加载首页的数据；*/

@SuppressLint("CustomSplashScreen")
class LaunchActivity : AppCompatActivity() {

    private val permissions = arrayOf(
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.READ_SMS,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launch)


        Handler(Looper.getMainLooper()).postDelayed({
            // 在这里执行你想要暂停的操作
            //登录判断
            preloadContent()
        }, 2000) // 延迟 2000 毫秒（即 2 秒）


    }


    private fun preloadContent() {
        //判断是否已登录APP
        val isLoggedIn = checkUserLoginStatus()
        if (isLoggedIn) {
            // 用户已登录， 跳转到主页登录页面
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            // 用户未登录，执行其他操作或跳转到登录页面
            performOtherActionOrNavigateToLogin()
        }
    }

    private fun checkUserLoginStatus(): Boolean {
        return ShareUtil.getBoolean(ShareUtil.APP_SHARED_PREFERENCES_LOGIN_STATUS)
    }

    private fun performOtherActionOrNavigateToLogin() {
        // 用户未登录时的处理逻辑，例如执行其他操作或跳转到登录页面
        // 跳转到登录页面
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}