package com.lelezu.app.xianzhuan.data.model

/**
 * @author:Administrator
 * @date:2023/7/26 0026
 * @description:
 *
 */

/**
 * @property wxAccount String 微信账号
 * @property vipLevel Int 会员等级
 * @property userId String 用户ID
 * @property rechargeAmount Float 充值金额
 * @property nickname String 用户昵称
 * @property mobilePhone String 手机号
 * @property lastLoginDt String 最后一次登录时间
 * @property headImageUrl String 	头像
 * @property createdDt String 注册时间
 * @property balanceAmount Float 	可用金额
 * @property recommendUserId String  师傅ID
 * @property level String  用户等级
 * @property taskAward Int 累计任
 * @property hasRewardNewerAward Boolean 是否领取了新人奖励
 * @constructor
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
    val level: String,
    val taskAward: Int,
    val recommendUserId: String?,
    val balanceAmount: Float,
    val hasRewardNewerAward: Boolean


)
