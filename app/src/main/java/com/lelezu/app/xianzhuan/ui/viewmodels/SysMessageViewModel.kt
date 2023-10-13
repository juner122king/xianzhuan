package com.lelezu.app.xianzhuan.ui.viewmodels

import android.os.Environment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.lelezu.app.xianzhuan.data.model.Announce
import com.lelezu.app.xianzhuan.data.model.Config
import com.lelezu.app.xianzhuan.data.model.Message
import com.lelezu.app.xianzhuan.data.model.RechargeRes
import com.lelezu.app.xianzhuan.data.model.Version
import com.lelezu.app.xianzhuan.data.repository.SysInformRepository
import com.lelezu.app.xianzhuan.utils.LogUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import kotlin.math.roundToInt

/**
 * @author:Administrator
 * @date:2023/7/25 0025
 * @description:系统消息相关
 *
 */
class SysMessageViewModel(private val sysInformRepository: SysInformRepository) : BaseViewModel() {

    val liveData: MutableLiveData<MutableList<Message>> = MutableLiveData()

    val isMark: MutableLiveData<Boolean> = MutableLiveData()

    val isLogout: MutableLiveData<Boolean> = MutableLiveData()//是否确认成功注销信息

    val msgNum: MutableLiveData<Int> = MutableLiveData()

    val version: MutableLiveData<Version> = MutableLiveData()

    val announce: MutableLiveData<List<Announce>> = MutableLiveData()

    val downloadProgress: MutableLiveData<Int> = MutableLiveData()//apk下载进度

    val apkPath: MutableLiveData<String> = MutableLiveData()//apk下载文件路径


    val rechargeResLiveData: MutableLiveData<RechargeRes> = MutableLiveData()//支付返回

    val registrconfig: MutableLiveData<Config> = MutableLiveData()//系统配置对象
    val bannerconfig: MutableLiveData<Config> = MutableLiveData()//banner对象
    val adconfig: MutableLiveData<Config> = MutableLiveData()//系统配置对象


    //获取系统消息列表
    fun recharge(
        rechargeAmount: String, type: Int, quitUrlType: Int
    ) = viewModelScope.launch {
        val list = sysInformRepository.recharge(
            rechargeAmount, type, quitUrlType
        )
        handleApiResponse(list, rechargeResLiveData)
    }


    //获取系统消息列表
    fun getAnnounce() = viewModelScope.launch {
        val list = sysInformRepository.apiAnnounce()
        handleApiResponse(list, announce)
    }

    //获取系统消息列表
    fun getMessageList() = viewModelScope.launch {
        val list = sysInformRepository.apiGetList(1, 100)
        handleApiListResponse(list, liveData)
    }


    //标记已读系统消息
    fun markSysMessage(msgs: List<String>) = viewModelScope.launch {
        val call = sysInformRepository.markSysMessage(msgs)
        handleApiResponse(call, isMark)
    }


    //确认注销信息
    fun markLogout(informId: String, status: String) = viewModelScope.launch {
        val call = sysInformRepository.markLogout(informId, status)
        handleApiResponse(call, isLogout)
    }


    //获取用户未读信息数量
    fun getSysMessageNum() = viewModelScope.launch {
        val call = sysInformRepository.getSysMessageNum()
        handleApiResponse(call, msgNum)

    }
    //获取用户未读信息数量
    fun apiRegistrConfig() = viewModelScope.launch {
        val call = sysInformRepository.apiRegistrConfig()
        handleApiResponse(call, registrconfig)

    }
    //首页轮播图
    fun apiCarouselConfig() = viewModelScope.launch {
        val call = sysInformRepository.apiCarouselConfig()
        handleApiResponse(call, bannerconfig)

    }
   //获取广告配置
    fun apiADConfig() = viewModelScope.launch {
        val call = sysInformRepository.apiADConfig()
        handleApiResponse(call, adconfig)

    }


    //检查新版本
    fun detection() = viewModelScope.launch {
        val call = sysInformRepository.detection()
        handleApiResponse(call, version)
    }

    //下载apk
    fun downloadApk(url: String) = viewModelScope.launch {

        val apkDownloader = ApkDownloader { progress, path ->
            // 更新自定义界面上的进度显示
            downloadProgress.postValue(progress)
            if (progress >= 100) apkPath.postValue(path)
        }

        apkDownloader.downloadApk(url, url.substringAfterLast("/"))
    }

    //文件下载器
    class ApkDownloader(private val progressCallback: (Int, String) -> Unit) {

        private val okHttpClient = OkHttpClient()

        suspend fun downloadApk(apkUrl: String, fileName: String) = withContext(Dispatchers.IO) {
            try {
                val externalStorageDirectory = Environment.getExternalStorageDirectory()
                val destinationFile = File(externalStorageDirectory, fileName)
                LogUtils.i("apk:", apkUrl)


                if (destinationFile.exists()) {
                    // 文件已经存在，根据需要处理
                    // 例如，你可以跳过下载或删除现有文件再重新下载
                    progressCallback(100, destinationFile.path)
                    return@withContext
                }

                val request = Request.Builder().url(apkUrl).build()
                val response = okHttpClient.newCall(request).execute()
                val responseBody = response.body

                if (responseBody != null) {
                    val inputStream = responseBody.byteStream()
                    val contentLength = responseBody.contentLength()
                    var downloadedSize = 0L

                    FileOutputStream(destinationFile).use { outputStream ->
                        val buffer = ByteArray(4 * 1024) // 4KB buffer
                        var bytesRead: Int
                        while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                            outputStream.write(buffer, 0, bytesRead)
                            downloadedSize += bytesRead.toLong()
                            val progress =
                                ((downloadedSize.toDouble() / contentLength) * 100).roundToInt()
                            progressCallback(progress, destinationFile.path)
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()

            }

        }
    }

    class ViewFactory(private val repository: SysInformRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SysMessageViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST") return SysMessageViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

