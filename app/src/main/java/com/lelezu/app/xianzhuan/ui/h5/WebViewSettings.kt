package com.lelezu.app.xianzhuan.ui.h5

import android.annotation.SuppressLint
import android.os.Build
import android.webkit.CookieManager
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import com.lelezu.app.xianzhuan.utils.ShareUtil
import com.lelezu.app.xianzhuan.utils.ShareUtil.APP_SHARED_PREFERENCES_LOGIN_TOKEN
import com.sdk.Unicorn.base.framework.utils.app.AppUtils


/**
 * @author:Administrator
 * @date:2023/8/2 0002
 * @description:
 *
 */
object WebViewSettings {

    //    var host = "http://192.168.3.7:8000/#/"
    var host = "http://192.168.3.35:8000/#/"

    var LINK_KEY = "URL_LINK"
    var URL_TITLE = "URL_TITLE"


    var link1 = "pages/index/dailyRewards/dailyRewards"
    var link2 = "pages/index/newcomerRewards/newcomerRewards"
    var link3 = "pages/apprentice/index"

    var link4 = "pages/apprentice/apprenticeIncome/index" //我的-收徒收益

    var link5 = "pages/publishTask/selectTaskClassification/index" //我的-发布任务

    var link6 = "pages/member/index" //我的-开通会员

    var link7 = "pages/reportForms/index" //我的-流水报表
    var link8 = "pages/balance/index?pageType=recharge" //充值
    var link9 = "" //提现
    var link10 = "pages/feedback/opinion/index?pageType=report" //举报维权

    var link11 = "pages/feedback/index" //客服与反馈

    var link12 = "pages/personalData/index" //设置个人资料

    var link13 = "pages/member/index" //开通会员


    @SuppressLint("SetJavaScriptEnabled")
    fun setDefaultWebSettings(webView: WebView) {
        val webSettings = webView.settings
        //5.0以上开启混合模式加载
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }
        webSettings.loadWithOverviewMode = true
        webSettings.useWideViewPort = true
        //允许js代码
        webSettings.javaScriptEnabled = true
        //允许SessionStorage/LocalStorage存储
        webSettings.domStorageEnabled = true
        //禁用放缩
        webSettings.displayZoomControls = false
        webSettings.builtInZoomControls = false
        //禁用文字缩放
        webSettings.textZoom = 100
//        //10M缓存，api 18后，系统自动管理。
//        webSettings.setAppCacheMaxSize(10 * 1024 * 1024)
//        //允许缓存，设置缓存位置
//        webSettings.setAppCacheEnabled(true)
//        webSettings.setAppCachePath(context.getDir("appcache", 0).getPath())

        webSettings.cacheMode

        //允许WebView使用File协议
        webSettings.allowFileAccess = true
        //不保存密码
        webSettings.savePassword = false
        //设置UA
//        webSettings.setUserAgentString(webSettings.userAgentString + " kaolaApp/" + AppUtils.getVersionName())
        //移除部分系统JavaScript接口
//        KaolaWebViewSecurity.removeJavascriptInterfaces(webView)
        //自动加载图片
        webSettings.loadsImagesAutomatically = true
        webView.settings.blockNetworkImage = false


        val cookieManager: CookieManager = CookieManager.getInstance()
        cookieManager.setAcceptCookie(true)     // 允许接受 Cookie
        // 跨域cookie读取
        cookieManager.setAcceptThirdPartyCookies(webView, true)
        cookieManager.setCookie(
            host, "${ShareUtil.getString(APP_SHARED_PREFERENCES_LOGIN_TOKEN)}"
        )
        cookieManager.flush()

    }


}