package com.lelezu.app.xianzhuan.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.lelezu.app.xianzhuan.R
import com.lelezu.app.xianzhuan.data.model.Task
import com.lelezu.app.xianzhuan.data.model.TaskQuery
import com.lelezu.app.xianzhuan.data.repository.TaskRepository.Companion.queryCondHIGHER
import com.lelezu.app.xianzhuan.data.repository.TaskRepository.Companion.queryCondLATEST
import com.lelezu.app.xianzhuan.data.repository.TaskRepository.Companion.queryCondSIMPLE
import com.lelezu.app.xianzhuan.data.repository.TaskRepository.Companion.queryCondTOP


/**
 *
 * 悬赏大厅
 */
class DashFragment : BaseFragment() {

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2

    // 定义一个包含Tab文字的List
    private var tabTextList = arrayOf<String>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_dash, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tabTextList = arrayOf(
            getString(R.string.dashboard_tab1_text),
            getString(R.string.dashboard_tab2_text),
            getString(R.string.dashboard_tab3_text),
            getString(R.string.dashboard_tab4_text)
        )
        tabLayout = view.findViewById(R.id.tab_task_list)
        viewPager = view.findViewById(R.id.task_vp)
        viewPager.adapter = TaskListFragmentPagerAdapter(requireActivity())

        viewPager.offscreenPageLimit = tabTextList.size
        initTaskTabLayout()
    }

    private fun initTaskTabLayout() {

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
                0 -> TaskListFragment.newInstance(
                    getTaskQuery(queryCondTOP)
                )

                1 -> TaskListFragment.newInstance(
                    getTaskQuery(queryCondSIMPLE, 1f)
                )

                2 -> TaskListFragment.newInstance(
                    getTaskQuery(queryCondHIGHER, null, 1f)
                )

                else -> TaskListFragment.newInstance(
                    getTaskQuery(queryCondLATEST)
                )
            }
        }

        private fun getTaskQuery(
            queryCond: String, highPrice: Float? = null, lowPrice: Float? = null
        ): TaskQuery {
            return TaskQuery(
                queryCond, 1, highPrice, lowPrice
            )
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = DashFragment()
    }


}