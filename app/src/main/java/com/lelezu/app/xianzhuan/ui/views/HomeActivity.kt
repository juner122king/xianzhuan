package com.lelezu.app.xianzhuan.ui.views

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.lelezu.app.xianzhuan.MyApplication
import com.lelezu.app.xianzhuan.R
import com.lelezu.app.xianzhuan.ui.adapters.HomeActivityAdapter
import com.lelezu.app.xianzhuan.ui.fragments.DashFragment
import com.lelezu.app.xianzhuan.ui.fragments.MainFragment
import com.lelezu.app.xianzhuan.ui.fragments.MyFragment
import com.lelezu.app.xianzhuan.ui.fragments.NotificaFragment
import com.lelezu.app.xianzhuan.ui.h5.WebViewSettings
import com.lelezu.app.xianzhuan.utils.ShareUtil
import com.lelezu.app.xianzhuan.utils.ShareUtil.APP_SHARED_PREFERENCES_IS_NEWER
import com.lelezu.app.xianzhuan.utils.ShareUtil.NEWER_IS_SHOW_DIALOG
import com.lelezu.app.xianzhuan.utils.ShareUtil.putBoolean

class HomeActivity : BaseActivity() {
    private val fragmentList: ArrayList<Fragment> = ArrayList()
    private lateinit var dialog: Dialog //新人奖窗口
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        showHomeView()

        //检查新版本
        checkNewV()

    }


    //已领取了新人奖励，直接显示主页面
    private fun showHomeView() {
        initHomeView()
        //添加是否领取新人奖判断 ：新用户登录且未显示过Dialog
        if (ShareUtil.getBoolean(APP_SHARED_PREFERENCES_IS_NEWER) && !ShareUtil.getBoolean(
                NEWER_IS_SHOW_DIALOG
            )
        ) showDialog()

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
                position: Int, positionOffset: Float, positionOffsetPixels: Int,
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


                    //弹出浮窗控件
//                    showFloat()
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

            val notificaFragment = NotificaFragment()
            fragmentList.add(notificaFragment)
        }


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
        if (MyApplication.isMarketVersion) return

        //未询问过更新版本
        if (!ShareUtil.getIsCHECKEDNV()) sysMessageViewModel.detection(true)

    }

    private fun showDialog() {
        if (MyApplication.isMarketVersion) return

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

}
