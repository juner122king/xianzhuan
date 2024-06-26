package com.lelezu.app.xianzhuan.ui.h5

import android.app.Activity
import android.webkit.JavascriptInterface
import com.hjq.permissions.OnPermissionCallback
import com.hjq.toast.ToastUtils
import com.lelezu.app.xianzhuan.ui.views.BaseActivity
import com.lelezu.app.xianzhuan.ui.views.HomeActivity
import com.lelezu.app.xianzhuan.utils.MyPermissionUtil

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
                ToastUtils.show("图片保存成功")
                (context as BaseActivity).saveImageToSystem(imageUrl, "dxz_share_pic")
            }

            override fun onDenied(permissions: MutableList<String>, never: Boolean) {
                //权限失败
                ToastUtils.show("您已拒绝授权，图片保存失败！")

            }
        })
    }

    @JavascriptInterface
    fun shareFriends(imageUrl: String) {
        MyPermissionUtil.saveImagesAndOpenAlbumApply(context, object : OnPermissionCallback {
            override fun onGranted(permissions: MutableList<String>, all: Boolean) {
                if (all) (context as BaseActivity).shareFriends(imageUrl) //获取权限成功
                else ToastUtils.show("获取部分权限成功，但部分权限未正常授予")
            }

            override fun onDenied(permissions: MutableList<String>, never: Boolean) {
                //权限失败
                ToastUtils.show("您已拒绝授权，图片分享失败！")
            }
        })
    }

    @JavascriptInterface
    fun homeCurrentView(item: Int) {

        context.runOnUiThread {
            (context as HomeActivity).currentViewPager(item)
        }

    }

    /**
     * 福利中心-弹出未看完视频提示弹窗
     * @param str String 窗口提示信息
     */
    @JavascriptInterface
    fun showFLContinueDialog(str: String) {
        ToastUtils.show("弹出未看完视频提示弹窗！")
//        (context as HomeActivity).showFLDialog(str)

    }

}
