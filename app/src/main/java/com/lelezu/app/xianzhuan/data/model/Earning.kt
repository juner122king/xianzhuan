package com.lelezu.app.xianzhuan.data.model


/**
 *
 * @property beEarned Float 待赚金额
 * @property subCount Int 徒弟总数
 * @property todayEarned Float?	今天已赚
 * @property totalEarned Float? 累计已赚
 * @property yesterdayEarned Float? 昨天已赚
 * @constructor 收徒收益
 */
data class Earning(
    var beEarned: Float = 0.0f,
    var subCount: Int = 0,
    var todayEarned: Float? = 0.0f,
    var totalEarned: Float? = 0.0f,
    var yesterdayEarned: Float? = 0.0f,
)