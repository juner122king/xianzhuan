package com.lelezu.app.xianzhuan.utils

import android.annotation.SuppressLint
import android.content.Context
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions

/**
 * @author:Administrator
 * @date:2023/9/1 0001
 * @description:系统权限管理工具类
 *
 */
object MyPermissionUtil {


    //申请保存图片权限
    fun saveImagesApply(context: Context, callback: OnPermissionCallback) {

        XXPermissions.with(context).permission(Permission.WRITE_EXTERNAL_STORAGE).request(callback)
    }

    //申请保存图片和打开相册权限
    fun saveImagesAndOpenAlbumApply(context: Context, callback: OnPermissionCallback) {

        XXPermissions.with(context)
            .permission(Permission.WRITE_EXTERNAL_STORAGE, Permission.READ_MEDIA_IMAGES)
            .request(callback)
    }


    //文件管理权限
    fun storageApply(context: Context, callback: OnPermissionCallback) {

        XXPermissions.with(context).permission(Permission.MANAGE_EXTERNAL_STORAGE).request(callback)
    }

    //申请打开相册权限
    @SuppressLint("SuspiciousIndentation")
    fun openAlbumApply(context: Context, callback: OnPermissionCallback) {
        val permission = Permission.READ_MEDIA_IMAGES

        XXPermissions.with(context).permission(permission).request(callback)
    }

   //申请读取手机权限与文件管理权限
    @SuppressLint("SuspiciousIndentation")
    fun readPhoneStateApply(context: Context, callback: OnPermissionCallback) {
        val permission = Permission.READ_PHONE_STATE
        val permission2 = Permission.WRITE_EXTERNAL_STORAGE
//        val permission3 = Permission.MANAGE_EXTERNAL_STORAGE
        XXPermissions.with(context).permission(permission,permission2).request(callback)
    }


}