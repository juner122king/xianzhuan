package com.lelezu.app.xianzhuan.ui.views

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.lelezu.app.xianzhuan.R
import com.lelezu.app.xianzhuan.ui.adapters.HomeActivityAdapter
import com.lelezu.app.xianzhuan.ui.fragments.DashFragment
import com.lelezu.app.xianzhuan.ui.fragments.MainFragment
import com.lelezu.app.xianzhuan.ui.fragments.MyFragment
import com.lelezu.app.xianzhuan.ui.fragments.NotificaFragment

class HomeActivity : AppCompatActivity() {
    private val fragmentList: ArrayList<Fragment> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val viewPager = findViewById<ViewPager2>(R.id.main_vp)
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.main_bnv)

        initData()

        val adapter = HomeActivityAdapter(supportFragmentManager, lifecycle, fragmentList)
        viewPager.adapter = adapter




        //  页面更改监听
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(
                position: Int, positionOffset: Float, positionOffsetPixels: Int
            ) {
                // Do nothing
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
                // Do nothing
            }
        })


        //  图标选择监听
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> viewPager.currentItem = 0
                R.id.navigation_dashboard -> viewPager.currentItem = 1
                R.id.navigation_notifications -> viewPager.currentItem = 2
                R.id.navigation_my -> viewPager.currentItem = 3
            }
            true
        }
    }

    private fun initData() {
        val mainFragment = MainFragment.newInstance(getString(R.string.title_home), "")
        fragmentList.add(mainFragment)

        val dashFragment = DashFragment.newInstance(getString(R.string.title_dashboard), "")
        fragmentList.add(dashFragment)

        val notificaFragment =
            NotificaFragment.newInstance(getString(R.string.title_notifications), "")
        fragmentList.add(notificaFragment)

        val myFragment = MyFragment.newInstance(getString(R.string.title_my), "")
        fragmentList.add(myFragment)
    }
}
