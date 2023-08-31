package com.lelezu.app.xianzhuan.data.model

/**
 * @author:Administrator
 * @date:2023/8/30 0030
 * @description:
 *
 */
data class Version(
    var download: String = "",
    var isNew: Boolean,
    var versionCode: Int,
    var versionName: String,
)
