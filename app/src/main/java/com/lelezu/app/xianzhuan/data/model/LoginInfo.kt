package com.lelezu.app.xianzhuan.data.model


//用户登录信息类，用于登录
/**
 *
 * @property appId String  	微信授权应用ID
 * @property loginMethod String  登录方式，MOBILE：手机号，WX：微信授权,可用值:WX:微信授权,MOBILE:手机号登录
 * @property mobileToken String? 前端SDK获取的accessToken，手机号登录时必传
 * @property mobileAccessToken String? 	前端SDK获取的token，手机号登录时必传
 * @property wxCode String? 	微信授权code，微信一键登录时必传
 * @constructor
 */
data class LoginInfo(
    var appId: String,
    var loginMethod: String,
    var mobileToken: String?,
    var mobileAccessToken: String?,
    var wxCode: String?
)