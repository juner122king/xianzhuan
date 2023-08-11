package com.lelezu.app.xianzhuan.ui.views

import android.os.Bundle
import androidx.activity.viewModels
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.tabs.TabLayout
import com.lelezu.app.xianzhuan.MyApplication
import com.lelezu.app.xianzhuan.R
import com.lelezu.app.xianzhuan.data.model.Task
import com.lelezu.app.xianzhuan.ui.adapters.TaskItemAdapter
import com.lelezu.app.xianzhuan.ui.viewmodels.HomeViewModel
import com.lelezu.app.xianzhuan.utils.ToastUtils

class MyTaskActivity : BaseActivity(), RefreshRecycleView.IOnScrollListener {

    private lateinit var recyclerView: RefreshRecycleView //下拉刷新RecycleView
    private lateinit var swiper: SwipeRefreshLayout//下拉刷新控件

    private lateinit var adapter1: TaskItemAdapter
    private lateinit var adapter2: TaskItemAdapter
    private lateinit var adapter3: TaskItemAdapter
    private lateinit var adapter4: TaskItemAdapter

    private var page: Int = 0//当前选择page  0为第一项：置顶

    private var current1: Int = 1//当前选择page1加载页
    private var current2: Int = 1//当前选择page2加载页
    private var current3: Int = 1//当前选择page3加载页
    private var current4: Int = 1//当前选择page4加载页

    private var auditStatus = 1//当前选择的子项状态 默认加载待提交

    private val homeViewModel: HomeViewModel by viewModels {
        HomeViewModel.ViewFactory((application as MyApplication).taskRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initView()
    }

    private fun initView() {
        val tabLayout = findViewById<TabLayout>(R.id.tab_task_list)
        recyclerView = findViewById(R.id.recyclerView)
        // 创建适配器，并将其绑定到 RecyclerView 上
        // 创建适配器，并将其绑定到 RecyclerView 上
        adapter1 = TaskItemAdapter(mutableListOf(), this)
        adapter2 = TaskItemAdapter(mutableListOf(), this)
        adapter3 = TaskItemAdapter(mutableListOf(), this)
        adapter4 = TaskItemAdapter(mutableListOf(), this)

        adapter1.setEmptyView(findViewById(R.id.recycler_layout))//设置空view
        adapter2.setEmptyView(findViewById(R.id.recycler_layout))
        adapter3.setEmptyView(findViewById(R.id.recycler_layout))
        adapter4.setEmptyView(findViewById(R.id.recycler_layout))

        recyclerView.adapter = adapter1


        recyclerView.setListener(this)
        recyclerView.setRefreshEnable(true)
        recyclerView.setLoadMoreEnable(true)

        swiper = findViewById(R.id.swiper)
        swiper.setColorSchemeResources(R.color.colorControlActivated)
        swiper.setOnRefreshListener {
            // 执行刷新操作
            refresh()
        }

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                page = tab.position
                //因供用一个recyclerView，所以当切换page时要设置相应的adapter
                when (page) {
                    0 -> {
                        recyclerView.adapter = adapter1
                        adapter1.notifyDataSetChanged()

                        auditStatus = 1
                    }

                    1 -> {
                        recyclerView.adapter = adapter2
                        adapter2.notifyDataSetChanged()
                        auditStatus = 2
                    }

                    2 -> {
                        recyclerView.adapter = adapter3
                        adapter3.notifyDataSetChanged()
                        auditStatus = 3
                    }

                    3 -> {
                        recyclerView.adapter = adapter4
                        adapter4.notifyDataSetChanged()
                        auditStatus = 4
                    }
                }
                loadData(false)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {


            }

            override fun onTabReselected(tab: TabLayout.Tab) {


            }
        })
        // 观察 ViewModel 中的任务列表数据变化
        homeViewModel.myTaskList.observe(this) {
            loadDone(it)
        }
        //弹出错误
        homeViewModel.errMessage.observe(this) {
            showToast(it)
        }
    }

    private fun initData() {

        page = 0;//当前选择page  0为第一项：置顶
        current1 = 1;//当前选择page1加载页
        current2 = 1;//当前选择page2加载页
        current3 = 1;//当前选择page3加载页
        current4 = 1;//当前选择page4加载页
        auditStatus = 1//当前选择的子项状态 默认加载待提交

    }

    override fun onResume() {
        super.onResume()
        initData()
        // 执行刷新操作
        refresh()

    }

    private fun loadDone(it: MutableList<Task>) {
        // 停止刷新动画
        swiper.isRefreshing = false
        if (it.isEmpty() && recyclerView.isLoadMore()) {
            showToast("没有更多了！")
        } else {
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
    }

    fun loadData(isLoad: Boolean) {
        when (page) {
            0 -> if (adapter1.itemCount == 0 || isLoad) {
                homeViewModel.getMyTaskList(auditStatus, current1)
            }

            1 -> if (adapter2.itemCount == 0 || isLoad) {
                homeViewModel.getMyTaskList(auditStatus, current2)
            }

            2 -> if (adapter3.itemCount == 0 || isLoad) {
                homeViewModel.getMyTaskList(auditStatus, current3)
            }

            else -> if (adapter4.itemCount == 0 || isLoad) {
                homeViewModel.getMyTaskList(auditStatus, current4)
            }
        }

    }

    override fun getLayoutId(): Int {
        return R.layout.activity_my_task
    }

    override fun getContentTitle(): String {
        return getString(R.string.title_task_my)
    }

    override fun isShowBack(): Boolean {
        return true
    }

    override fun onRefresh() {

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

    private fun refresh() {
        when (page) {
            0 -> current1 = 1
            1 -> current2 = 1
            2 -> current3 = 1
            3 -> current4 = 1
        }
        loadData(true)
    }


}