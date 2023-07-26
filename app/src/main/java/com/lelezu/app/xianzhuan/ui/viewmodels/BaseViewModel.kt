package com.lelezu.app.xianzhuan.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lelezu.app.xianzhuan.data.repository.TaskRepository

/**
 * @author:Administrator
 * @date:2023/7/25 0025
 * @description:
 *
 */
class BaseViewModel(): ViewModel() {


    class ViewFactory(private val repository: TaskRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST") return HomeViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

}