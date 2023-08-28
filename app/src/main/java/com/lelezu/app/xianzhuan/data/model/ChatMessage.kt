package com.lelezu.app.xianzhuan.data.model

/**
 * 联系消息实体类。
 *
 * @property contactContent 联系消息内容。
 * @property contactRecordId 联系记录ID。
 * @property createdDt 创建时间戳。
 * @property isImage 是否包含图片。
 * @property receiverUserAvatar 接收者头像URL。
 * @property receiverUserId 接收者用户ID。
 * @property receiverUsername 接收者用户名。
 * @property senderUserAvatar 发送者头像URL。
 * @property senderUserId 发送者用户ID。
 * @property vipDesc 接收者会员描述。
 * @property vipLevel 接收者会员等级。
 */
data class ChatMessage(
    val contactContent: String,
    val contactRecordId: String,
    val createdDt: String,
    val isImage: Boolean,
    val receiverUserAvatar: String,
    val receiverUserId: String,
    val receiverUsername: String,
    val senderUserAvatar: String,
    val senderUserId: String,
    val vipDesc: String,
    val vipLevel: Int,
    var isMe: Boolean
)
