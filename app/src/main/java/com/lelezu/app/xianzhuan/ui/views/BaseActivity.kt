package com.lelezu.app.xianzhuan.ui.views

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.lelezu.app.xianzhuan.MyApplication
import com.lelezu.app.xianzhuan.R
import com.lelezu.app.xianzhuan.ui.viewmodels.HomeViewModel
import com.lelezu.app.xianzhuan.ui.viewmodels.LoginViewModel
import com.lelezu.app.xianzhuan.utils.ToastUtils


/**
 * @author:Administrator
 * @date:2023/7/26 0026
 * @description:
 *
 */
abstract class BaseActivity : AppCompatActivity() {
    private var mBack: LinearLayout? = null
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
    private fun initViewModel() {

        loginViewModel.errMessage.observe(this){
            hideLoading()
            showToast(it)
        }
        homeViewModel.errMessage.observe(this){
            hideLoading()
            showToast(it)
        }
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


}