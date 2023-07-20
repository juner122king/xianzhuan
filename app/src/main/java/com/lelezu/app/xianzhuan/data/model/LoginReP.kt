package com.lelezu.app.xianzhuan.data.model

/**
 * @author:Administrator
 * @date:2023/7/19 0019
 * @description://用户登录信息返回体类，保存用户token
 *
 */
data class LoginReP(
    var accessToken: String, var expireAt: Long, var userId: String
)