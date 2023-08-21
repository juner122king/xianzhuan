package com.lelezu.app.xianzhuan.ui.h5

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.webkit.JavascriptInterface
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.lelezu.app.xianzhuan.ui.fragments.NotificaFragment
import com.lelezu.app.xianzhuan.ui.views.HomeActivity
import com.lelezu.app.xianzhuan.utils.LogUtils

/**
 * @author:Administrator
 * @date:2023/8/2 0002
 * @description:H5请求方法
 *
 */
class MyJavaScriptInterface(private val context: Activity) {
    @JavascriptInterface
    public fun saveImage(imageUrl: String) {
        toImage(imageUrl, false)
    }

    @JavascriptInterface
    public fun shareFriends(imageUrl: String) {
        toImage(imageUrl, true)
    }

    private val rc: Int = 321
    private fun toImage(imageUrl: String, isShare: Boolean) {
        LogUtils.i("打开相册  android级别：${Build.VERSION.SDK_INT}")
        // 检查图片权限
        if (ContextCompat.checkSelfPermission(
                context, Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            //13更高版本后的图片弹窗询问
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ActivityCompat.requestPermissions(
                    context, arrayOf(Manifest.permission.READ_MEDIA_IMAGES), rc
                )
            } else {

                ActivityCompat.requestPermissions(
                    context, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), rc
                )
            }
        } else {

            if (isShare) {//是否微信分享
                // 调用分享图片的方法
                (context as HomeActivity).shareFriends(imageUrl)
            } else {
                // 调用保存图片的方法
                (context as HomeActivity).saveImageToSystem(imageUrl)
            }
        }
    }
}