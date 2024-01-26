package com.lelezu.app.xianzhuan.data.repository

import com.lelezu.app.xianzhuan.data.ApiService
import com.lelezu.app.xianzhuan.data.model.Announce
import com.lelezu.app.xianzhuan.data.model.ApiResponse
import com.lelezu.app.xianzhuan.data.model.DBanner
import com.lelezu.app.xianzhuan.data.model.Config
import com.lelezu.app.xianzhuan.data.model.ListData
import com.lelezu.app.xianzhuan.data.model.Message
import com.lelezu.app.xianzhuan.data.model.Pending
import com.lelezu.app.xianzhuan.data.model.Recharge
import com.lelezu.app.xianzhuan.data.model.RechargeRes
import com.lelezu.app.xianzhuan.data.model.Version
import com.lelezu.app.xianzhuan.utils.ShareUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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
                current, size, loginToken, deviceId
            )
            executeApiCall(call)
        }


    //公告
    suspend fun apiAnnounce(): ApiResponse<List<Announce>> = withContext(Dispatchers.IO) {
        val call = apiService.getAnnounce(
            loginToken, deviceId
        )
        executeApiCall(call)
    }

    //主页提现公告
    suspend fun apiGetUserWithdraw(): ApiResponse<List<String>> = withContext(Dispatchers.IO) {
        val call = apiService.getUserWithdraw(
            loginToken, deviceId
        )
        executeApiCall(call)
    }

    //新版本
    suspend fun detection(): ApiResponse<Version> = withContext(Dispatchers.IO) {
        val call = apiService.detection(
            ShareUtil.getVersionCode().toString(), ShareUtil.getVersionName(), loginToken, deviceId
        )
        executeApiCall(call)
    }


    //标记已读系统消息
    suspend fun markSysMessage(msgIds: List<String>): ApiResponse<Boolean> =
        withContext(Dispatchers.IO) {
            val call = apiService.getMarkSysMessage(
                msgIds, loginToken, deviceId
            )
            executeApiCall(call)
        }

    //用户确认-取消注销账户
    suspend fun markLogout(informId: String, status: String): ApiResponse<Boolean> =
        withContext(Dispatchers.IO) {
            val call = apiService.markLogout(
                informId, status, loginToken, deviceId
            )
            executeApiCall(call)
        }


    //获取用户未读信息数量
    suspend fun getSysMessageNum(): ApiResponse<Int> = withContext(Dispatchers.IO) {
        val call = apiService.getSysMessageNum(
            loginToken, deviceId
        )
        executeApiCall(call)
    }


    //获取企业微信配置信息
    suspend fun apiRegistrConfig(): ApiResponse<Config> = withContext(Dispatchers.IO) {
        val call = apiService.REGISTR_CONFIG(
            loginToken, deviceId
        )
        executeApiCall(call)
    }

    //首页轮播图
    suspend fun apiCarouselConfig(): ApiResponse<List<DBanner>> = withContext(Dispatchers.IO) {
        val call = apiService.getBanner(
            loginToken, deviceId
        )
        executeApiCall(call)
    }

    //获取广告配置信息
    suspend fun apiADConfig(): ApiResponse<Config> = withContext(Dispatchers.IO) {
        val call = apiService.apiADConfig(
            loginToken, deviceId
        )
        executeApiCall(call)
    }

    //获取待处理消息
    suspend fun pending(): ApiResponse<Pending> = withContext(Dispatchers.IO) {
        val call = apiService.apiPending(
            loginToken, deviceId
        )
        executeApiCall(call)
    }


    /**
     * 用户充值
     * @param rechargeAmount String 	充值金额，必须为大于10的整数，最高限制9999, 单位:
     * @param type Int 支付方式，0-微信，1-支付宝，默认：0-微信
     * @param quitUrlType Int 退出返回的页面, 1-充值页面(默认) 2-会员页面
     * @return ApiResponse<Config>
     */
    suspend fun recharge(
        rechargeAmount: String, type: Int, quitUrlType: Int,
    ): ApiResponse<RechargeRes> = withContext(Dispatchers.IO) {
        val call = apiService.recharge(
            Recharge(rechargeAmount, quitUrlType, type), loginToken, deviceId
        )
        executeApiCall(call)
    }

}