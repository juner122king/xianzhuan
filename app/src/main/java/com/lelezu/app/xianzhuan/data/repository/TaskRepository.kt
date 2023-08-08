package com.lelezu.app.xianzhuan.data.repository

import android.util.Log
import com.lelezu.app.xianzhuan.data.ApiService
import com.lelezu.app.xianzhuan.data.model.ApiResponse
import com.lelezu.app.xianzhuan.data.model.ListData
import com.lelezu.app.xianzhuan.data.model.Req
import com.lelezu.app.xianzhuan.data.model.Task
import com.lelezu.app.xianzhuan.data.model.TaskQuery
import com.lelezu.app.xianzhuan.data.model.TaskSubmit
import com.lelezu.app.xianzhuan.data.model.TaskType
import com.lelezu.app.xianzhuan.utils.ShareUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Callback
import retrofit2.Response
import java.io.File

/**
 * @author:Administrator
 * @date:2023/7/21 0021
 * @description:与任务相关的数据
 *
 */
class TaskRepository(private var apiService: ApiService) {


    //获取任务列表
    suspend fun apiGetTaskList(query: TaskQuery): ListData<Task>? = withContext(Dispatchers.IO) {
        val queryCond = query.queryCond
        val current = query.current
        val highPrice = query.highPrice
        val lowPrice = query.lowPrice
        val size = query.size
        val taskStatus = query.taskStatus
        val taskTypeId = query.taskTypeId
        try {
            val response = apiService.getTaskList(
                queryCond, current, highPrice, lowPrice, size, taskStatus, taskTypeId,ShareUtil.getString(ShareUtil.APP_SHARED_PREFERENCES_LOGIN_TOKEN)
            ).execute()
            if (response.isSuccessful) {
                when (response.body()?.code) {
                    "000000" -> {
                        Log.d(
                            "APP接口TaskList",
                            "获取成功 : ToString: ${response.body()?.data?.toString()}"
                        )
                        response.body()?.data
                    }

                    else -> {
                        Log.d(
                            "APP接口TaskList",
                            "失败${response.body()?.code}:${response.body()?.message}"
                        )
                        null
                    }
                }
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    //获取我的任务列表
    suspend fun apiGetMyTaskList(query: TaskQuery): ListData<Task>? = withContext(Dispatchers.IO) {
        val queryCond = query.queryCond
        val current = query.current
        val highPrice = query.highPrice
        val lowPrice = query.lowPrice
        val size = query.size
        val taskStatus = query.taskStatus
        val taskTypeId = query.taskTypeId
        try {
            val response = apiService.getMyTaskList(
                queryCond, current, highPrice, lowPrice, size, taskStatus, taskTypeId,ShareUtil.getString(ShareUtil.APP_SHARED_PREFERENCES_LOGIN_TOKEN)
            ).execute()
            if (response.isSuccessful) {
                when (response.body()?.code) {
                    "000000" -> {
                        Log.d(
                            "APP接口TaskList",
                            "获取成功 : ToString: ${response.body()?.data?.toString()}"
                        )
                        response.body()?.data
                    }

                    else -> {
                        Log.d(
                            "APP接口TaskList",
                            "失败${response.body()?.code}:${response.body()?.message}"
                        )
                        null
                    }
                }
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    //获取任务类型列表
    suspend fun apiGetTaskTypeList(): List<TaskType>? = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getTaskTypeList(ShareUtil.getString(ShareUtil.APP_SHARED_PREFERENCES_LOGIN_TOKEN)).execute()
            if (response.isSuccessful) {
                when (response.body()?.code) {
                    "000000" -> {
                        Log.d(
                            "APP接口/type/options",
                            "获取成功 : ToString: ${response.body()?.data?.toString()}"
                        )
                        response.body()?.data
                    }

                    else -> {
                        Log.d(
                            "APP接口/type/options",
                            "失败${response.body()?.code}:${response.body()?.message}"
                        )
                        null
                    }
                }
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    //随机为用户推荐3个任务
    suspend fun apiShuffle(): MutableList<Task>? = withContext(Dispatchers.IO) {
        try {
            val response = apiService.shuffle(ShareUtil.getString(ShareUtil.APP_SHARED_PREFERENCES_LOGIN_TOKEN)).execute()
            if (response.isSuccessful) {
                when (response.body()?.code) {
                    "000000" -> {
                        Log.d(
                            "APP接口/type/Shuffle",
                            "获取成功 : ToString: ${response.body()?.data?.toString()}"
                        )
                        response.body()?.data
                    }

                    else -> {
                        Log.d(
                            "APP接口/type/Shuffle",
                            "失败${response.body()?.code}:${response.body()?.message}"
                        )
                        null
                    }
                }
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }


    //任务详情
    suspend fun apiTaskDetails(taskId: String): Task? = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getTaskInfo(taskId,ShareUtil.getString(ShareUtil.APP_SHARED_PREFERENCES_LOGIN_TOKEN)).execute()
            if (response.isSuccessful) {
                when (response.body()?.code) {
                    "000000" -> {
                        Log.d(
                            "APP接口 任务详情",
                            "获取成功 : ToString: ${response.body()?.data?.toString()}"
                        )
                        response.body()?.data
                    }

                    else -> {
                        Log.d(
                            "APP接口任务详情",
                            "失败${response.body()?.code}:${response.body()?.message}"
                        )
                        null
                    }
                }
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    //任务报名
    suspend fun apiTaskApply(taskId: String): ApiResponse<Boolean>? = withContext(Dispatchers.IO) {
        try {
            val response = apiService.taskApply(Req(taskId),ShareUtil.getString(ShareUtil.APP_SHARED_PREFERENCES_LOGIN_TOKEN)).execute()
            if (response.isSuccessful) {

                response.body()

            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    //任务提交
    suspend fun apiTaskSubmit(taskSubmit: TaskSubmit): Boolean? = withContext(Dispatchers.IO) {
        try {
            val response = apiService.taskSubmit(taskSubmit,ShareUtil.getString(ShareUtil.APP_SHARED_PREFERENCES_LOGIN_TOKEN)).execute()
            if (response.isSuccessful) {
                when (response.body()?.code) {
                    "000000" -> {
                        Log.d(
                            "APP接口 任务提交",
                            "获取成功 : ToString: ${response.body()?.data?.toString()}"
                        )
                        response.body()?.data
                    }

                    else -> {
                        Log.d(
                            "APP接口任务提交",
                            "失败${response.body()?.code}:${response.body()?.message}"
                        )
                        null
                    }
                }
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    //任务提交
    suspend fun apiUpload(imagePath: String): String? = withContext(Dispatchers.IO) {
        val imagePart = createImagePart(imagePath)
        try {
            val response = apiService.upload(imagePart,ShareUtil.getString(ShareUtil.APP_SHARED_PREFERENCES_LOGIN_TOKEN)).execute()
            if (response.isSuccessful) {
                when (response.body()?.code) {
                    "000000" -> {
                        Log.d(
                            "APP接口 任务提交",
                            "获取成功 : ToString: ${response.body()?.data?.toString()}"
                        )
                        response.body()?.data
                    }

                    else -> {
                        Log.d(
                            "APP接口任务提交",
                            "失败${response.body()?.code}:${response.body()?.message}"
                        )
                        null
                    }
                }
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    private fun createImagePart(imagePath: String): MultipartBody.Part {
        val file = File(imagePath)

        val mediaType = when {
            imagePath.endsWith(".png", true) -> "image/png".toMediaTypeOrNull()
            imagePath.endsWith(".jpg", true) -> "image/jpeg".toMediaTypeOrNull()
            else -> throw IllegalArgumentException("Unsupported file format")
        }
        val requestBody = file.asRequestBody(mediaType)

        return MultipartBody.Part.createFormData("file", file.name, requestBody)
    }

    companion object {
        //	任务状态(0-未报名，1-待提交，2-审核中，3-审核通过，4-审核被否，5-已取消，默认：0-未报名)
        const val auditStatus0 = 0
        const val auditStatus1 = 1
        const val auditStatus2 = 2
        const val auditStatus3 = 3
        const val auditStatus4 = 4
        const val auditStatus5 = 5


        //	任务查询条件(随机任务接口不传是返回 3 条数据),说明:TOP-置顶, SIMPLE-简单, HIGHER-高价, LATEST-最新, COMBO-组合(lowPrice、highPrice 和 taskTypeId 必传),可用值:TOP,SIMPLE,HIGHER,LATEST,COMBO
        const val queryCondTOP = "TOP"
        const val queryCondSIMPLE = "SIMPLE"
        const val queryCondHIGHER = "HIGHER"
        const val queryCondLATEST = "LATEST"
        const val queryCondCOMBO = "COMBO"

    }


}