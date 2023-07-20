package com.lelezu.app.xianzhuan.data.model


//用户登录信息类，用于登录
data class LoginInfo(
    var appId: String,
    var deviceToken: String,
    var loginMethod: String,
    var recommendUserId: String?,
    var wxCode: String
)