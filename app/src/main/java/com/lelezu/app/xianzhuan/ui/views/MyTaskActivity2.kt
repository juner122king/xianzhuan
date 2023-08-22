package com.lelezu.app.xianzhuan.ui.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.lelezu.app.xianzhuan.R
import com.lelezu.app.xianzhuan.data.model.Task
import com.lelezu.app.xianzhuan.data.model.TaskQuery
import com.lelezu.app.xianzhuan.ui.adapters.TaskItemAdapter
import com.lelezu.app.xianzhuan.ui.fragments.BaseFragment
import com.lelezu.app.xianzhuan.ui.fragments.MainFragment
import com.lelezu.app.xianzhuan.ui.fragments.MainTaskFragment
import com.lelezu.app.xianzhuan.ui.fragments.TaskListFragment
import com.zj.zjsdk.ad.ZjTaskAd

class MyTaskActivity2 : BaseActivity() {
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2

    // 定义一个包含Tab文字的List
    private var tabTextList = arrayOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tabTextList = arrayOf(
            getString(R.string.title_task_my1),
            getString(R.string.title_task_my2),
            getString(R.string.title_task_my3),
            getString(R.string.title_task_my4)
        )
        initView()
        initTaskTabLayout()
    }

    private fun initView() {
        tabLayout = findViewById(R.id.tab_task_list)
        viewPager = findViewById(R.id.task_vp)
        viewPager.adapter = TaskListFragmentPagerAdapter(this)

    }

    private fun initTaskTabLayout() {
        // 创建一个新的Tab对象
        val newTab = tabLayout.newTab()
        // 设置Tab的文本内容
        newTab.text = tabTextList[0]
        tabLayout.addTab(newTab)

        // 创建一个新的Tab对象 DK任务列表
        val newTab1 = tabLayout.newTab()
        // 创建一个新的Tab对象
        val newTab2 = tabLayout.newTab()

        // 将新的Tab添加到TabLayout中
        tabLayout.addTab(newTab1)
        tabLayout.addTab(newTab2)

        // 将TabLayout与ViewPager2关联起来
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabTextList[position]
        }.attach()

    }



    class TaskListFragmentPagerAdapter(activity: FragmentActivity) :
        FragmentStateAdapter(activity) {
        override fun getItemCount(): Int {
            return 4
        }
        override fun createFragment(position: Int): Fragment {
            return when (position) {
                //	提交完成任务状态(0-未报名 1-待提交 2-审核中 3-审核通过 4-审核被否 5-手动取消 6-超时取消)
                0 -> TaskListFragment.newInstance(1)
                1 -> TaskListFragment.newInstance(2)
                2 -> TaskListFragment.newInstance(3)
                else -> TaskListFragment.newInstance(4)
            }
        }
    }


    override fun getLayoutId(): Int {
        return R.layout.activity_my_task2
    }

    override fun getContentTitle(): String {
        return getString(R.string.title_task_my)
    }

    override fun isShowBack(): Boolean {
        return true
    }
}