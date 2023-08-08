package com.lelezu.app.xianzhuan.data.model

/**
 * @author:Administrator
 * @date:2023/7/20 0020
 * @description: 任务提交类
 *
 */

/**
 *
 * @property applyLogId String? 任务报名申请ID
 * @property uploadVerifyBody List<TaskUploadVerify>    任务上传验证
 * @constructor
 */

data class TaskSubmit(
    val applyLogId: String?,
    val uploadVerifyBody: List<TaskUploadVerify>

)