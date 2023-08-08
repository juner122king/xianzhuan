package com.lelezu.app.xianzhuan.ui.viewmodels

import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.lelezu.app.xianzhuan.MyApplication
import com.lelezu.app.xianzhuan.data.model.Task
import com.lelezu.app.xianzhuan.data.model.TaskQuery
import com.lelezu.app.xianzhuan.data.model.TaskSubmit
import com.lelezu.app.xianzhuan.data.model.TaskType
import com.lelezu.app.xianzhuan.data.model.TaskUploadVerify
import com.lelezu.app.xianzhuan.data.repository.TaskRepository
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

/**
 * @author:Administrator
 * @date:2023/7/20 0020
 * @description:主页的相关
 *
 */
class HomeViewModel(private val taskRepository: TaskRepository) : ViewModel() {

    // 定义一个 MutableLiveData 来保存任务列表
    val _taskList: MutableLiveData<MutableList<Task>> = MutableLiveData()

    // 我的任务列表
    val myTaskList: MutableLiveData<MutableList<Task>> = MutableLiveData()


    val shuffleList: MutableLiveData<MutableList<Task>> = MutableLiveData()

    val taskTypeList: MutableLiveData<List<TaskType>> = MutableLiveData()


    val task: MutableLiveData<Task> = MutableLiveData() //任务详情类

    val isApply: MutableLiveData<Boolean> = MutableLiveData() //是否报名成功
    val isUp: MutableLiveData<Boolean> = MutableLiveData() //是否提交成功


    val upLink: MutableLiveData<String> = MutableLiveData() //图片上传成功的返回Link


    val errMessage: MutableLiveData<String> = MutableLiveData() //接口返回的错误信息


    // 获取任务列表数据 简单查询条件
    fun getTaskList(queryCond: String, current: Int) = viewModelScope.launch {
        val taskList = taskRepository.apiGetTaskList(
            TaskQuery(
                queryCond, current, null, null, null, null, null
            )
        )
        if (taskList != null) _taskList.postValue(taskList.records)
        else _taskList.postValue(mutableListOf())
    }

    // 获取任务列表数据 简单查询条件
    fun getTaskList(taskQuery: TaskQuery) = viewModelScope.launch {
        val taskList = taskRepository.apiGetTaskList(
            taskQuery
        )
        if (taskList != null) _taskList.postValue(taskList.records)
        else _taskList.postValue(mutableListOf())
    }


    // 获取《我的》任务列表数据 简单查询条件
    fun getMyTaskList(auditStatus: Int, current: Int) = viewModelScope.launch {
        val taskList = taskRepository.apiGetMyTaskList(
            TaskQuery(
                null, current, null, null, null, auditStatus, null
            )
        )
        myTaskList.postValue(taskList!!.records)
    }

    // 获取任务类型数据
    fun getTaskTypeList() = viewModelScope.launch {
        val list = taskRepository.apiGetTaskTypeList()
        taskTypeList.postValue(list!!)
    }

    // 随机为用户推荐3个任务
    fun getShuffle() = viewModelScope.launch {
        val list = taskRepository.apiShuffle()
        shuffleList.postValue(list!!)
    }


    // 获取任务详情
    fun getTaskDetails(taskId: String) = viewModelScope.launch {
        val r = taskRepository.apiTaskDetails(taskId)
        task.postValue(r!!)


    }


    // 任务报名
    fun apiTaskApply(taskId: String) = viewModelScope.launch {
        val r = taskRepository.apiTaskApply(taskId)
        if (r != null) {
            when (r.code) {
                "000000" -> {
                    isApply.postValue(true)
                }

                else -> {
                    errMessage.postValue(r.message)
                }
            }
        } else {
            errMessage.postValue("报名失败！")
        }


    }


    // 任务提交
    fun apiTaskSubmit(applyLogId: String, verifys: List<TaskUploadVerify>) = viewModelScope.launch {

        Log.i("验证信息", verifys.toString())
        val isUploadValueEmpty = verifys.any {
            it.uploadValue.isEmpty() ?: true
        }
        if (!isUploadValueEmpty) {
            val r = taskRepository.apiTaskSubmit(TaskSubmit(applyLogId, verifys))
            isUp.postValue(r!!)
        } else {
            errMessage.postValue("验证内容不完整！")
        }


    }


    // 上传图片接口
    fun apiUpload(uri: Uri) = viewModelScope.launch {
        val r = taskRepository.apiUpload(getFilePathFromUri(uri))
        upLink.postValue(r!!)
    }


    private fun getFilePathFromUri(uri: Uri): String {
        var filePath = ""
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor =
            MyApplication.context?.contentResolver?.query(uri, projection, null, null, null)
        cursor?.let {
            if (it.moveToFirst()) {
                val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                filePath = it.getString(columnIndex)
            }
            cursor.close()
        }
        return filePath
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