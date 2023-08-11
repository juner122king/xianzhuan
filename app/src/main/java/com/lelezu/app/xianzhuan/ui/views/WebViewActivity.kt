package com.lelezu.app.xianzhuan.ui.views

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.github.lzyzsd.jsbridge.BridgeWebView
import com.lelezu.app.xianzhuan.R
import com.lelezu.app.xianzhuan.ui.h5.WebViewSettings
import com.lelezu.app.xianzhuan.ui.h5.WebViewSettings.LINK_KEY
import com.lelezu.app.xianzhuan.ui.h5.WebViewSettings.URL_TITLE
import com.lelezu.app.xianzhuan.utils.Base64Utils


class WebViewActivity : BaseActivity() {


    private lateinit var link: String
    private lateinit var wv: BridgeWebView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        link = intent.getStringExtra(LINK_KEY)!!

        wv = findViewById(R.id.webView)
        WebViewSettings.setDefaultWebSettings(wv)
        wv.loadUrl(WebViewSettings.host + link)
//        wv.loadUrl("https://liulanmi.com/labs/core.html")
        Log.i("H5调原生：", WebViewSettings.host + link)


        //向H5注入方法
        wv.registerHandler("chooseImage") { data, function ->
            openPhoto()
        }
    }


    private val rc: Int = 123


    private fun openPhoto() {
        // 检查图片权限
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // 请求权限
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), rc
            )
        } else {
            //上传图片，打开相册
            pickImageContract.launch(Unit)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == rc) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 用户授予了权限，继续进行文件操作
                //上传图片，打开相册
                pickImageContract.launch(Unit)
            } else {
                // 用户拒绝了权限，处理拒绝权限的情况
            }
        }
    }

    private val pickImageContract = registerForActivityResult(PickImageContract()) {
        if (it != null) {
            // 获取内容URI对应的文件路径
            val thread = Thread {
                val imageData = Base64Utils.zipPic(it)
                wv.post {
                    Log.i("H5调原生:", "图片字节码长度:${imageData?.length}")
                    Log.i("H5调原生:", "图片字节码:${imageData}")
                    wv.callHandler("showSelectedImage", imageData) {
                        //可以在这里弹出提示
                    }
                }
            }
            thread.start()
        }
    }

    //处理选择图片的请求和结果
    inner class PickImageContract : ActivityResultContract<Unit, Uri?>() {
        override fun createIntent(context: Context, input: Unit): Intent {
            return Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
            if (resultCode == Activity.RESULT_OK) {
                return intent?.data
            }
            return null
        }
    }


    override fun getLayoutId(): Int {
        return R.layout.activity_web_view_stzqactivity
    }

    override fun getContentTitle(): String? {
        return intent.getStringExtra(URL_TITLE)
    }

    override fun isShowBack(): Boolean {
        return true
    }

}