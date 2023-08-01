package com.lelezu.app.xianzhuan.ui.views

import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.lelezu.app.xianzhuan.MyApplication
import com.lelezu.app.xianzhuan.R
import com.lelezu.app.xianzhuan.data.repository.TaskRepository
import com.lelezu.app.xianzhuan.ui.adapters.TaskItemAdapter
import com.lelezu.app.xianzhuan.ui.viewmodels.HomeViewModel

class MyTaskActivity : BaseActivity(), RefreshRecycleView.IOnScrollListener {

    private lateinit var recyclerView: RefreshRecycleView //下拉刷新RecycleView


    private lateinit var adapter1: TaskItemAdapter
    private lateinit var adapter2: TaskItemAdapter
    private lateinit var adapter3: TaskItemAdapter
    private lateinit var adapter4: TaskItemAdapter

    private val mEFRESHLoad = 0 //下拉刷新
    private val mORELoad = 1 //加载更多
    private var nowAction = mEFRESHLoad//当前动作


    private var taskStatus = 1//当前选择的子项状态 默认加载待提交

    private val homeViewModel: HomeViewModel by viewModels {
        HomeViewModel.ViewFactory((application as MyApplication).taskRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val tabLayout = findViewById<TabLayout>(R.id.tab_task_list)
        recyclerView = findViewById(R.id.rv_task)
        // 创建适配器，并将其绑定到 RecyclerView 上
        adapter1 = TaskItemAdapter(mutableListOf(), this)
        recyclerView.adapter = adapter1
        // 可以在这里设置 RecyclerView 的布局管理器，例如：
        recyclerView.layoutManager = LinearLayoutManager(this)

        recyclerView.setListener(this)
        recyclerView.setRefreshEnable(true)
        recyclerView.setLoadMoreEnable(true)





        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {

                taskStatus = when (tab.position) {
                    0 -> TaskRepository.auditStatus1
                    1 -> TaskRepository.auditStatus2
                    2 -> TaskRepository.auditStatus3
                    else -> TaskRepository.auditStatus4
                }
                loadData()
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {


            }

            override fun onTabReselected(tab: TabLayout.Tab) {


            }
        })
        // 观察 ViewModel 中的任务列表数据变化
        homeViewModel.myTaskList.observe(this) {
            // 数据变化时更新 RecyclerView

            if (nowAction == mEFRESHLoad) adapter1.upData(it)
            else adapter1.addData(it)
        }

        // 异步获取数据并更新 RecyclerView
        loadData()

    }

    fun loadData() {
        homeViewModel.getMyTaskList(taskStatus)
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
        nowAction = mEFRESHLoad
        loadData()
    }


    override fun onLoadMore() {
        nowAction = mORELoad
        loadData()
    }

    override fun onLoaded() {

    }


}