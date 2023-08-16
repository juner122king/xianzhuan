package com.lelezu.app.xianzhuan.ui.viewmodels

import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lelezu.app.xianzhuan.MyApplication
import com.lelezu.app.xianzhuan.data.model.ApiEmptyResponse
import com.lelezu.app.xianzhuan.data.model.ApiErrorResponse
import com.lelezu.app.xianzhuan.data.model.ApiFailedResponse
import com.lelezu.app.xianzhuan.data.model.ApiResponse
import com.lelezu.app.xianzhuan.data.model.ApiSuccessResponse
import com.lelezu.app.xianzhuan.data.model.ErrResponse
import com.lelezu.app.xianzhuan.data.model.ListData
import com.lelezu.app.xianzhuan.data.model.LoginReP
import com.lelezu.app.xianzhuan.data.model.Message
import com.lelezu.app.xianzhuan.utils.ShareUtil.cleanInfo
import com.lelezu.app.xianzhuan.utils.ShareUtil.saveInfo

/**
 * @author:Administrator
 * @date:2023/7/25 0025
 * @description:
 *
 */
open class BaseViewModel : ViewModel() {


    val errMessage: MutableLiveData<ErrResponse> = MutableLiveData() //接口返回的错误信息

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
                Log.i("BaseViewModel:", "ApiSuccessResponse")
                liveData.postValue(r.data)
                if (r.data is LoginReP) saveInfo(r.data as LoginReP)//如果返回对角为登录回应对象就保存
            }
            is ApiFailedResponse -> {
                // 处理失败的响应
                failedResponse(r, r.message)
            }
            is ApiEmptyResponse -> {
                // 处理空的响应
                failedResponse(r, "data为null!")
            }
            is ApiErrorResponse -> {
                // 处理错误的响应
                failedResponse(r, r.error!!.message)
            }
        }
    }
    protected fun <T> handleApiListResponse(
        r: ApiResponse<ListData<T>>, liveData: MutableLiveData<MutableList<T>>
    ) {
        when (r) {
            is ApiSuccessResponse -> {
                if (r.response.records.isEmpty()) {
                    // 处理空列表的情况
                    failedResponse(r, "没有更多了")
                } else {
                    // 处理非空列表的情况
                    liveData.postValue(r.response.records)
                }
            }

            is ApiFailedResponse -> {
                // 处理失败的响应
                failedResponse(r, r.message)
            }

            is ApiEmptyResponse -> {

                // 处理空的响应
                failedResponse(r, "data为null!")
            }

            is ApiErrorResponse -> {
                // 处理错误的响应
                failedResponse(r, r.error!!.message)
            }
        }
    }





    //Token失效
    private fun <T> failedResponse(r: ApiResponse<T>, mes: String?) {
        Log.i("BaseViewModel:", "r.code:${r.code},mes:${mes}")
        errMessage.postValue(ErrResponse(r.code, mes))
        if (r.data is LoginReP || r.isTokenLose) onLoginFailed()
    }

    private fun onLoginFailed() {
        cleanInfo()//如果返回对象为登录回应对象就删除之前的登录信息
    }

}