package com.lelezu.app.xianzhuan.ui.views

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.github.lzyzsd.jsbridge.BridgeWebView
import com.hjq.permissions.OnPermissionCallback
import com.lelezu.app.xianzhuan.R
import com.lelezu.app.xianzhuan.ui.h5.WebViewSettings
import com.lelezu.app.xianzhuan.ui.h5.WebViewSettings.LINK_KEY
import com.lelezu.app.xianzhuan.ui.h5.WebViewSettings.URL_TITLE
import com.lelezu.app.xianzhuan.ui.h5.WebViewSettings.isProcessing
import com.lelezu.app.xianzhuan.ui.h5.WebViewSettings.link11
import com.lelezu.app.xianzhuan.utils.Base64Utils
import com.lelezu.app.xianzhuan.utils.LogUtils
import com.lelezu.app.xianzhuan.utils.MyPermissionUtil
import com.lelezu.app.xianzhuan.utils.ToastUtils


class WebViewActivity : BaseActivity() {

    private var LOG_TAG = "WebView______>"

    private lateinit var context: Context

    private lateinit var link: String
    private lateinit var wv: BridgeWebView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = this
        wv = findViewById(R.id.webView)
        link = intent.getStringExtra(LINK_KEY)!!
        WebViewSettings.setDefaultWebSettings(wv)


        //客服与反馈才进行标题变化
        if (link == link11) {
            //设置标题
            wv.webChromeClient = object : WebChromeClient() {
                override fun onReceivedTitle(view: WebView?, title: String?) {
                    super.onReceivedTitle(view, title)
                    if (title != null) {
                        setTitleText(title)
                    }
                }
            }
        }
        LogUtils.i(LOG_TAG, "加载Link:${link}")
        if (!intent.getBooleanExtra(isProcessing, true)) {

            //显示简单的用户协议页面
            wv.setInitialScale(200)
            wv.loadUrl(link)

            return
        }
        wv.loadUrl(link)

        //注入打开相册方法
        wv.registerHandler("chooseImage") { _, _ ->
            openPhoto()
        }

        //注入回到主页方法
        wv.registerHandler("backToHome") { fragmentPosition, _ ->
            backToHome(fragmentPosition)
        }

        //注入进入任务详情页面方法
        wv.registerHandler("gotoTaskDetails") { taskId, _ ->
            gotoTaskDetails(taskId)
        }

        //注入退出登录的方法
        wv.registerHandler("logOut") { _, _ ->
            logOut()
        }


        //处理返回键
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                backOrFinish()
            }
        })

        //返回监听
        super.mBack!!.setOnClickListener { backOrFinish() }



        if (link == WebViewSettings.link8) {//充值页面才添加拦截

            wv.webViewClient = object : WebViewClient() {
                @Deprecated("Deprecated in Java", ReplaceWith("false"))
                override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {


                    LogUtils.i(LOG_TAG, "url:${url}")


                    // ------  对alipays:相关的scheme处理 -------
                    if (url.startsWith("alipays:") || url.startsWith("alipay")) {

                        hideView()


                        try {
                            startActivity(Intent("android.intent.action.VIEW", Uri.parse(url)))
                        } catch (e: Exception) {
                            AlertDialog.Builder(context)
                                .setMessage("未检测到支付宝客户端，请安装后重试。").setPositiveButton(
                                    "立即安装"
                                ) { _, _ ->
                                    val alipayUrl = Uri.parse("https://d.alipay.com")
                                    startActivity(
                                        Intent(
                                            "android.intent.action.VIEW", alipayUrl
                                        )
                                    )
                                }.setNegativeButton("取消", null).show()
                        }
                        return true
                    }

                    // ------- 处理结束 -------
                    if (!(url.startsWith("http") || url.startsWith("https"))) {
                        return true
                    }
                    showView()
                    view.loadUrl(url)
                    return true
                }//拦截支付宝
            }
        }


    }

    fun backOrFinish() {
        if (wv.canGoBack()) {
            if (wv.url.equals(link)) finish()
            else wv.goBack()
        } else finish()
    }

    private fun openPhoto() {

        MyPermissionUtil.openAlbumApply(this, object : OnPermissionCallback {
            override fun onGranted(permissions: MutableList<String>, all: Boolean) {
                //获取权限成功
                //打开相册
                pickImageContract.launch(Unit)
            }
            override fun onDenied(permissions: MutableList<String>, never: Boolean) {
                //权限失败
                showToast("您已拒绝授权，相册打开失败！")
            }
        })
    }


    private val pickImageContract = registerForActivityResult(PickImageContract()) {
        if (it != null) {
            // 获取内容URI对应的文件路径
            val thread = Thread {
                val imageData = Base64Utils.zipPic(it)
                if (imageData != null && imageData.length > 1000 * 2000) showToast("图片不能超过2MB,请重新选择！")
                else wv.post {
                    Log.i("H5调原生:", "图片字节码长度:${imageData?.length}")
                    wv.callHandler("showSelectedImage", imageData) {
                        //可以在这里弹出提示
                    }
                }
            }
            thread.start()
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

}