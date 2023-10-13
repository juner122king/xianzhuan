package com.lelezu.app.xianzhuan.data.repository


import com.lelezu.app.xianzhuan.data.ApiService
import com.lelezu.app.xianzhuan.data.model.ApiResponse
import com.lelezu.app.xianzhuan.data.model.ChatList
import com.lelezu.app.xianzhuan.data.model.ChatMessage
import com.lelezu.app.xianzhuan.data.model.Earning
import com.lelezu.app.xianzhuan.data.model.Follows
import com.lelezu.app.xianzhuan.data.model.ListData
import com.lelezu.app.xianzhuan.data.model.LoginConfig
import com.lelezu.app.xianzhuan.data.model.LoginInfo
import com.lelezu.app.xianzhuan.data.model.LoginReP
import com.lelezu.app.xianzhuan.data.model.Related
import com.lelezu.app.xianzhuan.data.model.UserInfo
import com.lelezu.app.xianzhuan.data.model.Vip
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

/**
 * @author:Administrator
 * @date:2023/7/21 0021
 * @description:与用户相关的数据
 *
 */
class UserRepository(private var apiService: ApiService) : BaseRepository() {

    //获取登录信息
    suspend fun apiLogin(loginInfo: LoginInfo): ApiResponse<LoginReP> =
        withContext(Dispatchers.IO) {
            val call = apiService.getLogin(loginInfo)
            executeApiCall(call)
        }

    //用户注册
    suspend fun apiRegister(register: String): ApiResponse<LoginReP> = withContext(Dispatchers.IO) {
        val call = apiService.register(register)
        executeApiCall(call)
    }


    //获取用户信息
    suspend fun apiUserInfo(userId: String): ApiResponse<UserInfo> = withContext(Dispatchers.IO) {
        val call = apiService.getUserInfo(userId, loginToken)
        executeApiCall(call)
    }

    //获取收徒收益
    suspend fun apiEarnings(): ApiResponse<Earning> = withContext(Dispatchers.IO) {
        val call = apiService.getEarnings(loginToken)
        executeApiCall(call)
    }

    //获取会员过期时间
    suspend fun vipRest(): ApiResponse<Vip> = withContext(Dispatchers.IO) {
        val call = apiService.vipRest(loginToken)
        executeApiCall(call)
    }

    //个人中心任务相关的数据
    suspend fun apiRelated(): ApiResponse<Related> = withContext(Dispatchers.IO) {
        val call = apiService.getRelated(loginToken)
        executeApiCall(call)
    }

    //关注和粉丝数
    suspend fun apiFollows(userid: String): ApiResponse<Follows> = withContext(Dispatchers.IO) {
        val call = apiService.follows(userid, loginToken)
        executeApiCall(call)
    }

    //用户退出登录
    suspend fun logout(): ApiResponse<Boolean> = withContext(Dispatchers.IO) {
        val call = apiService.logout(loginToken)
        executeApiCall(call)
    }

    //关注-取关
    suspend fun onFollows(userid: String): ApiResponse<Boolean> = withContext(Dispatchers.IO) {
        val call = apiService.onFollows(userid, loginToken)
        executeApiCall(call)
    }

    //获取登录配置
    suspend fun loginConfig(): ApiResponse<LoginConfig> = withContext(Dispatchers.IO) {
        val call = apiService.loginConfig()
        executeApiCall(call)
    }

    //获取联系人列表
    suspend fun apiContactors(): ApiResponse<List<ChatList>> = withContext(Dispatchers.IO) {
        val call = apiService.apiContactors(loginToken)
        executeApiCall(call)
    }

    //消息标记已读
    suspend fun isRecord(senderUserId: String): ApiResponse<Boolean> = withContext(Dispatchers.IO) {
        val call = apiService.isRecord(senderUserId, loginToken)
        executeApiCall(call)
    }

    //发送消息
    suspend fun sendRecord(
        receiveId: String, content: String, type: Int
    ): ApiResponse<ListData<ChatMessage>> = withContext(Dispatchers.IO) {
        val call = apiService.sendRecord(receiveId, content, type, loginToken)
        executeApiCall(call)
    }

    //获取分页聊天信息

    /**
     *
     * @param receiverUserId String 	收信人ID
     * @return ApiResponse<Related>
     */
    suspend fun apiRecord(receiverUserId: String): MutableList<ChatMessage> =
        withContext(Dispatchers.IO) {
            val call1 = apiService.getRecord(receiverUserId, 999, loginToken)
            val call2 = apiService.getRecord(loginId, 999, loginToken)

            val mergedData = mutableListOf<ChatMessage>()


            runBlocking {
                val response1 = GlobalScope.launch(Dispatchers.IO) {
                    val response = call1.execute()
                    if (response.isSuccessful) {
                        val data = response.body()?.data?.records
                        data?.let {
                            mergedData.addAll(it)
                        }
                    }
                }

                val response2 = GlobalScope.launch(Dispatchers.IO) {
                    val response = call2.execute()
                    if (response.isSuccessful) {
                        val data = response.body()?.data?.records
                        data?.let {
                            mergedData.addAll(it)
                        }
                    }
                }

                // 等待两个请求都完成
                response1.join()
                response2.join()

                // 在这里 mergedChatMessages 将包含合并后的聊天消息

                putChatMessages(mergedData)
            }


        }

    private fun putChatMessages(dataList: MutableList<ChatMessage>): MutableList<ChatMessage> {
        var chatMessages = mutableListOf<ChatMessage>()

        for (message in dataList) {
            // 根据 senderUserId 是否等于 UserID 设置 isMe 的值
            message.isMe = message.senderUserId == loginId
            chatMessages.add(message)
        }

        return chatMessages
    }

}