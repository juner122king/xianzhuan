package com.lelezu.app.xianzhuan.data.repository

import com.lelezu.app.xianzhuan.data.model.ApiEmptyResponse
import com.lelezu.app.xianzhuan.data.model.ApiErrorResponse
import com.lelezu.app.xianzhuan.data.model.ApiFailedResponse
import com.lelezu.app.xianzhuan.data.model.ApiResponse
import com.lelezu.app.xianzhuan.data.model.ApiSuccessResponse

/**
 * @author:Administrator
 * @date:2023/7/28 0028
 * @description:
 *
 */
open class BaseRepository {
//    suspend fun <T> executeHttp(block: suspend () -> ApiResponse<T>): ApiResponse<T> {
//        runCatching {
//            block.invoke()
//        }.onSuccess { data: ApiResponse<T> ->
//            return handleHttpOk(data)
//        }.onFailure { e ->
//            return handleHttpError(e)
//        }
//        return ApiEmptyResponse()
//    }
//
//    /**
//     * 非后台返回错误，捕获到的异常
//     */
//    private fun <T> handleHttpError(e: Throwable): ApiErrorResponse<T> {
//        if (BuildConfig.DEBUG) e.printStackTrace()
//        handlingExceptions(e)
//        return ApiErrorResponse(e)
//    }
//
//    /**
//     * 返回200，但是还要判断isSuccess
//     */
//    private fun <T> handleHttpOk(data: ApiResponse<T>): ApiResponse<T> {
//        return if (data.code == "000000") {
//            getHttpSuccessResponse(data)
//        } else {
//            handlingApiExceptions(data.code, data.message)
//            ApiFailedResponse(data.code, data.message)
//        }
//    }
//
//    /**
//     * 成功和数据为空的处理
//     */
//    private fun <T> getHttpSuccessResponse(response: ApiResponse<T>): ApiResponse<T> {
//        return if (response.data == null || response.data is List<*> && (response.data as List<*>).isEmpty()) {
//            ApiEmptyResponse()
//        } else {
//            ApiSuccessResponse(response.data!!)
//        }
//    }

}