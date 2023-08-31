package com.lelezu.app.xianzhuan.ui.views

import android.Manifest
import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.ClipDescription
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.view.ContextMenu
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.lelezu.app.xianzhuan.MyApplication
import com.lelezu.app.xianzhuan.R
import com.lelezu.app.xianzhuan.data.model.ErrResponse
import com.lelezu.app.xianzhuan.ui.viewmodels.HomeViewModel
import com.lelezu.app.xianzhuan.ui.viewmodels.LoginViewModel
import com.lelezu.app.xianzhuan.ui.viewmodels.SysMessageViewModel
import com.lelezu.app.xianzhuan.utils.LogUtils
import com.lelezu.app.xianzhuan.utils.ShareUtil
import com.lelezu.app.xianzhuan.utils.ToastUtils


/**
 * @author:Administrator
 * @date:2023/7/26 0026
 * @description:
 *
 */
abstract class BaseActivity : AppCompatActivity() {


    public var mBack: LinearLayout? = null
    private var mTvTitle: TextView? = null
    private var mTvRight: TextView? = null
    private var mRltBase: RelativeLayout? = null
    private var rootView: View? = null

    private var loadingView: View? = null

    protected val loginViewModel: LoginViewModel by viewModels {
        LoginViewModel.LoginViewFactory((application as MyApplication).userRepository)
    }

    protected val homeViewModel: HomeViewModel by viewModels {
        HomeViewModel.ViewFactory((application as MyApplication).taskRepository)
    }
    protected val sysMessageViewModel: SysMessageViewModel by viewModels {
        SysMessageViewModel.ViewFactory((application as MyApplication).sysInformRepository)
    }

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

    }
    //监听token失效

    //重新打开登录页面
    private fun goToLoginView() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    fun logOut() {
        LogUtils.i("logOut", "logOut成功")
        showToast("退出成功！")
        ShareUtil.cleanInfo()
        goToLoginView()
    }


    protected fun backToHome(position: String) {
        LogUtils.d("进入主页:${position}")
        val intent = Intent(this, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        intent.putExtra("FragmentPosition", position)
        startActivity(intent)
    }


    protected fun gotoTaskDetails(taskId: String) {

        LogUtils.d("进入任务详情:${taskId}")
        val intent = Intent(this, TaskDetailsActivity::class.java)
        intent.putExtra("taskId", taskId)
        startActivity(intent)
    }

    protected fun goToHomeActivity() {

        val intent = Intent(MyApplication.context, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
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
        LogUtils.d("ErrResponse:${it}")
        hideLoading()
        showToast(it.message)
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

    public fun showBack() {
        mBack!!.visibility = View.VISIBLE
    }

    public fun hideBack() {
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
        ToastUtils.showToast(this, message, Toast.LENGTH_SHORT)
    }


    override fun onStop() {
        super.onStop()
        hideLoading()
    }

    private val rc: Int = 123
    protected fun checkPermissionRead() {

        // 检查图片权限
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            //13更高版本后的图片弹窗询问
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.READ_MEDIA_IMAGES), rc
                )
            } else {

                ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), rc
                )
            }
        }
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
                    // 在这里执行你需要的操作
//                    if (!ShareUtil.isConnected()) showToast("网络已连接")
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
            Toast.makeText(
                this,
                "保存图片：${ShareUtil.getString(ShareUtil.APP_TASK_PIC_DOWN_URL)}",
                Toast.LENGTH_SHORT
            ).show()
            //进行保存图片操作
            true
        }

        menu.getItem(1).setOnMenuItemClickListener {
            ivDialog.dismiss()
            true
        }
    }
}