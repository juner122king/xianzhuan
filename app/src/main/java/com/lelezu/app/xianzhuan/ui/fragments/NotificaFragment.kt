package com.lelezu.app.xianzhuan.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.github.lzyzsd.jsbridge.BridgeWebView
import com.lelezu.app.xianzhuan.R
import com.lelezu.app.xianzhuan.ui.h5.MyJavaScriptInterface
import com.lelezu.app.xianzhuan.ui.h5.WebViewSettings
import com.lelezu.app.xianzhuan.ui.h5.WebViewSettings.link3
import com.lelezu.app.xianzhuan.ui.views.HomeActivity

class NotificaFragment : Fragment() {

    private lateinit var wv: BridgeWebView
    private lateinit var link: String

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

        WebViewSettings.setDefaultWebSettings(wv)
        wv.addJavascriptInterface(MyJavaScriptInterface(requireActivity()), "Android")//注入方法

        link = WebViewSettings.host + link3
        wv.loadUrl(link)//最后才load

        wv.webViewClient = object : WebViewClient() {
            override fun onLoadResource(view: WebView?, url: String?) {
                super.onLoadResource(view, url)
                onIsShowBack()
            }
        }

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
            if (wv.url.equals(link)) requireActivity().finish()
            else wv.goBack()
        } else requireActivity().finish()
    }


    fun onIsShowBack() {
        if (wv.url.equals(WebViewSettings.host + link3)) (requireActivity() as HomeActivity).hideBack()
        else (requireActivity() as HomeActivity).showBack()

    }


}