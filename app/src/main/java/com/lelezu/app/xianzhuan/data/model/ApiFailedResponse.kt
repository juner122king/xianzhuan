package com.lelezu.app.xianzhuan.data.model

/**
 * @author:Administrator
 * @date:2023/7/28 0028
 * @description:
 *
 */
data class ApiFailedResponse<T>(override val code: String?, override val message: String?) : ApiResponse2<T>(code = code, message = code)
