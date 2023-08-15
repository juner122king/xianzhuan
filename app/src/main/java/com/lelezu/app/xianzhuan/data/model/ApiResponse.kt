package com.lelezu.app.xianzhuan.data.model

import java.io.Serializable

/**
 * @author:Administrator
 * @date:2023/7/19 0019
 * @description:泛型类ApiResponse<T>，其中T表示data字段的类型，可以根据接口的不同进行灵活的类型定义。
 *
 */
open class ApiResponse<T>(
    open val code: String? = null,
    open val data: T? = null,
    open val message: String? = null,
    open val error: Throwable? = null,
) : Serializable {
    val isSuccess: Boolean
        get() = code == "000000"
    val isTokenLose: Boolean
        get() = code == "300002" || code == "300003" || code == "300004"
}