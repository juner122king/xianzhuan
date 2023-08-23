package com.lelezu.app.xianzhuan.ui.viewmodels

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.lelezu.app.xianzhuan.data.model.ApiResponse
import com.lelezu.app.xianzhuan.data.model.ApiSuccessResponse
import com.lelezu.app.xianzhuan.data.model.ErrResponse
import com.lelezu.app.xianzhuan.data.model.ListData
import com.lelezu.app.xianzhuan.data.model.Task
import com.lelezu.app.xianzhuan.data.model.TaskQuery
import com.lelezu.app.xianzhuan.data.model.TaskSubmit
import com.lelezu.app.xianzhuan.data.model.TaskType
import com.lelezu.app.xianzhuan.data.model.TaskUploadVerify
import com.lelezu.app.xianzhuan.data.repository.TaskRepository
import kotlinx.coroutines.launch

/**
 * @author:Administrator
 * @date:2023/7/20 0020
 * @description:主页的相关
 *
 */
class HomeViewModel(private val taskRepository: TaskRepository) : BaseViewModel() {

    // 定义一个 MutableLiveData 来保存任务列表
    val taskList: MutableLiveData<MutableList<Task>> = SingleLiveEvent()


    val shuffleList: MutableLiveData<MutableList<Task>> = MutableLiveData()

    val taskTypeList: MutableLiveData<List<TaskType>> = MutableLiveData()


    val task: MutableLiveData<Task> = MutableLiveData() //任务详情类

    val isApply: MutableLiveData<Boolean> = MutableLiveData() //是否报名成功
    val isUp: MutableLiveData<Boolean> = MutableLiveData() //是否提交成功


    val upLink: MutableLiveData<String> = MutableLiveData() //图片上传成功的返回Link


    // 获取任务列表数据 简单查询条件
    fun getTaskList(taskQuery: TaskQuery, isMyTask: Boolean = false) = viewModelScope.launch {
        val apiListResponse = taskRepository.apiGetTaskList(
            taskQuery, isMyTask
        )
        handleApiListResponse(apiListResponse, taskList)
    }


    // 获取任务类型数据
    fun getTaskTypeList() = viewModelScope.launch {
        val r = taskRepository.apiGetTaskTypeList()

        handleApiResponse(r, taskTypeList)
    }

    // 随机为用户推荐3个任务
    fun getShuffle() = viewModelScope.launch {
        val r = taskRepository.apiShuffle()

        handleApiResponse(r, shuffleList)

    }


    // 获取任务详情
    fun getTaskDetails(taskId: String, applyLogId: String? = null) = viewModelScope.launch {
        val r = taskRepository.apiTaskDetails(taskId, applyLogId)

        handleApiResponse(r, task)


    }


    // 任务报名
    fun apiTaskApply(taskId: String) = viewModelScope.launch {
        val r = taskRepository.apiTaskApply(taskId)
        handleApiResponse(r, isApply)

    }


    // 任务提交
    fun apiTaskSubmit(applyLogId: String, verifys: List<TaskUploadVerify>) = viewModelScope.launch {

        Log.i("验证信息", verifys.toString())
        val isUploadValueEmpty = verifys.all { verify ->
            verify.uploadValue?.isNotEmpty() ?: false
        }
        if (isUploadValueEmpty) {
            val r = taskRepository.apiTaskSubmit(TaskSubmit(applyLogId, verifys))
            handleApiResponse(r, isUp)

        } else {
            errMessage.postValue(ErrResponse(null, "验证内容不完整！"))
        }
    }

    // 上传图片接口
    fun apiUpload(uri: Uri) = viewModelScope.launch {

        val r = taskRepository.apiUpload(getFilePathFromUri(uri))
        handleApiResponse(r, upLink)

    }


    class ViewFactory(private val repository: TaskRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST") return HomeViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

}