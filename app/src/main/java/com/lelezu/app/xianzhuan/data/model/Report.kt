package com.lelezu.app.xianzhuan.data.model


/**
 *
 * @property reportContent String 举报维权内容
 * @property contactLabel String contactLabel
 * @property reportedUserId String 被举报者ID
 * @property reportedTaskId String 被举报的任务ID
 * @constructor
 */
data class Report(
    var reportContent: String,
    var contactLabel: String,
    var reportedUserId: String,
    var reportedTaskId: String,
    var reportImages: List<String>,
)
