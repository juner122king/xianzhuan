package com.lelezu.app.xianzhuan.data.repository

import android.util.Log
import com.lelezu.app.xianzhuan.data.model.ApiEmptyResponse
import com.lelezu.app.xianzhuan.data.model.ApiErrorResponse
import com.lelezu.app.xianzhuan.data.model.ApiFailedResponse
import com.lelezu.app.xianzhuan.data.model.ApiResponse
import com.lelezu.app.xianzhuan.data.model.ApiSuccessResponse
import com.lelezu.app.xianzhuan.utils.ShareUtil
import retrofit2.Call

/**
 * @author:Administrator
 * @date:2023/7/28 0028
 * @description:
 *
 */
open class BaseRepository {
    protected val loginToken: String //访问值时执行get()
        get() = ShareUtil.getString(ShareUtil.APP_SHARED_PREFERENCES_LOGIN_TOKEN)

    protected inline fun <reified T> executeApiCall(call: Call<ApiResponse<T>>): ApiResponse<T> {
        return try {
            val response = call.execute()
            if (response.isSuccessful) {
                val body = response.body()
                when (body?.code) {
                    "000000" -> {
                        ApiSuccessResponse(body.data!!)
                    }

                    else -> {
                        ApiFailedResponse(body?.code, body?.message)
                    }
                }
            } else {
                ApiEmptyResponse()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            ApiErrorResponse(e)
        }
    }

}