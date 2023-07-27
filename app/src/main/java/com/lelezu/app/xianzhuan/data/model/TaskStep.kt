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
 * @property uploadImage String?    用户上传图片
 * @property useCaseImages List<String> 例图集合
 * @property webUrl String? 	网址
 * @constructor
 */
data class TaskStep(
    val stepDesc: String?,
    val stepId: String?,
    val uploadImage: String?,
    val useCaseImages: List<String>,
    val webUrl: String?
)