package com.lelezu.app.xianzhuan.ui.h5

import android.annotation.SuppressLint
import android.webkit.CookieManager
import android.webkit.WebSettings
import android.webkit.WebView
import com.lelezu.app.xianzhuan.MyApplication
import com.lelezu.app.xianzhuan.data.ApiConstants.HOST
import com.lelezu.app.xianzhuan.utils.ShareUtil
import com.lelezu.app.xianzhuan.utils.ShareUtil.APP_SHARED_PREFERENCES_DEVICE_ID
import com.lelezu.app.xianzhuan.utils.ShareUtil.APP_SHARED_PREFERENCES_LOGIN_TOKEN


/**
 * @author:Administrator
 * @date:2023/8/2 0002
 * @description:
 *
 */
object WebViewSettings {
    var TAG = "TAG"

    var LINK_KEY = "URL_LINK"
    var URL_TITLE = "URL_TITLE"

    var isProcessing = "isProcessing"
    var isDataUrl = "isDataUrl"//是否富文本
    var ANNOUNCEID = "announceId"//公告id
     var host = "$HOST/statics/dxz/h5/pages"

    var link100 = "$host/agreement/user"//《用户使用协议》
    var link101 = "$host/agreement/privacy"//《隐私政策》
    var link102 = "$host/apprentice/apprenticeRule/index"//合伙人规则页面

    var link103 = "$host/announcement/index"//公告页面

    var link14 = "$host/agreement/order" //接单规则

    var link1 = "$host/index/dailyRewards/dailyRewards"
    var link2 = "$host/index/newcomerRewards/newcomerRewards"
    var link3 = "$host/apprentice/index"//收徒赚钱
    var link31 = "$host/apprentice/apprenticePoster/index"//收徒赚钱子页面
    var link4 = "$host/apprentice/apprenticeIncome/index" //我的-收徒收益
    var link5 = "$host/publishTask/selectTaskClassification/index" //我的-发布任务
    var link51 = "$host/publishTask/selectTaskClassification/index" //我的-发布任务-编辑任务
    var link6 = "$host/shop/list/index" //我的店铺
    var link7 = "$host/reportForms/index" //我的-流水报表
    var link8 = "$host/balance/index?pageType=recharge" //充值

    var link9 = "$host/balance/index?pageType=withdrawal" //提现
    var link10 = "$host/feedback/opinion/index?pageType=report" //举报维权
    var link11 = "$host/feedback/index" //客服与反馈
    var link12 = "$host/personalData/index" //设置个人资料
    var link13 = "$host/member/index" //开通会员

    var link16 = "$host/followGzhOrWechat/index" //新人奖励页面
    var link17 = "$host/shareToProfit/index" //接单规则2
    var link18 = "$host/receiveWelfare/index" //领福利


    var link15 = "https://beian.miit.gov.cn/" //公安备案网站


    @SuppressLint("SetJavaScriptEnabled")
    fun setDefaultWebSettings(webView: WebView) {
        val webSettings = webView.settings
        //5.0以上开启混合模式加载

        webSettings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW

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

        webSettings.cacheMode

        //允许WebView使用File协议
        webSettings.allowFileAccess = true


        webSettings.loadsImagesAutomatically = true
        webView.settings.blockNetworkImage = false


        val cookieManager: CookieManager = CookieManager.getInstance()
        cookieManager.setAcceptCookie(true)     // 允许接受 Cookie
        // 跨域cookie读取
        cookieManager.setAcceptThirdPartyCookies(webView, true)

        // 设置多个 cookie
        val host1 = host
        val cookie1 = "Token=" + ShareUtil.getString(APP_SHARED_PREFERENCES_LOGIN_TOKEN)
        val cookie2 = "Device=" + ShareUtil.getString(APP_SHARED_PREFERENCES_DEVICE_ID)
        val cookie3 = "isMarket=" + MyApplication.isMarketVersion
        cookieManager.setCookie(host1, cookie1)
        cookieManager.setCookie(host1, cookie2)
        cookieManager.setCookie(host1, cookie3)




        cookieManager.flush()


    }


}