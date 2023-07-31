package com.lelezu.app.xianzhuan.data.repository

import android.util.Log
import com.lelezu.app.xianzhuan.data.ApiService
import com.lelezu.app.xianzhuan.data.model.ListData
import com.lelezu.app.xianzhuan.data.model.Req
import com.lelezu.app.xianzhuan.data.model.Task
import com.lelezu.app.xianzhuan.data.model.TaskQuery
import com.lelezu.app.xianzhuan.data.model.TaskType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Callback
import retrofit2.Response

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
                queryCond, current, highPrice, lowPrice, size, taskStatus, taskTypeId
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
            val response = apiService.getTaskTypeList().execute()
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
    suspend fun apiShuffle(): List<Task>? = withContext(Dispatchers.IO) {
        try {
            val response = apiService.shuffle().execute()
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
            val response = apiService.getTaskInfo(taskId).execute()
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
    suspend fun apiTaskApply(taskId: String): Boolean? = withContext(Dispatchers.IO) {
        try {
            val response = apiService.taskApply(Req(taskId)).execute()
            if (response.isSuccessful) {
                when (response.body()?.code) {
                    "000000" -> {
                        Log.d(
                            "APP接口 任务报名",
                            "获取成功 : ToString: ${response.body()?.data?.toString()}"
                        )
                        response.body()?.data
                    }

                    else -> {
                        Log.d(
                            "APP接口任务报名",
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


}