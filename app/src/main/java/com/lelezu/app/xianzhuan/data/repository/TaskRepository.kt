package com.lelezu.app.xianzhuan.data.repository

import com.lelezu.app.xianzhuan.data.ApiService
import com.lelezu.app.xianzhuan.data.model.ApiResponse
import com.lelezu.app.xianzhuan.data.model.ListData
import com.lelezu.app.xianzhuan.data.model.Req
import com.lelezu.app.xianzhuan.data.model.Task
import com.lelezu.app.xianzhuan.data.model.TaskQuery
import com.lelezu.app.xianzhuan.data.model.TaskSubmit
import com.lelezu.app.xianzhuan.data.model.TaskType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

/**
 * @author:Administrator
 * @date:2023/7/21 0021
 * @description:与任务相关的数据
 *
 */
class TaskRepository(private var apiService: ApiService) : BaseRepository() {


    //获取任务列表
    suspend fun apiGetTaskList(
        query: TaskQuery, isMyTask: Boolean = false
    ): ApiResponse<ListData<Task>> = withContext(Dispatchers.IO) {

        val call = if (isMyTask) {
            apiService.getMyTaskList(
                query.toMap(), loginToken
            )
        } else {
            apiService.getTaskList(
                query.toMap(), loginToken
            )
        }
        executeApiCall(call)
    }

    //获取任务类型列表
    suspend fun apiGetTaskTypeList(): ApiResponse<List<TaskType>> = withContext(Dispatchers.IO) {
        val call = apiService.getTaskTypeList(loginToken)
        executeApiCall(call)
    }

    //随机为用户推荐3个任务
    suspend fun apiShuffle(): ApiResponse<MutableList<Task>> = withContext(Dispatchers.IO) {

        val call = apiService.shuffle(loginToken)
        executeApiCall(call)
    }


    //任务详情
    suspend fun apiTaskDetails(taskId: String, applyLogId: String? = null): ApiResponse<Task> =
        withContext(Dispatchers.IO) {
            val call = apiService.getTaskInfo(taskId, applyLogId, loginToken)
            executeApiCall(call)
        }

    //任务报名
    suspend fun apiTaskApply(taskId: String): ApiResponse<Boolean> = withContext(Dispatchers.IO) {
        val call = apiService.taskApply(Req(taskId), loginToken)
        executeApiCall(call)

    }

    // 任务提交
    suspend fun apiTaskSubmit(taskSubmit: TaskSubmit): ApiResponse<Boolean> =
        withContext(Dispatchers.IO) {
            val call = apiService.taskSubmit(
                taskSubmit, loginToken
            )
            executeApiCall(call)
        }

    // 上传图片
    suspend fun apiUpload(imagePath: String): ApiResponse<String> = withContext(Dispatchers.IO) {
        val imagePart = createImagePart(imagePath)
        val call = apiService.upload(
            imagePart, loginToken
        )
        executeApiCall(call)
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


        //	任务查询条件(随机任务接口不传是返回 3 条数据),说明:TOP-置顶, SIMPLE-简单, HIGHER-高价, LATEST-最新, COMBO-组合(lowPrice、highPrice 和 taskTypeId 必传),可用值:TOP,SIMPLE,HIGHER,LATEST,COMBO
        const val queryCondTOP = "TOP"
        const val queryCondSIMPLE = "SIMPLE"
        const val queryCondHIGHER = "HIGHER"
        const val queryCondLATEST = "LATEST"
        const val queryCondCOMBO = "COMBO"

    }


}