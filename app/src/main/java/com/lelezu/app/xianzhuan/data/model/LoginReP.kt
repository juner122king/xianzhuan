package com.lelezu.app.xianzhuan.data.model

/**
 * @author:Administrator
 * @date:2023/7/19 0019
 * @description://用户登录or注册返回体,保存用户token
 *
 */

/**
 *
 * @property accessToken String 	accessToken登录凭证
 * @property expireAt Long   	凭证失效时间 毫秒数
 * @property userId String    用户ID
 * @property isNewer Boolean 新户标识，true-新用户，false-老用户
 * @constructor
 */
data class LoginReP(
    var accessToken: String, var expireAt: Long, var userId: String, var isNewer: Boolean
)