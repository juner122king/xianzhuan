package com.lelezu.app.xianzhuan.data.model

/**
 * @author:Administrator
 * @date:2023/7/20 0020
 * @description: 任务类的内部类
 *
 */
/**
 *
 * @property stepDesc String?   	步骤说明
 * @property stepId String? 步骤ID
 * @property useCaseImage 例图
 * @property webUrl String? 	网址
 * @property stepType Int? 	步骤类型
 * @constructor
 */
data class TaskStep(
    val stepDesc: String?,
    val stepId: String?,
    val useCaseImage: String?,
    val webUrl: String,
    val stepType: Int?
)