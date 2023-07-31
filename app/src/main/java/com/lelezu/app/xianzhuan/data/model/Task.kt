package com.lelezu.app.xianzhuan.data.model

/**
 * @author:Administrator
 * @date:2023/7/20 0020
 * @description: 任务类
 *
 */

/**
 *
 * @property applyLogId String? 任务报名申请ID
 * @property auditStatus Int 任务状态(0-未报名，1-待提交，2-审核中，3-审核通过，4-审核被否，5-已取消，默认：0-未报名)
 * @property createdDt String? 	发布时间
 * @property earnedCount Int 	多少人已赚
 * @property isTop Boolean 是否置顶
 * @property limitTimes Int     	限次(1-每日1次，2-每人1次，3-每人3次)
 * @property operateTime String?    剩余/提交/审核时间
 * @property quantity Int           数量
 * @property rejectReason String?   被否原因
 * @property remainApplyCount Int   剩余报名次数
 * @property rest Int               剩余数量
 * @property supportDevices List<String>    	支持设备
 * @property taskDesc String?       	任务说明
 * @property taskId String?     任务ID
 * @property taskStatus Int     任务状态(0-待提交，1-审核中，2-审核通过，3-审核被否，4-暂停中，5-已结束，默认：0-待提交)
 * @property taskStepList List<TaskStep>    	任务步骤
 * @property taskTitle String?  	任务标题
 * @property taskTypeIconImage String?  	任务类型图标
 * @property taskTypeId String? 任务类型ID
 * @property taskTypeTitle String?  任务类型标题
 * @property taskUploadVerifyList List<TaskUploadVerify>    任务上传验证
 * @property unitPrice Float  单价
 * @property updatedDt String?  	更新时间
 * @property userId String?  	发布者ID
 * @constructor
 */

data class Task(
    val applyLogId: String?,
    val auditStatus: Int,
    val createdDt: String?,
    val earnedCount: Int,
    val isTop: Boolean,
    val limitTimes: Int,
    val operateTime: String?,
    val quantity: Int,
    val rejectReason: String?,
    val remainApplyCount: Int,
    val rest: Int,
    val supportDevices: List<String>,
    val taskDesc: String?,
    val taskId: String,
    val taskStatus: Int,
    val taskStepList: List<TaskStep>,
    val taskTitle: String?,
    val taskTypeIconImage: String?,
    val taskTypeId: String?,
    val taskTypeTitle: String?,
    val taskUploadVerifyList: List<TaskUploadVerify>,
    val unitPrice: Float,
    val updatedDt: String?,
    val userId: String

)