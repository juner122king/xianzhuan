package com.lelezu.app.xianzhuan.data.model

/**
 * @author:Administrator
 * @date:2023/7/28 0028
 * @description:
 *
 */
data class ApiSuccessResponse<T>(val response: T) : ApiResponse2<T>(data = response)
