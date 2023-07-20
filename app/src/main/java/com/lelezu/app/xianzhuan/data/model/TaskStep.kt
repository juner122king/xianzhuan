package com.lelezu.app.xianzhuan.data.model

/**
 * @author:Administrator
 * @date:2023/7/20 0020
 * @description: 任务类的内部类
 *
 */
data class TaskStep(
    val stepDesc: String?,
    val stepId: String?,
    val uploadImage: String?,
    val useCaseImages: List<String>,
    val webUrl: String?
)