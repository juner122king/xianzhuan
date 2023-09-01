package com.lelezu.app.xianzhuan.ui.views

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.widget.Button
import androidx.core.content.FileProvider
import androidx.core.widget.ContentLoadingProgressBar
import com.hjq.permissions.OnPermissionCallback
import com.lelezu.app.xianzhuan.R
import com.lelezu.app.xianzhuan.ui.h5.WebViewSettings
import com.lelezu.app.xianzhuan.utils.MyPermissionUtil
import com.lelezu.app.xianzhuan.utils.ShareUtil
import com.lelezu.app.xianzhuan.utils.ShareUtil.versionCode
import com.lelezu.app.xianzhuan.utils.ShareUtil.versionName
import com.lelezu.app.xianzhuan.utils.ToastUtils
import java.io.File

class AutoOutActivity : BaseActivity(), OnClickListener {
    private lateinit var dialog: Dialog

    // 替换为你的 APK 下载链接和文件名
    private lateinit var apkUrl: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        findViewById<View>(R.id.tv_agreement).setOnClickListener(this)
        findViewById<View>(R.id.tv_newVersion).setOnClickListener(this)


        sysMessageViewModel.version.observe(this) {
            if (it.isNew) {
                //有新版本
                findViewById<View>(R.id.tv_newVersion).visibility = View.VISIBLE
                apkUrl = it.download
            }

        }

        val pInfo = packageManager.getPackageInfo(packageName, 0)
        ShareUtil.putString(versionName, pInfo.versionName)
        ShareUtil.putInt(versionCode, pInfo.versionCode)
    }

    override fun onResume() {
        super.onResume()
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
                onUpData()
            }

        }
    }

    //询问权限
    private fun onUpData() {
        MyPermissionUtil.storageApply(this, object : OnPermissionCallback {
            override fun onGranted(permissions: MutableList<String>, all: Boolean) {
                if (all) showDialog()
                else showToast("获取部分权限成功，但部分权限未正常授予")
            }

            override fun onDenied(permissions: MutableList<String>, never: Boolean) {
                //权限失败
                showToast("您已拒绝授权，更新失败！")
            }
        })
    }

    private fun showDialog() {
        dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_download)

        val progressBar: ContentLoadingProgressBar = dialog.findViewById(R.id.progressBar)
        val btnDownload: Button = dialog.findViewById(R.id.btnDownload)
        val btnDownloadNo: Button = dialog.findViewById(R.id.btnDownloadNo)

        btnDownload.setOnClickListener {
            // 在点击下载按钮时执行下载操作
            startDownload()
        }
        btnDownloadNo.setOnClickListener {
            // 在点击下载按钮时执行下载操作
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun startDownload() {
        sysMessageViewModel.downloadApk(apkUrl)
        sysMessageViewModel.downloadProgress.observe(this) {
            // 更新进度条
            val progressBar: ContentLoadingProgressBar = dialog.findViewById(R.id.progressBar)
            progressBar.progress = it
            if (it == 100) {
                // 下载完成时，隐藏对话框
                dialog.dismiss()

            }
        }
        sysMessageViewModel.apkPath.observe(this) {
            //安装apk
            openFileWithFilePath(it)
        }
    }

    private fun openFileWithFilePath(filePath: String) {
        val file = File(filePath)
        val uri = FileProvider.getUriForFile(this, "com.lelezu.app.xianzhuan.fileprovider", file)
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(uri, "application/vnd.android.package-archive")
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivity(intent)
    }


}