package com.lelezu.app.xianzhuan.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lelezu.app.xianzhuan.data.ApiConstants
import com.lelezu.app.xianzhuan.data.ApiService
import com.lelezu.app.xianzhuan.data.model.ApiResponse
import com.lelezu.app.xianzhuan.data.model.ListData
import com.lelezu.app.xianzhuan.data.model.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * @author:Administrator
 * @date:2023/7/20 0020
 * @description:
 *
 */
class HomeViewModel : ViewModel() {
    private val apiService: ApiService

    //初始化
    init {
        val retrofit = Retrofit.Builder().baseUrl(ApiConstants.HOST)
            .addConverterFactory(GsonConverterFactory.create())
            .client(OkHttpClient.Builder().addInterceptor { chain ->
                val originalRequest = chain.request()
                val newRequest = originalRequest.newBuilder()
                    .header("Authorization", "get")
                    .build()
                chain.proceed(newRequest)
            }.build()).build()
        apiService = retrofit.create(ApiService::class.java)
    }

    // 定义一个 MutableLiveData 来保存任务列表
    private val _taskList = MutableLiveData<List<Task>>()
    val taskList: LiveData<List<Task>> get() = _taskList

    fun fetchDataFromApi() {
        viewModelScope.launch {
            // 在 IO 线程中异步获取数据
            val taskList = withContext(Dispatchers.IO) {
                // 这里模拟异步获取数据的操作
                delay(1000)
                listOf(
                    Task("数据1"),
                    // 添加更多项...
                )
            }
            // 数据获取完成后，将数据设置到 MutableLiveData 中
            _taskList.value = taskList
        }
    }

    // 获取任务列表数据
    fun getTaskList() {
        val call = apiService.getTaskList()
        call.enqueue(object : Callback<ApiResponse<ListData>> {
            override fun onResponse(
                call: Call<ApiResponse<ListData>>, response: Response<ApiResponse<ListData>>
            ) {
                if (response.isSuccessful) {
                    val listData = response.body()?.data
                    val itemList = listData?.records ?: emptyList()
                    _taskList.value = itemList
                } else {
                    // Handle error response
                }
            }

            override fun onFailure(call: Call<ApiResponse<ListData>>, t: Throwable) {
                // Handle network error
            }
        })
    }

}