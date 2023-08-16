package com.lelezu.app.xianzhuan.ui.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.lelezu.app.xianzhuan.data.model.Announce
import com.lelezu.app.xianzhuan.data.model.ListData
import com.lelezu.app.xianzhuan.data.model.Message
import com.lelezu.app.xianzhuan.data.repository.SysInformRepository
import kotlinx.coroutines.launch

/**
 * @author:Administrator
 * @date:2023/7/25 0025
 * @description:系统消息相关
 *
 */
class SysMessageViewModel(private val sysInformRepository: SysInformRepository) : BaseViewModel() {

    val liveData: MutableLiveData<MutableList<Message>> = MutableLiveData()

    val isMark: MutableLiveData<Boolean> = MutableLiveData()
    val msgNum: MutableLiveData<Int> = MutableLiveData()

    val announce: MutableLiveData<List<Announce>> = MutableLiveData()


    //获取系统消息列表
    fun getAnnounce() = viewModelScope.launch {
        val list = sysInformRepository.apiAnnounce()
        handleApiResponse(list, announce)
    }

    //获取系统消息列表
    fun getMessageList() = viewModelScope.launch {
        val list = sysInformRepository.apiGetList(1, 100)
        handleApiListResponse(list, liveData)
    }


    //标记已读系统消息
    fun markSysMessage(msgs: List<String>) = viewModelScope.launch {
        val call = sysInformRepository.markSysMessage(msgs)
        handleApiResponse(call, isMark)
    }


    //获取用户未读信息数量
    fun getSysMessageNum() = viewModelScope.launch {
        val call = sysInformRepository.getSysMessageNum()
        handleApiResponse(call, msgNum)
    }


    class ViewFactory(private val repository: SysInformRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SysMessageViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST") return SysMessageViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

