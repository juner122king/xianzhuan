package com.lelezu.app.xianzhuan.ui.views

import android.Manifest
import android.content.ClipDescription
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.github.lzyzsd.jsbridge.BridgeWebView
import com.lelezu.app.xianzhuan.MyApplication
import com.lelezu.app.xianzhuan.R
import com.lelezu.app.xianzhuan.data.model.ErrResponse
import com.lelezu.app.xianzhuan.ui.h5.WebViewSettings
import com.lelezu.app.xianzhuan.ui.viewmodels.HomeViewModel
import com.lelezu.app.xianzhuan.ui.viewmodels.LoginViewModel
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        rootView = View.inflate(this, R.layout.activity_title, null)
        setContentView(getLayoutId())
        addContent()
        setContentView(rootView)
        initViewModel()

        val loadingView = findViewById<View>(R.id.loadingView)
        setLoadingView(loadingView)

    }
    //监听token失效

    //重新打开登录页面
    private fun goToLoginView() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
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

    public fun showBack(){
        mBack!!.visibility = View.VISIBLE
    }
    public fun hideBack(){
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
        mRltBase?.visibility = View.VISIBLE
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

}