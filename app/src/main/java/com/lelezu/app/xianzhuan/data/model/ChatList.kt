package com.lelezu.app.xianzhuan.data.model

/**
 * 联系消息实体类。
 *
 * @property userId
 * @property avatar
 * @property nickname
 * @property unreadCount
 */
data class ChatList(
    val userId: String,
    val avatar: String,
    val nickname: String,
    val unreadCount: Int,

)
