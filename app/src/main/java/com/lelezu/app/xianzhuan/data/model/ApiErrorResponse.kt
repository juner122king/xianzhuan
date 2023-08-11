package com.lelezu.app.xianzhuan.data.model

/**
 * @author:Administrator
 * @date:2023/7/28 0028
 * @description:
 *
 */
data class ApiErrorResponse<T>(val throwable: Throwable) : ApiResponse<T>(error = throwable)
