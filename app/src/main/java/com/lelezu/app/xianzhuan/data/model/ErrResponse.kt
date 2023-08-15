package com.lelezu.app.xianzhuan.data.model

/**
 * @author:Administrator
 * @date:2023/7/19 0019
 * @description:泛型类ApiResponse<T>，其中T表示data字段的类型，可以根据接口的不同进行灵活的类型定义。
 *
 */
data class ErrResponse(
    val code: String?,
    val message: String? = null,

    ) {
    fun isTokenLose(): Boolean {
        return code == "300002" || code == "300003" || code == "300004"
    }
}