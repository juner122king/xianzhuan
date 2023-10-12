package com.lelezu.app.xianzhuan.ui.views

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.widget.Button
import android.widget.TextView
import androidx.core.content.FileProvider
import androidx.core.widget.ContentLoadingProgressBar
import com.hjq.permissions.OnPermissionCallback
import com.lelezu.app.xianzhuan.R
import com.lelezu.app.xianzhuan.ui.h5.WebViewSettings
import com.lelezu.app.xianzhuan.utils.MyPermissionUtil
import com.lelezu.app.xianzhuan.utils.ShareUtil
import com.lelezu.app.xianzhuan.utils.ShareUtil.versionCode
import com.lelezu.app.xianzhuan.utils.ShareUtil.versionName
import java.io.File

class AutoOutActivity : BaseActivity(), OnClickListener {
    private lateinit var dialog: Dialog

    // 替换为你的 APK 下载链接和文件名
    private lateinit var apkUrl: String
    private lateinit var progressBar: ContentLoadingProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        findViewById<View>(R.id.tv_agreement).setOnClickListener(this)
        findViewById<View>(R.id.tv_newVersion).setOnClickListener(this)
        findViewById<View>(R.id.tv_url).setOnClickListener(this)


        sysMessageViewModel.version.observe(this) {
            if (it.isNew) {
                //有新版本
                findViewById<View>(R.id.tv_newVersion).visibility = View.VISIBLE
                apkUrl = it.download
            }
        }
    }


    override fun onStart() {
        super.onStart()
        //检查新版本
        sysMessageViewModel.detection()
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_auto_out
    }

    override fun getContentTitle(): String {
        return getString(R.string.title_activity_out)
    }

    override fun isShowBack(): Boolean {
        return true
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.tv_agreement -> {
                val intent = Intent(this, WebViewActivity::class.java)
                intent.putExtra(WebViewSettings.LINK_KEY, WebViewSettings.link100)
                intent.putExtra(WebViewSettings.URL_TITLE, getString(R.string.text_agreement))
                intent.putExtra(WebViewSettings.isProcessing, false)
                startActivity(intent)
            }

            R.id.tv_newVersion -> {
                //询问权限
                showDownDialog()
            }

            R.id.tv_url -> {
                //跳到备案网站

                val intent = Intent(this, WebViewActivity::class.java)
                intent.putExtra(WebViewSettings.LINK_KEY, WebViewSettings.link15)
                intent.putExtra(WebViewSettings.isProcessing, false)
                startActivity(intent)

            }
        }
    }


}