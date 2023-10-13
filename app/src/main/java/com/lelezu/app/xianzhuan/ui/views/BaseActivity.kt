package com.lelezu.app.xianzhuan.ui.views

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.ClipDescription
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.ContextMenu
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.widget.ContentLoadingProgressBar
import com.hjq.permissions.OnPermissionCallback
import com.hjq.toast.ToastUtils
import com.lelezu.app.xianzhuan.MyApplication
import com.lelezu.app.xianzhuan.R
import com.lelezu.app.xianzhuan.data.model.ErrResponse
import com.lelezu.app.xianzhuan.ui.h5.WebViewSettings
import com.lelezu.app.xianzhuan.ui.viewmodels.HomeViewModel
import com.lelezu.app.xianzhuan.ui.viewmodels.LoginViewModel
import com.lelezu.app.xianzhuan.ui.viewmodels.SysMessageViewModel
import com.lelezu.app.xianzhuan.utils.LogUtils
import com.lelezu.app.xianzhuan.utils.MyPermissionUtil
import com.lelezu.app.xianzhuan.utils.ShareUtil
import com.lelezu.app.xianzhuan.wxapi.WxLogin
import java.io.File


/**
 * @author:Administrator
 * @date:2023/7/26 0026
 * @description:
 *
 */
abstract class BaseActivity : AppCompatActivity() {


    var mBack: LinearLayout? = null
    private var mTvTitle: TextView? = null
    private var mTvRight: TextView? = null
    private var mRltBase: RelativeLayout? = null
    private var rootView: View? = null

    private var loadingView: View? = null

    protected val loginViewModel: LoginViewModel by viewModels {
        LoginViewModel.LoginViewFactory((application as MyApplication).userRepository)
    }

    val homeViewModel: HomeViewModel by viewModels {
        HomeViewModel.ViewFactory((application as MyApplication).taskRepository)
    }
    protected val sysMessageViewModel: SysMessageViewModel by viewModels {
        SysMessageViewModel.ViewFactory((application as MyApplication).sysInformRepository)
    }

    private lateinit var dialog: Dialog

    // 替换为你的 APK 下载链接和文件名
    private lateinit var apkUrl: String
    private lateinit var progressBar: ContentLoadingProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        rootView = View.inflate(this, R.layout.activity_title, null)
        setContentView(getLayoutId())
        addContent()
        setContentView(rootView)
        initViewModel()

        val loadingView = findViewById<View>(R.id.loadingView)
        setLoadingView(loadingView)


        // 初始化并注册网络连接状态广播接收器
        connectivityReceiver = ConnectivityReceiver()
        registerReceiver(
            connectivityReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        )


        //开始--示例图打开功能
        ivDialog = Dialog(this, R.style.FullActivity)
        val attributes = window.attributes
        attributes.width = WindowManager.LayoutParams.MATCH_PARENT
        attributes.height = WindowManager.LayoutParams.MATCH_PARENT
        ivDialog.window?.attributes = attributes


        sysMessageViewModel.version.observe(this) {
            if (it.isNew) {
                //有新版本
                apkUrl = it.download

                showDownDialog()
            }

        }

        val pInfo = packageManager.getPackageInfo(packageName, 0)
        ShareUtil.putString(ShareUtil.versionName, pInfo.versionName)
        ShareUtil.putInt(ShareUtil.versionCode, pInfo.versionCode)

    }

    protected fun showDownDialog() {
        dialog = Dialog(this)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setContentView(R.layout.dialog_download)

        progressBar = dialog.findViewById(R.id.progressBar)
        val btnDownload: TextView = dialog.findViewById(R.id.btnDownload)
        val btnDownloadNo: TextView = dialog.findViewById(R.id.btnDownloadNo)

        btnDownload.setOnClickListener {
            // 在点击下载按钮时执行下载操作

            onUpData()

        }
        btnDownloadNo.setOnClickListener {
            // 在点击下载按钮时执行下载操作
            dialog.dismiss()
            ShareUtil.putBoolean(ShareUtil.CHECKED_NEW_VISON, true)
        }

        dialog.show()
    }

    //询问权限
    private fun onUpData() {
        MyPermissionUtil.storageApply(this, object : OnPermissionCallback {
            override fun onGranted(permissions: MutableList<String>, all: Boolean) {
                if (all) startDownload()
                else showToast("获取部分权限成功，但部分权限未正常授予")
            }

            override fun onDenied(permissions: MutableList<String>, never: Boolean) {
                //权限失败
                showToast("您已拒绝授权，更新失败！")
            }
        })
    }

    private fun startDownload() {
        progressBar.visibility = View.VISIBLE
        sysMessageViewModel.downloadApk(apkUrl)
        sysMessageViewModel.downloadProgress.observe(this) {
            // 更新进度条
            progressBar.progress = it

        }
        sysMessageViewModel.apkPath.observe(this) {
            dialog.dismiss()
            //安装apk
            openFileWithFilePath(it)
            ShareUtil.cleanInfo()

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


    //重新打开登录页面
    private fun goToLoginView() {
        ShareUtil.cleanInfo()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    //退出登录
    fun logOut() {

        showLoading()
        loginViewModel.logout()
        loginViewModel.isLogOut.observe(this) {
            hideLoading()
            if (it) {
                goToLoginView()
            } else showToast("退出登录失败！")
        }
    }

    //注销账号
    fun cancelOut() {
        goToLoginView()
    }


    protected fun backToHome(position: String) {
//        ToastUtils.show("页面：$position")
        val intent = Intent(this, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        intent.putExtra("FragmentPosition", position)
        startActivity(intent)
    }


    protected fun gotoTaskDetails(taskId: String) {


        val intent = Intent(this, TaskDetailsActivity::class.java)
        intent.putExtra("taskId", taskId)
        startActivity(intent)
    }

    protected fun goToHomeActivity() {
        showLoading()
        //获取 是否需要关注企业微信的配置信息
        sysMessageViewModel.apiRegistrConfig()

        sysMessageViewModel.registrconfig.observe(this) {

            hideLoading()
            if (it.confValue.isEnabled) {//开启新用户注册关注
                //执行获取用户信息接口
                loginViewModel.getUserInfo(ShareUtil.getString(ShareUtil.APP_SHARED_PREFERENCES_LOGIN_ID))
                loginViewModel.userInfo.observe(this) { it2 ->
                    if (it2.hasRewardNewerAward) {
                        toHome()
                    } else {
//                showToast("未领取新人奖励")
                        val intent = Intent(this, WebViewActivity::class.java)
                        intent.putExtra(WebViewSettings.LINK_KEY, WebViewSettings.link16)
                        intent.putExtra(WebViewSettings.URL_TITLE, "新人奖励")
                        startActivity(intent)
                        finish()
                    }
                }
            } else {
                toHome()
            }
        }
    }

    private fun toHome() {
        val intent = Intent(MyApplication.context, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }


    protected fun goToSysMessageActivity() {

        val intent = Intent(MyApplication.context, MessageActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun initViewModel() {

        loginViewModel.errMessage.observe(this) {


            onErrMessage(it)
        }
        homeViewModel.errMessage.observe(this) {
            onErrMessage(it)
        }
    }

    private fun onErrMessage(it: ErrResponse) {

        hideLoading()
        if (it.message != "Token不存在或已失效") showToast(it.message)
        if (it.isTokenLose()) goToLoginView()    //重新打开登录页面
    }


    private fun addContent() {
        mBack = rootView!!.findViewById<View>(R.id.back) as LinearLayout
        mTvTitle = rootView!!.findViewById<View>(R.id.tv_title) as TextView
        mTvRight = rootView!!.findViewById<View>(R.id.tv_right) as TextView
        mRltBase = rootView!!.findViewById<View>(R.id.rlt_base) as RelativeLayout
        val flContent = rootView!!.findViewById<View>(R.id.fl_content) as FrameLayout
        mTvTitle!!.text = if (getContentTitle() == null) "" else getContentTitle()


        mBack!!.setOnClickListener { finish() } //java8写法，特此备注一下


        val content = View.inflate(this, getLayoutId(), null)
        if (content != null) {
            val params = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT
            )
            flContent.addView(content, params)
        }

        if (isShowBack()) mBack!!.visibility = View.VISIBLE
        else mBack!!.visibility = View.GONE


    }

    fun showBack() {
        mBack!!.visibility = View.VISIBLE
    }

    fun hideBack() {
        mBack!!.visibility = View.GONE
    }


    fun showRightText(text: String) {
        mTvRight!!.text = text
        mTvRight!!.visibility = View.VISIBLE

        mTvRight!!.setOnClickListener {

            when (text) {
                getString(R.string.dashboard_tab5_text) -> startActivity(//跳到筛选
                    Intent(
                        this, TaskCondSelectActivity::class.java
                    )
                )
            }
        }
    }


    fun hideRightText() {
        mTvRight!!.visibility = View.GONE
    }


    fun setTitleText(t: String) {
        mTvTitle!!.text = t
    }

    fun hideView() {
        mRltBase?.visibility = View.GONE
    }

    fun showView() {
        if (mRltBase?.visibility != View.VISIBLE) mRltBase?.visibility = View.VISIBLE
    }

    protected fun setLoadingView(view: View) {
        loadingView = view
    }

    fun showLoading() {

        loadingView?.visibility = View.VISIBLE
    }

    fun hideLoading() {
        loadingView?.visibility = View.GONE
    }


    /**
     * 获取布局ID
     *
     * @return
     */
    protected abstract fun getLayoutId(): Int

    /**
     * title赋值
     *
     * @return
     */
    protected abstract fun getContentTitle(): String?

    protected abstract fun isShowBack(): Boolean


    protected open fun showToast(message: String?) {
        ToastUtils.show(message)
    }


    override fun onStop() {
        super.onStop()
        hideLoading()
    }

    /**
     * 首先，它检查剪贴板是否有主要剪贴内容，即剪贴板是否有数据。如果没有数据，pasteItem.isEnabled将被设置为false，禁用粘贴菜单项。
     * 接下来，它检查剪贴板的主要剪贴描述是否包含纯文本的 MIME 类型。如果剪贴板的内容不是纯文本，pasteItem.isEnabled将被设置为false，禁用粘贴菜单项。
     * 最后，如果剪贴板的内容是纯文本，pasteItem.isEnabled将被设置为true，启用粘贴菜单项。
     *
     * ！！Android 10以及以上版本限制了对剪贴板数据的访问  监听焦点变化再获取剪切板数据
     */
    protected fun getClipBoar() {
        LogUtils.i("获取剪切板数据", "获取剪切板数据")

        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        when {
            !clipboard.hasPrimaryClip() -> {
                LogUtils.i("粘贴板没有内容")
            }

            !(clipboard.primaryClipDescription?.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN))!! -> {
                LogUtils.i("粘贴板有内容，但不是纯文本")
            }

            else -> {
                val text = clipboard.primaryClip?.getItemAt(0)?.text.toString()
                LogUtils.i("粘贴板文本内容:${text}")
                ShareUtil.putRecommendUserId(text)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        // 取消注册广播接收器，以避免内存泄漏
        unregisterReceiver(connectivityReceiver)
    }

    private lateinit var connectivityReceiver: ConnectivityReceiver

    // 网络连接状态广播接收器
    inner class ConnectivityReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == ConnectivityManager.CONNECTIVITY_ACTION) {
                val connectivityManager =
                    getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val networkInfo = connectivityManager.activeNetworkInfo
                val isConnected = networkInfo?.isConnectedOrConnecting == true

                if (isConnected) {
                    // 网络已连接

                    ShareUtil.putConnected(true)

                } else {
                    // 网络未连接
                    // 在这里执行你需要的操作
                    showToast("网络未连接")
                    ShareUtil.putConnected(false)
                }
            }
        }
    }

    protected lateinit var ivDialog: Dialog
    override fun onCreateContextMenu(
        menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menu?.add(0, 1, 0, "保存")
        menu?.add(0, 2, 1, "取消")

        menu!!.getItem(0).setOnMenuItemClickListener {
            showToast("保存成功")
            //进行保存图片操作
            saveImageToSystem(ShareUtil.getString(ShareUtil.APP_TASK_PIC_DOWN_URL), "dxz_task_pic")
            true
        }

        menu.getItem(1).setOnMenuItemClickListener {
            ivDialog.dismiss()
            true
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


    /**
     * H5执行方法：进行微信支付
     * @param rechargeAmount String 充值金额，需要在H5页面返回
     */
    protected fun onWXPay(rechargeAmount: String) {

        showLoading()
        sysMessageViewModel.rechargeResLiveData.observe(this) {

            hideLoading()
            LogUtils.i("WX支付", it.toString())
            //获取预支付订单信息
            val payment = it.prepayPaymentResp
            WxLogin.onPrePay(application, payment)

        }
        sysMessageViewModel.recharge(rechargeAmount, 0, 1)
    }


    /**
     * H5执行方法：跳转系统信息页面
     */
    protected fun toSysMessage() {

        LogUtils.i("跳转系统信息页面")
        goToSysMessageActivity()

    }

    fun saveImageToSystem(imageUrl: String, imageName: String) {
        val request = DownloadManager.Request(Uri.parse(imageUrl))
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "$imageName.jpg")

        val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        downloadManager.enqueue(request)
    }

    //分享微信
    fun shareFriends(imageUrl: String) {
        Log.i("H5分享图片", "imageUrl：${imageUrl}")
        // 注册广播接收器
        val intentFilter = IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        registerReceiver(downloadReceiver, intentFilter)
        saveImageToSystem(imageUrl, "dxz_share_pic")
    }

    private val downloadReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == DownloadManager.ACTION_DOWNLOAD_COMPLETE) {
                // 下载完成时的处理
                val downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                handleDownloadComplete(downloadId)
            }
        }
    }
    @SuppressLint("Range")
    private fun handleDownloadComplete(downloadId: Long) {
        val query = DownloadManager.Query().apply {
            setFilterById(downloadId)
        }

        val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val cursor = downloadManager.query(query)

        if (cursor.moveToFirst()) {
            val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
            if (status == DownloadManager.STATUS_SUCCESSFUL) {
                // 下载成功的处理
                val localUri =
                    cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI))

                Log.i("下载完成", "imageUrl：${localUri}")
                WxLogin.localWx(application, localUri)
            } else {
                // 下载失败的处理
                val reason = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_REASON))
                showToast("下载失败，失败原因：$reason")
            }
        }

        cursor.close()
        unregisterReceiver(downloadReceiver)

    }
}