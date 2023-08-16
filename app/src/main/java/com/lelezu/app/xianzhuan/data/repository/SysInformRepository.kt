package com.lelezu.app.xianzhuan.data.repository

import android.content.Context.MODE_PRIVATE
import android.util.Log
import com.lelezu.app.xianzhuan.MyApplication
import com.lelezu.app.xianzhuan.data.ApiService
import com.lelezu.app.xianzhuan.data.model.Announce
import com.lelezu.app.xianzhuan.data.model.ApiResponse
import com.lelezu.app.xianzhuan.data.model.Config
import com.lelezu.app.xianzhuan.data.model.ListData
import com.lelezu.app.xianzhuan.data.model.LoginInfo
import com.lelezu.app.xianzhuan.data.model.LoginReP
import com.lelezu.app.xianzhuan.data.model.Message
import com.lelezu.app.xianzhuan.utils.ShareUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * @author:Administrator
 * @date:2023/7/25 0021
 * @description:与系统消息相关的数据
 *
 */
class SysInformRepository(private var apiService: ApiService) : BaseRepository() {

    //获取系统消息列表
    suspend fun apiGetList(current: Int, size: Int): ApiResponse<ListData<Message>> =
        withContext(Dispatchers.IO) {
            val call = apiService.getSysMessageList(
                current, size, ShareUtil.getString(ShareUtil.APP_SHARED_PREFERENCES_LOGIN_TOKEN)
            )
            executeApiCall(call)
        }


    //公告
    suspend fun apiAnnounce(): ApiResponse<List<Announce>> = withContext(Dispatchers.IO) {
        val call = apiService.getAnnounce(
            ShareUtil.getString(ShareUtil.APP_SHARED_PREFERENCES_LOGIN_TOKEN)
        )
        executeApiCall(call)
    }


    //标记已读系统消息
    suspend fun markSysMessage(msgIds: List<String>): ApiResponse<Boolean> =
        withContext(Dispatchers.IO) {
            val call = apiService.getMarkSysMessage(
                msgIds, ShareUtil.getString(ShareUtil.APP_SHARED_PREFERENCES_LOGIN_TOKEN)
            )
            executeApiCall(call)
        }


    //获取用户未读信息数量
    suspend fun getSysMessageNum(): ApiResponse<Int> = withContext(Dispatchers.IO) {
        val call = apiService.getSysMessageNum(
            ShareUtil.getString(ShareUtil.APP_SHARED_PREFERENCES_LOGIN_TOKEN)
        )
        executeApiCall(call)
    }


    //获取系统配置信息
    suspend fun apiConfig(confType: String, configKey: String): ApiResponse<Config> =
        withContext(Dispatchers.IO) {
            val call = apiService.getConfig(
                confType,
                configKey,
                ShareUtil.getString(ShareUtil.APP_SHARED_PREFERENCES_LOGIN_TOKEN)
            )
            executeApiCall(call)
        }

}