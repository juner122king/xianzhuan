package com.lelezu.app.xianzhuan.data.model

/**
 * @author:Administrator
 * @date:2023/7/19 0019
 * @description:泛型类ApiResponse<T>，其中T表示data字段的类型，可以根据接口的不同进行灵活的类型定义。
 *
 */
data class ApiResponse<T>(
    val code: String,
    val data: T?,
    val message: String
)