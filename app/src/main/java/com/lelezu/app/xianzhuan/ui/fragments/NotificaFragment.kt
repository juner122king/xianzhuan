package com.lelezu.app.xianzhuan.ui.fragments

import android.annotation.SuppressLint
import android.net.http.SslError
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.SslErrorHandler
import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.github.lzyzsd.jsbridge.BridgeWebView
import com.github.lzyzsd.jsbridge.BridgeWebViewClient
import com.hjq.toast.ToastUtils
import com.lelezu.app.xianzhuan.R
import com.lelezu.app.xianzhuan.ui.h5.MyJavaScriptInterface
import com.lelezu.app.xianzhuan.ui.h5.WebViewSettings
import com.lelezu.app.xianzhuan.ui.h5.WebViewSettings.link3
import com.lelezu.app.xianzhuan.ui.h5.WebViewSettings.link31
import com.lelezu.app.xianzhuan.ui.views.HomeActivity
import com.lelezu.app.xianzhuan.utils.LogUtils
import java.lang.IllegalStateException

class NotificaFragment : Fragment() {

    private lateinit var wv: BridgeWebView
    private var isWebViewloadError = false //记录webView是否已经加载出错
    private lateinit var err_view: View
    private lateinit var iv_but_re: View //刷新按钮


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
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
        wv.addJavascriptInterface(MyJavaScriptInterface(requireActivity()), "Android")//注入方法

        wv.loadUrl(link31)//最后才load

        wv.webViewClient = object : BridgeWebViewClient(wv) {
            override fun onLoadResource(view: WebView?, url: String?) {
                super.onLoadResource(view, url)
//                onIsShowBack()
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
        wv.webChromeClient = object : WebChromeClient() {

            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                if (newProgress == 100) {
                    //加载100%
                    if (!isWebViewloadError && View.VISIBLE == err_view.visibility) {
                        err_view.visibility = View.GONE//隐藏失败页面
                        showWebView()
                    }
                }
            }

        }
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


}