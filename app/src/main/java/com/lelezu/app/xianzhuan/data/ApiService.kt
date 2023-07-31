package com.lelezu.app.xianzhuan.data

import com.lelezu.app.xianzhuan.data.model.ApiResponse
import com.lelezu.app.xianzhuan.data.model.Config
import com.lelezu.app.xianzhuan.data.model.ListData
import com.lelezu.app.xianzhuan.data.model.LoginInfo
import com.lelezu.app.xianzhuan.data.model.LoginReP
import com.lelezu.app.xianzhuan.data.model.Message
import com.lelezu.app.xianzhuan.data.model.Register
import com.lelezu.app.xianzhuan.data.model.Req
import com.lelezu.app.xianzhuan.data.model.Task
import com.lelezu.app.xianzhuan.data.model.TaskType
import com.lelezu.app.xianzhuan.data.model.UserInfo
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

//API 接口
interface ApiService {


    @POST("/dxz/app/user/register")  //用户注册
    fun register(@Body register: Register): Call<ApiResponse<LoginReP>>

    @POST("/dxz/app/user/login")  //用户登录
    fun getLogin(@Body loginInfo: LoginInfo): Call<ApiResponse<LoginReP>>

    @GET("/dxz/app/user/info")  //用户信息
    fun getUserInfo( @Query("userId") userId: String): Call<ApiResponse<UserInfo>>

    @GET("/dxz/app/task/page/details/{taskId}")//获取任务详情
    fun getTaskInfo(@Path("taskId") id: String): Call<ApiResponse<Task>>

    @GET("/dxz/app/task/user/apply")//任务报名
    fun taskApply(@Body req: Req): Call<ApiResponse<Boolean>>

    /**
     * //获取任务列表
     * @param current Int      	当前页，默认第1页
     * @param highPrice Float   最高价
     * @param lowPrice Float   最低价
     * @param queryCond String  	任务查询条件,说明:TOP-置顶, SIMPLE-简单, HIGHER-高价, LATEST-最新, COMBO-组合(lowPrice、highPrice 和 taskTypeId 必传),可用值:TOP,SIMPLE,HIGHER,LATEST,COMBO
     * @param size Int  	页大小，默认每页15条
     * @param taskStatus Int 	任务状态
     * @param taskTypeId String 任务类型ID
     * @return Call<ApiResponse<ListData>>
     */
    @GET("/dxz/app/task/page")
    fun getTaskList(
        @Query("queryCond") queryCond: String,
        @Query("current") current: Int?,
        @Query("highPrice") highPrice: Float?,
        @Query("lowPrice") lowPrice: Float?,
        @Query("size") size: Int?,
        @Query("taskStatus") taskStatus: Int?,
        @Query("taskTypeId") taskTypeId: String?
    ): Call<ApiResponse<ListData<Task>>>

    @GET("/dxz/app/task/type/options")//获取任务类型列表
    fun getTaskTypeList(): Call<ApiResponse<List<TaskType>>>
    @GET("/dxz/app/task/shuffle")//随机为用户推荐3个任务
    fun shuffle(): Call<ApiResponse<List<Task>>>


    @GET("/dxz/app/sys/inform/page")//获取系统消息
    fun getSysMessageList(
        @Query("current") current: Int, @Query("size") size: Int
    ): Call<ApiResponse<ListData<Message>>>


    @GET("/dxz/app/sys/config")//获取系统配置信息
    fun getConfig(
        @Path("confType") confType: String, @Path("configKey") configKey: String
    ): Call<ApiResponse<Config>>


}