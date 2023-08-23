package com.lelezu.app.xianzhuan.ui.views

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.lelezu.app.xianzhuan.R
import com.lelezu.app.xianzhuan.data.model.TaskQuery
import com.lelezu.app.xianzhuan.ui.fragments.TaskListFragment

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

        // 将TabLayout与ViewPager2关联起来
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabTextList[position]
        }.attach()

        viewPager.setCurrentItem(intent.getIntExtra("selectedTab", 0), false)//打开哪一页面
    }


    class TaskListFragmentPagerAdapter(activity: FragmentActivity) :
        FragmentStateAdapter(activity) {
        override fun getItemCount(): Int {
            return 4
        }

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                //	提交完成任务状态(0-未报名 1-待提交 2-审核中 3-审核通过 4-审核被否 5-手动取消 6-超时取消)
                0 -> TaskListFragment.newInstance(
                    getTaskQuery(1), true
                )

                1 -> TaskListFragment.newInstance(
                    getTaskQuery(2), true
                )

                2 -> TaskListFragment.newInstance(
                    getTaskQuery(3), true
                )

                else -> TaskListFragment.newInstance(
                    getTaskQuery(4), true
                )
            }
        }

        private fun getTaskQuery(status: Int): TaskQuery {
            return TaskQuery(
                null, 1, null, null, null, status, null
            )
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