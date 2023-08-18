package com.lelezu.app.xianzhuan.ui.views

import android.Manifest
import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.lelezu.app.xianzhuan.R
import com.lelezu.app.xianzhuan.ui.adapters.HomeActivityAdapter
import com.lelezu.app.xianzhuan.ui.fragments.DashFragment
import com.lelezu.app.xianzhuan.ui.fragments.MainFragment
import com.lelezu.app.xianzhuan.ui.fragments.MyFragment
import com.lelezu.app.xianzhuan.ui.fragments.NotificaFragment
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
                    viewPager.setCurrentItem(0,false)
                    setTitleText(getString(R.string.title_activity_home))

                }

                R.id.navigation_dashboard -> {
//                    viewPager.currentItem = 1
                    viewPager.setCurrentItem(1,false)
                    showRightText(getString(R.string.dashboard_tab5_text))
                    setTitleText(getString(R.string.title_dashboard))
                }

                R.id.navigation_notifications -> {
//                    viewPager.currentItem = 2
                    viewPager.setCurrentItem(2,false)
                    setTitleText(getString(R.string.title_notifications))

                }

                R.id.navigation_my -> {
//                    viewPager.currentItem = 3
                    viewPager.setCurrentItem(3,false)
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

    }

    private fun initData() {
        val mainFragment = MainFragment.newInstance()
        fragmentList.add(mainFragment)

        val dashFragment = DashFragment.newInstance()
        fragmentList.add(dashFragment)

        val notificaFragment = NotificaFragment.newInstance()
        fragmentList.add(notificaFragment)

        val myFragment = MyFragment.newInstance()
        fragmentList.add(myFragment)
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

        val imageName = "1455"

        showToast("图片已保存:${Environment.DIRECTORY_DOWNLOADS}$imageName")

        val request = DownloadManager.Request(Uri.parse(imageUrl))
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "$imageName.jpg")

        val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        downloadManager.enqueue(request)
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
    //分享微信
    fun shareFriends(imageUrl: String) {
        Log.i("H5分享图片", "imageUrl：${imageUrl}")
        // 注册广播接收器
        val intentFilter = IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        registerReceiver(downloadReceiver, intentFilter)
        saveImageToSystem(imageUrl)



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
                val localUri = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI))

                Log.i("下载完成", "imageUrl：${localUri}")
                WxLogin.localWx(localUri)
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
