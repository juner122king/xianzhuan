package com.lelezu.app.xianzhuan.data.model


/**
 *
 * @property concernCnt String 关注数
 * @property fanCnt String 粉丝数
 * @property isConcerned Boolean 	是否关注
 * @constructor
 */
data class Follows(
    val concernCnt: String,
    val fanCnt: String,
    val isConcerned: Boolean,

)
