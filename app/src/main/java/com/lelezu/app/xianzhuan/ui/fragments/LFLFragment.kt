package com.lelezu.app.xianzhuan.ui.fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.net.http.SslError
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.SslErrorHandler
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.github.lzyzsd.jsbridge.BridgeWebView
import com.github.lzyzsd.jsbridge.BridgeWebViewClient
import com.hjq.toast.ToastUtils
import com.lelezu.app.xianzhuan.MyApplication
import com.lelezu.app.xianzhuan.R
import com.lelezu.app.xianzhuan.ui.h5.MyJavaScriptInterface
import com.lelezu.app.xianzhuan.ui.h5.WebViewSettings
import com.lelezu.app.xianzhuan.ui.h5.WebViewSettings.link18
import com.lelezu.app.xianzhuan.ui.views.HomeActivity
import com.lelezu.app.xianzhuan.utils.LogUtils
import com.zj.zjsdk.ZjSdk

class LFLFragment : Fragment() {

    private lateinit var wv: BridgeWebView
    private var isWebViewloadError = false //记录webView是否已经加载出错
    private lateinit var err_view: View
    private lateinit var iv_but_re: View //刷新按钮

    private var isReload: Boolean = false //是否需要刷新

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notifica, container, false)
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        wv = view.findViewById(R.id.webView)
        err_view = view.findViewById(R.id.err_view)
        iv_but_re = view.findViewById(R.id.iv_but_re)

        WebViewSettings.setDefaultWebSettings(wv)


        registerJavaScriptHandlers()
        registerJSBridge()
        wv.webViewClient = object : BridgeWebViewClient(wv) {


            override fun onReceivedError(
                view: WebView?, errorCode: Int, description: String?, failingUrl: String?,
            ) {
                super.onReceivedError(view, errorCode, description, failingUrl)
                showERRView()
            }

            override fun onReceivedSslError(
                view: WebView?, handler: SslErrorHandler?, error: SslError?,
            ) {
                handler!!.proceed()
                showERRView()
            }

        }
        wv.webChromeClient = object : WebChromeClient() {


            //页面变化监听
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                if (newProgress == 100) {
                    onIsShowBack()
                    //加载100%
                    if (!isWebViewloadError && View.VISIBLE == err_view.visibility) {
                        err_view.visibility = View.GONE//隐藏失败页面
                        showWebView()

                    }
                }
            }


        }


    }


    //H5调用原生的方法
    private fun registerJavaScriptHandlers() {
//        //弹出未看完视频弹窗
//        wv.registerHandler("showTDialog") { _, _ -> showDialog(dialogStr) }

        wv.addJavascriptInterface(MyJavaScriptInterface(requireActivity()), "Android")//注入方法
    }


    //任务墙 封装 H5
    private fun registerJSBridge() {
        ZjSdk.registerJSBridge(requireActivity(), wv)
        wv.loadUrl(link18)//

    }


    fun showERRView() {
        LogUtils.i("webview", "网页加载失败！")
        ToastUtils.show("网页加载失败！请检查网络！")
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

    override fun onResume() {
        super.onResume()
        onIsShowBack()
        //返回监听
        (requireActivity() as HomeActivity).mBack!!.setOnClickListener { backOrFinish() }

        // 处理返回键事件
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // 在这里执行你的操作，比如弹出对话框或导航
                // 如果你想拦截返回键事件，不执行默认操作，可以不调用super.handleOnBackPressed()
                // 如果想执行默认的返回操作（比如退出当前 Fragment），调用super.handleOnBackPressed()
                backOrFinish()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    fun backOrFinish() {
        if (wv.canGoBack()) {
            if (wv.url.equals(link18)) requireActivity().finish()
            else {
                wv.goBack()
            }

        } else requireActivity().finish()
    }


    fun onIsShowBack() {

        if (isAdded) {
            // 执行与 Fragment 相关的操作
            if (wv.url.equals(link18)) { //当前加载的url为非link18主页时，显示原生返回键，否则隐藏
                (requireActivity() as HomeActivity).hideBack()
                LogUtils.i("WebView", "onIsShowBack()_hideBack()")

                if (isReload) {
                    isReload = false
                }

            } else {
                (requireActivity() as HomeActivity).showBack()
                LogUtils.i("WebView", "onIsShowBack()_showBack()")
                isReload = true

            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        ZjSdk.unregisterJSBridge(wv)
    }


}