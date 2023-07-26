package com.lelezu.app.xianzhuan.data.model

/**
 * @author:Administrator
 * @date:2023/7/26 0026
 * @description:
 *
 */
data class UserInfo(

    val wxAccount: String,
    val vipLevel: Int,
    val userId: String,
    val rechargeAmount: Float,
    val nickname: String,
    val mobilePhone: String,
    val lastLoginDt: String,
    val headImageUrl: String,
    val createdDt: String,
    val balanceAmount: Float


)
