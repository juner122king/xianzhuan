package com.lelezu.app.xianzhuan.data

import com.lelezu.app.xianzhuan.data.model.Announce
import com.lelezu.app.xianzhuan.data.model.ApiResponse
import com.lelezu.app.xianzhuan.data.model.ChatMessage
import com.lelezu.app.xianzhuan.data.model.Config
import com.lelezu.app.xianzhuan.data.model.Earning
import com.lelezu.app.xianzhuan.data.model.ListData
import com.lelezu.app.xianzhuan.data.model.LoginInfo
import com.lelezu.app.xianzhuan.data.model.LoginReP
import com.lelezu.app.xianzhuan.data.model.Message
import com.lelezu.app.xianzhuan.data.model.Related
import com.lelezu.app.xianzhuan.data.model.Req
import com.lelezu.app.xianzhuan.data.model.Task
import com.lelezu.app.xianzhuan.data.model.TaskSubmit
import com.lelezu.app.xianzhuan.data.model.TaskType
import com.lelezu.app.xianzhuan.data.model.UserInfo
import com.lelezu.app.xianzhuan.data.model.Version
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
import retrofit2.http.QueryMap

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


    @POST("/dxz/app/user/contact/record")  //发送消息
    fun sendRecord(
        @Query("receiveId") receiveId: String,
        @Query("content") content: String,
        @Query("isImage") isImage: Boolean = false,
        @Header("Authorization") token: String
    ): Call<ApiResponse<ListData<ChatMessage>>>

    @GET("/dxz/app/user/contact/record")  //获取分页聊天信息
    fun getRecord(
        @Query("receiverUserId") receiverUserId: String,
        @Query("size") size: Int? = null,
        @Header("Authorization") token: String
    ): Call<ApiResponse<ListData<ChatMessage>>>


    @GET("/dxz/app/task/page/details/")//获取任务详情
    fun getTaskInfo(
        @Query("taskId") id: String,
        @Query("applyLogId") applyLogId: String? = null,
        @Header("Authorization") token: String
    ): Call<ApiResponse<Task>>

    @POST("/dxz/app/task/user/apply")//任务报名
    fun taskApply(
        @Body req: Req, @Header("Authorization") token: String
    ): Call<ApiResponse<Boolean>>

    /**
     * //获取任务列表
     */
    @GET("/dxz/app/task/page")
    fun getTaskList(
        @QueryMap params: Map<String, String?>, @Header("Authorization") token: String
    ): Call<ApiResponse<ListData<Task>>>


    /**
     * //获取我的任务列表
     */
    @GET("/dxz/app/task/mine/apply/page")
    fun getMyTaskList(
        @QueryMap params: Map<String, String?>, @Header("Authorization") token: String
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
        @Body msgIds: List<String>, @Header("Authorization") token: String
    ): Call<ApiResponse<Boolean>>

    @GET("/dxz/app/sys/config")//获取系统配置信息
    fun getConfig(
        @Path("confType") confType: String,
        @Path("configKey") configKey: String,
        @Header("Authorization") token: String
    ): Call<ApiResponse<Config>>


    @GET("/dxz/app/announce")//公告
    fun getAnnounce(
        @Header("Authorization") token: String
    ): Call<ApiResponse<List<Announce>>>


    @POST("/dxz/app/task/submit")//用户提交任务
    fun taskSubmit(
        @Body taskSubmit: TaskSubmit, @Header("Authorization") token: String
    ): Call<ApiResponse<Boolean>>


    @GET("/dxz/app/sys/config/apk/update/detection")//新版本检测
    fun detection(
        @Query("versionCode") versionCode: String,
        @Query("versionName") versionName: String,
        @Header("Authorization") token: String
    ): Call<ApiResponse<Version>>


    @Multipart
    @POST("/dxz/app/sys/file/image/upload")//上传图片
    fun upload(
        @Part image: MultipartBody.Part, @Header("Authorization") token: String
    ): Call<ApiResponse<String>>


}