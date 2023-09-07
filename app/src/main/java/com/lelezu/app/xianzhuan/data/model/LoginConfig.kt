package com.lelezu.app.xianzhuan.data.model

/**
 * @author:Administrator
 * @date:2023/9/11 0011
 * @description:
 *
 */
data class LoginConfig(

    var confType: String,
    var confKey: String,
    var confName: String,
    var confDesc: String,
    var confValue: ConfValue

) {
    data class ConfValue(
        var registerEnabled: Boolean, var page: Int//page  登录页面, 0: 微信登录, 1: 手机

    )

}