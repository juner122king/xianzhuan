package com.lelezu.app.xianzhuan.ui.fragments

import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.StrictMode.VmPolicy.Builder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.lelezu.app.xianzhuan.R
import com.lelezu.app.xianzhuan.data.model.Task
import com.lelezu.app.xianzhuan.data.model.TaskQuery
import com.lelezu.app.xianzhuan.ui.adapters.TaskItemAdapter
import com.lelezu.app.xianzhuan.ui.views.RefreshRecycleView
import com.lelezu.app.xianzhuan.utils.LogUtils

private const val ARG_PARAM = "TaskQuery"
private const val ARG_PARAM2 = "isMyTask"

/**
 * @author:Administrator
 * @date:2023/8/22 0022
 * @description:
 *
 */
class TaskListFragment : BaseFragment(), RefreshRecycleView.IOnScrollListener {

    private lateinit var recyclerView: RefreshRecycleView //下拉刷新RecycleView
    private lateinit var swiper: SwipeRefreshLayout//下拉刷新控件
    private lateinit var adapter: TaskItemAdapter
    private lateinit var taskQuery: TaskQuery // 获取任任务列表的请求体

    private var isMyTask: Boolean = false// 列表的显示类型
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            isMyTask = it.getBoolean(ARG_PARAM2)
            taskQuery = when {
                SDK_INT >= 33 -> it.getParcelable(ARG_PARAM, TaskQuery::class.java)!!
                else -> it.getParcelable(ARG_PARAM)!!
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.rv_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.recyclerView)
        swiper = view.findViewById(R.id.swiper)
        swiper.setColorSchemeResources(R.color.colorControlActivated)
        swiper.setOnRefreshListener {
            // 执行刷新操作
            refresh()
        }
        setSwipeRefreshLayout(swiper)

        adapter = TaskItemAdapter(mutableListOf(), requireActivity(), isMyTask)
        adapter.setEmptyView(view.findViewById(R.id.recycler_layout))//设置空view
        recyclerView.adapter = adapter


        recyclerView.setListener(this)
        recyclerView.setRefreshEnable(true)
        recyclerView.setLoadMoreEnable(true)


        refresh()

    }


    //
    override fun onResume() {
        super.onResume()
        observeList()
    }

    private fun observeList() {

        homeViewModel.taskList.observe(viewLifecycleOwner) {
            LogUtils.i(it.toString())
            loadDone(it)
        }

        homeViewModel.emptyListMessage.observe(viewLifecycleOwner) {
            // 如果列表为空，禁加载更多
            recyclerView.setLoadMoreEnable(false)
        }
    }

    private fun loadDone(it: MutableList<Task>) {

        // 停止刷新动画
        swiper.isRefreshing = false
        if (recyclerView.isLoadMore()) adapter.addData(it)
        else adapter.upData(it)

    }

    private fun loadData() {

        homeViewModel.getTaskList(
            taskQuery, isMyTask
        )
    }

    private fun refresh() {
        taskQuery.current = 1
        loadData()
    }

    override fun onRefresh() {
    }

    override fun onLoadMore() {
        taskQuery.current = taskQuery.current.inc()//页数+1
        loadData()

    }

    override fun onLoaded() {

    }

    companion object {

        @JvmStatic
        fun newInstance(taskQuery: TaskQuery, isMyTask: Boolean = false) =
            TaskListFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_PARAM, taskQuery)
                    putBoolean(ARG_PARAM2, isMyTask)
                }
            }
    }
}
