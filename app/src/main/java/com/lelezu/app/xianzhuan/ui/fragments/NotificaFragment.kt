package com.lelezu.app.xianzhuan.ui.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.github.lzyzsd.jsbridge.BridgeWebView
import com.lelezu.app.xianzhuan.R
import com.lelezu.app.xianzhuan.ui.h5.MyJavaScriptInterface
import com.lelezu.app.xianzhuan.ui.h5.WebViewSettings
import com.lelezu.app.xianzhuan.ui.h5.WebViewSettings.link3
import com.lelezu.app.xianzhuan.ui.views.HomeActivity
import com.lelezu.app.xianzhuan.utils.LogUtils


class NotificaFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notifica, container, false)
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val wv: BridgeWebView = view.findViewById(R.id.webView)

        WebViewSettings.setDefaultWebSettings(wv)
        wv.addJavascriptInterface(MyJavaScriptInterface(requireActivity()), "Android")//注入方法
        wv.loadUrl(WebViewSettings.host + link3)//最后才load


    }

    companion object {
        @JvmStatic
        fun newInstance() = NotificaFragment()
    }


}