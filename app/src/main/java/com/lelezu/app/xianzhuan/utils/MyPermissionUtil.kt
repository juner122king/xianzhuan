package com.lelezu.app.xianzhuan.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Build
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.hjq.toast.ToastUtils

/**
 * @author:Administrator
 * @date:2023/9/1 0001
 * @description:系统权限管理工具类
 *
 */
object MyPermissionUtil {


    //申请保存图片权限
    fun saveImagesApply(context: Activity, callback: OnPermissionCallback) {
        if (checkActivityStu(context)) return
        XXPermissions.with(context).permission(Permission.WRITE_EXTERNAL_STORAGE).request(callback)
    }

    //申请保存图片和打开相册权限
    fun saveImagesAndOpenAlbumApply(context: Activity, callback: OnPermissionCallback) {
        if (checkActivityStu(context)) return
        XXPermissions.with(context)
            .permission(Permission.WRITE_EXTERNAL_STORAGE, Permission.READ_MEDIA_IMAGES)
            .request(callback)
    }


    //文件管理权限
    fun storageApply(context: Activity, callback: OnPermissionCallback) {
        if (checkActivityStu(context)) return
        XXPermissions.with(context).permission(Permission.MANAGE_EXTERNAL_STORAGE).request(callback)
    }

    //申请打开相册权限
    @SuppressLint("SuspiciousIndentation")
    fun openAlbumApply(context: Activity, callback: OnPermissionCallback) {
        if (checkActivityStu(context)) return
        val permission = Permission.READ_MEDIA_IMAGES
        XXPermissions.with(context).permission(permission).request(callback)
    }

    //申请读取手机权限与文件管理权限
    @SuppressLint("SuspiciousIndentation")
    fun readPhoneStateApply(context: Activity, callback: OnPermissionCallback) {
        if (checkActivityStu(context)) return

        if (context.applicationInfo.targetSdkVersion >= Build.VERSION_CODES.TIRAMISU) {//大于ANDROID_13

            val ps = arrayOf(
                Permission.READ_PHONE_STATE,
                Permission.WRITE_EXTERNAL_STORAGE,
                Permission.READ_MEDIA_IMAGES,
            )
            XXPermissions.with(context).permission(ps).request(callback)

        } else {

            val ps = arrayOf(
                Permission.READ_PHONE_STATE,
                Permission.WRITE_EXTERNAL_STORAGE,
                Permission.READ_EXTERNAL_STORAGE
            )
            XXPermissions.with(context).permission(ps).request(callback)
        }
    }

    private fun checkActivityStu(activity: Activity): Boolean {

        return if (activity.isFinishing || activity.isDestroyed) {
            //Activity状态不正常，不进行权限申请
//            ToastUtils.show("Activity状态不正常，权限申请失败")
            true
        } else false


    }

}