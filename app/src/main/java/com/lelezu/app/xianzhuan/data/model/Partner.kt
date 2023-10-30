package com.lelezu.app.xianzhuan.data.model

import android.os.Parcel
import android.os.Parcelable

/**
 * @author:Administrator
 * @date:2023/9/20 0020
 * @description: 合伙人后台数据
 *
 */

/**
 *
 * @property createdDate String 时间
 * @property nickName String 用户昵称
 * @property partnerId String 	合伙人ID
 * @property performance String 	团队业绩
 * @property settlementStatus Int 结算状态, 0-待结算, 1-已结算, 2-封号没收
 * @property taskCount String 完成数量
 * @property teamCount String 	团队人数
 * @property teamLevel String 团队奖励等级
 * @property teamNewCount String 	团队新增人数
 * @property earning String 	结算金额
 * @property performanceId String 	团队ID
 * @property avatar String 	头像
 * @constructor
 */


data class Partner(
    val createdDate: String?,
    val nickName: String?,
    val partnerId: String?,
    val performance: String? = "-",
    val settlementStatus: Int,
    val taskCount: String?,
    val teamCount: String? = "0",
    val teamLevel: String? = "-",
    val teamNewCount: String? = "0",
    val earning: String?,
    val performanceId: String?,
    val avatar: String?,
    val rate: String?,
    val reward: String?,
    val completeTaskCnt: String?,
)