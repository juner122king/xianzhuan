package com.lelezu.app.xianzhuan.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.lelezu.app.xianzhuan.R
import com.lelezu.app.xianzhuan.data.model.Task
import com.lelezu.app.xianzhuan.data.model.TaskQuery
import com.lelezu.app.xianzhuan.ui.adapters.TaskItemAdapter
import com.lelezu.app.xianzhuan.ui.views.RefreshRecycleView


/**
 *
 * 悬赏大厅
 */
class DashFragment : BaseFragment(), RefreshRecycleView.IOnScrollListener {


    private lateinit var recyclerView: RefreshRecycleView
    private lateinit var adapter1: TaskItemAdapter
    private lateinit var adapter2: TaskItemAdapter
    private lateinit var adapter3: TaskItemAdapter
    private lateinit var adapter4: TaskItemAdapter

    private lateinit var swiper: SwipeRefreshLayout//下拉刷新控件

    private var page: Int = 0;//当前选择page  0为第一项：置顶

    private var current1: Int = 1;//当前选择page1加载页
    private var current2: Int = 1;//当前选择page2加载页
    private var current3: Int = 1;//当前选择page3加载页
    private var current4: Int = 1;//当前选择page4加载页


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_dash, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val context = requireContext()
        val tabLayout = view.findViewById<TabLayout>(R.id.tab_task_list)
        recyclerView = view.findViewById(R.id.recyclerView)
        // 创建适配器，并将其绑定到 RecyclerView 上
        adapter1 = TaskItemAdapter(mutableListOf(), context)
        adapter2 = TaskItemAdapter(mutableListOf(), context)
        adapter3 = TaskItemAdapter(mutableListOf(), context)
        adapter4 = TaskItemAdapter(mutableListOf(), context)

        adapter1.setEmptyView(view.findViewById(R.id.recycler_layout))//设置空view
        adapter2.setEmptyView(view.findViewById(R.id.recycler_layout))
        adapter3.setEmptyView(view.findViewById(R.id.recycler_layout))
        adapter4.setEmptyView(view.findViewById(R.id.recycler_layout))

        recyclerView.adapter = adapter1

        recyclerView.setListener(this)
        recyclerView.setRefreshEnable(true)
        recyclerView.setLoadMoreEnable(true)



        swiper = view.findViewById(R.id.swiper)
        swiper.setColorSchemeResources(R.color.colorControlActivated)
        swiper.setOnRefreshListener {
            // 执行刷新操作
            refresh()
        }
        setSwipeRefreshLayout(swiper)



        tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                page = tab.position
                //因供用一个recyclerView，所以当切换page时要设置相应的adapter
                when (page) {
                    0 -> recyclerView.adapter = adapter1
                    1 -> recyclerView.adapter = adapter2
                    2 -> recyclerView.adapter = adapter3
                    3 -> recyclerView.adapter = adapter4
                }
                loadData(false)

            }

            override fun onTabUnselected(tab: TabLayout.Tab) {


            }

            override fun onTabReselected(tab: TabLayout.Tab) {


            }
        })


        // 观察 ViewModel 中的任务列表数据变化
        homeViewModel.taskList.observe(viewLifecycleOwner) {

            loadDone(it)
        }
        loadData(true)//正常加载

    }


    private fun loadDone(it: MutableList<Task>) {

        // 停止刷新动画
        swiper.isRefreshing = false
        when (page) {
            0 -> if (recyclerView.isLoadMore()) adapter1.addData(it)
            else adapter1.upData(it)

            1 -> if (recyclerView.isLoadMore()) adapter2.addData(it)
            else adapter2.upData(it)

            2 -> if (recyclerView.isLoadMore()) adapter3.addData(it)
            else adapter3.upData(it)

            3 -> if (recyclerView.isLoadMore()) adapter4.addData(it)
            else adapter4.upData(it)
        }

    }

    private fun loadData(isLoad: Boolean) {
        when (page) {
            0 -> if (adapter1.itemCount == 0 || isLoad) {
                homeViewModel.getTaskList(TaskQuery("TOP", current1, null, null, null, null, null))
            }

            1 -> if (adapter2.itemCount == 0 || isLoad) {
                homeViewModel.getTaskList(
                    TaskQuery(
                        "SIMPLE", current2, 1f, null, null, null, null
                    )
                )
            }

            2 -> if (adapter3.itemCount == 0 || isLoad) {
                homeViewModel.getTaskList(
                    TaskQuery(
                        "HIGHER", current3, null, 1f, null, null, null
                    )
                )
            }

            else -> if (adapter4.itemCount == 0 || isLoad) {
                homeViewModel.getTaskList(
                    TaskQuery(
                        "LATEST", current4, null, null, null, null, null
                    )
                )
            }
        }
    }

    private fun refresh() {
        when (page) {
            0 -> current1 = 1
            1 -> current2 = 1
            2 -> current3 = 1
            3 -> current4 = 1
        }
        loadData(true)
    }

    override fun onRefresh() {

        //
    }


    override fun onLoadMore() {
        when (page) {
            0 -> current1 = current1.inc()
            1 -> current2 = current2.inc()
            2 -> current3 = current3.inc()
            3 -> current4 = current4.inc()
        }
        loadData(true)
    }

    override fun onLoaded() {
    }


    companion object {
        @JvmStatic
        fun newInstance() = DashFragment()
    }


}