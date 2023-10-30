package com.lelezu.app.xianzhuan.utils

import android.content.Context
import android.text.TextUtils
import com.fendasz.moku.planet.utils.thirdparty.baidu.util.DeviceId
import java.util.UUID

/**
 * @author:Administrator
 * @date:2023/10/28 0028
 * @description:
 *
 */
object UUIDUtils {

    fun getDeviceID(context: Context): String {

        var deviceId = ShareUtil.getString(ShareUtil.APP_SHARED_PREFERENCES_DEVICE_ID)
        if (TextUtils.isEmpty(deviceId)) {
            deviceId = DeviceId.getDeviceID(context) //第三方获取

            if (TextUtils.isEmpty(deviceId)) {
                deviceId = UUID.randomUUID().toString()//每次调用都会生成一个全新的标识符
            }

            //保存Id
            ShareUtil.putString(
                ShareUtil.APP_SHARED_PREFERENCES_DEVICE_ID, deviceId
            )
        }
        //设备ID
        LogUtils.i(
            "UUIDUtils", "设备ID:${deviceId}"
        )
        return deviceId
    }

}