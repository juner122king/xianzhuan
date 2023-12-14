package com.lelezu.app.xianzhuan.jiguang

import android.content.Context
import cn.jpush.android.api.CustomMessage
import cn.jpush.android.service.JPushMessageService
import com.hjq.toast.ToastUtils

/**
 * @author:Administrator
 * @date:2023/8/3 0003
 * @description:
 *
 */
class JGuangReceiver : JPushMessageService() {

    override fun onMessage(p0: Context?, p1: CustomMessage?) {

        ToastUtils.show("收到JPush自定义消息:$p1")

    }
}