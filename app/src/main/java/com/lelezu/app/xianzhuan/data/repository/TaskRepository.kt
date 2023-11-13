package com.lelezu.app.xianzhuan.data.repository

import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import com.lelezu.app.xianzhuan.MyApplication
import com.lelezu.app.xianzhuan.data.ApiService
import com.lelezu.app.xianzhuan.data.model.ApiResponse
import com.lelezu.app.xianzhuan.data.model.Complete
import com.lelezu.app.xianzhuan.data.model.ListData
import com.lelezu.app.xianzhuan.data.model.Partner
import com.lelezu.app.xianzhuan.data.model.Req
import com.lelezu.app.xianzhuan.data.model.Task
import com.lelezu.app.xianzhuan.data.model.TaskQuery
import com.lelezu.app.xianzhuan.data.model.TaskSubmit
import com.lelezu.app.xianzhuan.data.model.TaskType
import com.lelezu.app.xianzhuan.utils.Base64Utils
import com.lelezu.app.xianzhuan.utils.LogUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
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
                query.toMap(), loginToken, deviceId
            )
        } else {
            apiService.getTaskList(
                query.toMap(), loginToken, deviceId
            )
        }
        executeApiCall(call)
    }

    //获取任务类型列表
    suspend fun apiGetTaskTypeList(): ApiResponse<List<TaskType>> = withContext(Dispatchers.IO) {
        val call = apiService.getTaskTypeList(loginToken, deviceId)
        executeApiCall(call)
    }

    //随机为用户推荐3个任务
    suspend fun apiShuffle(): ApiResponse<MutableList<Task>> = withContext(Dispatchers.IO) {

        val call = apiService.shuffle(loginToken, deviceId)
        executeApiCall(call)
    }


    //获取雇主发布的任务
    suspend fun apiMasterTask(userId: String): ApiResponse<ListData<Task>> =
        withContext(Dispatchers.IO) {

            val call = apiService.masterTask("TOP", "2", userId, loginToken, "", deviceId)
            executeApiCall(call)
        }


    //任务详情
    suspend fun apiTaskDetails(taskId: String, applyLogId: String? = null): ApiResponse<Task> =
        withContext(Dispatchers.IO) {
            val call = apiService.getTaskInfo(taskId, applyLogId, loginToken, deviceId)
            executeApiCall(call)
        }

    //任务报名
    suspend fun apiTaskApply(taskId: String): ApiResponse<Boolean> = withContext(Dispatchers.IO) {
        val call = apiService.taskApply(Req(taskId), loginToken, deviceId)
        executeApiCall(call)

    }

    //添加我的师傅
    suspend fun apiGetMater(userId: String): ApiResponse<Boolean> = withContext(Dispatchers.IO) {
        val call = apiService.bindMaster(userId, loginToken, deviceId)
        executeApiCall(call)

    }

    //合伙人后台
    suspend fun apiPartnerBack(): ApiResponse<Partner> = withContext(Dispatchers.IO) {
        val call = apiService.partnerBack(loginToken, deviceId)
        executeApiCall(call)

    }

    //合伙人团队
    suspend fun apiPartnerTeam(): ApiResponse<ListData<Partner>> = withContext(Dispatchers.IO) {
        val call = apiService.partnerTeamList(loginToken, deviceId)
        executeApiCall(call)

    }

    //合伙人后台结算记录
    suspend fun apiPartnerBackList(): ApiResponse<ListData<Partner>> = withContext(Dispatchers.IO) {
        val call = apiService.partnerBackList(loginToken, deviceId)
        executeApiCall(call)

    }

    // 任务提交
    suspend fun apiTaskSubmit(taskSubmit: TaskSubmit): ApiResponse<Boolean> =
        withContext(Dispatchers.IO) {
            LogUtils.i("测试", "修改请求体：$taskSubmit")
            val call = apiService.taskSubmit(
                taskSubmit, loginToken, deviceId
            )
            executeApiCall(call)
        }

    // 小程序任务完成校验
    suspend fun miniTaskComplete(applyLogId: String): ApiResponse<Boolean> =
        withContext(Dispatchers.IO) {
            LogUtils.i("测试", "小程序任务完成校验：$applyLogId")
            val call = apiService.miniTaskComplete(
                Complete(applyLogId), loginToken, deviceId
            )
            executeApiCall(call)
        }

    // 上传图片
    /**
     *
     * @param uri String 图片路径
     * @return ApiResponse<String>
     */
    suspend fun apiUpload(uri: Uri): ApiResponse<String> = withContext(Dispatchers.IO) {
        val imagePart = createImagePart(uri)

        val call = apiService.upload(
            imagePart, loginToken, deviceId
        )
        executeApiCall(call)
    }


    private fun createImagePart(imagePath: Uri): MultipartBody.Part {
        val compressedImageBytes = Base64Utils.zipPic(imagePath, 100)

        val mediaType = when {
            getFilePathFromUri(imagePath).endsWith(".png", true) -> "image/png".toMediaTypeOrNull()
            getFilePathFromUri(imagePath).endsWith(".jpg", true) -> "image/jpeg".toMediaTypeOrNull()
            else -> throw IllegalArgumentException("Unsupported file format")
        }

        val requestBody = compressedImageBytes!!.toRequestBody(mediaType)
        return MultipartBody.Part.createFormData(
            "file", File(getFilePathFromUri(imagePath)).name, requestBody
        )
    }


    companion object {

        //	任务查询条件(随机任务接口不传是返回 3 条数据),说明:TOP-置顶, SIMPLE-简单, HIGHER-高价, LATEST-最新, COMBO-组合(lowPrice、highPrice 和 taskTypeId 必传),可用值:TOP,SIMPLE,HIGHER,LATEST,COMBO
        const val queryCondTOP = "TOP"
        const val queryCondSIMPLE = "SIMPLE"
        const val queryCondHIGHER = "HIGHER"
        const val queryCondLATEST = "LATEST"
        const val queryCondCOMBO = "COMBO"

    }


    private fun getFilePathFromUri(uri: Uri): String {
        Log.i("zipPic:", "上传图片的uri:${uri}")
        var filePath = ""
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = MyApplication.context.contentResolver?.query(uri, projection, null, null, null)
        cursor?.let {
            if (it.moveToFirst()) {
                val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                filePath = it.getString(columnIndex)
            }
            cursor.close()
        }
        Log.i("zipPic:", "上传图片的路径filePath:${filePath}")
        return filePath
    }

}