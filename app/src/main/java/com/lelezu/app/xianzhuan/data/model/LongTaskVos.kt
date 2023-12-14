package com.lelezu.app.xianzhuan.data.model

/**
 * @author:Administrator
 * @date:2023/7/20 0020
 * @description:
 *
 */

/**
 *
 * @property verifyId Int? 	验证类型（1-收集截图，2-收集信息,0-无需验证）
 * @property originStepAmount 例图
 * @property stepAmount String? 验证说明
 * @property auditStatus Int? 步骤状态 0-未报名 {1-待提交 2-审核中 3-审核通过 4-审核被否} 5-手动取消 6-超时取消
 * @constructor
 */
data class LongTaskVos(
    val sortSeq: Int,
    val verifyId: String,
    val originStepAmount: Float,
    val stepAmount: Float,
    val completeAuditId: String,
    val auditStatus: Int,
    val taskUploadVerifyList: List<TaskUploadVerify>
)