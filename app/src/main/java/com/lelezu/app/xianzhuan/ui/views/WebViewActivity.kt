package com.lelezu.app.xianzhuan.ui.views

import android.content.Intent
import android.net.Uri
import android.net.http.SslError
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.webkit.CookieManager
import android.webkit.SslErrorHandler
import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import com.github.lzyzsd.jsbridge.BridgeWebView
import com.github.lzyzsd.jsbridge.BridgeWebViewClient
import com.hjq.permissions.OnPermissionCallback
import com.lelezu.app.xianzhuan.R
import com.lelezu.app.xianzhuan.ui.h5.MyJavaScriptInterface
import com.lelezu.app.xianzhuan.ui.h5.WebViewSettings
import com.lelezu.app.xianzhuan.ui.h5.WebViewSettings.LINK_KEY
import com.lelezu.app.xianzhuan.ui.h5.WebViewSettings.URL_TITLE
import com.lelezu.app.xianzhuan.ui.h5.WebViewSettings.isDataUrl
import com.lelezu.app.xianzhuan.ui.h5.WebViewSettings.isProcessing
import com.lelezu.app.xianzhuan.ui.h5.WebViewSettings.link103
import com.lelezu.app.xianzhuan.ui.h5.WebViewSettings.link11
import com.lelezu.app.xianzhuan.ui.h5.WebViewSettings.link13
import com.lelezu.app.xianzhuan.ui.h5.WebViewSettings.link16
import com.lelezu.app.xianzhuan.ui.h5.WebViewSettings.link17
import com.lelezu.app.xianzhuan.ui.h5.WebViewSettings.link5
import com.lelezu.app.xianzhuan.ui.h5.WebViewSettings.link6
import com.lelezu.app.xianzhuan.ui.h5.WebViewSettings.link8
import com.lelezu.app.xianzhuan.utils.Base64Utils
import com.lelezu.app.xianzhuan.utils.LogUtils
import com.lelezu.app.xianzhuan.utils.MyPermissionUtil
import com.lelezu.app.xianzhuan.utils.ShareUtil


class WebViewActivity : BaseActivity() {
    private lateinit var link: String
    private lateinit var wv: BridgeWebView
    private lateinit var err_view: View
    private lateinit var iv_but_re: View //刷新按钮

    private var isWebViewloadError = false //记录webView是否已经加载出错


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        wv = findViewById(R.id.webView)
        err_view = findViewById(R.id.err_view)
        iv_but_re = findViewById(R.id.iv_but_re)

        link = intent.getStringExtra(LINK_KEY)!!
        WebViewSettings.setDefaultWebSettings(wv)


        if (link == link11 || link == link5 || link == link6 || link == link103) {  //link5：发布任务
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
                LogUtils.i("WebView", view.url!!)
                if (!view.url!!.contains(title)) {
                    setTitleText(title)
                }
            }

            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                if (newProgress == 100) {
                    LogUtils.i("WebView", "加载100%")
                    //加载100%
                    if (!isWebViewloadError && View.VISIBLE == err_view.visibility) {
                        err_view.visibility = View.GONE//隐藏失败页面
                        showWebView()
                    }
                }

            }

        }
    }

    private fun loadSimplePage() {
        if (intent.getBooleanExtra(isDataUrl, false)) {
            //加载富文本
            wv.setInitialScale(200)
//            wv.loadDataWithBaseURL(null, link, "text/html", "utf-8", null)
//            wv.loadUrl(link)


        } else {
            wv.setInitialScale(200)
            wv.loadUrl(link)
        }
    }

    private fun setupWebView() {
        wv.loadUrl(link)

        setupWebViewClient()
        registerJavaScriptHandlers()

        if (link == link17) postTaskId()//分享赚页面post任务ID给H5
        if (link == link103) postAnnounceId()//公告页面post公告ID给H5
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


        //打开公告
        wv.registerHandler("goAnnouncement") { _, _ -> backOrFinish() }


        wv.addJavascriptInterface(MyJavaScriptInterface(this), "Android")//注入方法


    }


    private fun setupWebViewClient() {

        wv.webViewClient = object : BridgeWebViewClient(wv) {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                super.shouldOverrideUrlLoading(view, url)
                LogUtils.i("webview", url)
                //支付页面需要处理支付宝调转判断
                if (link == link8 || link == link13) {
                    handleAlipayScheme(url)
                    if (!(url.startsWith("http") || url.startsWith("https"))) {
                        return true
                    }
                    showView()
                    view.loadUrl(url)
                }
                return false
            }

            override fun onReceivedError(
                view: WebView?, errorCode: Int, description: String?, failingUrl: String?
            ) {
                super.onReceivedError(view, errorCode, description, failingUrl)
                showERRView()
            }

            override fun onReceivedSslError(
                view: WebView?, handler: SslErrorHandler?, error: SslError?
            ) {

                handler!!.proceed()

                showERRView()
            }

        }

    }

    fun showERRView() {
        LogUtils.i("webview", "网页加载失败！")
        showToast("网页加载失败！请检查网络！")
        isWebViewloadError = true
        err_view.visibility = View.VISIBLE
        wv.visibility = View.GONE

        iv_but_re.setOnClickListener {

            Handler().postDelayed(Runnable { wv.reload() }, 500)
        }
    }

    private fun showWebView() {
        err_view.visibility = View.GONE
        wv.visibility = View.VISIBLE

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
                if (wv.url!!.contains("publishTask/index")) {//发布任务页面拦截
//                    showToast("返回拦截成功，已调用H5弹出窗口方法:showDraftModal")
                    wv.callHandler("showDraftModal", "Android", null)
                } else if (wv.url!!.contains("balance/transactionStatus/index")) {//支付成功页面拦截
                    wv.callHandler("goBack", "Android", null)
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
                val imageData = Base64Utils.zipPic2(result, 100)//
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

    override fun onStop() {
        super.onStop()
        if (link == link16) {
            finish()
        }
    }


    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        LogUtils.i("webview", "onNewIntent")
        // 处理WX支付结果
        handleIntent(intent)

    }

    private fun handleIntent(intent: Intent?) {
        intent?.let {
            val extraData = it.getStringExtra("type")
            LogUtils.i("webview", "type:$extraData")
            if (!extraData.isNullOrEmpty()) {
                // 处理 extraData，执行相应的操作
                wv.callHandler("onWXPay", extraData, null)
            }
        }
    }

    private fun openApprentice() {
        val intent = Intent(this, WebViewActivity::class.java)
        intent.putExtra(WebViewSettings.LINK_KEY, WebViewSettings.link102)
        intent.putExtra(WebViewSettings.URL_TITLE, "规则说明")
        startActivity(intent)
    }

    //  link17  分享赚页面接收收徒id和任务id
    private fun postTaskId() {

        val taskId = intent.getStringExtra(WebViewSettings.TAG)
        val cookieManager: CookieManager = CookieManager.getInstance()
        cookieManager.setAcceptCookie(true)     // 允许接受 Cookie
        // 跨域cookie读取
        cookieManager.setAcceptThirdPartyCookies(wv, true)

        // 设置多个 cookie
        val host1 = WebViewSettings.host
        val cookie1 = "taskId=$taskId"
        val cookie2 = "userId=${ShareUtil.getString(ShareUtil.APP_SHARED_PREFERENCES_LOGIN_ID)}"
        cookieManager.setCookie(host1, cookie1)
        cookieManager.setCookie(host1, cookie2)

        cookieManager.flush()

    }

    //  link17  设置公告ID
    private fun postAnnounceId() {

        val announceId = intent.getStringExtra(WebViewSettings.ANNOUNCEID)
        val cookieManager: CookieManager = CookieManager.getInstance()
        cookieManager.setAcceptCookie(true)     // 允许接受 Cookie
        // 跨域cookie读取
        cookieManager.setAcceptThirdPartyCookies(wv, true)

        // 设置多个 cookie
        val host1 = WebViewSettings.host
        val cookie1 = "announceId=$announceId"
        cookieManager.setCookie(host1, cookie1)
        cookieManager.flush()

    }
}