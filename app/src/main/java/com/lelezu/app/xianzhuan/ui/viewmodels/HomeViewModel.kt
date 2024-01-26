package com.lelezu.app.xianzhuan.ui.viewmodels

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.lelezu.app.xianzhuan.data.model.ErrResponse
import com.lelezu.app.xianzhuan.data.model.Limit
import com.lelezu.app.xianzhuan.data.model.LongTaskVos
import com.lelezu.app.xianzhuan.data.model.Partner
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
    val limit: MutableLiveData<Limit> = MutableLiveData() //报名前的验证信息
    val isReport: MutableLiveData<Boolean> = MutableLiveData() //是否举报成功
    val isCancel: MutableLiveData<Boolean> = MutableLiveData() //是否取消成功
    val isUp: MutableLiveData<Boolean> = MutableLiveData() //是否提交成功

    val isCompleteMini: MutableLiveData<Boolean> = MutableLiveData() //是否成功校验小程序

    val isBind: MutableLiveData<Boolean> = MutableLiveData() //是否绑定师傅成功

    val upLink: MutableLiveData<String> = MutableLiveData() //图片上传成功的返回Link
    val upltLink: MutableLiveData<String> = MutableLiveData() //图片上传成功的返回Link

    val partnerLiveData: MutableLiveData<Partner> = MutableLiveData() //合伙人后台
    val partnerListLiveData: MutableLiveData<MutableList<Partner>> = MutableLiveData() //合伙人后台结算记录

    val teamLiveData: MutableLiveData<MutableList<Partner>> = MutableLiveData() //合伙人团队


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

    // 报名前的验证接口
    fun limitTask() = viewModelScope.launch {
        val r = taskRepository.limitTask()
        handleApiResponse(r, limit,false)
    }

    // 任务举报
    fun taskReport(
        reportContent: String,

        reportedUserId: String,
        reportedTaskId: String,
        contactLabel: String="",
    ) = viewModelScope.launch {
        val r =
            taskRepository.taskReport(reportContent, contactLabel, reportedUserId, reportedTaskId)
        handleApiResponse(r, isReport)
    }

    // 任务取消
    fun apiTaskCancel(applyLogId: String) = viewModelScope.launch {
        val r = taskRepository.apiTaskCancel(applyLogId)
        handleApiResponse(r, isCancel)
    }


    // 添加我的师傅
    fun apiGetMater(userId: String) = viewModelScope.launch {
        val r = taskRepository.apiGetMater(userId)
        handleApiResponse(r, isBind)
    }

    // 合伙人后台数据
    fun apiPartnerBack() = viewModelScope.launch {
        val r = taskRepository.apiPartnerBack()
        handleApiResponse(r, partnerLiveData)
    }

    // 合伙人团队
    fun apiPartnerTeam() = viewModelScope.launch {
        val r = taskRepository.apiPartnerTeam()
        handleApiListResponse(r, teamLiveData)
    }

    // 合伙人后台结算记录
    fun apiPartnerBackList() = viewModelScope.launch {
        val r = taskRepository.apiPartnerBackList()
        handleApiListResponse(r, partnerListLiveData)
    }

    // 合伙人后台结算记录
    fun miniTaskComplete(applyLogId: String) = viewModelScope.launch {
        val r = taskRepository.miniTaskComplete(applyLogId)
        handleApiResponse(r, isCompleteMini)
    }


    // 任务提交
    fun apiTaskSubmit(
        applyLogId: String?,
        verifys: List<TaskUploadVerify>?,
        isLongTask: Boolean,
        auditId: String? = null,
    ) = viewModelScope.launch {

        if (verifys.isNullOrEmpty()) {
            errMessage.postValue(ErrResponse(null, "请上传相关验证内容！"))
        } else {

            val isUploadValueEmpty = verifys.any { verify ->
                verify.uploadValue == null
            }
            if (!isUploadValueEmpty) {
                val r = taskRepository.apiTaskSubmit(
                    TaskSubmit(applyLogId, verifys, auditId), isLongTask
                )
                handleApiResponse(r, isUp)
            } else {
                val statusMap = mapOf(
                    1 to "1",
                    2 to "1、2",
                    3 to "1、2、3",
                    4 to "1、2、3、4",
                    5 to "1、2、3、4、5",
                    6 to "1、2、3、4、5、6",
                    7 to "1、2、3、4、5、6、7",
                    8 to "1、2、3、4、5、6、7、8",
                    // 可以继续添加其他映射关系
                )
                errMessage.postValue(
                    ErrResponse(
                        null, "请按第${statusMap[verifys.size]}项要求完善验证信息后再提交"
                    )
                )
            }
        }
    }


    // 上传图片接口
    fun apiUpload(uri: Uri, isLongTask: Boolean = false) = viewModelScope.launch {
        val r = taskRepository.apiUpload(uri)
        if (isLongTask) handleApiResponse(r, upltLink)
        else handleApiResponse(r, upLink)
    }


    // 获取雇主发布的任务列表
    fun getMasterTaskList(userId: String) = viewModelScope.launch {
        val apiListResponse = taskRepository.apiMasterTask(userId)
        handleApiListResponse(apiListResponse, taskList)
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