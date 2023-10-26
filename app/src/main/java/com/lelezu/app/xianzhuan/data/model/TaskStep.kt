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
 * @property stepType Int? 	步骤类型 （1-图文，2-网址，3-小程序关联ID，4-小程序搜索场景值，5-搜索应用示例图,7-小程序搜索）
 * @property hasComplete Boolean	是否已关注小程序
 * @property userName String	小程序原始id
 * @property searchAppName String	小程序名字
 * @constructor
 */
data class TaskStep(
    val stepDesc: String,
    val stepId: String?,
    val useCaseImage: String?,
    val webUrl: String,
    val stepType: Int?,
    val hasComplete: Boolean,
    val userName: String,
    val searchAppName: String,
)