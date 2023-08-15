package com.lelezu.app.xianzhuan.data

import com.lelezu.app.xianzhuan.data.model.ApiResponse
import com.lelezu.app.xianzhuan.data.model.Config
import com.lelezu.app.xianzhuan.data.model.Earning
import com.lelezu.app.xianzhuan.data.model.ListData
import com.lelezu.app.xianzhuan.data.model.LoginInfo
import com.lelezu.app.xianzhuan.data.model.LoginReP
import com.lelezu.app.xianzhuan.data.model.Message
import com.lelezu.app.xianzhuan.data.model.Register
import com.lelezu.app.xianzhuan.data.model.Related
import com.lelezu.app.xianzhuan.data.model.Req
import com.lelezu.app.xianzhuan.data.model.Task
import com.lelezu.app.xianzhuan.data.model.TaskSubmit
import com.lelezu.app.xianzhuan.data.model.TaskType
import com.lelezu.app.xianzhuan.data.model.UserInfo
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

//API 接口
interface ApiService {


    @POST("/dxz/app/user/register")  //用户注册
    fun register(@Query("encryptStr") encryptStr: String): Call<ApiResponse<LoginReP>>

    @POST("/dxz/app/user/login")  //用户登录
    fun getLogin(@Body loginInfo: LoginInfo): Call<ApiResponse<LoginReP>>

    @GET("/dxz/app/user/info")  //用户信息
    fun getUserInfo(
        @Query("userId") userId: String, @Header("Authorization") token: String
    ): Call<ApiResponse<UserInfo>>

    @GET("/dxz/app/user/receive/apprentice/earnings")  //收徒收益
    fun getEarnings(@Header("Authorization") token: String): Call<ApiResponse<Earning>>

    @GET("/dxz/app/user/task/related")  //个人中心任务相关的数据
    fun getRelated(@Header("Authorization") token: String): Call<ApiResponse<Related>>


    @GET("/dxz/app/task/page/details/{taskId}")//获取任务详情
    fun getTaskInfo(
        @Path("taskId") id: String, @Header("Authorization") token: String
    ): Call<ApiResponse<Task>>

    @POST("/dxz/app/task/user/apply")//任务报名
    fun taskApply(
        @Body req: Req, @Header("Authorization") token: String
    ): Call<ApiResponse<Boolean>>

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
        @Query("queryCond") queryCond: String?,
        @Query("current") current: Int?,
        @Query("highPrice") highPrice: Float?,
        @Query("lowPrice") lowPrice: Float?,
        @Query("size") size: Int?,
        @Query("taskStatus") taskStatus: Int?,
        @Query("taskTypeId") taskTypeId: String?,
        @Header("Authorization") token: String
    ): Call<ApiResponse<ListData<Task>>>

    /**
     * //获取我的任务列表
     * @param current Int      	当前页，默认第1页
     * @param highPrice Float   最高价
     * @param lowPrice Float   最低价
     * @param queryCond String  	任务查询条件,说明:TOP-置顶, SIMPLE-简单, HIGHER-高价, LATEST-最新, COMBO-组合(lowPrice、highPrice 和 taskTypeId 必传),可用值:TOP,SIMPLE,HIGHER,LATEST,COMBO
     * @param size Int  	页大小，默认每页15条
     * @param taskStatus Int 	任务状态
     * @param taskTypeId String 任务类型ID
     * @return Call<ApiResponse<ListData>>
     */
    @GET("/dxz/app/task/mine/apply/page")
    fun getMyTaskList(
        @Query("queryCond") queryCond: String?,
        @Query("current") current: Int?,
        @Query("highPrice") highPrice: Float?,
        @Query("lowPrice") lowPrice: Float?,
        @Query("size") size: Int?,
        @Query("taskStatus") taskStatus: Int?,
        @Query("taskTypeId") taskTypeId: String?,
        @Header("Authorization") token: String
    ): Call<ApiResponse<ListData<Task>>>

    @GET("/dxz/app/task/type/options")//获取任务类型列表
    fun getTaskTypeList(@Header("Authorization") token: String): Call<ApiResponse<List<TaskType>>>

    @GET("/dxz/app/task/shuffle")//随机为用户推荐3个任务
    fun shuffle(@Header("Authorization") token: String): Call<ApiResponse<MutableList<Task>>>


    @GET("/dxz/app/sys/inform/page")//获取系统消息
    fun getSysMessageList(
        @Query("current") current: Int,
        @Query("size") size: Int,
        @Header("Authorization") token: String
    ): Call<ApiResponse<ListData<Message>>>

    @GET("/dxz/app/sys/inform/count/unread")//获取用户未读信息数量
    fun getSysMessageNum(
        @Header("Authorization") token: String
    ): Call<ApiResponse<Int>>

    @POST("/dxz/app/sys/inform/mark/read")//标记已读系统消息
    fun getMarkSysMessage(
        @Query("msgIds") msgId: List<String>, @Header("Authorization") token: String
    ): Call<ApiResponse<Boolean>>

    @GET("/dxz/app/sys/config")//获取系统配置信息
    fun getConfig(
        @Path("confType") confType: String,
        @Path("configKey") configKey: String,
        @Header("Authorization") token: String
    ): Call<ApiResponse<Config>>


    @POST("/dxz/app/task/submit")//用户提交任务
    fun taskSubmit(
        @Body taskSubmit: TaskSubmit, @Header("Authorization") token: String
    ): Call<ApiResponse<Boolean>>

    @Multipart
    @POST("/dxz/app/sys/file/image/upload")//上传图片
    fun upload(
        @Part image: MultipartBody.Part, @Header("Authorization") token: String
    ): Call<ApiResponse<String>>


}