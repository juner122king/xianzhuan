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
 *
 *
 */
data class Message(

    val msgTitle: String?,
    val msgContent: String?,
    val isRead: Boolean?,
    val msgId: String?,
    val createdDt: String?

)