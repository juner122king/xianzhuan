package com.lelezu.app.xianzhuan.ui.fragments
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.fragment.app.Fragment
import com.lelezu.app.xianzhuan.R
import com.lelezu.app.xianzhuan.ui.h5.MyJavaScriptInterface
import com.lelezu.app.xianzhuan.ui.h5.WebViewSettings
import com.lelezu.app.xianzhuan.ui.h5.WebViewSettings.link3


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
        val webView: WebView = view.findViewById(R.id.webView)

        WebViewSettings.setDefaultWebSettings(webView)
        webView.addJavascriptInterface(MyJavaScriptInterface(requireContext()), "Android")//注入方法
        webView.loadUrl(WebViewSettings.host + link3)//最后才load

    }

    companion object {

        @JvmStatic
        fun newInstance() = NotificaFragment()
    }


}