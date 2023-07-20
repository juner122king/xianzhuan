package com.lelezu.app.xianzhuan

import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.text.TextUtils
import android.util.Log
import com.netease.htprotect.HTProtect
import com.netease.htprotect.HTProtectConfig
import com.netease.htprotect.callback.GetTokenCallback
import com.netease.htprotect.callback.HTPCallback
import java.io.BufferedReader
import java.io.FileReader

/**
 * @author:Administrator
 * @date:2023/7/19 0019
 * @description:
 *
 */
class MyApplication : Application() {

    //声明公共变量
    companion object {
        var context: Context? = null
    }

    override fun onCreate() {
        super.onCreate()
        context = this
        //获取包名
        var packageName = this.packageName
        //获取进程名
        var processName = getProcessName(android.os.Process.myPid())
        //获取版本名称
        var versionName = getVersionName()

        Log.i("MyApplication", packageName + "========" + processName + "=========" + versionName)

        //进行第三方sdk初始化

        //易盾
        val config = HTProtectConfig().apply {

            var serverType = 2
            var channel = "testchannel"
        }
        val callback = HTPCallback { paramInt, paramString ->
            Log.d("Test", "code is: $paramInt String is: $paramString")
            // paramInt返回200说明初始化成功
        }

        HTProtect.init(context, "YD00525369360953", callback, config)


    }

    fun getVersionName(): String {
        var packageManager: PackageManager = packageManager
        var info = packageManager.getPackageInfo(packageName, 0)
        return info.versionName
    }

    fun getProcessName(pid: Int): String {
        var bufferReader: BufferedReader? = null
        bufferReader = BufferedReader(FileReader("/proc/" + pid + "/cmdline"))
        var processName = bufferReader.readLine()
        if (!TextUtils.isEmpty(processName)) {
            processName = packageName.trim()
        }
        bufferReader.close()
        return processName
    }
}