package com.lelezu.app.xianzhuan.data

object ApiConstants {
    /**
    host:              test.ipandata.com
    basePath:          /dxz
     */
    const val HOST = "https://test.ipandata.com" //
    const val BASE_pATH = "/dxz" //


    const val LOGIN_METHOD_PHONE = "PHONE"    //登录方式，PHONE：手机号，WX：微信授权，WX_PHONE：微信和手机号组合,可用值:WX:微信授权
    const val LOGIN_METHOD_WX = "WX"    //登录方式，PHONE：手机号，WX：微信授权，WX_PHONE：微信和手机号组合,可用值:WX:微信授权
    const val LOGIN_METHOD_WX_PHONE =
        "WX_PHONE"    //登录方式，PHONE：手机号，WX：微信授权，WX_PHONE：微信和手机号组合,可用值:WX:微信授权
}