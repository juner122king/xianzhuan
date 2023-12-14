package com.lelezu.app.xianzhuan.data.model

/**
 * @author:Administrator
 * @date:2023/8/30 0030
 * @description:
 *
 */

/**
 *
 * @property download String
 * @property isNew Boolean
 * @property versionCode Int
 * @property versionName String
 * @property isForce Boolean 是否强制更新
 * @constructor
 */
data class Version(
    var download: String = "",
    var isNew: Boolean,
    var versionCode: Int,
    var versionName: String,
    var isForce: Boolean,
)
