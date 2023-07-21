package com.lelezu.app.xianzhuan.ui.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lelezu.app.xianzhuan.MyApplication
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
import okhttp3.logging.HttpLoggingInterceptor
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
        val token = MyApplication.context?.getSharedPreferences("ApiPrefs", Context.MODE_PRIVATE)
            ?.getString("LoginToken", "")


        //Log查看过滤器，上线版去掉
        val logInterceptor = HttpLoggingInterceptor()
        logInterceptor.level = HttpLoggingInterceptor.Level.BODY
        logInterceptor.level = HttpLoggingInterceptor.Level.HEADERS

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(logInterceptor)
            .addInterceptor { chain ->
            val originalRequest = chain.request()
            val newRequest = originalRequest.newBuilder().header("Authorization", token!!).build()
            chain.proceed(newRequest)
        }.build()

        val retrofit = Retrofit.Builder().baseUrl(ApiConstants.HOST)
            .addConverterFactory(GsonConverterFactory.create()).client(okHttpClient).build()
        apiService = retrofit.create(ApiService::class.java)
    }

    // 定义一个 MutableLiveData 来保存任务列表
    private val _taskList = MutableLiveData<List<Task>>()
    val taskList: LiveData<List<Task>> get() = _taskList

    // 获取任务列表数据
    fun getTaskList() {
        val call = apiService.getTaskList("TOP")
        call.enqueue(object : Callback<ApiResponse<ListData>> {
            override fun onResponse(
                call: Call<ApiResponse<ListData>>, response: Response<ApiResponse<ListData>>
            ) {

                Log.d(
                    "APP获取任务列表接口task()",
                    "  ${response.body()?.code}: ${response.body()?.message}"
                )

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