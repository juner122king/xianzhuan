package com.lelezu.app.xianzhuan.ui.views

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.lelezu.app.xianzhuan.R
import com.lelezu.app.xianzhuan.ui.viewmodels.LoginViewModel


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
    private val permissionRequestCode = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launch)

        //权限判断
//        checkPermissionsAndStartActivity()
        //登录判断
        preloadContent()

    }

    private fun checkPermissionsAndStartActivity() {
        val missingPermissions = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (missingPermissions.isNotEmpty()) {
            //全部授权
            requestPermissions(missingPermissions.toTypedArray(), permissionRequestCode)
        } else {

        }
    }

    private fun preloadContent() {
        //判断是否已登录APP
        val isLoggedIn = checkUserLoginStatus()
        if (isLoggedIn) {
            // 用户已登录，执行登录接口验证
//            val viewModel = ViewModelProvider(this)[LoginViewModel::class.java]
//            viewModel.verifyUserAccount()

        } else {
            // 用户未登录，执行其他操作或跳转到登录页面
            performOtherActionOrNavigateToLogin()
        }
    }

    private fun checkUserLoginStatus(): Boolean {
        // 从本地存储中获取登录状态，例如使用SharedPreferences
        val sharedPreferences = getSharedPreferences("ApiPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean("LoginStatus", false)


//        sharedPreferences.getString("wechat_code", false)
    }

    private fun performOtherActionOrNavigateToLogin() {
        // 用户未登录时的处理逻辑，例如执行其他操作或跳转到登录页面
        // 跳转到登录页面
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }


    //处理权限请求的结果
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            permissionRequestCode -> {
                val grantedPermissions =
                    grantResults.filter { it == PackageManager.PERMISSION_GRANTED }
                if (grantedPermissions.size == grantResults.size) {
//                    Toast.makeText(this, "全部授权", Toast.LENGTH_LONG).show()
                } else {
//                    Toast.makeText(this, "权限不全", Toast.LENGTH_LONG).show()
                }


                //权限授权完成后进行
                //登录判断
//                preloadContent()

            }
        }
    }
}