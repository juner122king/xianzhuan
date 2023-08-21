package com.lelezu.app.xianzhuan.ui.views

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.github.lzyzsd.jsbridge.BridgeWebView
import com.lelezu.app.xianzhuan.R
import com.lelezu.app.xianzhuan.ui.h5.WebViewSettings
import com.lelezu.app.xianzhuan.ui.h5.WebViewSettings.LINK_KEY
import com.lelezu.app.xianzhuan.ui.h5.WebViewSettings.URL_TITLE
import com.lelezu.app.xianzhuan.ui.h5.WebViewSettings.isProcessing
import com.lelezu.app.xianzhuan.utils.Base64Utils
import com.lelezu.app.xianzhuan.utils.LogUtils


class WebViewActivity : BaseActivity() {

    private lateinit var context: Context

    private lateinit var link: String
    private lateinit var wv: BridgeWebView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = this
        wv = findViewById(R.id.webView)

        link = intent.getStringExtra(LINK_KEY)!!
        WebViewSettings.setDefaultWebSettings(wv)
        Log.i("WebView_URL",  link)
        if (!intent.getBooleanExtra(isProcessing, true)) {

            //显示简单的用户协议页面
            wv.setInitialScale(200)
            wv.loadUrl(link)
            return
        }

        wv.loadUrl(WebViewSettings.host + link)

        //注入打开相册方法
        wv.registerHandler("chooseImage") { data, function ->
            openPhoto()
        }

        //注入回到主页方法
        wv.registerHandler("backToHome") { fragmentPosition, function ->
            backToHome(fragmentPosition)
        }

        //注入进入任务详情页面方法
        wv.registerHandler("gotoTaskDetails") { taskId, function ->
            gotoTaskDetails(taskId)
        }


        //处理返回键
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                backOrFinish()
            }
        })

        super.mBack!!.setOnClickListener { backOrFinish() }


        if (link == WebViewSettings.link8)//充值页面才添加拦截
        //添加uri拦截
            wv.webViewClient = object : WebViewClient() {
                @Deprecated("Deprecated in Java", ReplaceWith("false"))
                override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {

                    // ------  对alipays:相关的scheme处理 -------
                    if (url.startsWith("alipays:") || url.startsWith("alipay")) {
                        try {
                            startActivity(Intent("android.intent.action.VIEW", Uri.parse(url)))
                        } catch (e: Exception) {
                            AlertDialog.Builder(context)
                                .setMessage("未检测到支付宝客户端，请安装后重试。").setPositiveButton(
                                    "立即安装",
                                    DialogInterface.OnClickListener { dialog, which ->
                                        val alipayUrl = Uri.parse("https://d.alipay.com")
                                        startActivity(
                                            Intent(
                                                "android.intent.action.VIEW", alipayUrl
                                            )
                                        )
                                    }).setNegativeButton("取消", null).show()
                        }
                        return true
                    }
                    // ------- 处理结束 -------
                    if (!(url.startsWith("http") || url.startsWith("https"))) {
                        return true
                    }
                    view.loadUrl(url)
                    return true
                }
            }



    }


    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = Uri.fromParts("package", packageName, null)
        startActivity(intent)
    }


    fun backOrFinish() {
        if (wv.canGoBack()) {
            if (wv.url.equals(WebViewSettings.host + link)) finish()
            else wv.goBack()
        } else finish()
    }


    private val rc: Int = 123
    private fun openPhoto() {
        LogUtils.i("打开相册  android级别：${Build.VERSION.SDK_INT}")
        // 检查图片权限
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            //13更高版本后的图片弹窗询问
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.READ_MEDIA_IMAGES), rc
                )
            } else {

                ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), rc
                )
            }
        } else {
            //上传图片，打开相册
            pickImageContract.launch(Unit)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == rc) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 用户授予了权限，继续进行文件操作
                //上传图片，打开相册
                pickImageContract.launch(Unit)
            } else {
                // 用户拒绝了权限，处理拒绝权限的情况
            }
        }
    }

    private val pickImageContract = registerForActivityResult(PickImageContract()) {
        if (it != null) {
            // 获取内容URI对应的文件路径
            val thread = Thread {
                val imageData = Base64Utils.zipPic(it)
                wv.post {
                    Log.i("H5调原生:", "图片字节码长度:${imageData?.length}")
                    Log.i("H5调原生:", "图片字节码:${imageData}")
                    wv.callHandler("showSelectedImage", imageData) {
                        //可以在这里弹出提示
                    }
                }
            }
            thread.start()
        }
    }

    //处理选择图片的请求和结果
    inner class PickImageContract : ActivityResultContract<Unit, Uri?>() {
        override fun createIntent(context: Context, input: Unit): Intent {
            return Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
            if (resultCode == Activity.RESULT_OK) {
                return intent?.data
            }
            return null
        }
    }


    override fun getLayoutId(): Int {
        return R.layout.activity_web_view_stzqactivity
    }

    override fun getContentTitle(): String? {
        return intent.getStringExtra(URL_TITLE)
    }

    override fun isShowBack(): Boolean {
        return true
    }
    protected fun showPermissionAlertDialog(message: String,negaString:String) {
        val alertDialog =
            AlertDialog.Builder(this).setTitle("权限请求").setMessage(message)
                .setPositiveButton("前往设置开启权限") { _, _ ->
                    openAppSettings()
                }.setNegativeButton("取消") { dialog, _ ->
                    showToast(negaString)
                    dialog.dismiss()
                }.create()

        alertDialog.show()
    }
}