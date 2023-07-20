package com.lelezu.app.xianzhuan.data.model

/**
 * @author:Administrator
 * @date:2023/7/20 0020
 * @description:
 *
 */
data class TaskUploadVerify(
    val uploadImage: String?,
    val useCaseImages: List<String>,
    val verifyDesc: String?,
    val verifyId: String?
)