package com.lelezu.app.xianzhuan.data.model

/**
 *
 * @property announceContent String公告内容
 * @property announceId String 公告ID
 * @property announceTitle String 公告标题
 * @property createdDt String 创建时间
 * @property filename String 公告文件名
 * @property isEnabled Boolean 	状态
 * @property sortSeq Int? 	排序
 * @constructor
 */
data class Announce(

    val announceContent: String,
    val announceId: String,
    val announceTitle: String,
    val createdDt: String,
    val filename: String,
    val isEnabled: Boolean,
    val sortSeq: Int

)