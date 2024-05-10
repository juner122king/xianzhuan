package com.lelezu.app.xianzhuan.ui.views

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.lelezu.app.xianzhuan.MyApplication
import com.lelezu.app.xianzhuan.R
import com.lelezu.app.xianzhuan.ui.adapters.HomeActivityAdapter
import com.lelezu.app.xianzhuan.ui.fragments.DashFragment
import com.lelezu.app.xianzhuan.ui.fragments.LFLFragment
import com.lelezu.app.xianzhuan.ui.fragments.MainFragment
import com.lelezu.app.xianzhuan.ui.fragments.MyFragment
import com.lelezu.app.xianzhuan.ui.fragments.NotificaFragment
import com.lelezu.app.xianzhuan.ui.h5.WebViewSettings
import com.lelezu.app.xianzhuan.utils.ShareUtil
import com.lelezu.app.xianzhuan.utils.ShareUtil.APP_SHARED_PREFERENCES_IS_NEWER
import com.lelezu.app.xianzhuan.utils.ShareUtil.NEWER_IS_SHOW_DIALOG
import com.lelezu.app.xianzhuan.utils.ShareUtil.putBoolean
import com.lzf.easyfloat.EasyFloat


class HomeActivity : BaseActivity() {
    private val fragmentList: ArrayList<Fragment> = ArrayList()
    private lateinit var dialog: Dialog //新人奖窗口
    private lateinit var viewPager: ViewPager2 //新人奖窗口

    private var isAnimationRunning = false
    private lateinit var floatingControl: View
    private lateinit var iv_a: TextView




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        postCacheTime()

        showHomeView()
        //检查新版本
        checkNewV()
        hideView()


    }


    private fun postCacheTime() {
        //赏金强提示, 登录进来时缓存当前时间
        sysMessageViewModel.tipCacheTime()
        //监听赏金到账对象变化
        sysMessageViewModel.topList.observe(this) {

            if (it.isNotEmpty()) {
                //获取最新的到账
                val award = it[it.size - 1].award
                showActivityFloat(award)
            } else {
//                ToastUtils.show("暂时没有赏金收益")


            }
        }
    }


    override fun onResume() {
        super.onResume()
        //执行获取待处理消息接口
        sysMessageViewModel.pending()
    }


    //已领取了新人奖励，直接显示主页面
    private fun showHomeView() {
        initHomeView()
        //添加是否领取新人奖判断 ：新用户登录且未显示过Dialog
        if (ShareUtil.getBoolean(APP_SHARED_PREFERENCES_IS_NEWER) && !ShareUtil.getBoolean(
                NEWER_IS_SHOW_DIALOG
            )
        ) showDialog()


        //创建消息浮窗
        createMessageFloat()


        //监听发布任务前的验证接口
        homeViewModel.limit.observe(this) {
            if (it.isLimit) {
                val intent = Intent(this, WebViewActivity::class.java)
                intent.putExtra(WebViewSettings.LINK_KEY, WebViewSettings.link5)
                intent.putExtra(WebViewSettings.URL_TITLE, "选择任务分类")
                startActivity(intent)
            } else {
                showPostDialog(it.endTime)
            }
        }

    }

    private fun initHomeView() {

        floatingControl = findViewById(R.id.floating_control)
        iv_a = findViewById(R.id.iv_a)

        viewPager = findViewById(R.id.main_vp)
        viewPager.isUserInputEnabled = false//禁止滑动
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.main_bnv)
        initData()
        val adapter = HomeActivityAdapter(supportFragmentManager, lifecycle, fragmentList)
        viewPager.adapter = adapter
        //  页面更改监听
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(
                position: Int, positionOffset: Float, positionOffsetPixels: Int,
            ) {
            }

            override fun onPageSelected(position: Int) {

                if (position == 0) {
                    //执行接口，获取赏金收益列表
                    sysMessageViewModel.tipList()
                }
                when (position) {
                    0 -> bottomNavigationView.selectedItemId = R.id.navigation_home
                    1 -> bottomNavigationView.selectedItemId = R.id.navigation_dashboard
                    2 -> bottomNavigationView.selectedItemId = R.id.navigation_lfl
                    3 -> bottomNavigationView.selectedItemId = R.id.navigation_notifications
                    4 -> bottomNavigationView.selectedItemId = R.id.navigation_my
                }
            }

            override fun onPageScrollStateChanged(state: Int) {
            }
        })


        //  图标选择监听
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->

            // 获取当前选中项的ID
            val itemId = item.itemId
            // 获取当前已经选中的项的ID
            val selectedItemId = bottomNavigationView.selectedItemId

            // 判断当前点击的项是否为已经选中的项, 如果是已经选中的项，则不执行任何操作
            if (itemId == selectedItemId) {
                false
            } else {
                hideRightText()
                when (itemId) {
                    R.id.navigation_home -> {
                        viewPager.setCurrentItem(0, false)
                        setTitleText(getString(R.string.title_activity_home))
                        showFloat()
                        hideView()
                        hideBack()
                    }

                    R.id.navigation_dashboard -> {
                        viewPager.setCurrentItem(1, false)
                        showRightText(getString(R.string.dashboard_tab5_text))
                        setTitleText(getString(R.string.title_dashboard))
                        hideMFloat()
                        showView()
                        hideBack()
                    }

                    R.id.navigation_lfl -> {
                        viewPager.setCurrentItem(2, false)
                        setTitleText(getString(R.string.title_lfl))
                        hideMFloat()
                        showView()

                    }

                    R.id.navigation_notifications -> {
                        viewPager.setCurrentItem(3, false)
                        setTitleText(getString(R.string.title_notifications))
                        hideMFloat()
                        showView()
                        hideBack()
                    }

                    R.id.navigation_my -> {
                        viewPager.setCurrentItem(4, false)
                        setTitleText(getString(R.string.title_my))
                        hideView()
                        hideMFloat()
                        hideBack()
                    }
                }
                true
            }


        }


        //处理H5页面返回主页的动作
        val fragmentPosition = intent.getStringExtra("FragmentPosition")
        if (fragmentPosition != null) {
            when (fragmentPosition) {
                "1" -> viewPager.currentItem = 0
                "2" -> viewPager.currentItem = 1
                "3" -> viewPager.currentItem = 2
                "4" -> viewPager.currentItem = 3
                "5" -> viewPager.currentItem = 4
            }
        }

        //去掉长按吐司

        val bottomNavView: View = bottomNavigationView.getChildAt(0)
        bottomNavView.findViewById<View>(R.id.navigation_home).setOnLongClickListener { true }
        bottomNavView.findViewById<View>(R.id.navigation_my).setOnLongClickListener { true }
        if (!MyApplication.isMarketVersion) {
            bottomNavView.findViewById<View>(R.id.navigation_dashboard)
                .setOnLongClickListener { true }
            bottomNavView.findViewById<View>(R.id.navigation_notifications)
                .setOnLongClickListener { true }
        }


    }

    private fun initData() {
        val mainFragment = MainFragment.newInstance()
        fragmentList.add(mainFragment)

        if (!MyApplication.isMarketVersion) {
            val dashFragment = DashFragment.newInstance()
            fragmentList.add(dashFragment)

            val lflFragment = LFLFragment()
            fragmentList.add(lflFragment)

            val notificaFragment = NotificaFragment()
            fragmentList.add(notificaFragment)
        }


        val myFragment = MyFragment.newInstance()
        fragmentList.add(myFragment)


    }

    fun currentViewPager(item: Int) {
        showView()
        viewPager.setCurrentItem(item, false)
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_home
    }

    override fun getContentTitle(): String {
        return getString(R.string.title_activity_home)
    }

    //是否显示返回键
    override fun isShowBack(): Boolean {
        return false
    }


    private fun showDialog() {

        dialog = Dialog(this)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setContentView(R.layout.dialog_new_user)

        val ok: View = dialog.findViewById(R.id.ok)
        ok.setOnClickListener {

            dialog.dismiss()
            putBoolean(NEWER_IS_SHOW_DIALOG, true)//显示过窗口
            putBoolean(APP_SHARED_PREFERENCES_IS_NEWER, false)//非新人

            //确定
            //跳到新人奖励
            val intent = Intent(this, WebViewActivity::class.java)
            intent.putExtra(WebViewSettings.LINK_KEY, WebViewSettings.link2)
            intent.putExtra(WebViewSettings.URL_TITLE, getString(R.string.btm_xrjl))
            startActivity(intent)
        }
        dialog.show()
    }

    private fun showActivityFloat(amt: String) {

        if (isAnimationRunning) return

        iv_a.text = "+${amt}元"

        // 初始化动画
        val animSetIn = AnimatorSet()
        val animSetOut = AnimatorSet()

        // 从右侧滑出动画
        val slideInAnimation = ObjectAnimator.ofFloat(
            floatingControl, View.TRANSLATION_X, floatingControl.width.toFloat(), 0f
        ).apply {
            duration = 1000 // 设置动画持续时间
        }

        // 回到初始位置并隐藏动画
        val slideOutAnimation = ObjectAnimator.ofFloat(
            floatingControl, View.TRANSLATION_X, 0f, floatingControl.width.toFloat()
        ).apply {
            duration = 800 // 设置动画持续时间
        }


        // 设置延迟4秒执行
        animSetIn.playSequentially(slideInAnimation)
        animSetIn.startDelay = 0
        // 设置延迟4秒执行
        animSetOut.playSequentially(slideOutAnimation)
        animSetOut.startDelay = 0

        // 设置标志变量为true，表示动画正在执行
        isAnimationRunning = true
        animSetIn.start()
        floatingControl.visibility = View.VISIBLE

        Handler(Looper.getMainLooper()).postDelayed({
            animSetOut.start()
        }, 4000L)//4秒后隐藏控件


        // 监听动画执行完毕事件
        animSetOut.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                // 将标志变量设置为false，表示动画执行完毕
                isAnimationRunning = false
                // 隐藏floatingControl
                floatingControl.visibility = View.INVISIBLE
            }
        })

    }

    override fun onDestroy() {
        super.onDestroy()
        EasyFloat.dismiss(MFloat_TAG)
        homeViewModel.limit.removeObservers(this)
    }


}
