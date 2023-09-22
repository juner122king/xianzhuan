package com.lelezu.app.xianzhuan.data.model

/**
 * 联系消息实体类。
 *
 * @property contactContent 联系消息内容。
 * @property contactRecordId 联系记录ID。
 * @property createdDt 创建时间戳。
 * @property receiverUserAvatar 接收者头像URL。
 * @property receiverUserId 接收者用户ID。
 * @property receiverUsername 接收者用户名。
 * @property senderUserAvatar 发送者头像URL。
 * @property senderUserId 发送者用户ID。
 * @property vipDesc 接收者会员描述。
 * @property vipLevel 接收者会员等级。
 * @property type 发送的消息类型。 0-文本 ，1-图片，2-任务
 */
data class ChatMessage(
    val contactContent: String,
    val contactRecordId: String,
    val createdDt: String,
    val receiverUserAvatar: String,
    val receiverUserId: String,
    val receiverUsername: String,
    val senderUserAvatar: String,
    val senderUserId: String,
    val vipDesc: String,
    val vipLevel: Int,
    val type: Int,
    var isMe: Boolean
)
