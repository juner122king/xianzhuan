package com.lelezu.app.xianzhuan.data.model

/**
 * @author:Administrator
 * @date:2023/7/20 0020
 * @description:
 *
 */

/**
 *
 * @property verifyType Int? 	验证类型（1-收集截图，2-收集信息）
 * @property useCaseImage 例图
 * @property uploadImage 用户上传图片
 * @property verifyDesc String? 验证说明
 * @property verifyId String?   验证项ID
 * @constructor
 */
data class TaskUploadVerify(
    val verifyType: Int?,
    val useCaseImage: String,
    val verifyDesc: String?,
    val verifyId: String?,
    var uploadImage: String = ""
)