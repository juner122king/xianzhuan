package com.lelezu.app.xianzhuan.data.model

/**
 * @author:Administrator
 * @date:2023/7/25 0020
 * @description: 消息类
 *      createdDt	消息创建时间	string
        isRead	是否已读	boolean
        msgContent	内容	string
        msgId	消息id	string
        msgTitle	标题	string
        type	0-正常类型，1-注销类型，2-需确认类型	Int
 *
 *
 */
data class Message(

    val msgTitle: String?,
    val msgContent: String?,
    val isRead: Boolean,
    val msgId: String,
    val createdDt: String?,
    val type: Int

)