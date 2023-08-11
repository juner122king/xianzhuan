package com.lelezu.app.xianzhuan.ui.views

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.lelezu.app.xianzhuan.R
import com.lelezu.app.xianzhuan.ui.adapters.HomeActivityAdapter
import com.lelezu.app.xianzhuan.ui.fragments.DashFragment
import com.lelezu.app.xianzhuan.ui.fragments.MainFragment
import com.lelezu.app.xianzhuan.ui.fragments.MyFragment
import com.lelezu.app.xianzhuan.ui.fragments.NotificaFragment
import com.lelezu.app.xianzhuan.utils.ToastUtils
import com.lelezu.app.xianzhuan.wxapi.WxLogin

class HomeActivity : BaseActivity() {
    private val fragmentList: ArrayList<Fragment> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
            when (item.itemId) {
                R.id.navigation_home -> {
                    viewPager.currentItem = 0
                    setTitleText(getString(R.string.title_activity_home))

                }

                R.id.navigation_dashboard -> {
                    viewPager.currentItem = 1
                    showRightText(getString(R.string.dashboard_tab5_text))
                    setTitleText(getString(R.string.title_dashboard))
                }

                R.id.navigation_notifications -> {
                    viewPager.currentItem = 2
                    setTitleText(getString(R.string.title_notifications))

                }

                R.id.navigation_my -> {
                    viewPager.currentItem = 3
                    setTitleText(getString(R.string.title_my))
                    hideView()

                }
            }
            true
        }


        initZjTask()


    }


    private fun initData() {
        val mainFragment = MainFragment.newInstance()
        fragmentList.add(mainFragment)

        val dashFragment = DashFragment.newInstance(getString(R.string.title_dashboard), "")
        fragmentList.add(dashFragment)

        val notificaFragment =
            NotificaFragment.newInstance(getString(R.string.title_notifications), "")
        fragmentList.add(notificaFragment)

        val myFragment = MyFragment.newInstance(getString(R.string.title_my), "")
        fragmentList.add(myFragment)
    }


    private fun initZjTask() {



    }

    override fun getLayoutId(): Int {
        return R.layout.activity_home
    }

    override fun getContentTitle(): String? {
        return getString(R.string.title_activity_home)
    }

    //是否显示返回键
    override fun isShowBack(): Boolean {
        return false
    }


    //保存图片方法
    fun saveImageToSystem(imageUrl: String) {
        Log.i("H5保存图片", "成功  imageUrl：${imageUrl}")

        val imageName = getString(R.string.app_name) + "_" + System.currentTimeMillis()

        ToastUtils.showToast(this, "图片已保存:${Environment.DIRECTORY_DOWNLOADS}$imageName", 0)

        val request = DownloadManager.Request(Uri.parse(imageUrl))
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "$imageName.jpg")

        val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        downloadManager.enqueue(request)
    }

    //分享微信
    fun shareFriends(imageUrl: String) {
        Log.i("H5分享图片", "imageUrl：${imageUrl}")


        WxLogin.webWx(imageUrl)

    }

}
