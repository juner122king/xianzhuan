package com.lelezu.app.xianzhuan.data

import com.lelezu.app.xianzhuan.data.model.ApiResponse
import com.lelezu.app.xianzhuan.data.model.ListData
import com.lelezu.app.xianzhuan.data.model.LoginInfo
import com.lelezu.app.xianzhuan.data.model.LoginReP
import com.lelezu.app.xianzhuan.data.model.Task
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

//API 接口
interface ApiService {


    @POST("/dxz/app/user/login")  //用户登录
    fun getLogin(@Body loginInfo: LoginInfo): Call<ApiResponse<LoginReP>>

    @GET("/dxz/app/task/page/details/{taskId}")//获取任务详情
    fun getTaskInfo(@Path("taskId") id: String): Call<ApiResponse<Task>>

    @GET("/dxz/app/task/page")//获取任务列表
    fun getTaskList(
        @Query("current") current: Int = 0,
        @Query("highPrice") highPrice: Float = 0.0f,
        @Query("lowPrice") lowPrice: Float = 0.0f,
        @Query("queryCond") queryCond: String = "",
        @Query("size") size: Int = 0,
        @Query("taskStatus") taskStatus: Int = 0,
        @Query("taskTypeId") taskTypeId: String = ""
    ): Call<ApiResponse<ListData>>


}