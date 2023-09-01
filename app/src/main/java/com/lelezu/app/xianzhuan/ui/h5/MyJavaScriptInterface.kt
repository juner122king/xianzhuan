package com.lelezu.app.xianzhuan.ui.h5

import android.app.Activity
import android.webkit.JavascriptInterface
import android.widget.Toast
import com.hjq.permissions.OnPermissionCallback
import com.lelezu.app.xianzhuan.ui.views.HomeActivity
import com.lelezu.app.xianzhuan.utils.MyPermissionUtil
import com.lelezu.app.xianzhuan.utils.ToastUtils

/**
 * @author:Administrator
 * @date:2023/8/2 0002
 * @description:H5请求方法
 *
 */
class MyJavaScriptInterface(private val context: Activity) {
    @JavascriptInterface
    fun saveImage(imageUrl: String) {
        MyPermissionUtil.saveImagesApply(context, object : OnPermissionCallback {
            override fun onGranted(permissions: MutableList<String>, all: Boolean) {
                //获取权限成功
                // 调用保存图片的方法
                Toast.makeText(context, "图片保存成功", Toast.LENGTH_SHORT).show()
                (context as HomeActivity).saveImageToSystem(imageUrl)
            }

            override fun onDenied(permissions: MutableList<String>, never: Boolean) {
                //权限失败
                ToastUtils.showToast(context, "您已拒绝授权，图片保存失败！", 0)

            }
        })
    }

    @JavascriptInterface
    fun shareFriends(imageUrl: String) {
        MyPermissionUtil.saveImagesAndOpenAlbumApply(context, object : OnPermissionCallback {
            override fun onGranted(permissions: MutableList<String>, all: Boolean) {
                if (all) (context as HomeActivity).shareFriends(imageUrl) //获取权限成功
                else ToastUtils.showToast(context, "获取部分权限成功，但部分权限未正常授予")
            }

            override fun onDenied(permissions: MutableList<String>, never: Boolean) {
                //权限失败
                ToastUtils.showToast(context, "您已拒绝授权，图片分享失败！", 0)
            }
        })
    }

}
