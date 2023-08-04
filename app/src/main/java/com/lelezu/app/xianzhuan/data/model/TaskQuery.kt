package com.lelezu.app.xianzhuan.data.model

/**
 * @author:Administrator
 * @date:2023/7/26 0026
 * @description:任务查询条件类
 *
 */
data class TaskQuery(

    val queryCond: String?,
    val current: Int? = null,
    val highPrice: Float? = null,
    val lowPrice: Float? = null,
    val size: Int? = null,
    val taskStatus: Int? = null,
    val taskTypeId: String? = null
)
