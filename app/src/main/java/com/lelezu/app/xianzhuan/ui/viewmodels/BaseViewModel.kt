package com.lelezu.app.xianzhuan.ui.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lelezu.app.xianzhuan.data.model.ApiEmptyResponse
import com.lelezu.app.xianzhuan.data.model.ApiErrorResponse
import com.lelezu.app.xianzhuan.data.model.ApiFailedResponse
import com.lelezu.app.xianzhuan.data.model.ApiResponse
import com.lelezu.app.xianzhuan.data.model.ApiSuccessResponse
import com.lelezu.app.xianzhuan.data.model.ErrResponse
import com.lelezu.app.xianzhuan.data.model.ListData
import com.lelezu.app.xianzhuan.data.model.LoginReP
import com.lelezu.app.xianzhuan.utils.LogUtils
import com.lelezu.app.xianzhuan.utils.ShareUtil.cleanInfo
import com.lelezu.app.xianzhuan.utils.ShareUtil.isConnected
import com.lelezu.app.xianzhuan.utils.ShareUtil.saveInfo

/**
 * @author:Administrator
 * @date:2023/7/25 0025
 * @description:
 *
 */
open class BaseViewModel : ViewModel() {


    val errMessage: MutableLiveData<ErrResponse> = MutableLiveData() //接口返回的错误信息

    val emptyListMessage: MutableLiveData<Boolean> = MutableLiveData() //接口列表数据为空的错误信息

    protected fun <T> handleApiResponse(
        r: ApiResponse<T>,
        liveData: MutableLiveData<T>,
        isPost: Boolean = true,
    ) {
        when (r) {
            is ApiSuccessResponse -> {
                // 处理成功的响应
                if (isPost)
                    liveData.postValue(r.data)
                else
                    liveData.value = r.data
                if (r.data is LoginReP) saveInfo(r.data as LoginReP)//如果返回对象为登录回应对象就保存
            }

            is ApiFailedResponse -> {
                // 处理失败的响应
                failedResponse(r, r.message)
            }

            is ApiEmptyResponse -> {
                // 处理空的响应
                failedResponse(r, "获取数据异常，请稍候再试。!")
            }

            is ApiErrorResponse -> {
                // 处理错误的响应
//                failedResponse(r, r.error!!.message)
            }
        }
    }

    protected fun <T> handleApiListResponse(
        r: ApiResponse<ListData<T>>,
        liveData: MutableLiveData<MutableList<T>>,
    ) {
        when (r) {
            is ApiSuccessResponse -> {
                if (r.response.records.isEmpty()) {
                    // 处理空列表的情况
                    emptyListMessage.postValue(true)
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
                failedResponse(r, "获取数据异常，请稍候再试!")
            }

            is ApiErrorResponse -> {
                // 处理错误的响应
//                failedResponse(r, r.error!!.message)
            }
        }
    }


    private fun <T> failedResponse(r: ApiResponse<T>, mes: String?) {

        LogUtils.i("333", r.toString())
        if (isConnected()) {

            errMessage.postValue(ErrResponse(r.code, mes))//有网络，正常报错

        } else errMessage.postValue(ErrResponse(null, "网络未连接"))//无网络，提示网络未连接

        if (r.data is LoginReP || r.isTokenLose) onLoginFailed()
    }


    private fun onLoginFailed() {
        cleanInfo()//如果返回对象为登录回应对象就删除之前的登录信息
    }

}