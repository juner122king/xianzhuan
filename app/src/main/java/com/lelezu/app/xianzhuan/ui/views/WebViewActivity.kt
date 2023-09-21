package com.lelezu.app.xianzhuan.ui.views

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import com.github.lzyzsd.jsbridge.BridgeWebView
import com.github.lzyzsd.jsbridge.CallBackFunction
import com.google.gson.Gson
import com.hjq.permissions.OnPermissionCallback
import com.lelezu.app.xianzhuan.R
import com.lelezu.app.xianzhuan.ui.h5.WebViewSettings
import com.lelezu.app.xianzhuan.ui.h5.WebViewSettings.LINK_KEY
import com.lelezu.app.xianzhuan.ui.h5.WebViewSettings.URL_TITLE
import com.lelezu.app.xianzhuan.ui.h5.WebViewSettings.isProcessing
import com.lelezu.app.xianzhuan.ui.h5.WebViewSettings.link11
import com.lelezu.app.xianzhuan.ui.h5.WebViewSettings.link13
import com.lelezu.app.xianzhuan.ui.h5.WebViewSettings.link5
import com.lelezu.app.xianzhuan.ui.h5.WebViewSettings.link8
import com.lelezu.app.xianzhuan.utils.Base64Utils
import com.lelezu.app.xianzhuan.utils.LogUtils
import com.lelezu.app.xianzhuan.utils.MyPermissionUtil


class WebViewActivity : BaseActivity() {
    private lateinit var link: String
    private lateinit var wv: BridgeWebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        wv = findViewById(R.id.webView)
        link = intent.getStringExtra(LINK_KEY)!!
        WebViewSettings.setDefaultWebSettings(wv)

        if (link == link11 || link == link5) {  //link5：发布任务
            setWebViewTitle()
        }

        if (!intent.getBooleanExtra(isProcessing, true)) {
            loadSimplePage()
            return
        }

        setupWebView()
        setupBackButtonListener()
    }

    override fun onResume() {
        super.onResume()
        showView()
    }

    private fun setWebViewTitle() {
        wv.webChromeClient = object : WebChromeClient() {
            override fun onReceivedTitle(view: WebView, title: String) {
//                super.onReceivedTitle(view, title)
                LogUtils.i("WebView", view.url!!)

                if (!view.url!!.contains(title)) {
                    setTitleText(title)
                }
//                LogUtils.i("WebView", title)
            }


        }
    }

    private fun loadSimplePage() {
        wv.setInitialScale(200)
        wv.loadUrl(link)
    }

    private fun setupWebView() {
        wv.loadUrl(link)
        registerJavaScriptHandlers()
        setupWebViewClient()
    }

    private fun registerJavaScriptHandlers() {
        wv.registerHandler("chooseImage") { _, _ -> openPhoto() }
        wv.registerHandler("backToHome") { fragmentPosition, _ -> backToHome(fragmentPosition) }
        wv.registerHandler("gotoTaskDetails") { taskId, _ -> gotoTaskDetails(taskId) }
        wv.registerHandler("logOut") { _, _ -> logOut() }
        wv.registerHandler("gotoPermissionSettings") { _, _ -> gotoPermissionSettings() }

        wv.registerHandler("wxPay") { rechargeAmount, _ -> onWXPay(rechargeAmount) }//打开原生微信支付

        wv.registerHandler("toSysMessage") { _, _ -> toSysMessage() }//注销申请

        //发布任务页面需要保存草稿，返回上页面前需要跑一下H5的保存草稿方法，然后不论是否都跑原生方法 backOrFinish()
        wv.registerHandler("goBack") { _, _ -> backOrFinish() }

    }


    private fun setupWebViewClient() {
        if (link == link8 || link == link13) {
            wv.webViewClient = object : WebViewClient() {
                @Deprecated("Deprecated in Java", ReplaceWith("false"))
                override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                    LogUtils.i("webview", url)
                    handleAlipayScheme(url)
                    if (!(url.startsWith("http") || url.startsWith("https"))) {
                        return true
                    }
                    showView()
                    view.loadUrl(url)
                    return true
                }
            }
        }

    }

    private fun handleAlipayScheme(url: String) {
        if (url.startsWith("alipays:") || url.startsWith("alipay")) {
            hideView()
            try {
                startActivity(Intent("android.intent.action.VIEW", Uri.parse(url)))
            } catch (e: Exception) {
                showAlipayInstallDialog()
            }
        }
    }

    private fun showAlipayInstallDialog() {
        AlertDialog.Builder(this).setMessage("未检测到支付宝客户端，请安装后重试。")
            .setPositiveButton("立即安装") { _, _ -> startAlipayInstallation() }
            .setNegativeButton("取消", null).show()
    }

    private fun startAlipayInstallation() {
        val alipayUrl = Uri.parse("https://d.alipay.com")
        startActivity(Intent("android.intent.action.VIEW", alipayUrl))
    }

    private fun setupBackButtonListener() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                backOrFinish()
            }
        })
        super.mBack!!.setOnClickListener { backOrFinish() }
    }

    private fun backOrFinish() {
        if (wv.canGoBack()) {
            if (wv.url == link) finish()
            else {//次级页面
                //如果是发布任务的次级页面，则需要保存草稿，返回上页面前需要跑一下H5的保存草稿方法 publishTask/index为发布编辑页面url
                if (wv.url!!.contains("publishTask/index")) {
                    showToast("返回拦截成功，已调用H5弹出窗口方法:showDraftModal")
                    wv.callHandler("showDraftModal", "Android", null)
                } else {
                    wv.goBack()
                }
            }

        } else finish()
    }

    private fun openPhoto() {
        MyPermissionUtil.openAlbumApply(this, object : OnPermissionCallback {
            override fun onGranted(permissions: MutableList<String>, all: Boolean) {
                pickImageContract.launch(Unit)
            }

            override fun onDenied(permissions: MutableList<String>, never: Boolean) {
                showToast("您已拒绝授权，相册打开失败！")
            }
        })
    }

    private val pickImageContract = registerForActivityResult(PickImageContract()) { result ->
        if (result != null) {
            val thread = Thread {
                val imageData = Base64Utils.zipPic2(result, 90)//
                if (imageData == null) {
                    // 如果 imageData 为 null，执行处理空值的操作
                    // 例如，显示一个提示消息或采取其他适当的操作
                    showToast("图片不支持，请重新选择！")
                } else {
                    wv.post {
                        LogUtils.i("图片字节码长度:${imageData.length}")
                        wv.callHandler("showSelectedImage", imageData) {}
                    }
                }
            }
            thread.start()
        }
    }

    private fun gotoPermissionSettings() {
        val intent = Intent(this, PermissionsActivity::class.java)
        startActivity(intent)
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