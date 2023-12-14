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
 * @property auditStatus Int 用户查看的任务状态(0-未报名，1-待提交，2-审核中，3-审核通过，4-审核被否，5-已取消，默认：0-未报名)  7:长单任务 进行中
 * @property createdDt String? 	发布时间
 * @property earnedCount Int 	多少人已赚
 * @property isTop Boolean 是否置顶
 * @property limitTimes Int     	限次(1-每日1次，2-每人1次，3-每人3次)
 * @property operateTime String?    剩余/提交/审核时间
 * @property quantity Int           数量
 * @property rejectReason String?   被否原因
 * @property deadlineTime Int?   订单提交截止时间，单位：小时
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
 * @property shareAmount Float  分享赚
 * @property updatedDt String?  	更新时间
 * @property userId String?  	发布者ID
 * @property taskType String?  	任务分类（1：APP端注册用户发布的任务，2：官方任务(小程序任务)）
 * @property taskPlatform Int  	"任务平台 1:APP  2:WX小程序  3:支付宝小程序"
 * @property longTaskVos LongTaskVos  	长任务步骤
 * @property progressNotify 长任务 进度通知  0-无通知  1-审核通过  2-审核不通过
 *
 * @property completeStepCnt 长任务步骤完成数
 * @property stepCnt 长任务步骤总数
 * @constructor
 */

data class Task(
    val applyLogId: String,
    val auditStatus: Int,
    val createdDt: String?,
    val earnedCount: Int,
    val isTop: Boolean,
    val limitTimes: Int,
    val operateTime: String?,
    val deadlineTime: Int?,
    val quantity: Int,
    val rejectReason: String?,
    val remainApplyCount: Int,
    val rest: Int,
    val supportDevices: List<String>,
    val taskDesc: String?,
    val taskId: String,
    val taskLabel: String,
    val taskStatus: Int,
    val taskStepList: List<TaskStep>,
    val taskTitle: String?,
    val taskTypeIconImage: String?,
    val taskTypeId: String?,
    val taskTypeTitle: String?,
    val taskUploadVerifyList: List<TaskUploadVerify>,
    val unitPrice: Float,
    val shareAmount: Float,
    val updatedDt: String?,
    val taskType: String?,
    val taskPlatform: Int,
    val userId: String,
    val userInfo: UserInfo,
    val longTaskVos: List<LongTaskVos>?,
    val progressNotify: Int,
    val completeStepCnt: Int,
    val stepCnt: Int,
) {
    val _shareAmount: String
        get() {
            return if (shareAmount < 0.01) {
                ""
            } else {
                shareAmount.toString() + "元"
            }
        }

    val stepCntNo: String
        get() {
            return if (stepCnt > 0) {
                "($completeStepCnt/$stepCnt)"
            } else {
                ""
            }
        }
}