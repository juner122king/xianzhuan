package com.lelezu.app.xianzhuan.data.model

/**
 * @author:Administrator
 * @date:2023/7/20 0020
 * @description:
 *
 */

/**
 *
 * @property uploadImage String?    	用户上传图片
 * @property useCaseImages List<String> 例图集合
 * @property verifyDesc String? 验证说明
 * @property verifyId String?   验证项ID
 * @constructor
 */
data class TaskUploadVerify(
    val uploadImage: String?,
    val useCaseImages: List<String>,
    val verifyDesc: String?,
    val verifyId: String?
)