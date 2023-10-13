package com.lelezu.app.xianzhuan.ui.views

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.lelezu.app.xianzhuan.R
import com.lelezu.app.xianzhuan.ui.adapters.HomeActivityAdapter
import com.lelezu.app.xianzhuan.ui.fragments.DashFragment
import com.lelezu.app.xianzhuan.ui.fragments.MainFragment
import com.lelezu.app.xianzhuan.ui.fragments.MyFragment
import com.lelezu.app.xianzhuan.ui.fragments.NotificaFragment
import com.lelezu.app.xianzhuan.utils.LogUtils
import com.lelezu.app.xianzhuan.utils.ShareUtil
import com.lelezu.app.xianzhuan.wxapi.WxLogin

class HomeActivity : BaseActivity() {
    private val fragmentList: ArrayList<Fragment> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        LogUtils.i(
            "HomeActivity",
            "LOGIN_ID:" + ShareUtil.getString(ShareUtil.APP_SHARED_PREFERENCES_LOGIN_ID)
        )


        showHomeView()

        //检查新版本
        checkNewV()
    }


    //已领取了新人奖励，直接显示主页面
    private fun showHomeView() {
        initHomeView()
    }

    private fun initHomeView() {


        val viewPager = findViewById<ViewPager2>(R.id.main_vp)
        viewPager.isUserInputEnabled = false//禁止滑动
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.main_bnv)
        initData()
        val adapter = HomeActivityAdapter(supportFragmentManager, lifecycle, fragmentList)
        viewPager.adapter = adapter
        //  页面更改监听
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(
                position: Int, positionOffset: Float, positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                when (position) {
                    0 -> bottomNavigationView.selectedItemId = R.id.navigation_home
                    1 -> bottomNavigationView.selectedItemId = R.id.navigation_dashboard
                    2 -> bottomNavigationView.selectedItemId = R.id.navigation_notifications
                    3 -> bottomNavigationView.selectedItemId = R.id.navigation_my
                }
            }

            override fun onPageScrollStateChanged(state: Int) {
            }
        })
        //  图标选择监听
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            hideRightText()
            showView()
            hideBack()
            when (item.itemId) {
                R.id.navigation_home -> {
                    viewPager.setCurrentItem(0, false)
                    setTitleText(getString(R.string.title_activity_home))

                }

                R.id.navigation_dashboard -> {
                    viewPager.setCurrentItem(1, false)
                    showRightText(getString(R.string.dashboard_tab5_text))
                    setTitleText(getString(R.string.title_dashboard))
                }

                R.id.navigation_notifications -> {
                    viewPager.setCurrentItem(2, false)
                    setTitleText(getString(R.string.title_notifications))


                }

                R.id.navigation_my -> {
                    viewPager.setCurrentItem(3, false)
                    setTitleText(getString(R.string.title_my))
                    hideView()

                }
            }
            true
        }


        //处理H5页面返回主页的动作
        val fragmentPosition = intent.getStringExtra("FragmentPosition")
        if (fragmentPosition != null) {
            when (fragmentPosition) {
                "1" -> viewPager.currentItem = 0
                "2" -> viewPager.currentItem = 1
                "3" -> viewPager.currentItem = 2
                "4" -> viewPager.currentItem = 3
            }
        }

        //去掉长按吐司

        val bottomNavView: View = bottomNavigationView.getChildAt(0)
        bottomNavView.findViewById<View>(R.id.navigation_home).setOnLongClickListener { true }
        bottomNavView.findViewById<View>(R.id.navigation_dashboard).setOnLongClickListener { true }
        bottomNavView.findViewById<View>(R.id.navigation_notifications)
            .setOnLongClickListener { true }
        bottomNavView.findViewById<View>(R.id.navigation_my).setOnLongClickListener { true }
    }

    private fun initData() {
        val mainFragment = MainFragment.newInstance()
        fragmentList.add(mainFragment)

        val dashFragment = DashFragment.newInstance()
        fragmentList.add(dashFragment)

        val notificaFragment = NotificaFragment()
        fragmentList.add(notificaFragment)

        val myFragment = MyFragment.newInstance()
        fragmentList.add(myFragment)
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

    private fun checkNewV() {
        if (!ShareUtil.getBoolean(ShareUtil.CHECKED_NEW_VISON)) {//未询问过更新版本
            //检查新版本
            sysMessageViewModel.detection()
        }
    }
}
