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
    val _taskList: MutableLiveData<List<Task>> = MutableLiveData()


    val shuffleList: MutableLiveData<List<Task>> = MutableLiveData()

    val taskTypeList: MutableLiveData<List<TaskType>> = MutableLiveData()


    val task: MutableLiveData<Task> = MutableLiveData() //任务详情类

    // 获取任务列表数据 简单查询条件
    fun getTaskList(query: TaskQuery) = viewModelScope.launch {
        val taskList = taskRepository.apiGetTaskList(query)
        _taskList.postValue(taskList!!.records)
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


    class ViewFactory(private val repository: TaskRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST") return HomeViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

}