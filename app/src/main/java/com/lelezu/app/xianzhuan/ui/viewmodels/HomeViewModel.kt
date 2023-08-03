package com.lelezu.app.xianzhuan.ui.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.lelezu.app.xianzhuan.data.model.Task
import com.lelezu.app.xianzhuan.data.model.TaskQuery
import com.lelezu.app.xianzhuan.data.model.TaskType
import com.lelezu.app.xianzhuan.data.repository.TaskRepository
import kotlinx.coroutines.launch

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

//    val randomTaskLD: MutableLiveData<Task> = MutableLiveData() //随机任务

    // 获取任务列表数据 简单查询条件
    fun getTaskList(query: TaskQuery) = viewModelScope.launch {
        val taskList = taskRepository.apiGetTaskList(query)
        if (taskList != null)
            _taskList.postValue(taskList.records)
        else
            _taskList.postValue(mutableListOf())
    }

    // 获取《我的》任务列表数据 简单查询条件
    fun getMyTaskList(taskStatus: Int) = viewModelScope.launch {
        val taskList = taskRepository.apiGetMyTaskList(
            TaskQuery(
                TaskRepository.queryCondLATEST,
                null,
                null,
                null,
                null,
                taskStatus,
                null
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
        isApply.postValue(r!!)
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