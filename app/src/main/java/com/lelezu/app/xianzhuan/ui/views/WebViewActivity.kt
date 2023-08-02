package com.lelezu.app.xianzhuan.ui.views

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebView
import androidx.activity.result.contract.ActivityResultContract
import com.lelezu.app.xianzhuan.R
import com.lelezu.app.xianzhuan.ui.h5.WebViewSettings
import com.lelezu.app.xianzhuan.ui.h5.WebViewSettings.LINK_KEY
import com.lelezu.app.xianzhuan.ui.h5.WebViewSettings.URL_TITLE
import java.io.File

class WebViewActivity : BaseActivity() {


    private val pickImageContract = registerForActivityResult(PickImageContract()) {
        if (it != null) {
            // 可以根据需要更新对应的数据项或刷新整个列表
            wv.loadUrl("javascript:showSelectedImage('$it')")
        }
    }


    private lateinit var link: String
    private lateinit var wv: WebView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        link = intent.getStringExtra(LINK_KEY)!!

        wv = findViewById(R.id.webView)
        WebViewSettings.setDefaultWebSettings(wv)

        wv.addJavascriptInterface(JavaScriptInterface(), "Android")//注入方法

        wv.loadUrl(WebViewSettings.host + link)
        Log.i("H5调原生：", WebViewSettings.host + link)


    }

    inner class JavaScriptInterface {
        @JavascriptInterface
        fun chooseImage() {
            //上传图片，打开相册
            Log.i("H5调原生", "打开相册成功")
            pickImageContract.launch(Unit)
        }

        @JavascriptInterface
        fun getImageData(imagePath: String) {
            val imageFile = File(imagePath)
            val imageBytes = imageFile.readBytes()
            val imageData = Base64.encodeToString(imageBytes, Base64.DEFAULT)
            wv.loadUrl("javascript:showSelectedImage('$imageData')")
        }
    }


    private fun getImagePath(uri: Uri): String? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor: Cursor? = contentResolver.query(uri, projection, null, null, null)
        cursor?.let {
            if (it.moveToFirst()) {
                val columnIndex: Int = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                val imagePath: String = it.getString(columnIndex)
                it.close()
                return imagePath
            }
        }
        return null
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