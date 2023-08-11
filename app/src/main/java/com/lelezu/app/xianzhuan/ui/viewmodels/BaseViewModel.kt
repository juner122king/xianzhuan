package com.lelezu.app.xianzhuan.ui.viewmodels

import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lelezu.app.xianzhuan.MyApplication
import com.lelezu.app.xianzhuan.data.model.ApiEmptyResponse
import com.lelezu.app.xianzhuan.data.model.ApiErrorResponse
import com.lelezu.app.xianzhuan.data.model.ApiFailedResponse
import com.lelezu.app.xianzhuan.data.model.ApiResponse
import com.lelezu.app.xianzhuan.data.model.ApiSuccessResponse
import com.lelezu.app.xianzhuan.data.model.ListData
import com.lelezu.app.xianzhuan.data.repository.TaskRepository

/**
 * @author:Administrator
 * @date:2023/7/25 0025
 * @description:
 *
 */
open class BaseViewModel : ViewModel() {


     val errMessage: MutableLiveData<String> = MutableLiveData() //接口返回的错误信息

    protected fun getFilePathFromUri(uri: Uri): String {
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
    protected fun <T> handleApiResponse(r: ApiResponse<T>, liveData: MutableLiveData<T>) {
        when (r) {
            is ApiSuccessResponse -> {
                // 处理成功的响应
                liveData.postValue(r.data)
            }

            is ApiFailedResponse -> {
                // 处理失败的响应
                errMessage.postValue(r.message!!)
            }

            is ApiEmptyResponse -> {
                // 处理空的响应
                errMessage.postValue("返回data为null")
            }

            is ApiErrorResponse -> {
                // 处理错误的响应
                errMessage.postValue(r.throwable.message)
            }
        }
    }

    protected fun <T> handleApiListResponse(
        r: ApiResponse<ListData<T>>, liveData: MutableLiveData<MutableList<T>>
    ) {
        when (r) {
            is ApiSuccessResponse -> {
                // 处理成功的响应
                liveData.postValue(r.response.records)
            }

            is ApiFailedResponse -> {
                // 处理失败的响应
                errMessage.postValue(r.message!!)
            }

            is ApiEmptyResponse -> {
                // 处理空的响应
                errMessage.postValue("返回data为null")
            }

            is ApiErrorResponse -> {
                // 处理错误的响应
                errMessage.postValue(r.throwable.message)
            }
        }
    }

}