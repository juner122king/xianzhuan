package com.lelezu.app.xianzhuan.data.model

/**
 * @author:Administrator
 * @date:2023/7/20 0020
 * @description:
 *
 */

/**
 *
 * @property verifyType Int? 	验证类型（1-收集截图，2-收集信息,0-无需验证）
 * @property useCaseImage 例图
 * @property verifyDesc String? 验证说明
 * @property verifyId String?   验证项ID
 * @property uploadValue String?   	上传图片/收集信息，需根据verifyType区分  负责上传
 * @constructor
 */
data class TaskUploadVerify(
    val verifyType: Int?,
    val useCaseImage: String,
    val verifyDesc: String?,
    val verifyId: String?,
    var uploadValue: String?
)