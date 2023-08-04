package com.lelezu.app.xianzhuan.ui.h5

import android.content.Context
import android.webkit.JavascriptInterface
import com.lelezu.app.xianzhuan.ui.fragments.NotificaFragment
import com.lelezu.app.xianzhuan.ui.views.HomeActivity

/**
 * @author:Administrator
 * @date:2023/8/2 0002
 * @description:H5请求方法
 *
 */
class MyJavaScriptInterface(private val context: Context) {
    @JavascriptInterface
    public fun saveImage(imageUrl: String) {
        // 调用保存图片的方法
        (context as HomeActivity).saveImageToSystem(imageUrl)
    }

    @JavascriptInterface
    public fun shareFriends(imageUrl: String) {
        // 调用保存图片的方法
        (context as HomeActivity).shareFriends(imageUrl)
    }


}