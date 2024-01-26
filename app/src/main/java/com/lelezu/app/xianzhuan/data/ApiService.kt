package com.lelezu.app.xianzhuan.data

import com.lelezu.app.xianzhuan.data.model.Announce
import com.lelezu.app.xianzhuan.data.model.ApiResponse
import com.lelezu.app.xianzhuan.data.model.DBanner
import com.lelezu.app.xianzhuan.data.model.ChatList
import com.lelezu.app.xianzhuan.data.model.ChatMessage
import com.lelezu.app.xianzhuan.data.model.Complete
import com.lelezu.app.xianzhuan.data.model.Config
import com.lelezu.app.xianzhuan.data.model.Earning
import com.lelezu.app.xianzhuan.data.model.Follows
import com.lelezu.app.xianzhuan.data.model.ListData
import com.lelezu.app.xianzhuan.data.model.LoginConfig
import com.lelezu.app.xianzhuan.data.model.LoginInfo
import com.lelezu.app.xianzhuan.data.model.LoginReP
import com.lelezu.app.xianzhuan.data.model.Message
import com.lelezu.app.xianzhuan.data.model.Partner
import com.lelezu.app.xianzhuan.data.model.Pending
import com.lelezu.app.xianzhuan.data.model.Recharge
import com.lelezu.app.xianzhuan.data.model.RechargeRes
import com.lelezu.app.xianzhuan.data.model.Related
import com.lelezu.app.xianzhuan.data.model.Req
import com.lelezu.app.xianzhuan.data.model.Req2
import com.lelezu.app.xianzhuan.data.model.Task
import com.lelezu.app.xianzhuan.data.model.TaskSubmit
import com.lelezu.app.xianzhuan.data.model.TaskType
import com.lelezu.app.xianzhuan.data.model.UserInfo
import com.lelezu.app.xianzhuan.data.model.Version
import com.lelezu.app.xianzhuan.data.model.Vip
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap

//API 接口
interface ApiService {


    @POST("/dxz/app/user/register")  //用户注册
    fun register(@Query("encryptStr") encryptStr: String): Call<ApiResponse<LoginReP>>

    @POST("/dxz/app/user/login")  //用户登录
    fun getLogin(
        @Body loginInfo: LoginInfo, @Header("Device") device: String,
    ): Call<ApiResponse<LoginReP>>

    @GET("/dxz/app/user/info")  //用户信息
    fun getUserInfo(
        @Query("userId") userId: String,
        @Header("Authorization") token: String,
        @Header("Device") device: String,
    ): Call<ApiResponse<UserInfo>>

    @GET("/dxz/app/user/receive/apprentice/earnings")  //收徒收益
    fun getEarnings(
        @Header("Authorization") token: String, @Header("Device") device: String,
    ): Call<ApiResponse<Earning>>

    @GET("/dxz/app/user/task/related")  //个人中心任务相关的数据
    fun getRelated(
        @Header("Authorization") token: String, @Header("Device") device: String,
    ): Call<ApiResponse<Related>>


    @POST("/dxz/app/user/contact/record")  //发送消息
    fun sendRecord(
        @Query("receiveId") receiveId: String,
        @Query("content") content: String,
        @Query("type") type: Int = 0,
        @Header("Authorization") token: String,
        @Header("Device") device: String,
    ): Call<ApiResponse<ListData<ChatMessage>>>

    @GET("/dxz/app/user/contact/record")  //获取分页聊天信息
    fun getRecord(
        @Query("receiverUserId") receiverUserId: String,
        @Query("size") size: Int? = null,
        @Header("Authorization") token: String,
        @Header("Device") device: String,
    ): Call<ApiResponse<ListData<ChatMessage>>>


    /**
     *
     * @param senderUserId String 消息发送人ID
     * @param token String
     * @return Call<ApiResponse<ListData<ChatMessage>>>
     */
    @PUT("/dxz/app/user/contact/record")  //消息标记已读
    fun isRecord(
        @Query("senderUserId") senderUserId: String,
        @Header("Authorization") token: String,
        @Header("Device") device: String,
    ): Call<ApiResponse<Boolean>>


    @GET("/dxz/app/task/page/details")//获取任务详情
    fun getTaskInfo(
        @Query("taskId") id: String,
        @Query("applyLogId") applyLogId: String? = null,
        @Header("Authorization") token: String,
        @Header("Device") device: String,
    ): Call<ApiResponse<Task>>



    @POST("/dxz/app/task/user/apply")//任务报名
    fun taskApply(
        @Body req: Req, @Header("Authorization") token: String, @Header("Device") device: String,
    ): Call<ApiResponse<Boolean>>


    @POST("/dxz/app/task/user/cancel/{applyLogId}")//任务取消
    fun taskCancel(
        @Path("applyLogId") applyLogId: String, @Header("Authorization") token: String, @Header("Device") device: String,
    ): Call<ApiResponse<Boolean>>



    /**
     * //获取任务列表
     */
    @GET("/dxz/app/task/page")
    fun getTaskList(
        @QueryMap params: Map<String, String?>,
        @Header("Authorization") token: String,
        @Header("Device") device: String,
    ): Call<ApiResponse<ListData<Task>>>


    /**
     * 获取我的任务列表
     */
    @GET("/dxz/app/task/mine/apply/page")
    fun getMyTaskList(
        @QueryMap params: Map<String, String?>,
        @Header("Authorization") token: String,
        @Header("Device") device: String,
    ): Call<ApiResponse<ListData<Task>>>


    @GET("/dxz/app/task/type/options")//获取任务类型列表
    fun getTaskTypeList(
        @Header("Authorization") token: String, @Header("Device") device: String,
    ): Call<ApiResponse<List<TaskType>>>

    @GET("/dxz/app/task/shuffle")//随机为用户推荐3个任务
    fun shuffle(
        @Header("Authorization") token: String, @Header("Device") device: String,
    ): Call<ApiResponse<MutableList<Task>>>


    @GET("/dxz/app/task/mine/store/page")//获取雇主发布的任务
    fun masterTask(
        @Query("queryCond") queryCond: String,
        @Query("taskStatus") taskStatus: String,
        @Query("userId") userId: String,
        @Header("Authorization") token: String,
        @Query("taskTypeId") taskTypeId: String,
        @Header("Device") device: String,
    ): Call<ApiResponse<ListData<Task>>>


    @GET("/dxz/app/sys/inform/page")//获取系统消息
    fun getSysMessageList(
        @Query("current") current: Int,
        @Query("size") size: Int,
        @Header("Authorization") token: String,
        @Header("Device") device: String,
    ): Call<ApiResponse<ListData<Message>>>

    @GET("/dxz/app/sys/inform/count/unread")//获取用户未读信息数量
    fun getSysMessageNum(
        @Header("Authorization") token: String, @Header("Device") device: String,
    ): Call<ApiResponse<Int>>

    @POST("/dxz/app/sys/inform/mark/read")//标记已读系统消息
    fun getMarkSysMessage(
        @Body msgIds: List<String>,
        @Header("Authorization") token: String,
        @Header("Device") device: String,
    ): Call<ApiResponse<Boolean>>


    @PUT("/dxz/app/user/account/cancel/confirm/{informId}/{status}")//用户确认-取消注销账户
    fun markLogout(
        @Path("informId") informId: String,
        @Path("status") status: String,
        @Header("Authorization") token: String,
        @Header("Device") device: String,
    ): Call<ApiResponse<Boolean>>

    @GET("/dxz/app/sys/config")//获取系统配置信息
    fun getConfig(
        @Path("confType") confType: String,
        @Path("configKey") configKey: String,
        @Header("Authorization") token: String,
        @Header("Device") device: String,
    ): Call<ApiResponse<Config>>


    @GET("/dxz/app/announce")//公告
    fun getAnnounce(
        @Header("Authorization") token: String, @Header("Device") device: String,
    ): Call<ApiResponse<List<Announce>>>

    @GET("/dxz/app/user/withdraw/success/data")//主页提现公告
    fun getUserWithdraw(
        @Header("Authorization") token: String, @Header("Device") device: String,
    ): Call<ApiResponse<List<String>>>


    @POST("/dxz/app/task/submit")//用户提交任务
    fun taskSubmit(
        @Body taskSubmit: TaskSubmit,
        @Header("Authorization") token: String,
        @Header("Device") device: String,
    ): Call<ApiResponse<Boolean>>


    @POST("/dxz/app/task/submit/long/task")//用户提交长单任务
    fun longTaskSubmit(
        @Body taskSubmit: TaskSubmit,
        @Header("Authorization") token: String,
        @Header("Device") device: String,
    ): Call<ApiResponse<Boolean>>

    @POST("/dxz/app/task/complete")//小程序任务完成校验
    fun miniTaskComplete(
        @Body applyLogId: Complete,
        @Header("Authorization") token: String,
        @Header("Device") device: String,
    ): Call<ApiResponse<Boolean>>


    @GET("/dxz/app/sys/config/apk/update/detection")//新版本检测
    fun detection(
        @Query("versionCode") versionCode: String,
        @Query("versionName") versionName: String,
        @Header("Authorization") token: String,
        @Header("Device") device: String,
    ): Call<ApiResponse<Version>>


    @Multipart
    @POST("/dxz/app/sys/file/image/upload")//上传图片
    fun upload(
        @Part image: MultipartBody.Part,
        @Header("Authorization") token: String,
        @Header("Device") device: String,
    ): Call<ApiResponse<String>>

    @GET("/dxz/app/sys/config/SYSTEM/LOGIN_CONFIG")//登录页面控制
    fun loginConfig(): Call<ApiResponse<LoginConfig>>

    @GET("/dxz/app/user/contact/record/contactors")//联系人列表
    fun apiContactors(
        @Header("Authorization") token: String, @Header("Device") device: String,
    ): Call<ApiResponse<List<ChatList>>>


    @GET("/dxz/app/user/follows")//关注和粉丝数
    fun follows(
        @Query("userId") userId: String,
        @Header("Authorization") token: String,
        @Header("Device") device: String,
    ): Call<ApiResponse<Follows>>

    @POST("/dxz/app/user/follows")//关注-取关
    fun onFollows(
        @Query("userId") userId: String,
        @Header("Authorization") token: String,
        @Header("Device") device: String,
    ): Call<ApiResponse<Boolean>>

    @POST("/dxz/app/user/logout")//退出登录
    fun logout(
        @Header("Authorization") token: String, @Header("Device") device: String,
    ): Call<ApiResponse<Boolean>>


    @POST("/dxz/app/user/recharge")//用户充值
    fun recharge(
        @Body recharge: Recharge,
        @Header("Authorization") token: String,
        @Header("Device") device: String,
    ): Call<ApiResponse<RechargeRes>>


    @POST("/dxz/app/user/bind/master")// 绑定师傅
    fun bindMaster(
        @Query("userId") userId: String,
        @Header("Authorization") token: String,
        @Header("Device") device: String,
    ): Call<ApiResponse<Boolean>>

    @GET("/dxz/app/partner/back")// 合伙人后台
    fun partnerBack(
        @Header("Authorization") token: String, @Header("Device") device: String,
    ): Call<ApiResponse<Partner>>

    @GET("/dxz/app/partner/list")// 合伙人后台结算记录
    fun partnerBackList(
        @Header("Authorization") token: String, @Header("Device") device: String,
    ): Call<ApiResponse<ListData<Partner>>>

    @GET("/dxz/app/partner/apprentice/list")// 合伙人团队
    fun partnerTeamList(
        @Header("Authorization") token: String, @Header("Device") device: String,
    ): Call<ApiResponse<ListData<Partner>>>


    @GET("/dxz/app/sys/config/SYSTEM/REGISTRY_CONCERN_CONFIG")// 关注企业微信配置信息
    fun REGISTR_CONFIG(
        @Header("Authorization") token: String,
        @Header("Device") device: String,

        ): Call<ApiResponse<Config>>

    @GET("/dxz/app/sys/config/ACTIVITY/CAROUSEL")// 首页轮播图
    fun CAROUSEL(
        @Header("Authorization") token: String, @Header("Device") device: String,
    ): Call<ApiResponse<Config>>


    @GET("/dxz/app/sys/banner")// 首页轮播图2  新方法
    fun getBanner(
        @Header("Authorization") token: String, @Header("Device") device: String,
    ): Call<ApiResponse<List<DBanner>>>


    @GET("/dxz/app/sys/config/SYSTEM/AD")// 广告配置信息
    fun apiADConfig(
        @Header("Authorization") token: String, @Header("Device") device: String,
    ): Call<ApiResponse<Config>>


    @GET("/dxz/app/user/pending/data")// 待处理消息
    fun apiPending(
        @Header("Authorization") token: String, @Header("Device") device: String,
    ): Call<ApiResponse<Pending>>

    @GET("/dxz/app/user/vip/rest")// 会员过期时间
    fun vipRest(
        @Header("Authorization") token: String, @Header("Device") device: String,
    ): Call<ApiResponse<Vip>>

}